package raw_monaid_expr

// Monoid: has to follow these rules.
// Integer subtraction is not a monoid because subtraction is not associative:
// (1 - 2) -3 !== 1 - (2 -3)
// Semigroup: is just the combiine part of a monoid, without the empty part

object CheckMonoidsHelpers {
  def associativeLaw[A](x: A, y: A, z: A)(implicit m: Monoid[A]): Boolean = {
    m.combine(x, m.combine(y, z)) ==
      m.combine(m.combine(x, y), z)
  }

  def identityLaw[A](x: A)(implicit m: Monoid[A]): Boolean = {
    (m.combine(x, m.empty) == x) &&
    (m.combine(m.empty, x) == x)
  }
}

trait Semigroup[A] {
  def combine(x: A, y: A): A
}

object Semigroup {
  def apply[A](implicit semigroup: Semigroup[A]) = semigroup
}

trait Monoid[A] extends Semigroup[A] {
  def empty: A
}

object Monoid {
  def apply[A](implicit monoid: Monoid[A]) = monoid
}

// Monoid for Boolean
object BooleanMonoids {
  implicit val booleanAndMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(a: Boolean, b: Boolean) = a && b
      def empty = true
    }
  /*
  // order doesn't matter.
  import BooleanMonoids._
  import CheckMonoidsHelpers._
  associativeLaw(true,  true, false)(booleanAndMonoid) => true
  associativeLaw(true,  false, true)(booleanAndMonoid) => true
  associativeLaw(false, true, true)(booleanAndMonoid) => true

  // empty is identity
  identityLaw(true)(booleanAndMonoid) => true
  identityLaw(false)(booleanAndMonoid) => true
   */

  implicit val booleanOrMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(a: Boolean, b: Boolean) = a || b
      def empty = false
    }

  // exclusive or
  implicit val booleanEitherMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(a: Boolean, b: Boolean) = (a && !b) || (!a && b)
      def empty = false
    }
  // exclusive or
  implicit val booleanXnorMonoid: Monoid[Boolean] =
    new Monoid[Boolean] {
      def combine(a: Boolean, b: Boolean) = (a || !b) && (!a || b)
      def empty = true
    }
}

object SetMonoids {
  // We need to define setUnionMonoid as a method rather than a value so we can accept the type parameter A
  implicit def setUnionMonoids[A]: Monoid[Set[A]] =
    new Monoid[Set[A]] {
      def combine(a: Set[A], b: Set[A]) = a union b
      def empty = Set.empty[A]
    }
  /*
    import SetMonoids._
    val intSetMonoid = Monoid[Set[Int]]
    val strSetMonoid = Monoid[Set[String]]

    intSetMonoid.combine(Set(1, 2), Set(2, 3))
    // res18: Set[Int] = Set(1, 2, 3)
    strSetMonoid.combine(Set("A", "B"), Set("B", "C"))
    // res19: Set[String] = Set("A", "B", "C")
   */

  // no Set complement and set difference

  // symdiff is ok
  // (Set(1,5) diff Set(1,2,3)) union Set(1,2,3) diff Set(1,5) and
  // (Set(1,5) diff Set(1,2,3)) union (Set(1,2,3) diff Set(1,5)) are the same.
  implicit def symDiffMonoid[A]: Monoid[Set[A]] = new Monoid[Set[A]] {
    def combine(a: Set[A], b: Set[A]) = (a diff b) union (b diff a)
    def empty = Set.empty[A]
  }
}

object SetSemigroups {
  implicit def setIntersectionSemigroup[A]: Semigroup[Set[A]] =
    new Semigroup[Set[A]] {
      def combine(a: Set[A], b: Set[A]) = a intersect b
    }
}
