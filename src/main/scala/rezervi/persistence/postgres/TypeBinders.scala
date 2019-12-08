package rezervi.persistence.postgres

import java.sql.ResultSet
import java.util.UUID

import rezervi.model.security.UserId
import rezervi.model.session.SessionId
import rezervi.model.theater.TheaterId
import scalikejdbc.TypeBinder

object TypeBinders {
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
}
