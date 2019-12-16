package rezervi.model.theater

import rezervi.model.theater.plan.Plan

case class TheaterView(
  id: TheaterId,
  name: String,
  address: String,
  plan: Plan
)
