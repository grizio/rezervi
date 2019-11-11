package rezervi.router.json

import java.util.UUID

import spray.json.{JsString, JsValue, JsonFormat, deserializationError}

trait CommonJson {
  implicit val uuidFormat: JsonFormat[UUID] = new JsonFormat[UUID] {
    override def read(json: JsValue): UUID = json match {
      case JsString(value) =>
        try {
          UUID.fromString(value)
        } catch {
          case _: IllegalArgumentException => deserializationError("UUID expected")
        }
      case _ => deserializationError("UUID expected")
    }

    override def write(obj: UUID): JsValue = {
      JsString(obj.toString)
    }
  }
}
