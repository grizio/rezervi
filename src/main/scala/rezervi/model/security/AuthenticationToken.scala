package rezervi.model.security

import java.util.UUID

case class AuthenticationToken(
  uid: UserId,
  key: UUID,
  encodedSecret: String
)