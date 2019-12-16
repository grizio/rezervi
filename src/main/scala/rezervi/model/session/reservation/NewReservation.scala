package rezervi.model.session.reservation

case class NewReservation(
  name: String,
  email: String,
  seats: Seq[ReservationSeat],
  comment: Option[String]
)