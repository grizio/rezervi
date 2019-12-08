package rezervi.persistence.postgres.schema

import java.time.Instant

import rezervi.model.session.{Session, SessionId}
import rezervi.model.theater.{Theater, TheaterId}
import scalikejdbc._

case class SessionTable(
  id: SessionId,
  date: Instant,
  theaterId: TheaterId
)

object SessionSchema extends SQLSyntaxSupport[SessionTable] {

  import rezervi.persistence.postgres.TypeBinders._

  override def tableName: String = "session"

  override def columns: collection.Seq[String] = Seq("id", "date", "theater_id")

  def apply(s: ResultName[SessionTable], t: ResultName[Theater])(rs: WrappedResultSet): Session = {
    Session(
      id = rs.get[SessionId](s.id),
      date = rs.get[Instant](s.date),
      theater = TheaterSchema.apply(t)(rs)
    )
  }
}
