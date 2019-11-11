package rezervi.utils

import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.Base64

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.mindrot.jbcrypt.BCrypt
import rezervi.RezerviConfig

import scala.concurrent.{ExecutionContext, Future}

/**
 * Crypto utilities with a specific execution context to avoid blocking others (http, database)
 */
class Crypto(
  config: RezerviConfig
)(implicit executionContext: ExecutionContext) {
  private val secretKey = new SecretKeySpec(config.security.secret.getBytes(StandardCharsets.UTF_8), "AES")
  private val secureRandom = new SecureRandom()

  def bcrypt(plaintext: String): Future[String] = Future {
    BCrypt.hashpw(plaintext, BCrypt.gensalt())
  }

  def bcryptCheck(plaintext: String, hashed: String): Future[Boolean] = Future {
    BCrypt.checkpw(plaintext, hashed)
  }

  def aesEncode(plaintext: String): Future[String] = Future {
    val encipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    encipher.init(Cipher.ENCRYPT_MODE, secretKey)
    val result = encipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8))
    Base64.getEncoder.encodeToString(result)
  }

  def aesDecode(encoded: String): Future[String] = Future {
    val encipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    encipher.init(Cipher.DECRYPT_MODE, secretKey)
    val result = encipher.doFinal(Base64.getDecoder.decode(encoded))
    new String(result, StandardCharsets.UTF_8)
  }

  def aesCheck(plaintext: String, encoded: String): Future[Boolean] = {
    aesEncode(plaintext).map(_ == encoded)
  }

  def generateString(length: Int = 50): String = {
    val bufferBytes = new Array[Byte](length)
    secureRandom.nextBytes(bufferBytes)
    Base64.getEncoder.encodeToString(bufferBytes)
  }
}
