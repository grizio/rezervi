package rezervi.domain.security

import java.util.UUID

import rezervi.model.security.{AuthenticationToken, AuthenticationTokenView, NormalizedUUID}
import rezervi.persistence.postgres.AuthenticationRepository
import rezervi.router.AuthenticatedUser
import rezervi.utils.Crypto

import scala.concurrent.{ExecutionContext, Future}

class ManageToken(
  crypto: Crypto,
  authenticationRepository: AuthenticationRepository
)(implicit ec: ExecutionContext) {
  def generateToken(user: AuthenticatedUser): Future[AuthenticationTokenView] = {
    val secret = crypto.generateString()
    for {
      encodedSecret <- crypto.aesEncode(secret)
      authenticationToken = AuthenticationToken(
        uid = user.uid,
        key = UUID.randomUUID(),
        encodedSecret = encodedSecret
      )
      _ <- authenticationRepository.persist(authenticationToken)
    } yield {
      toAuthenticationTokenView(authenticationToken, secret)
    }
  }

  def listTokens(user: AuthenticatedUser): Future[Seq[AuthenticationTokenView]] = {
    for {
      authenticationTokens <- authenticationRepository.findAuthenticationTokensByUid(user.uid)
      result <- Future.traverse(authenticationTokens) { authenticationToken =>
        crypto.aesDecode(authenticationToken.encodedSecret)
          .map(secret => toAuthenticationTokenView(authenticationToken, secret))
      }
    } yield {
      result
    }
  }

  def deleteToken(user: AuthenticatedUser, tokenKey: NormalizedUUID): Future[Unit] = {
    authenticationRepository.deleteAuthenticationToken(user.uid, tokenKey)
  }

  private def toAuthenticationTokenView(authenticationToken: AuthenticationToken, secret: String): AuthenticationTokenView = {
    AuthenticationTokenView(
      uid = authenticationToken.uid,
      key = NormalizedUUID(authenticationToken.key).raw,
      secret = secret,
      token = s"${NormalizedUUID(authenticationToken.key).raw}/${secret}"
    )
  }
}
