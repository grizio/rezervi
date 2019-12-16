package rezervi.router.json

import java.time.Instant
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.util.UUID

import spray.json.{JsArray, JsObject, JsString, JsValue, JsonFormat, JsonReader, JsonWriter, RootJsonReader, RootJsonWriter, deserializationError}

import scala.xml.{Elem, SAXException, XML}

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

  implicit val dateFormat: JsonFormat[Instant] = new JsonFormat[Instant] {
    override def read(json: JsValue): Instant = json match {
      case JsString(value) =>
        try {
          Instant.parse(value)
        } catch {
          case _: DateTimeParseException => deserializationError("Date expected")
        }
      case _ => deserializationError("Date expected")
    }

    override def write(obj: Instant): JsValue = {
      JsString(DateTimeFormatter.ISO_INSTANT.format(obj))
    }
  }

  implicit val xmlFormat: JsonFormat[Elem] = new JsonFormat[Elem] {
    override def read(json: JsValue): Elem = json match {
      case JsString(value) =>
        try {
          XML.loadString(value)
        } catch {
          case _: SAXException => deserializationError("XML expected")
        }
      case _ => deserializationError("XML expected")
    }

    override def write(obj: Elem): JsValue = {
      JsString(obj.toString())
    }
  }

  implicit def seqReader[A](implicit reader: JsonReader[A]): RootJsonReader[Seq[A]] = {
    case JsArray(values) => values.map(reader.read)
    case _ => deserializationError("Array expected")
  }

  implicit def seqWriter[A](implicit writer: JsonWriter[A]): RootJsonWriter[Seq[A]] = (seq: Seq[A]) => {
    JsArray(seq.map(writer.write).toVector)
  }

  def idFormat[A](apply: UUID => A, value: A => UUID): JsonFormat[A] = new JsonFormat[A] {
    override def read(json: JsValue): A = apply(uuidFormat.read(json))

    override def write(obj: A): JsValue = uuidFormat.write(value(obj))
  }

  def codeFormat[A](apply: String => A, value: A => String): JsonFormat[A] = new JsonFormat[A] {
    override def read(json: JsValue): A = json match {
      case JsString(value) => apply(value)
      case _ => deserializationError("Code expected")
    }

    override def write(obj: A): JsValue = JsString(value(obj))
  }
}
