package rezervi.domain.session.command

import cats.data.EitherT
import cats.implicits._
import rezervi.model.session.SessionId
import rezervi.model.session.reservation.{NewReservation, Reservation}
import rezervi.persistence.postgres.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

class Reserve(sessionRepository: SessionRepository)(implicit ec: ExecutionContext) {
  type Result = EitherT[Future, ReserveResult, ReserveResult]

  def reserve(sessionId: SessionId, newReservation: NewReservation): Future[ReserveResult] = {
    val result: Result = for {
      session <- EitherT.fromOptionF(sessionRepository.find(sessionId), ReserveResult.NotFound)
      reservation = Reservation(
        name = newReservation.name,
        email = newReservation.email,
        seats = newReservation.seats,
        comment = newReservation.comment
      )
      _ <- EitherT(ReservationValidation.validateReservation(reservation, session))
      updatedSession = session.copy(
        reservations = session.reservations.appended(reservation)
      )
      _ <- EitherT.liftF(sessionRepository.update(updatedSession))
    } yield {
      ReserveResult.Valid(reservation)
    }
    result.merge
  }
}
