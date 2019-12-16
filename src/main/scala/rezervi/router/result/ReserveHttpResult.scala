package rezervi.router.result

import rezervi.domain.session.command.ReserveResult
import rezervi.router.json.Jsons

trait ReserveHttpResult {
  implicit val reserveHttpResult: ToHttpResult[ReserveResult] = {
    case ReserveResult.NotFound => HttpResult.notFound()
    case ReserveResult.NotAuthorized => HttpResult.forbidden()
    case ReserveResult.Invalid(errors) => HttpResult.badRequest(Jsons.validationErrorsJson.write(errors))
    case ReserveResult.Valid(reservation) => HttpResult.ok(Jsons.reservationFormat.write(reservation))
  }
}
