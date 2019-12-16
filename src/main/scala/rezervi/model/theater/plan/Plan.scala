package rezervi.model.theater.plan

case class Plan(
  seatTypes: Seq[SeatType],
  schema: scala.xml.Elem
) {
  lazy val seats: Seq[Seat] = {
    (schema \\ "_")
      .iterator
      .flatMap[Seat] { node =>
        (node.attribute("data-seat"), node.attribute("data-type")) match {
          case (Some(seat), Some(seatType)) => Some(Seat(seat.text, SeatType(seatType.text)))
          case _ => None
        }
      }
      .toSeq
  }
}