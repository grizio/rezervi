package rezervi

import akka.actor.ActorSystem
import rezervi.domain.security.{Authenticate, ManageToken}
import rezervi.persistence.postgres.AuthenticationRepository
import rezervi.router.{Router, SecurityRouter}
import rezervi.utils.Crypto

import scala.concurrent.ExecutionContext

class ApplicationLoader(config: RezerviConfig)(implicit system: ActorSystem) {
  lazy val databaseExecutionContext: ExecutionContext = system.dispatchers.lookup("db-context")
  lazy val serviceExecutionContext: ExecutionContext = system.dispatchers.lookup("service-context")
  lazy val cryptoExecutionContext: ExecutionContext = system.dispatchers.lookup("crypto-context")

  lazy val router = new Router(securityRouter, manageToken)
  lazy val securityRouter = new SecurityRouter(authenticate)

  lazy val authenticate = new Authenticate(crypto, authenticationRepository)(serviceExecutionContext)
  lazy val manageToken = new ManageToken(crypto, authenticationRepository)(serviceExecutionContext)

  lazy val authenticationRepository = new AuthenticationRepository()(databaseExecutionContext)

  lazy val crypto = new Crypto(config)(cryptoExecutionContext)
}
