package rezervi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import org.flywaydb.core.Flyway
import scalikejdbc.ConnectionPool

import scala.concurrent.{ExecutionContext, Future}
import scala.io.StdIn

object Boot {
  def main(args: Array[String]): Unit = {
    val config = RezerviConfig.load()
    implicit val system: ActorSystem = ActorSystem(config.system.name)

    processDatabaseMigration(config)
    startDatabaseConnectionPool(config)
    val startedServer = startServer(config)
    waitServerTermination(config, startedServer)
  }

  private def processDatabaseMigration(config: RezerviConfig): Unit = {
    Flyway.configure()
      .dataSource(config.database.url, config.database.username, config.database.password)
      .load()
      .migrate()
  }

  private def startDatabaseConnectionPool(config: RezerviConfig): Unit = {
    ConnectionPool.singleton(config.database.url, config.database.username, config.database.password)
  }

  private def startServer(config: RezerviConfig)(implicit system: ActorSystem): Future[Http.ServerBinding] = {
    val applicationLoader = new ApplicationLoader(config)
    Http().bindAndHandle(applicationLoader.router.buildRoutes(), config.http.interface, config.http.port)
  }

  private def waitServerTermination(config: RezerviConfig, startedServer: Future[Http.ServerBinding])(implicit system: ActorSystem): Unit = {
    implicit val dispatcher: ExecutionContext = system.dispatcher

    println(s"Server online at http://${config.http.interface}:${config.http.port}")
    println("Press RETURN to stop...")
    StdIn.readLine()

    startedServer
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
