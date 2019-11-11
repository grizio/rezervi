package rezervi.domain.security

import cats.data.OptionT
import cats.implicits._
import rezervi.model.security.ApiKey
import rezervi.persistence.postgres.AuthenticationRepository
import rezervi.router.AuthenticatedUser
import rezervi.utils.Crypto

import scala.concurrent.{ExecutionContext, Future}

class Authenticate(
  crypto: Crypto,
  authenticationRepository: AuthenticationRepository
)(implicit ec: ExecutionContext) {
  def usingBasic(username: String, password: String): Future[Option[AuthenticatedUser]] = {
    (for {
      auth <- OptionT(authenticationRepository.findAuthenticationLocal(username))
      passwordIsValid <- OptionT.liftF(crypto.bcryptCheck(password, auth.encryptedPassword))
      if passwordIsValid
    } yield {
      AuthenticatedUser(auth.uid)
    }).value
  }

  def usingOAuth2(iss: String, sub: String): Future[Option[AuthenticatedUser]] = {
    (for {
      auth <- OptionT(authenticationRepository.findAuthenticationOAuth2(iss, sub))
    } yield {
      AuthenticatedUser(auth.uid)
    }).value
  }

  def usingApiKey(apiKey: ApiKey): Future[Option[AuthenticatedUser]] = {
    (for {
      auth <- OptionT(authenticationRepository.findAuthenticationToken(apiKey))
      secretIsValid <- OptionT.liftF(crypto.aesCheck(apiKey.secret, auth.encodedSecret))
      if secretIsValid
    } yield {
      AuthenticatedUser(auth.uid)
    }).value
  }
}
