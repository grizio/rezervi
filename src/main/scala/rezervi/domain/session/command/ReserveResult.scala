package rezervi.domain.session.command

import rezervi.model.session.reservation.Reservation
import rezervi.utils.Validation

sealed trait ReserveResult

object ReserveResult {
  case class Valid(reservation: Reservation) extends ReserveResult
  case class Invalid(errors: Validation.Errors) extends ReserveResult
  case object NotFound extends ReserveResult
  case object NotAuthorized extends ReserveResult
}
