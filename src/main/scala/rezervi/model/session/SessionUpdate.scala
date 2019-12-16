package rezervi.model.session

import java.time.Instant

case class SessionUpdate(
  date: Instant,
  prices: Seq[Price]
)
