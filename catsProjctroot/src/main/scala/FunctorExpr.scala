import cats.Contravariant
import cats.syntax.contravariant._
import cats.Show
import cats.instances.string._

/** The simplified version of definitions:
  *
  * trait Contravariant[F[_]] {
  * def contramap[A, B](fa: F[A])(f: B => A): F[B]
  * }
  *
  * trait Invariant[F[_]] {
  * def imap[A, B](fa: F[A])(f: A => B)(g: B => A): F[B]
  * }
  */

object FunctorExpr {
  val showString = Show[String]
  val showSymbol =
    Contravariant[Show].contramap(showString)((sym: Symbol) => s"'${sym.name}'")
  // show method for type class instance just as it is.
  showSymbol.show(Symbol("dave"));
  // String = 'dave'

  // or
  showString
    .contramap[Symbol](sym => s"'${sym.name}'")
    .show(Symbol("dave"))
}
