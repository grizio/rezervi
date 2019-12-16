package rezervi.domain.session.command

import java.time.Instant

import rezervi.model.session.Session
import rezervi.utils.Validation

import scala.concurrent.Future

object SessionValidation {
  def validate(session: Session): Future[Either[ManageSessionResult.Invalid, Session]] = {
    Future.successful {
      Validation.all(
        validateDate(session),
        pricesIsNotEmpty(session),
        noUnknownSeatType(session),
        allSeatTypesHavePrice(session)
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

  private def pricesIsNotEmpty(session: Session): Validation = {
    if (session.prices.isEmpty) {
      Validation.ko("prices", "Please indicate at least one price")
    } else {
      Validation.ok
    }
  }

  private def noUnknownSeatType(session: Session): Validation = {
    val availableSeatTypes = session.theater.plan.seatTypes
    Validation.all(
      session.prices.zipWithIndex.map { case (price, index) =>
        val invalidSeatTypes = price.acceptedSeatTypes.filterNot(seatType => availableSeatTypes.contains(seatType))
        if (invalidSeatTypes.nonEmpty) {
          Validation.ko(s"prices.${index}.acceptedSeatTypes", s"The seat types ${invalidSeatTypes.map(_.value).mkString(", ")} are unknown")
        } else {
          Validation.ok
        }
      }
    )
  }

  private def allSeatTypesHavePrice(session: Session): Validation = {
    Validation.all(
      session.theater.plan.seatTypes.map { seatType =>
        if (session.prices.exists(_.acceptedSeatTypes.contains(seatType))) {
          Validation.ok
        } else {
          Validation.ko("prices", s"There is no price for seat type ${seatType.value}")
        }
      }
    )
  }
}
