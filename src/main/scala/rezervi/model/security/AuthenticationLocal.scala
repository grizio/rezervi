package rezervi.model.security

case class AuthenticationLocal(
  uid: UserId,
  username: String,
  encryptedPassword: String
)