package rezervi.persistence.postgres.schema

import rezervi.model.security.{AuthenticationLocal, UserId}
import scalikejdbc._

object AuthenticationLocalSchema extends SQLSyntaxSupport[AuthenticationLocal] {
  import rezervi.persistence.postgres.TypeBinders._

  override def tableName: String = "authentication_local"

  override def columns: collection.Seq[String] = Seq("uid", "username", "encrypted_password")

  def apply(g: ResultName[AuthenticationLocal])(rs: WrappedResultSet): AuthenticationLocal = {
    AuthenticationLocal(
      uid = rs.get[UserId](g.uid),
      username = rs.get[String](g.username),
      encryptedPassword = rs.get[String](g.encryptedPassword)
    )
  }
}
