package rezervi.domain.theater.command

import java.util.UUID

import cats.data.EitherT
import cats.implicits._
import rezervi.model.theater._
import rezervi.model.theater.adapters.TheaterAdapter
import rezervi.persistence.postgres.TheaterRepository
import rezervi.router.AuthenticatedUser

import scala.concurrent.{ExecutionContext, Future}

class ManageTheater(theaterRepository: TheaterRepository)(implicit ec: ExecutionContext) {
  type Result = EitherT[Future, ManageTheaterResult, ManageTheaterResult]

  def createTheater(theaterCreation: TheaterCreation, user: AuthenticatedUser): Future[ManageTheaterResult] = {
    val theater = Theater(
      id = TheaterId(UUID.randomUUID()),
      uid = user.uid,
      name = theaterCreation.name,
      address = theaterCreation.address,
      plan = theaterCreation.plan
    )
    val result: Result = for {
      _ <- EitherT(TheaterValidation.validate(theater))
      _ <- EitherT.liftF(theaterRepository.insert(theater))
    } yield {
      ManageTheaterResult.Valid(TheaterAdapter.toTheaterView(theater))
    }
    result.merge
  }

  def updateTheater(theaterId: TheaterId, theaterUpdate: TheaterUpdate, user: AuthenticatedUser): Future[ManageTheaterResult] = {
    val result: Result = for {
      currentTheater <- EitherT.fromOptionF(theaterRepository.find(theaterId), ManageTheaterResult.NotFound)
      _ <- EitherT.cond[Future](currentTheater.uid == user.uid, (), ManageTheaterResult.NotAuthorized)
      newTheater = currentTheater.copy(
        name = theaterUpdate.name,
        address = theaterUpdate.address,
        plan = theaterUpdate.plan
      )
      _ <- EitherT(TheaterValidation.validate(newTheater))
      _ <- EitherT.liftF(theaterRepository.update(newTheater))
    } yield {
      ManageTheaterResult.Valid(TheaterAdapter.toTheaterView(newTheater))
    }
    result.merge
  }

  def removeTheater(theaterId: TheaterId, user: AuthenticatedUser): Future[ManageTheaterResult] = {
    val result: Result = for {
      currentTheater <- EitherT.fromOptionF(theaterRepository.find(theaterId), ManageTheaterResult.Done)
      _ <- EitherT.cond[Future](currentTheater.uid == user.uid, (), ManageTheaterResult.NotAuthorized)
      _ <- EitherT.liftF(theaterRepository.delete(theaterId))
    } yield {
      ManageTheaterResult.Done
    }
    result.merge
  }
}
