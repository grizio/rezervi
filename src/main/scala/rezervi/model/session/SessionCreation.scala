package rezervi.model.session

import java.time.Instant

import rezervi.model.theater.TheaterId

case class SessionCreation(
  date: Instant,
  theaterId: TheaterId,
  prices: Seq[Price]
)
