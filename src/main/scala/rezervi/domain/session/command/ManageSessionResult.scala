package rezervi.domain.session.command

import rezervi.model.session.SessionView
import rezervi.utils.Validation

sealed trait ManageSessionResult

object ManageSessionResult {
  case class Valid(session: SessionView) extends ManageSessionResult
  case class Invalid(errors: Validation.Errors) extends ManageSessionResult
  case object NotFound extends ManageSessionResult
  case object NotAuthorized extends ManageSessionResult
  case object Done extends ManageSessionResult
}


