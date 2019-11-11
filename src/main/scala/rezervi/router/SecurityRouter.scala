package rezervi.router

import akka.http.scaladsl.model.headers.{BasicHttpCredentials, HttpChallenge, HttpChallenges}
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.BasicDirectives.{extractExecutionContext, provide}
import akka.http.scaladsl.server.directives.FutureDirectives.onSuccess
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.directives.{AuthenticationDirective, AuthenticationResult, Credentials}
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import pdi.jwt.{JwtOptions, JwtSprayJson}
import rezervi.domain.security.Authenticate
import rezervi.model.security.ApiKey

import scala.concurrent.Future
import scala.util.{Failure, Success}

class SecurityRouter(authenticationService: Authenticate) {
  def onAuthenticated: AuthenticationDirective[AuthenticatedUser] = {
    onBasic | onOAuth2 | onApiKey
  }

  private val realm = "Authenticated API"

  private def onBasic: AuthenticationDirective[AuthenticatedUser] = {
    extractExecutionContext.flatMap { implicit ec =>
      authenticateOrRejectWithChallenge[BasicHttpCredentials, AuthenticatedUser] { cred =>
        basicAuthenticator(cred).map {
          case Some(t) => AuthenticationResult.success(t)
          case None => AuthenticationResult.failWithChallenge(HttpChallenges.basic(realm))
        }
      }
    }
  }

  private def basicAuthenticator(credentials: Option[BasicHttpCredentials]): Future[Option[AuthenticatedUser]] = {
    credentials match {
      case Some(BasicHttpCredentials(username, password)) =>
        authenticationService.usingBasic(username, password)

      case _ =>
        Future.successful(None)
    }
  }

  private def onOAuth2: AuthenticationDirective[AuthenticatedUser] = {
    authenticateOAuth2Async(realm, oauth2Authenticator)
  }

  private def oauth2Authenticator(credentials: Credentials): Future[Option[AuthenticatedUser]] = {
    credentials match {
      case provided: Credentials.Provided =>
        // /!\ Signature not confirmed
        JwtSprayJson.decode(provided.identifier, JwtOptions.DEFAULT.copy(signature = false)) match {
          case Success(token) =>
            (token.issuer, token.subject) match {
              case (Some(issuer), Some(subject)) =>
                authenticationService.usingOAuth2(iss = issuer, sub = subject)
              case _ => Future.successful(None)
            }

          case Failure(_) =>
            Future.successful(None)
        }
      case _ => Future.successful(None)
    }
  }

  private def onApiKey: AuthenticationDirective[AuthenticatedUser] = {
    extractExecutionContext.flatMap { implicit ec =>
      optionalHeaderValueByName("X-API-KEY").flatMap { cred =>
        onSuccess(tokenAuthenticator(cred)).flatMap {
          case Some(user) =>
            provide(user)

          case None =>
            val cause = if (cred.isEmpty) CredentialsMissing else CredentialsRejected
            reject(AuthenticationFailedRejection(cause, HttpChallenge("API-KEY", realm))): Directive1[AuthenticatedUser]
        }
      }
    }
  }

  private def tokenAuthenticator(token: Option[String]): Future[Option[AuthenticatedUser]] = {
    token match {
      case Some(token) => authenticationService.usingApiKey(ApiKey.fromRaw(token))
      case _ => Future.successful(None)
    }
  }
}
