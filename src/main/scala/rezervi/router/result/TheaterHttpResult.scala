package rezervi.router.result

import rezervi.domain.theater.command.ManageTheaterResult
import rezervi.router.json.Jsons

trait TheaterHttpResult {
  implicit val manageTheaterHttpResult: ToHttpResult[ManageTheaterResult] = {
    case ManageTheaterResult.NotFound => HttpResult.notFound()
    case ManageTheaterResult.NotAuthorized => HttpResult.forbidden()
    case ManageTheaterResult.Invalid(errors) => HttpResult.badRequest(Jsons.validationErrorsJson.write(errors))
    case ManageTheaterResult.Valid(theater) => HttpResult.ok(Jsons.theaterViewWriter.write(theater))
    case ManageTheaterResult.Done => HttpResult.noContent
  }
}
