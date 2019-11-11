package rezervi.model.security

case class AuthenticationOAuth2(
  uid: UserId,
  iss: String,
  sub: String
)