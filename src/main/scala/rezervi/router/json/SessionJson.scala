package rezervi.router.json

import rezervi.model.session.{Price, PriceName, SessionCreation, SessionId, SessionUpdate, SessionView}
import rezervi.model.theater.TheaterView
import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonFormat, RootJsonReader, RootJsonWriter}

trait SessionJson extends DefaultJsonProtocol with CommonJson with TheaterJson {
  implicit val sessionIdFormat: JsonFormat[SessionId] = idFormat(SessionId.apply, _.value)
  private implicit val implicitTheaterViewFormat: RootJsonFormat[TheaterView] = jsonFormat4(TheaterView)
  implicit val implicitPriceNameFormat: JsonFormat[PriceName] = codeFormat(PriceName.apply, _.value)
  implicit val implicitPriceFormat: JsonFormat[Price] = jsonFormat4(Price)

  implicit val sessionViewWriter: RootJsonWriter[SessionView] = jsonFormat4(SessionView)

  implicit val sessionCreationReader: RootJsonReader[SessionCreation] = jsonFormat3(SessionCreation)
  implicit val sessionUpdateReader: RootJsonReader[SessionUpdate] = jsonFormat2(SessionUpdate)
}
