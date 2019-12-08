package rezervi.domain.session.query

import rezervi.model.session.adapters.SessionAdapter
import rezervi.model.session.{SessionId, SessionView}
import rezervi.persistence.postgres.SessionRepository
import rezervi.router.AuthenticatedUser

import scala.concurrent.{ExecutionContext, Future}

class FindSessions(sessionRepository: SessionRepository)(implicit ec: ExecutionContext) {
  def find(sessionId: SessionId): Future[Option[SessionView]] = {
    sessionRepository.find(sessionId)
      .map { sessionOpt =>
        sessionOpt.map(SessionAdapter.toSessionView)
      }
  }

  def listForAuthenticatedUser(user: AuthenticatedUser): Future[Seq[SessionView]] = {
    sessionRepository.findByUser(user.uid)
      .map { sessions =>
        sessions.map(SessionAdapter.toSessionView)
      }
  }
}
