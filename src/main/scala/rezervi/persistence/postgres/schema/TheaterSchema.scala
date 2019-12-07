package rezervi.persistence.postgres.schema

import java.sql.ResultSet

import rezervi.model.security.UserId
import rezervi.model.theater.{Plan, Theater, TheaterId}
import scalikejdbc._
import spray.json._

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

  implicit val planTypeBinder: TypeBinder[Plan] = new TypeBinder[Plan] {
    override def apply(rs: ResultSet, columnIndex: Int): Plan = {
      parse(rs.getString(columnIndex))
    }

    override def apply(rs: ResultSet, columnLabel: String): Plan = {
      parse(rs.getString(columnLabel))
    }

    private def parse(value: String): Plan = {
      value.parseJson match {
        case jsObject: JsObject => Plan(jsObject)
        case _ => throw new RuntimeException("Invalid JsObject when parsing theater.plan")
      }
    }
  }
}
