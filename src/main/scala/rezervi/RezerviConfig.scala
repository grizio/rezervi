package rezervi

import com.typesafe.config.{Config, ConfigFactory}

case class RezerviConfig(
  system: SystemConfig,
  http: HttpConfig,
  database: DatabaseConfig,
  security: SecurityConfig
)

case class SystemConfig(
  name: String
)

case class HttpConfig(
  interface: String,
  port: Int
)

case class DatabaseConfig(
  url: String,
  username: String,
  password: String
)

case class SecurityConfig(
  secret: String
)

object RezerviConfig {
  def load(): RezerviConfig = {
    val config = ConfigFactory.load()
    RezerviConfig(
      system = loadSystem(config.getConfig("rezervi.system")),
      http = loadHttp(config.getConfig("rezervi.http")),
      database = loadDatabase(config.getConfig("rezervi.database")),
      security = loadSecurity(config.getConfig("rezervi.security"))
    )
  }

  private def loadSystem(config: Config): SystemConfig = {
    SystemConfig(
      name = config.getString("name")
    )
  }

  private def loadHttp(config: Config): HttpConfig = {
    HttpConfig(
      interface = config.getString("interface"),
      port = config.getInt("port")
    )
  }

  private def loadDatabase(config: Config): DatabaseConfig = {
    DatabaseConfig(
      url = config.getString("url"),
      username = config.getString("username"),
      password = config.getString("password")
    )
  }

  private def loadSecurity(config: Config): SecurityConfig = {
    SecurityConfig(
      secret = config.getString("secret"),
    )
  }
}