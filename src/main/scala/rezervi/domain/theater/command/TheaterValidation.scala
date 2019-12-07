package rezervi.domain.theater.command

import rezervi.model.theater.Theater
import rezervi.utils.Validation

import scala.concurrent.Future

object TheaterValidation {
  def validate(theater: Theater): Future[Either[ManageTheaterResult.Invalid, Theater]] = {
    Future.successful {
      Validation.all(
        validateName(theater),
        validateAddress(theater),
        validatePlan(theater)
      )
        .toEither(ManageTheaterResult.Invalid, theater)
    }
  }

  private def validateName(theater: Theater): Validation = {
    if (theater.name.isEmpty) {
      Validation.ko("name", "Required name")
    } else if (theater.name.length > 255) {
      Validation.ko("name", "Name too long")
    } else {
      Validation.ok
    }
  }

  private def validateAddress(theater: Theater): Validation = {
    if (theater.address.isEmpty) {
      Validation.ko("address", "Required address")
    } else if (theater.address.length > 255) {
      Validation.ko("address", "Address too long")
    } else {
      Validation.ok
    }
  }

  private def validatePlan(theater: Theater): Validation = {
    Validation.ok
  }
}
