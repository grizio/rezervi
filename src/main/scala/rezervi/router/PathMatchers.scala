package rezervi.router

import java.util.UUID

import akka.http.scaladsl.model.Uri.Path
import akka.http.scaladsl.server.PathMatcher.{Matched, Unmatched}
import akka.http.scaladsl.server.{PathMatcher, PathMatcher1}
import rezervi.model.theater.TheaterId

object PathMatchers {
  abstract class IdSegment[A](applyId: UUID => A) extends PathMatcher1[A] {
    def apply(path: Path): PathMatcher.Matching[Tuple1[A]] = path match {
      case Path.Segment(segment, tail) =>
        try {
          Matched(tail, Tuple1(applyId(UUID.fromString(segment))))
        } catch {
          case _: IllegalArgumentException => Unmatched
        }
      case _ =>
        Unmatched
    }
  }

  object TheaterIdSegment extends IdSegment(TheaterId)

}
