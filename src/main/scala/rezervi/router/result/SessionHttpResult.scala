package rezervi.router.result

import rezervi.domain.session.command.ManageSessionResult
import rezervi.domain.theater.command.ManageTheaterResult
import rezervi.router.json.Jsons

trait SessionHttpResult {
  implicit val manageSessionHttpResult: ToHttpResult[ManageSessionResult] = {
    case ManageSessionResult.NotFound => HttpResult.notFound()
    case ManageSessionResult.NotAuthorized => HttpResult.forbidden()
    case ManageSessionResult.Invalid(errors) => HttpResult.badRequest(Jsons.validationErrorsJson.write(errors))
    case ManageSessionResult.Valid(session) => HttpResult.ok(Jsons.sessionViewWriter.write(session))
    case ManageSessionResult.Done => HttpResult.noContent
  }
}
