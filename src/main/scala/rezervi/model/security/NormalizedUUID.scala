package rezervi.model.security

import java.util.UUID

case class NormalizedUUID(value: UUID) extends AnyVal {
  def raw: String = value.toString.replaceAll("-", "")
}

object NormalizedUUID {
  def apply(raw: String): NormalizedUUID = {
    if (raw.length == 32) {
      val rawUUID = Seq(
        raw.substring(0, 8),
        raw.substring(8, 12),
        raw.substring(12, 16),
        raw.substring(16, 20),
        raw.substring(20)
      ).mkString("-")
      NormalizedUUID(UUID.fromString(rawUUID))
    } else {
      throw new IllegalArgumentException("The value should have a length of 32")
    }
  }
}