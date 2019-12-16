package rezervi.model.theater

import rezervi.model.security.UserId
import rezervi.model.theater.plan.Plan

case class Theater(
  id: TheaterId,
  uid: UserId,
  name: String,
  address: String,
  plan: Plan
)

