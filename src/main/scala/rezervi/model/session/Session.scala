package rezervi.model.session

import java.time.Instant

import rezervi.model.theater.Theater

case class Session(
  id: SessionId,
  date: Instant,
  theater: Theater
)
