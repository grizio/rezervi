package rezervi.model.security

case class AuthenticationTokenView(
  uid: UserId,
  key: String,
  secret: String,
  token: String
)
