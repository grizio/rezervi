package rezervi.router.json

import rezervi.model.theater._
import rezervi.model.theater.plan.{Plan, SeatType}
import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonReader, RootJsonWriter}

trait TheaterJson extends DefaultJsonProtocol with CommonJson {
  implicit val theaterIdFormat: JsonFormat[TheaterId] = idFormat(TheaterId.apply, _.value)
  implicit val seatTypeFormat: JsonFormat[SeatType] = codeFormat(SeatType.apply, _.value)
  implicit val planFormat: JsonFormat[Plan] = jsonFormat(Plan, "seatTypes", "schema")

  implicit val theaterViewWriter: RootJsonWriter[TheaterView] = jsonFormat4(TheaterView)

  implicit val theaterCreationReader: RootJsonReader[TheaterCreation] = jsonFormat3(TheaterCreation)
  implicit val theaterUpdateReader: RootJsonReader[TheaterUpdate] = jsonFormat3(TheaterUpdate)
}
