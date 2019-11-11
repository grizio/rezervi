package rezervi.router.json

import rezervi.model.security.UserId
import spray.json.{JsValue, JsonFormat}

trait UserJson extends CommonJson {
  implicit val userIdWriter: JsonFormat[UserId] = new JsonFormat[UserId] {
    override def read(json: JsValue): UserId = UserId(uuidFormat.read(json))

    override def write(obj: UserId): JsValue = uuidFormat.write(obj.value)
  }
}
