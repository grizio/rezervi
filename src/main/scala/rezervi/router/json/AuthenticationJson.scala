package rezervi.router.json

import rezervi.model.security.AuthenticationTokenView
import spray.json.{DefaultJsonProtocol, JsonFormat}

trait AuthenticationJson extends DefaultJsonProtocol with UserJson {
  implicit val authenticationTokenViewWriter: JsonFormat[AuthenticationTokenView] = jsonFormat4(AuthenticationTokenView)
}
