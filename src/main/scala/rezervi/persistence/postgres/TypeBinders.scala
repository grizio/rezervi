package rezervi.persistence.postgres

import java.sql.ResultSet
import java.util.UUID

import rezervi.model.security.UserId
import rezervi.model.session.reservation.Reservation
import rezervi.model.session.{Price, SessionId}
import rezervi.model.theater.TheaterId
import rezervi.model.theater.plan.Plan
import scalikejdbc.TypeBinder
import spray.json._

object TypeBinders {
  import rezervi.router.json.Jsons._

  implicit val uuidTypeBinder: TypeBinder[UUID] = new TypeBinder[UUID] {
    override def apply(rs: ResultSet, columnIndex: Int): UUID = UUID.fromString(rs.getString(columnIndex))

    override def apply(rs: ResultSet, columnLabel: String): UUID = UUID.fromString(rs.getString(columnLabel))
  }

  implicit val userIdTypeBinder: TypeBinder[UserId] = idTypeBinder(UserId.apply)

  implicit val theaterIdTypeBinder: TypeBinder[TheaterId] = idTypeBinder(TheaterId.apply)

  implicit val sessionIdTypeBinder: TypeBinder[SessionId] = idTypeBinder(SessionId.apply)

  private def idTypeBinder[Id](idApply: UUID => Id): TypeBinder[Id] = new TypeBinder[Id] {
    override def apply(rs: ResultSet, columnIndex: Int): Id = {
      idApply(UUID.fromString(rs.getString(columnIndex)))
    }

    override def apply(rs: ResultSet, columnLabel: String): Id = {
      idApply(UUID.fromString(rs.getString(columnLabel)))
    }
  }

  implicit val pricesTypeBinder: TypeBinder[Seq[Price]] = jsonArrayTypeBinder[Price]
  implicit val reservationsTypeBinder: TypeBinder[Seq[Reservation]] = jsonArrayTypeBinder[Reservation]
  implicit val planTypeBinder: TypeBinder[Plan] = jsonObjectTypeBinder[Plan]

  private def jsonArrayTypeBinder[A: JsonReader]: TypeBinder[Seq[A]] = new TypeBinder[Seq[A]] {
    override def apply(rs: ResultSet, columnIndex: Int): Seq[A] = parse(rs.getString(columnIndex))

    override def apply(rs: ResultSet, columnLabel: String): Seq[A] = parse(rs.getString(columnLabel))

    private def parse(raw: String): Seq[A] = raw.parseJson.convertTo[Seq[A]]
  }

  private def jsonObjectTypeBinder[A: JsonReader]: TypeBinder[A] = new TypeBinder[A] {
    override def apply(rs: ResultSet, columnIndex: Int): A = parse(rs.getString(columnIndex))

    override def apply(rs: ResultSet, columnLabel: String): A = parse(rs.getString(columnLabel))

    private def parse(raw: String): A = raw.parseJson.convertTo[A]
  }

  def jsonParam[A: JsonWriter](value: A): String = {
    implicitly[JsonWriter[A]].write(value).compactPrint
  }
}
