import cats.Contravariant
import cats.syntax.contravariant._ // for contramap
import cats.Show
import cats.instances.string._

import cats.Monoid
import cats.instances.string._ // for Monoid
import cats.syntax.invariant._ // for imap
import cats.syntax.semigroup._ // for |+|

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
  // Show.show has to be String, but the given is Symbol and before it's consumed by
  // it's converted from Symbol to String by contravariant.
  // String = 'dave'

  // or with 'contravariant'
  showString
    .contramap[Symbol](sym => s"'${sym.name}'")
    .show(Symbol("dave"))

  implicit val symbolMonoid: Monoid[Symbol] =
    Monoid[String].imap(Symbol.apply)(_.name)

  Monoid[Symbol].empty
  Symbol("a") |+| Symbol("few") |+| Symbol("words")
  /*
  1. accept two Symbols as parameters;
  2. convert the Symbols to Strings;
  3. combine the Strings using Monoid[String];
  4. convert the result back to a Symbol.

  We can implement combine using imap,
  passing functions of type String => Symbol and Symbol => String as parameters
   */

  // import cats.Functor
  // import cats.instances.function._
  // import cats.syntax.functor._
  // import cats.syntax.contravariant._

  // val func1 = (x: Int) => x.toDouble
  // val func2 = (y: Double) => y * 2
  // Hypothetical example. This won't actually compile:
  // val func3c: Int => Double = func2.contramap(func1)

  // needs to be done these below to actually work things out:
  // type <=[B, A] = A => B
  // type F[A] = Double <= A
  // val func2b: Double <= Double = func2
  // val func3c = func2b.contramap(func1)
}
