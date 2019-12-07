package rezervi.model.theater.adapters

import rezervi.model.theater.{Theater, TheaterView}

object TheaterAdapter {
  def toTheaterView(theater: Theater): TheaterView = {
    TheaterView(
      id = theater.id,
      name = theater.name,
      address = theater.address,
      plan = theater.plan
    )
  }
}
