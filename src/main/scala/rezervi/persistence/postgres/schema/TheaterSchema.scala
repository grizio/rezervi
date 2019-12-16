package rezervi.persistence.postgres.schema

import rezervi.model.security.UserId
import rezervi.model.theater.plan.Plan
import rezervi.model.theater.{Theater, TheaterId}
import scalikejdbc._

object TheaterSchema extends SQLSyntaxSupport[Theater] {

  import rezervi.persistence.postgres.TypeBinders._

  override def tableName: String = "theater"

  override def columns: collection.Seq[String] = Seq("id", "uid", "name", "address", "plan")

  def apply(t: ResultName[Theater])(rs: WrappedResultSet): Theater = {
    Theater(
      id = rs.get[TheaterId](t.id),
      uid = rs.get[UserId](t.uid),
      name = rs.get[String](t.name),
      address = rs.get[String](t.address),
      plan = rs.get[Plan](t.plan)
    )
  }
}
