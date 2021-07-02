import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import cats.instances.function._
import cats.syntax.functor._
import cats.Functor
import cats.instances.list._ // for Functor
import cats.instances.option._ // for Functor

object FunctorExpr {
  def run(): Unit = {
    val future: Future[String] =
      Future(123)
        .map(n => n + 1)
        .map(n => n * 2)
        .map(n => s"${n}!")

    Await.result(future, 1.second)
  }

  val func1: Int => Double = (x: Int) => x.toDouble
  val func2: Double => Double = (y: Double) => y * 2

  /** “mapping” over a Function1 is function composition
    */
  // composition using map
  val r1 = (func1 map func2)(1)
  // composition using andThen
  val r2 = (func1 andThen func2)(1)
  // composition written out by hand.
  val r3 = func2(func1(1))
}

object UseFunctor {
  val list1 = List(1, 2, 3)
  val list2 = Functor[List].map(list1)(_ * 2)

  val op1 = Option(123)
  val op2 = Functor[Option].map(op1)(_.toString) // Option(String) = Some(123)

  val func1 = (x: Int) => x + 1
  val liftedFunc = Functor[Option].lift(func1)
  liftedFunc(Option(1))

  Functor[List].as(list1, "As") // List[String] = List("As","As","As")

  def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] =
    start.map(n => n + 1 * 2)
  doMath(Option(20))
  doMath(List(1, 2, 3))

  /*
  Converts source to cats Functor type and applies cats map.
  The map methods of FunctorOps requires an implicit Functor as a parameter:
    This means this code will only compile if we have a Functor for F in scope.

  map method in cats.syntax.functor. Here’s a simplified version of the code:

  implicit class FunctorOps[F[_], A](src: F[A]) {
    def map[B](func: A => B)(implicit functor: Functor[F]): F[B] =
      functor.map(src)(func)
  }
   */

  // Custom Type. cats has cats.instances.option
  implicit val optionFunctor: Functor[Option] = new Functor[Option] {
    def map[A, B](value: Option[A])(func: A => B): Option[B] = value.map(func)
  }
}

object BranchingOut {
  sealed trait Tree[+A]
  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
  final case class Leaf[A](value: A) extends Tree[A]

  implicit val treeFunctor: Functor[Tree] =
    new Functor[Tree] {
      def map[A, B](tree: Tree[A])(func: A => B): Tree[B] = tree match {
        case Branch(left, right) => Branch(map(left)(func), map(right)(func))
        case Leaf(value)         => Leaf(func(value))
      }
    }

  /* 'Smart constructors' without this, we will get an error like below
   * Branch(Leaf(10), Leaf(20)).map(_ * 2)
   * error: value map is not a member of BranchingOut.Branch[Int] ..
   * -> this means Scala compiler is looking for Functor for Branch not Tree, which we only defined.
   *
   * So improve to explicitly convert the type from Branch to Tree:
   * Tree.branch(Tree.leaf(10), Tree.leaf(20)).map(_ * 2)
   * => BranchingOut.Tree[Int] = Branch(Leaf(20),Leaf(40))
   *
   * Or even do like:
   * Tree.branch(
   *  Tree.branch(Tree.leaf(10), Tree.leaf(20)).map(_ * 2),
   *  Tree.branch(Tree.leaf(30), Tree.leaf(40)).map(_ * 2)
   * )
   *
   */
  object Tree {
    def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)
    def leaf[A](value: A): Tree[A] = Leaf(value)
  }
}

object ContraMapExpr {
  // Type class
  trait Printable[A] { self =>
    def format(value: A): String
    def contramap[B](func: B => A): Printable[B] =
      new Printable[B] {
        def format(value: B): String = self.format(func(value))
      }
  }

  final case class Box[A](value: A)

  // Instance
  object ContraMapPrintableInstances {
    implicit val stringPrintable: Printable[String] =
      new Printable[String] {
        def format(value: String): String = s"'${value}'"
      }

    implicit val booleanPrintable: Printable[Boolean] =
      new Printable[Boolean] {
        def format(value: Boolean): String = if (value) "yes" else "no"
      }

    // define manually...
    // implicit def boxPrintable[A](implicit
    //     p: Printable[A]
    // ): Printable[Box[A]] =
    //   new Printable[Box[A]] {
    //     def format(box: Box[A]): String = p.format(box.value)
    //   }

    // or use contra map
    implicit def contraMapBoxPrintable[A](implicit
        p: Printable[A]
    ): Printable[Box[A]] = p.contramap[Box[A]](_.value)
    // first use _.value (B => A: Box => String), then apply overrode format method
  }

  // Use
  object ContraMapPrintable {
    def format[A](value: A)(implicit p: Printable[A]): String = p.format(value)
    def print[A](value: A)(implicit p: Printable[A]): Unit = println(
      format(value)
    )
  }
  /*
    import ContraMapExpr._, ContraMapPrintable._, ContraMapPrintableInstances._
    format("hello")
    // String = "'hello'"
    format(true)
    // String = "yes"
    format(Box("hello world")) // Contra map
    // String = "'hello world'"
   */
}
