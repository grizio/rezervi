package rezervi.model.session

import java.time.Instant

import rezervi.model.theater.TheaterView

case class SessionView(
  id: SessionId,
  date: Instant,
  theater: TheaterView,
  prices: Seq[Price]
)
