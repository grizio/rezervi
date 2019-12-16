package rezervi.model.theater

import rezervi.model.theater.plan.Plan

case class TheaterCreation(
  name: String,
  address: String,
  plan: Plan
)
