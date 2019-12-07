package rezervi.model.theater

import rezervi.model.security.UserId

case class Theater(
  id: TheaterId,
  uid: UserId,
  name: String,
  address: String,
  plan: Plan
)

