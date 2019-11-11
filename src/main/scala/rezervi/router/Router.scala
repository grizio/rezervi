package rezervi.router

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import rezervi.domain.security.ManageToken
import rezervi.model.security.NormalizedUUID
import spray.json.JsonWriter

import scala.concurrent.Future

class Router(
  securityRouter: SecurityRouter,
  manageToken: ManageToken
) {
  import rezervi.router.json.Jsons._

  def buildRoutes(): Route = {
    openApi() ~ security() ~ theater()
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
        action(manageToken.listTokens(user))
      },
      (post & path("security" / "tokens") & securityRouter.onAuthenticated) { user =>
        action(manageToken.generateToken(user))
      },
      (delete & path("security" / "tokens" / Segment) & securityRouter.onAuthenticated) { (tokenKey, user) =>
        action(manageToken.deleteToken(user, NormalizedUUID(tokenKey)))
      }
    )
  }

  private def theater(): Route = {
    (get & path("theaters") & securityRouter.onAuthenticated) { user =>
      complete(user.uid.toString)
    }
  }

  private def action[Result](op: => Future[Result])(implicit writer: JsonWriter[Result]): Route = {
    onSuccess(op) { result =>
      complete {
        HttpResponse(
          status = StatusCodes.OK,
          entity = HttpEntity(ContentTypes.`application/json`, writer.write(result).compactPrint)
        )
      }
    }
  }
}
