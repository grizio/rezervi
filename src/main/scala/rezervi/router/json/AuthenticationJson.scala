package rezervi.router.json

import rezervi.model.security.AuthenticationTokenView
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait AuthenticationJson extends DefaultJsonProtocol with UserJson {
  implicit val authenticationTokenViewWriter: RootJsonFormat[AuthenticationTokenView] = jsonFormat4(AuthenticationTokenView)
}
