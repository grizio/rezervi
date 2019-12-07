package rezervi.domain.theater.command

import rezervi.model.theater.TheaterView
import rezervi.utils.Validation

sealed trait ManageTheaterResult

object ManageTheaterResult {
  case class Valid(theater: TheaterView) extends ManageTheaterResult
  case class Invalid(errors: Validation.Errors) extends ManageTheaterResult
  case object NotFound extends ManageTheaterResult
  case object NotAuthorized extends ManageTheaterResult
  case object Done extends ManageTheaterResult
}


