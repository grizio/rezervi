package rezervi.model.security

case class ApiKey(key: NormalizedUUID, secret: String)

object ApiKey {
  def fromRaw(raw: String): ApiKey = {
    val parts = raw.trim.split("/")
    if (parts.length == 2) {
      try {
        ApiKey(
          key = NormalizedUUID(parts(0)),
          secret = parts(1)
        )
      } catch {
        case _: IllegalArgumentException => throw new RuntimeException("Invalid ApiKey format")
      }
    } else {
      throw new RuntimeException("Invalid ApiKey format")
    }
  }
}