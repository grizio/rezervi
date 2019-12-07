package rezervi.domain.theater.query

import rezervi.model.theater.TheaterView
import rezervi.model.theater.adapters.TheaterAdapter
import rezervi.persistence.postgres.TheaterRepository
import rezervi.router.AuthenticatedUser

import scala.concurrent.{ExecutionContext, Future}

class FindTheaters(theaterRepository: TheaterRepository)(implicit ec: ExecutionContext) {
  def listForAuthenticatedUser(user: AuthenticatedUser): Future[Seq[TheaterView]] = {
    theaterRepository.findByUser(user.uid)
      .map { theaters =>
        theaters.map(TheaterAdapter.toTheaterView)
      }
  }
}
