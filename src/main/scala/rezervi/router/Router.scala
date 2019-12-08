package rezervi.router

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import rezervi.domain.security.ManageToken
import rezervi.domain.session.command.ManageSession
import rezervi.domain.session.query.FindSessions
import rezervi.domain.theater.command.ManageTheater
import rezervi.domain.theater.query.FindTheaters
import rezervi.model.security.NormalizedUUID
import rezervi.model.session.{SessionCreation, SessionUpdate}
import rezervi.model.theater.{TheaterCreation, TheaterUpdate}
import rezervi.router.result.ToHttpResult
import spray.json.RootJsonWriter

import scala.concurrent.Future

class Router(
  securityRouter: SecurityRouter,
  manageToken: ManageToken,
  findTheaters: FindTheaters,
  manageTheater: ManageTheater,
  findSessions: FindSessions,
  manageSession: ManageSession
) extends SprayJsonSupport {

  import rezervi.router.PathMatchers._
  import rezervi.router.json.Jsons._
  import rezervi.router.result.HttpResults._

  def buildRoutes(): Route = {
    openApi() ~ security() ~ theater() ~ session()
  }

  private def openApi(): Route = {
    path("openapi.yaml") {
      getFromFile("src/main/resources/openapi.yaml")
    } ~
      pathPrefix("openapi") {
        getFromDirectory("src/main/resources/openapi")
      }
  }

  private def security(): Route = {
    concat(
      (get & path("security" / "tokens") & securityRouter.onAuthenticated) { user =>
        asJson(manageToken.listTokens(user))
      },
      (post & path("security" / "tokens") & securityRouter.onAuthenticated) { user =>
        asJson(manageToken.generateToken(user))
      },
      (delete & path("security" / "tokens" / Segment) & securityRouter.onAuthenticated) { (tokenKey, user) =>
        asJson(manageToken.deleteToken(user, NormalizedUUID(tokenKey)))
      }
    )
  }

  private def theater(): Route = {
    concat(
      (get & path("theaters") & securityRouter.onAuthenticated) { user =>
        asJson(findTheaters.listForAuthenticatedUser(user))
      },
      (post & path("theaters") & entity(as[TheaterCreation]) & securityRouter.onAuthenticated) { (theaterCreation, user) =>
        asResult(manageTheater.createTheater(theaterCreation, user))
      },
      (put & path("theaters" / TheaterIdSegment) & entity(as[TheaterUpdate]) & securityRouter.onAuthenticated) { (theaterId, theaterUpdate, user) =>
        asResult(manageTheater.updateTheater(theaterId, theaterUpdate, user))
      },
      (delete & path("theaters" / TheaterIdSegment) & securityRouter.onAuthenticated) { (theaterId, user) =>
        asResult(manageTheater.removeTheater(theaterId, user))
      }
    )
  }

  private def session(): Route = {
    concat(
      (get & path("sessions") & securityRouter.onAuthenticated) { user =>
        asJson(findSessions.listForAuthenticatedUser(user))
      },
      (post & path("sessions") & entity(as[SessionCreation]) & securityRouter.onAuthenticated) { (sessionCreation, user) =>
        asResult(manageSession.createSession(sessionCreation, user))
      },
      (put & path("sessions" / SessionIdSegment) & entity(as[SessionUpdate]) & securityRouter.onAuthenticated) { (sessionId, sessionUpdate, user) =>
        asResult(manageSession.updateSession(sessionId, sessionUpdate, user))
      },
      (delete & path("sessions" / SessionIdSegment) & securityRouter.onAuthenticated) { (sessionId, user) =>
        asResult(manageSession.removeSession(sessionId, user))
      }
    )
  }

  private def asJson[Result](op: => Future[Result])(implicit writer: RootJsonWriter[Result]): Route = {
    onSuccess(op) { result =>
      complete {
        HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`, writer.write(result).compactPrint)
        )
      }
    }
  }

  private def asResult[Result](op: => Future[Result])(implicit toHttpResult: ToHttpResult[Result]): Route = {
    onSuccess(op) { result =>
      complete {
        toHttpResult.toHttpResult(result).toHttpResponse
      }
    }
  }
}
