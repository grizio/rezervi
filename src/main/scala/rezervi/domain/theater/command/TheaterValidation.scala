package rezervi.domain.theater.command

import rezervi.model.theater.Theater
import rezervi.model.theater.plan.Plan
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
    Validation.all(
      validateSeatTypesAreUnique(theater.plan),
      validateSeatNamesAreUnique(theater.plan),
      validateSeatsHaveValidSeatTypes(theater.plan)
    )
  }

  private def validateSeatTypesAreUnique(plan: Plan): Validation = {
    val duplicatedSeatTypes = plan.seatTypes.filter(seatType => plan.seatTypes.count(_ == seatType) > 1).distinct
    if (duplicatedSeatTypes.nonEmpty) {
      Validation.ko("plan.seatTypes", s"Duplicated seat types: ${duplicatedSeatTypes.map(_.value).mkString(", ")}")
    } else {
      Validation.ok
    }
  }

  private def validateSeatNamesAreUnique(plan: Plan): Validation = {
    val seatNames = plan.seats.map(_.name)
    val duplicatedSeatNames = seatNames.filter(seatName => seatNames.count(_ == seatName) > 1).distinct
    if (duplicatedSeatNames.nonEmpty) {
      Validation.ko("plan.schema", s"Duplicated seat names: ${duplicatedSeatNames.mkString(", ")}")
    } else {
      Validation.ok
    }
  }

  private def validateSeatsHaveValidSeatTypes(plan: Plan): Validation = {
    val invalidSeats = plan.seats.filterNot(seat => plan.seatTypes.contains(seat.seatType))
    if (invalidSeats.nonEmpty) {
      Validation.ko("plan.schema", s"Seats ${invalidSeats.map(_.name).mkString(", ")} have an invalid seat type")
    } else {
      Validation.ok
    }
  }
}
