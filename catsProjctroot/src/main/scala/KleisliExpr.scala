object KleisliExpr {
  import cats.data.Kleisli
  import cats.instances.list._

  val step1: Kleisli[List, Int, Int] = Kleisli(x => List(x + 1, x - 1))
  val step2: Kleisli[List, Int, Int] = Kleisli(x => List(x, -x))
  val step3: Kleisli[List, Int, Int] = Kleisli(x => List(x * 2, x / 2))

  val pipeline = step1 andThen step2 andThen step3
  // pipeline.run(20) // List[Int] = List(42, 10, -42, -10, 38, 9, -38, -9)
}

object ValidationWithKleisili {
  import cats.Semigroup
  import cats.data.{NonEmptyList, Validated}
  import cats.Semigroup
  import cats.data.Validated._ // for Valid and Invalid
  import cats.syntax.semigroup._ // for |+|
  import cats.syntax.apply._ // for mapN
  import cats.syntax.validated._ // for valid and invalid

  object CaseStudy10Validation {
    sealed trait Predicate[E, A] {
      import Predicate._
      def apply(a: A)(implicit s: Semigroup[E]): Validated[E, A] =
        this match {
          case Pure(func)       => func(a)
          case And(left, right) => (left(a), right(a)).mapN((_, _) => a)
          case Or(left, right) =>
            left(a) match {
              case Valid(a) => Valid(a)
              case Invalid(e1) => {
                right(a) match {
                  case Valid(a)    => Valid(a)
                  case Invalid(e2) => Invalid(e1 |+| e2)
                }
              }
            }
        }

      def and(that: Predicate[E, A]): Predicate[E, A] = And(this, that)
      def or(that: Predicate[E, A]): Predicate[E, A] = Or(this, that)
    } // end of Predicate trait

    object Predicate {
      final case class And[E, A](left: Predicate[E, A], right: Predicate[E, A])
          extends Predicate[E, A]
      final case class Or[E, A](left: Predicate[E, A], right: Predicate[E, A])
          extends Predicate[E, A]
      final case class Pure[E, A](func: A => Validated[E, A])
          extends Predicate[E, A]

      def apply[E, A](fn: A => Validated[E, A]): Predicate[E, A] = Pure(fn)
      def lift[E, A](err: E, fn: A => Boolean): Predicate[E, A] =
        pure(a => if (fn(a)) a.valid else err.invalid)
      def pure[E, A](f: A => Validated[E, A]): Predicate[E, A] = Pure(f)
    }

    sealed trait Check[E, A, B] {
      import Check._
      def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B]
      def map[C](f: B => C): Check[E, A, C] = Map[E, A, B, C](this, f)
      def flatMap[C](f: B => Check[E, A, C]) = FlatMap[E, A, B, C](this, f)
      def andThen[C](next: Check[E, B, C]): Check[E, A, C] =
        AndThen[E, A, B, C](this, next)
    } // end of Check class

    object Check {
      def apply[E, A](pred: Predicate[E, A]): Check[E, A, A] = PurePredicate(
        pred
      )
      def apply[E, A, B](f: A => Validated[E, B]): Check[E, A, B] = Pure(f)

      final case class Pure[E, A, B](func: A => Validated[E, B])
          extends Check[E, A, B] {
        def apply(in: A)(implicit s: Semigroup[E]): Validated[E, B] = func(in)
      } // end of Pure class

      final case class PurePredicate[E, A](pred: Predicate[E, A])
          extends Check[E, A, A] {
        def apply(in: A)(implicit s: Semigroup[E]): Validated[E, A] = pred(in)
      } // end of PurePredicate class

      final case class Map[E, A, B, C](check: Check[E, A, B], func: B => C)
          extends Check[E, A, C] {
        def apply(in: A)(implicit s: Semigroup[E]): Validated[E, C] =
          check(in).map(func)
      }

      final case class FlatMap[E, A, B, C](
          check: Check[E, A, B],
          func: B => Check[E, A, C]
      ) extends Check[E, A, C] {
        def apply(in: A)(implicit s: Semigroup[E]): Validated[E, C] =
          check(in).withEither(_.flatMap(b => func(b)(in).toEither))
      }

      final case class AndThen[E, A, B, C](
          check1: Check[E, A, B],
          check2: Check[E, B, C]
      ) extends Check[E, A, C] {
        def apply(a: A)(implicit s: Semigroup[E]): Validated[E, C] =
          check1(a).withEither(_.flatMap(b => check2(b).toEither))
      }
    } // end of Check object
  } // end of ValidationWithKleisili object
}
