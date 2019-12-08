package rezervi.router.json

import rezervi.model.session.{SessionCreation, SessionId, SessionUpdate, SessionView}
import rezervi.model.theater.TheaterView
import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonFormat, RootJsonReader, RootJsonWriter}

trait SessionJson extends DefaultJsonProtocol with CommonJson with TheaterJson {
  implicit val sessionIdFormat: JsonFormat[SessionId] = idFormat(SessionId.apply, _.value)
  private implicit val implicitTheaterViewFormat: RootJsonFormat[TheaterView] = jsonFormat4(TheaterView)
  implicit val sessionViewWriter: RootJsonWriter[SessionView] = jsonFormat3(SessionView)

  implicit val sessionCreationReader: RootJsonReader[SessionCreation] = jsonFormat2(SessionCreation)
  implicit val sessionUpdateReader: RootJsonReader[SessionUpdate] = jsonFormat1(SessionUpdate)
}
