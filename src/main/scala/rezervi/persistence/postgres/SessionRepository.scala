package rezervi.persistence.postgres

import rezervi.model.security.UserId
import rezervi.model.session.reservation.Reservation
import rezervi.model.session.{Price, Session, SessionId}
import rezervi.persistence.postgres.schema.{SessionSchema, TheaterSchema}
import rezervi.router.json.Jsons
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

class SessionRepository()(implicit ec: ExecutionContext) {
  import Jsons._
  import TypeBinders._

  def find(id: SessionId): Future[Option[Session]] = Future {
    DB.readOnly { implicit ss =>
      val s = SessionSchema.syntax("s")
      val t = TheaterSchema.syntax("t")
      sql"""select ${s.result.*}, ${t.result.*}
            from ${SessionSchema.as(s)}
            left join ${TheaterSchema.as(t)} on ${s.theaterId} = ${t.id}
            where ${s.id} = ${id.value}"""
        .map(SessionSchema(s.resultName, t.resultName))
        .headOption()
        .apply()
    }
  }

  def findByUser(uid: UserId): Future[Seq[Session]] = Future {
    DB.readOnly { implicit ss =>
      val s = SessionSchema.syntax("s")
      val t = TheaterSchema.syntax("t")
      sql"""select ${s.result.*}, ${t.result.*}
            from ${SessionSchema.as(s)}
            left join ${TheaterSchema.as(t)} on ${s.theaterId} = ${t.id}
            where ${t.uid} = ${uid.value}"""
        .map(SessionSchema(s.resultName, t.resultName))
        .list()
        .apply()
    }
  }

  def insert(session: Session): Future[Unit] = Future {
    DB.localTx { implicit ss =>
      val s = SessionSchema.column
      sql"""insert into ${SessionSchema.table} (${s.id}, ${s.date}, ${s.theaterId}, ${s.prices}, ${s.reservations})
            values (${session.id.value}, ${session.date}, ${session.theater.id.value}, ${jsonParam(session.prices)}::jsonb, ${jsonParam(session.reservations)}::jsonb)"""
        .update()
        .apply()
    }
  }

  def update(session: Session): Future[Unit] = Future {
    DB.localTx { implicit ss =>
      val s = SessionSchema.column
      sql"""update ${SessionSchema.table}
            set ${s.date} = ${session.date},
                ${s.theaterId} = ${session.theater.id.value},
                ${s.prices} = ${jsonParam(session.prices)}::jsonb,
                ${s.reservations} = ${jsonParam(session.reservations)}::jsonb
            where ${s.id} = ${session.id.value}"""
        .update()
        .apply()
    }
  }

  def delete(sessionId: SessionId): Future[Unit] = Future {
    DB.localTx { implicit ss =>
      val s = SessionSchema.column
      sql"""delete from ${SessionSchema.table}
            where ${s.id} = ${sessionId.value}"""
        .update()
        .apply()
    }
  }
}
