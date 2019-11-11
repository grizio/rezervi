package rezervi.persistence.postgres

import java.sql.ResultSet
import java.util.UUID

import rezervi.model.security.UserId
import scalikejdbc.TypeBinder

object TypeBinders {
  implicit val uuidTypeBinder: TypeBinder[UUID] = new TypeBinder[UUID] {
    override def apply(rs: ResultSet, columnIndex: Int): UUID = UUID.fromString(rs.getString(columnIndex))

    override def apply(rs: ResultSet, columnLabel: String): UUID = UUID.fromString(rs.getString(columnLabel))
  }

  implicit val userIdTypeBinder: TypeBinder[UserId] = new TypeBinder[UserId] {
    override def apply(rs: ResultSet, columnIndex: Int): UserId = {
      UserId(UUID.fromString(rs.getString(columnIndex)))
    }

    override def apply(rs: ResultSet, columnLabel: String): UserId = {
      UserId(UUID.fromString(rs.getString(columnLabel)))
    }
  }
}
