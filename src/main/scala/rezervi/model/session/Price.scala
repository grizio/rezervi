package rezervi.model.session

import rezervi.model.theater.plan.SeatType

case class Price(
  name: PriceName,
  acceptedSeatTypes: Seq[SeatType],
  price: BigDecimal,
  comment: Option[String]
)