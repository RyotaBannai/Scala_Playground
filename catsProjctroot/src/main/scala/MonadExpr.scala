import cats.Monad
import cats.instances.option._ // for Monad
import cats.instances.list._ // for Monad

import cats.syntax.functor._ // for map
import cats.syntax.flatMap._ // for flatMap

import cats.Id
import cats.syntax.either._
import cats.MonadError
import cats.instances.either._

import cats.syntax.applicative._ // for pure
import cats.syntax.applicativeError._ // for raiseError etc
import cats.syntax.monadError._ // for ensure

import util.Try
import cats.instances.try_._ // for MonadError
import java.lang.IllegalArgumentException

object MonadExpr {
  val opt1 = Monad[Option].pure(3)
  val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
  val opt3 = Monad[Option].map(opt2)(a => 100 * a)

  val list1 = Monad[List].pure(3)
  val list2 = Monad[List].flatMap(List(1, 2, 3))(a => List(a, a * 10))
  val list3 = Monad[List].map(list2)(a => a + 123)

  def sumSquare[F[_]: Monad](a: F[Int], b: F[Int]): F[Int] = for {
    x <- a
    y <- b
  } yield x * x + y * y

  object MethodsForId {
    def pure[A](value: A): Id[A] = value
    def map[A, B](initial: Id[A])(func: A => B): Id[B] = func(initial)
    def flatMap[A, B](initial: Id[A])(func: A => Id[B]): Id[B] = func(initial)
  }
}

object MonadErrorHandling {
  sealed trait LoginError extends Throwable with Serializable
  final case class UserNotFound(username: String) extends LoginError
  final case class PasswordIncorrect(username: String) extends LoginError
  case object UnexpectedError extends LoginError
  case class User(username: String, password: String)
  type LoginResult = Either[LoginError, User]

  def handleError(error: LoginError): Unit = error match {
    case UserNotFound(u)      => println(s"User not found: $u")
    case PasswordIncorrect(u) => println(s"password incorrect: $u")
    case UnexpectedError      => println(s"Unexpected error")
  }

  val r1: LoginResult = User("dave", "passw0rd").asRight
  val r2: LoginResult = UserNotFound("dave").asLeft

  /* Definition of Either fold.
    Applies `fa` if this is a `Left` or `fb` if this is a `Right`.
    def fold[C](fa: A => C, fb: B => C): C = this match {
      case Right(b) => fb(b)
      case Left(a)  => fa(a)
    }
   */
  // r1.fold(handleError, println)
  // r2.fold(handleError, println)

  /* TODO: What else can to improve this Error Handling
  -　Error recovery is important when processing large jobs.
  　We don’t want to run a job for a day and then find it failed on the last element.
  - Error reporting is equally important. We need to know what went wrong,
    not just that something went wrong.
  - In a number of cases, we want to collect all the errors, not just the first one we encountered.
    A typical example is validating a web form. It’s a far better experience to report all errors
    to the user when they submit a form than to report them one at a time.
   */
}

object UseMonadError {
  /*
    trait MonadError[F[_], E] extends Monad[F] {...}
    // F is the type of the monad
    // E is the type of error contained within F

    - MonadError extends ApplicativeError
   */

  type ErrorOr[A] = Either[String, A]
  val monadError = MonadError[ErrorOr, String]

  val success = monadError.pure(42) // ErrorOr[Int] Right
  val failure = monadError.raiseError("Badness") // ErrorOr[Nothing] ! Left
  // ErrorOr[Int] ! Left
  val failureWithType = "Badness".raiseError[ErrorOr, Int]

  // handleErrorWith is the complement of raiseError, similar with recover method of Future
  val r = monadError.handleErrorWith(failure) {
    // ErrorOr[String] Right
    case "Badness" => monadError.pure("It's ok friend.")
    // ErrorOr[String] ! Left
    case _ => monadError.raiseError("It's not good bro.")
  }

  val exn: Throwable = new RuntimeException("It's all gone wrong")
  val inst = exn.raiseError[Try, Int]
  // scala.util.Try[Int] = Failure(java.lang.RuntimeException: It's all gone wrong)

  // def ageErrorHandler = (age: Int) =>
  //   age.handleErrorWith {
  //     case a >= 18 => a.pure[Try]
  //     case _ =>
  //       new IllegalArgumentException(
  //         "Age must be greater than or equal to 18"
  //       ).raiseError[F, Int]
  //   }

  def validateAdult[F[_]](age: Int)(implicit
      me: MonadError[F, Throwable]
  ): F[Int] =
    if (age >= 18) age.pure[F]
    else
      new IllegalArgumentException(
        "Age must be greater than or equal to 18"
      ).raiseError[F, Int]

  validateAdult[Try](18) // Success(18)
  validateAdult[Try](17) // Failure(17)
  // res8: Try[Int] = Failure(
  //   java.lang.IllegalArgumentException: Age must be greater than or equal to 18

  type ExceptionOr[A] = Either[Throwable, A]
  validateAdult[ExceptionOr](-1)
  // res9: ExceptionOr[Int] = Left(
  //   java.lang.IllegalArgumentException: Age must be greater than or equal to 18
  // )

}
