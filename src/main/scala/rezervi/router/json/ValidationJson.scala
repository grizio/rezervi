package rezervi.router.json

import rezervi.utils.Validation
import rezervi.utils.Validation.Errors
import spray.json.{JsArray, JsObject, JsString, RootJsonWriter}

trait ValidationJson {
  implicit val validationErrorsJson: RootJsonWriter[Validation.Errors] = (errors: Errors) => {
    JsObject(errors.map {
      case (key, value) => key -> JsArray(value.map(JsString(_)): _*)
    })
  }
}
