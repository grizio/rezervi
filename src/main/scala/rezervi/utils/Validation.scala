package rezervi.utils

import cats.Applicative
import cats.data.EitherT

sealed trait Validation {
  def toEither: Either[Validation.Errors, Unit]

  def toEither[E, A](onKo: Validation.Errors => E, onOk: => A): Either[E, A]

  def toEitherT[F[_]](implicit F: Applicative[F]): EitherT[F, Validation.Errors, Unit] = {
    EitherT.fromEither[F](toEither)
  }
}

case class ValidationKO(errors: Validation.Errors) extends Validation {
  override def toEither: Either[Validation.Errors, Unit] = Left(errors)

  def toEither[E, A](onKo: Validation.Errors => E, onOk: => A): Either[E, A] = Left(onKo(errors))
}

case object ValidationOK extends Validation {
  override def toEither: Either[Validation.Errors, Unit] = Right()

  def toEither[E, A](onKo: Validation.Errors => E, onOk: => A): Either[E, A] = Right(onOk)
}

object Validation {
  type Errors = Map[String, Seq[String]]

  def all(validations: Validation*): Validation = {
    validations.fold(ValidationOK) { (acc, current) =>
      (acc, current) match {
        case (ValidationOK, ValidationOK) => ValidationOK
        case (errors: ValidationKO, ValidationOK) => errors
        case (ValidationOK, errors: ValidationKO) => errors
        case (ValidationKO(errors1), ValidationKO(errors2)) =>
          ValidationKO(
            (errors1.toSeq ++ errors2.toSeq)
              .groupMapReduce(_._1)(_._2)(_ ++ _)
          )
      }
    }
  }

  def ok: Validation = ValidationOK

  def ko(field: String, value: String): Validation = ValidationKO(Map(field -> Seq(value)))
}
