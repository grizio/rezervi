package rezervi.router.json

import java.util.UUID

import spray.json.{JsArray, JsObject, JsString, JsValue, JsonFormat, JsonWriter, RootJsonWriter, deserializationError}

trait CommonJson {
  implicit val unitWriter: RootJsonWriter[Unit] = _ => JsObject()
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

  implicit def seqWriter[A](implicit writer: JsonWriter[A]): RootJsonWriter[Seq[A]] = (seq: Seq[A]) => {
    JsArray(seq.map(writer.write).toVector)
  }

  def idFormat[A](apply: UUID => A, value: A => UUID): JsonFormat[A] = new JsonFormat[A] {
    override def read(json: JsValue): A = apply(uuidFormat.read(json))

    override def write(obj: A): JsValue = uuidFormat.write(value(obj))
  }
}
