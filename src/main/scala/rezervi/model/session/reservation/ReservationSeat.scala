package rezervi.model.session.reservation

import rezervi.model.session.PriceName

case class ReservationSeat(
  seat: String,
  price: PriceName
)