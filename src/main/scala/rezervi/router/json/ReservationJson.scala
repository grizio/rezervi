package rezervi.router.json

import rezervi.model.session.PriceName
import rezervi.model.session.reservation.{NewReservation, Reservation, ReservationSeat}
import spray.json.{DefaultJsonProtocol, JsonFormat, RootJsonFormat, RootJsonReader, RootJsonWriter}

trait ReservationJson extends DefaultJsonProtocol with CommonJson {
  implicit val priceNameFormat: JsonFormat[PriceName] = codeFormat(PriceName.apply, _.value)
  implicit val reservationSeatFormat: JsonFormat[ReservationSeat] = jsonFormat2(ReservationSeat)

  implicit val reservationFormat: RootJsonFormat[Reservation] = jsonFormat4(Reservation)

  implicit val newReservationReader: RootJsonReader[NewReservation] = jsonFormat4(NewReservation)
}
