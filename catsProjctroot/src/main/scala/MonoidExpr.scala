import cats.Monoid
import cats.Semigroup
import cats.instances.int._ // for Monoid
import cats.instances.option._ // for Monoid
import cats.syntax.semigroup._ // for |+|

object MonoidExpr {
  val a = Option(22)
  val b = Option(20)
  Monoid[Option[Int]].combine(a, b)
  // Option[Int] = Some(42)
  // a |+| b => the same result.
}
