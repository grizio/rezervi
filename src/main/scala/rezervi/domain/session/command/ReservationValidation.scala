package rezervi.domain.session.command

import java.time.Instant

import rezervi.model.session.Session
import rezervi.model.session.reservation.Reservation
import rezervi.utils.Validation

import scala.concurrent.Future

object ReservationValidation {
  def validateReservation(reservation: Reservation, session: Session): Future[Either[ReserveResult.Invalid, Reservation]] = {
    Future.successful {
      Validation.all(
        sessionIsNotClosed(session),
        hasAtLeastOneSeat(reservation),
        allSeatsExist(reservation, session),
        noSeatIsTaken(reservation, session),
        allPricesAreValid(reservation, session)
      )
        .toEither(ReserveResult.Invalid, reservation)
    }
  }

  private def sessionIsNotClosed(session: Session): Validation = {
    if (session.date.isBefore(Instant.now)) {
      Validation.ko("session", "The session is already closed")
    } else {
      Validation.ok
    }
  }

  private def hasAtLeastOneSeat(reservation: Reservation): Validation = {
    if (reservation.seats.nonEmpty) {
      Validation.ok
    } else {
      Validation.ko("seats", "Please choose at least one seat")
    }
  }

  private def allSeatsExist(reservation: Reservation, session: Session): Validation = {
    Validation.all(
      reservation.seats.zipWithIndex.map { case (seat, index) =>
        if (session.theater.plan.seats.exists(_.name == seat.seat)) {
          Validation.ok
        } else {
          Validation.ko(s"seats.${index}.seat", s"The seat ${seat.seat} does not exist")
        }
      }
    )
  }

  private def noSeatIsTaken(reservation: Reservation, session: Session): Validation = {
    val takenSeats = session.reservations.flatMap(_.seats)
    Validation.all(
      reservation.seats.zipWithIndex.map { case (seat, index) =>
        if (takenSeats.forall(_.seat != seat.seat)) {
          Validation.ok
        } else {
          Validation.ko(s"seats.${index}.seat", s"The seat ${seat.seat} is already taken")
        }
      }
    )
  }

  private def allPricesAreValid(reservation: Reservation, session: Session): Validation = {
    Validation.all(
      reservation.seats.zipWithIndex.map { case (seat, index) =>
        val priceOpt = session.prices.find(_.name == seat.price)
        val seatTypeOpt = session.theater.plan.seats.find(_.name == seat.seat)
        (priceOpt, seatTypeOpt) match {
          case (None, _) => Validation.ko(s"seats.${index}.price", s"The price ${seat.price.value} does not exist")
          case (_, None) => Validation.ignored
          case (Some(price), Some(seatType)) =>
            if (price.acceptedSeatTypes.contains(seatType.seatType)) {
              Validation.ok
            } else {
              Validation.ko(s"seats.${index}.price", s"The price ${seat.price.value} is not acceptable for seat ${seat.seat}")
            }
        }
      }
    )
  }
}
