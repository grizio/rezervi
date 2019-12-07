package rezervi.router.result

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, ResponseEntity, StatusCode, StatusCodes}
import spray.json.JsValue

case class HttpResult(status: StatusCode, value: Option[JsValue]) {
  def toHttpResponse: HttpResponse = {
    HttpResponse(
      status = status,
      entity = {
        value
          .map(jsValue => HttpEntity(ContentTypes.`application/json`, jsValue.compactPrint))
          .getOrElse(HttpEntity.Empty)
      }
    )
  }
}

object HttpResult {
  def ok(value: JsValue): HttpResult = HttpResult(StatusCodes.OK, Some(value))

  def notFound(): HttpResult = HttpResult(StatusCodes.NotFound, None)
  def notFound(value: JsValue): HttpResult = HttpResult(StatusCodes.NotFound, Some(value))

  def forbidden(): HttpResult = HttpResult(StatusCodes.Forbidden, None)
  def forbidden(value: JsValue): HttpResult = HttpResult(StatusCodes.Forbidden, Some(value))

  def badRequest(): HttpResult = HttpResult(StatusCodes.BadRequest, None)
  def badRequest(value: JsValue): HttpResult = HttpResult(StatusCodes.BadRequest, Some(value))

  def noContent: HttpResult = HttpResult(StatusCodes.NoContent, None)
}

trait ToHttpResult[A] {
  def toHttpResult(value: A): HttpResult
}