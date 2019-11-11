package rezervi.persistence.postgres

import rezervi.model.security._
import rezervi.persistence.postgres.schema.{AuthenticationLocalSchema, AuthenticationOAuth2Schema, AuthenticationTokenSchema}
import scalikejdbc._

import scala.concurrent.{ExecutionContext, Future}


class AuthenticationRepository()(implicit ec: ExecutionContext) {
  def findAuthenticationLocal(username: String): Future[Option[AuthenticationLocal]] = Future {
    DB.readOnly { implicit session =>
      val auth = AuthenticationLocalSchema.syntax("auth")
      sql"""select ${auth.result.*}
            from ${AuthenticationLocalSchema.as(auth)}
            where ${auth.username} = ${username}"""
        .map(AuthenticationLocalSchema(auth.resultName))
        .headOption()
        .apply()
    }
  }

  def findAuthenticationOAuth2(iss: String, sub: String): Future[Option[AuthenticationOAuth2]] = Future {
    DB.readOnly { implicit session =>
      val auth = AuthenticationOAuth2Schema.syntax("auth")
      sql"""select ${auth.result.*}
            from ${AuthenticationOAuth2Schema.as(auth)}
            where ${auth.iss} = ${iss} and ${auth.sub} = ${sub}"""
        .map(AuthenticationOAuth2Schema(auth.resultName))
        .headOption()
        .apply()
    }
  }

  def findAuthenticationToken(apiKey: ApiKey): Future[Option[AuthenticationToken]] = Future {
    DB.readOnly { implicit session =>
      val auth = AuthenticationTokenSchema.syntax("auth")
      sql"""select ${auth.result.*}
            from ${AuthenticationTokenSchema.as(auth)}
            where ${auth.key} = ${apiKey.key.value}"""
        .map(AuthenticationTokenSchema(auth.resultName))
        .headOption()
        .apply()
    }
  }

  def findAuthenticationTokensByUid(uid: UserId): Future[Seq[AuthenticationToken]] = Future {
    DB.readOnly { implicit session =>
      val auth = AuthenticationTokenSchema.syntax("auth")
      sql"""select ${auth.result.*}
            from ${AuthenticationTokenSchema.as(auth)}
            where ${auth.uid} = ${uid.value}"""
        .map(AuthenticationTokenSchema(auth.resultName))
        .list()
        .apply()
    }
  }

  def persist(authenticationToken: AuthenticationToken): Future[Unit] = Future {
    DB.localTx { implicit session =>
      val c = AuthenticationTokenSchema.column
      sql"""insert into ${AuthenticationTokenSchema.table} (${c.uid}, ${c.key}, ${c.encodedSecret})
            values (${authenticationToken.uid.value}, ${authenticationToken.key}, ${authenticationToken.encodedSecret})"""
        .update()
        .apply()
    }
  }

  def deleteAuthenticationToken(uid: UserId, key: NormalizedUUID): Future[Unit] = Future {
    DB.localTx { implicit session =>
      val auth = AuthenticationTokenSchema.syntax("auth")
      sql"""delete from ${AuthenticationTokenSchema.as(auth)}
            where ${auth.uid} = ${uid.value} and ${auth.key} = ${key.value}"""
        .update()
        .apply()
    }
  }
}
