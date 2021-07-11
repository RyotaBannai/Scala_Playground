import scala.language.postfixOps

/** CRDTs: Commutative Replicated Data Types
  */
object CRDTExpr {
  final case class GCounter(counters: Map[String, Int]) {
    def increment(machine: String, amount: Int) = {
      val value = amount + counters.getOrElse(machine, 0)
      GCounter(counters + (machine -> amount))
    }

    // def merge(that: GCounter): GCounter =
    //   GCounter(
    //     that.counters map { case (k, v) =>
    //       (k -> (v max this.counters.getOrElse(k, 0)))
    //     } toMap
    //   )

    /* get all possible element by merging two maps*/
    def merge(that: GCounter): GCounter = GCounter(
      that.counters ++ this.counters.map { case (k, v) =>
        k -> (v max that.counters.getOrElse(k, 0))
      }
    )
    def total: Int = counters.values.sum
  }
}

object BoundedSemiLatticeObject {
  import cats.kernel.CommutativeMonoid

  /** BoundedSemiLattice = idempotent commutative monoid
    */
  trait BoundedSemiLattice[A] extends CommutativeMonoid[A] {
    def combine(x: A, y: A): A
    def empty: A
  } // end of BoundedSemiLattice class
  object BoundedSemiLattice {
    implicit val intInstance: BoundedSemiLattice[Int] =
      new BoundedSemiLattice[Int] {
        def combine(x: Int, y: Int): Int = x max y
        def empty: Int = 0
      }
    implicit def setInstance[A]: BoundedSemiLattice[Set[A]] =
      new BoundedSemiLattice[Set[A]] {
        def combine(x: Set[A], y: Set[A]): Set[A] = x union y
        def empty: Set[A] = Set.empty[A]
      }
  } // end of BoundedSemiLattice object
} // end of BoundedSemiLatticeObject object

object CRDTGeneralisedExpr {
  import cats.kernel.CommutativeMonoid
  import BoundedSemiLatticeObject._

  import cats.Monoid
  import cats.instances.list._ // for Monoid
  import cats.instances.map._ // for Monoid
  import cats.syntax.semigroup._ // for |+|
  import cats.syntax.foldable._ // for combineAll

  final case class GCounter[A](counters: Map[String, A]) {
    def increment(machine: String, amount: A)(implicit
        m: CommutativeMonoid[A]
    ) = {
      val value = amount |+| counters.getOrElse(machine, m.empty)
      GCounter(counters + (machine -> amount))
    }

    def merge(that: GCounter[A])(implicit
        m: BoundedSemiLattice[A]
    ): GCounter[A] = GCounter(
      // No!
      // that.counters ++ this.counters.map { case (k, v) =>
      //   k -> (v combine that.counters.getOrElse(k, m.empty))
      // }

      // Yes. thank to map Monoid.
      this.counters |+| that.counters
    )
    def total(implicit m: CommutativeMonoid[A]): A =
      this.counters.values.toList.combineAll
  }
}

object AbstractOverCRDTs {
  import cats.kernel.CommutativeMonoid
  import BoundedSemiLatticeObject._

  import cats.Monoid
  import cats.instances.list._ // for Monoid
  import cats.instances.map._ // for Monoid
  import cats.syntax.semigroup._ // for |+|
  import cats.syntax.foldable._ // for combineAll

  trait GCounter[F[_, _], K, V] {
    def increment(f: F[K, V])(key: K, value: V)(implicit
        m: CommutativeMonoid[V]
    ): F[K, V]
    def merge(f1: F[K, V], f2: F[K, V])(implicit
        m: BoundedSemiLattice[V]
    ): F[K, V]
    def total(f: F[K, V])(implicit m: CommutativeMonoid[V]): V
  } // end of GCounter trait

  object GCounter {
    def apply[F[_, _], K, V](implicit counter: GCounter[F, K, V]) = counter
  }

  implicit def mapCounterInstance[K, V] = new GCounter[Map, K, V] {
    def increment(map: Map[K, V])(key: K, value: V)(implicit
        m: CommutativeMonoid[V]
    ): Map[K, V] = {
      val total = value |+| map.getOrElse(key, m.empty)
      map + (key -> total)
    }

    def merge(f1: Map[K, V], f2: Map[K, V])(implicit
        m: BoundedSemiLattice[V]
    ): Map[K, V] = f1 |+| f2

    def total(f: Map[K, V])(implicit m: CommutativeMonoid[V]): V =
      f.values.toList.combineAll

    /*
      import cats.instances.int._ // for Monoid

      val g1 = Map("a" -> 7, "b" -> 3)
      val g2 = Map("a" -> 2, "b" -> 5)

      val counter = GCounter[Map, String, Int]

      val merged = counter.merge(g1, g2)
      // merged: Map[String, Int] = Map("a" -> 7, "b" -> 5)
      val total  = counter.total(merged)
      // total: Int = 12
     */
  }

} // end of AbstractOverCRDTs object
