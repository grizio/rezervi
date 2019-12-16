package rezervi.domain.session.command

import java.util.UUID

import cats.data.EitherT
import cats.implicits._
import rezervi.model.session.adapters.SessionAdapter
import rezervi.model.session.{Session, SessionCreation, SessionId, SessionUpdate}
import rezervi.persistence.postgres.{SessionRepository, TheaterRepository}
import rezervi.router.AuthenticatedUser
import rezervi.utils.Validation

import scala.concurrent.{ExecutionContext, Future}

class ManageSession(
  theaterRepository: TheaterRepository,
  sessionRepository: SessionRepository
)(implicit ec: ExecutionContext) {
  type Result = EitherT[Future, ManageSessionResult, ManageSessionResult]

  private val unknownTheaterError = Validation.errors("theater", "The theater does not exist")

  def createSession(sessionCreation: SessionCreation, user: AuthenticatedUser): Future[ManageSessionResult] = {
    val result: Result = for {
      theater <- EitherT.fromOptionF(theaterRepository.find(sessionCreation.theaterId), ManageSessionResult.Invalid(unknownTheaterError))
      _ <- EitherT.cond[Future](theater.uid == user.uid, (), ManageSessionResult.NotAuthorized)
      session = Session(
        id = SessionId(UUID.randomUUID()),
        date = sessionCreation.date,
        theater = theater,
        prices = sessionCreation.prices,
        reservations = Seq.empty
      )
      _ <- EitherT(SessionValidation.validate(session))
      _ <- EitherT.liftF(sessionRepository.insert(session))
    } yield {
      ManageSessionResult.Valid(SessionAdapter.toSessionView(session))
    }
    result.merge
  }

  def updateSession(sessionId: SessionId, sessionUpdate: SessionUpdate, user: AuthenticatedUser): Future[ManageSessionResult] = {
    val result: Result = for {
      currentSession <- EitherT.fromOptionF(sessionRepository.find(sessionId), ManageSessionResult.NotFound)
      _ <- EitherT.cond[Future](currentSession.theater.uid == user.uid, (), ManageSessionResult.NotAuthorized)
      newSession = currentSession.copy(
        date = sessionUpdate.date,
        prices = sessionUpdate.prices
      )
      _ <- EitherT(SessionValidation.validate(newSession))
      _ <- EitherT.liftF(sessionRepository.update(newSession))
    } yield {
      ManageSessionResult.Valid(SessionAdapter.toSessionView(newSession))
    }
    result.merge
  }

  def removeSession(sessionId: SessionId, user: AuthenticatedUser): Future[ManageSessionResult] = {
    val result: Result = for {
      currentSession <- EitherT.fromOptionF(sessionRepository.find(sessionId), ManageSessionResult.Done)
      _ <- EitherT.cond[Future](currentSession.theater.uid == user.uid, (), ManageSessionResult.NotAuthorized)
      _ <- EitherT.liftF(sessionRepository.delete(sessionId))
    } yield {
      ManageSessionResult.Done
    }
    result.merge
  }
}
