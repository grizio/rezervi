package rezervi.persistence.postgres

import rezervi.model.security.UserId
import rezervi.model.theater.{Theater, TheaterId}
import rezervi.persistence.postgres.schema.TheaterSchema
import rezervi.router.json.Jsons
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}

class TheaterRepository()(implicit ec: ExecutionContext) {
  import Jsons._
  import TypeBinders._

  def findByUser(uid: UserId): Future[Seq[Theater]] = Future {
    DB.readOnly { implicit session =>
      val t = TheaterSchema.syntax("t")
      sql"""select ${t.result.*}
            from ${TheaterSchema.as(t)}
            where ${t.uid} = ${uid.value}"""
        .map(TheaterSchema(t.resultName))
        .list()
        .apply()
    }
  }

  def find(id: TheaterId): Future[Option[Theater]] = Future {
    DB.readOnly { implicit session =>
      val t = TheaterSchema.syntax("t")
      sql"""select ${t.result.*}
            from ${TheaterSchema.as(t)}
            where ${t.id} = ${id.value}"""
        .map(TheaterSchema(t.resultName))
        .headOption()
        .apply()
    }
  }

  def insert(theater: Theater): Future[Unit] = Future {
    DB.localTx { implicit session =>
      val t = TheaterSchema.column
      sql"""insert into ${TheaterSchema.table} (${t.id}, ${t.uid}, ${t.name}, ${t.address}, ${t.plan})
            values (${theater.id.value}, ${theater.uid.value}, ${theater.name}, ${theater.address}, ${jsonParam(theater.plan)}::jsonb)"""
        .update()
        .apply()
    }
  }

  def update(theater: Theater): Future[Unit] = Future {
    DB.localTx { implicit session =>
      val t = TheaterSchema.column
      sql"""update ${TheaterSchema.table}
            set ${t.uid} = ${theater.uid.value},
                ${t.name} = ${theater.name},
                ${t.address} = ${theater.address},
                ${t.plan} = ${jsonParam(theater.plan)}::jsonb
            where ${t.id} = ${theater.id.value}"""
        .update()
        .apply()
    }
  }

  def delete(theaterId: TheaterId): Future[Unit] = Future {
    DB.localTx { implicit session =>
      val t = TheaterSchema.column
      sql"""delete from ${TheaterSchema.table}
            where ${t.id} = ${theaterId.value}"""
        .update()
        .apply()
    }
  }
}
