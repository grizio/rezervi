package rezervi.model.theater

import rezervi.model.theater.plan.Plan

case class TheaterUpdate(
  name: String,
  address: String,
  plan: Plan
)
