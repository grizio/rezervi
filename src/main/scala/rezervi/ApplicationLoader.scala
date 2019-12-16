package rezervi

import akka.actor.ActorSystem
import rezervi.domain.security.{Authenticate, ManageToken}
import rezervi.domain.session.command.{ManageSession, Reserve}
import rezervi.domain.session.query.FindSessions
import rezervi.domain.theater.command.ManageTheater
import rezervi.domain.theater.query.FindTheaters
import rezervi.persistence.postgres.{AuthenticationRepository, SessionRepository, TheaterRepository}
import rezervi.router.{Router, SecurityRouter}
import rezervi.utils.Crypto

import scala.concurrent.ExecutionContext

class ApplicationLoader(config: RezerviConfig)(implicit system: ActorSystem) {
  lazy val databaseExecutionContext: ExecutionContext = system.dispatchers.lookup("db-context")
  lazy val serviceExecutionContext: ExecutionContext = system.dispatchers.lookup("service-context")
  lazy val cryptoExecutionContext: ExecutionContext = system.dispatchers.lookup("crypto-context")

  lazy val router = new Router(securityRouter, manageToken, findTheaters, manageTheater, findSessions, manageSession, reserve)
  lazy val securityRouter = new SecurityRouter(authenticate)

  lazy val authenticate = new Authenticate(crypto, authenticationRepository)(serviceExecutionContext)
  lazy val manageToken = new ManageToken(crypto, authenticationRepository)(serviceExecutionContext)
  lazy val findTheaters = new FindTheaters(theaterRepository)(serviceExecutionContext)
  lazy val manageTheater = new ManageTheater(theaterRepository)(serviceExecutionContext)
  lazy val findSessions = new FindSessions(sessionRepository)(serviceExecutionContext)
  lazy val manageSession = new ManageSession(theaterRepository, sessionRepository)(serviceExecutionContext)
  lazy val reserve = new Reserve(sessionRepository)(serviceExecutionContext)

  lazy val authenticationRepository = new AuthenticationRepository()(databaseExecutionContext)
  lazy val theaterRepository = new TheaterRepository()(databaseExecutionContext)
  lazy val sessionRepository = new SessionRepository()(databaseExecutionContext)

  lazy val crypto = new Crypto(config)(cryptoExecutionContext)
}
