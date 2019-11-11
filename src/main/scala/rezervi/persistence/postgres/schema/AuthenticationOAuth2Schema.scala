package rezervi.persistence.postgres.schema

import rezervi.model.security.{AuthenticationOAuth2, UserId}
import scalikejdbc._

object AuthenticationOAuth2Schema extends SQLSyntaxSupport[AuthenticationOAuth2] {
  import rezervi.persistence.postgres.TypeBinders._

  override def tableName: String = "authentication_oauth2"

  override def columns: collection.Seq[String] = Seq("uid", "iss", "sub")

  def apply(g: ResultName[AuthenticationOAuth2])(rs: WrappedResultSet): AuthenticationOAuth2 = {
    AuthenticationOAuth2(
      uid = rs.get[UserId](g.uid),
      iss = rs.get[String](g.iss),
      sub = rs.get[String](g.sub)
    )
  }
}
