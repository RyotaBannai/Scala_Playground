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

object SuperAdder {
  // def add(items: Lift[Int]): Int =
  //   items.foldLeft(0)(_ + _)

  // by using Monoids
  // accepts implicit Monoids using Context bounds.
  // this works for Int and Option and other.
  def add[A: Monoid](items: List[A]): A =
    items.foldLeft(implicitly[Monoid[A]].empty)(_ |+| _)

  /*
  import cats.instances.int._
  import cats.instances.string._
  import cats.instances.option._
  import SuperAdder._

  add(List(1,2,3,4))
  add(List("test", ",", "code"))
  add(List(Some(1), Some(2), None))

  'add(List(Some(1), Some(2), Some(3)))' doesn't work
  because compiler infers this as List[Some[Int]] rather than List[Option[Int]]
   */

  case class Order(totalCost: Double, quantity: Double)

  implicit val monoid: Monoid[Order] = new Monoid[Order] {
    def combine(o1: Order, o2: Order) =
      Order(o1.totalCost + o2.totalCost, o1.quantity + o2.quantity)

    def empty = Order(0, 0)
  }
  /*
  import cats.Monoid
  import SuperAdder._
  add(List(Order(100, 1), Order(200, 3), Monoid[Order].empty))
   */
}
