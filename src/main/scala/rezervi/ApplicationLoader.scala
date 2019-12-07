package rezervi

import akka.actor.ActorSystem
import rezervi.domain.security.{Authenticate, ManageToken}
import rezervi.domain.theater.command.ManageTheater
import rezervi.domain.theater.query.FindTheaters
import rezervi.persistence.postgres.{AuthenticationRepository, TheaterRepository}
import rezervi.router.{Router, SecurityRouter}
import rezervi.utils.Crypto

import scala.concurrent.ExecutionContext

class ApplicationLoader(config: RezerviConfig)(implicit system: ActorSystem) {
  lazy val databaseExecutionContext: ExecutionContext = system.dispatchers.lookup("db-context")
  lazy val serviceExecutionContext: ExecutionContext = system.dispatchers.lookup("service-context")
  lazy val cryptoExecutionContext: ExecutionContext = system.dispatchers.lookup("crypto-context")

  lazy val router = new Router(securityRouter, manageToken, findTheaters, manageTheater)
  lazy val securityRouter = new SecurityRouter(authenticate)

  lazy val authenticate = new Authenticate(crypto, authenticationRepository)(serviceExecutionContext)
  lazy val manageToken = new ManageToken(crypto, authenticationRepository)(serviceExecutionContext)
  lazy val findTheaters = new FindTheaters(theaterRepository)(serviceExecutionContext)
  lazy val manageTheater = new ManageTheater(theaterRepository)(serviceExecutionContext)

  lazy val authenticationRepository = new AuthenticationRepository()(databaseExecutionContext)
  lazy val theaterRepository = new TheaterRepository()(databaseExecutionContext)

  lazy val crypto = new Crypto(config)(cryptoExecutionContext)
}
