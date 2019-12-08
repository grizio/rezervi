package rezervi.model.session.adapters

import rezervi.model.session.{Session, SessionView}
import rezervi.model.theater.adapters.TheaterAdapter

object SessionAdapter {
  def toSessionView(session: Session): SessionView = {
    SessionView(
      id = session.id,
      date = session.date,
      theater = TheaterAdapter.toTheaterView(session.theater)
    )
  }
}
