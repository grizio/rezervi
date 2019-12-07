package rezervi.router.json

import rezervi.model.security.UserId
import spray.json.JsonFormat

trait UserJson extends CommonJson {
  implicit val userIdWriter: JsonFormat[UserId] = idFormat(UserId.apply, _.value)
}
