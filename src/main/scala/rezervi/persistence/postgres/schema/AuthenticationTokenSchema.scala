package rezervi.persistence.postgres.schema

import java.util.UUID

import rezervi.model.security.{AuthenticationToken, UserId}
import scalikejdbc._

object AuthenticationTokenSchema extends SQLSyntaxSupport[AuthenticationToken] {
  import rezervi.persistence.postgres.TypeBinders._

  override def tableName: String = "authentication_token"

  override def columns: collection.Seq[String] = Seq("uid", "key", "encoded_secret")

  def apply(g: ResultName[AuthenticationToken])(rs: WrappedResultSet): AuthenticationToken = {
    AuthenticationToken(
      uid = rs.get[UserId](g.uid),
      key = rs.get[UUID](g.key),
      encodedSecret = rs.get[String](g.encodedSecret)
    )
  }
}
