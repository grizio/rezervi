package rezervi.model.session.reservation

case class Reservation(
  name: String,
  email: String,
  seats: Seq[ReservationSeat],
  comment: Option[String]
)
