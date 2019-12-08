package rezervi.domain.session.command

import java.time.Instant

import rezervi.model.session.Session
import rezervi.utils.Validation

import scala.concurrent.Future

object SessionValidation {
  def validate(session: Session): Future[Either[ManageSessionResult.Invalid, Session]] = {
    Future.successful {
      Validation.all(
        validateDate(session)
      )
        .toEither(ManageSessionResult.Invalid, session)
    }
  }

  private def validateDate(session: Session): Validation = {
    if (session.date.isBefore(Instant.now)) {
      Validation.ko("date", "The date is required")
    } else {
      Validation.ok
    }
  }
}
