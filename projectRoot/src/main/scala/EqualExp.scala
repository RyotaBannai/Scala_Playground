class EqualExp {}

object EqualExp {
  class Point(val x: Int, val y: Int) {
    override def hashCode() = (x, y).##
    // better impl
    override def equals(other: Any): Boolean = other match {
      case that: Point =>
        (that canEqual this) && this.x == that.x && this.y == that.y
      case _ => false
    }
    def canEqual(other: Any) = other.isInstanceOf[Point]
  }

  object Color extends Enumeration {
    val Red, Orange, Yellow, Green, Blue, Indigo, Violet = Value
  }

  class ColoredPoint(x: Int, y: Int, val color: Color.Value)
      extends Point(x, y) {
    override def equals(other: Any): Boolean = other match {
      case that: ColoredPoint =>
        (that canEqual this) && (this.color == that.color) && super.equals(that)
      case _ => false
    }
    override def canEqual(other: Any) = other.isInstanceOf[ColoredPoint]
  }

  /* equivalence for parameterized type
   */
  trait Tree[+T] {
    def elem: T
    def left: Tree[T]
    def right: Tree[T]
  }

  object EmptyTree extends Tree[Nothing] {
    def elem = throw new NoSuchElementException("EmptyTree.elem")
    def left = throw new NoSuchElementException("EmptyTree.left")
    def right = throw new NoSuchElementException("EmptyTree.left")
  }

  // 空でない木を表す.
  class Branch[T](
      val elem: T,
      val left: Tree[T],
      val right: Tree[T]
  ) extends Tree[T] {
    override def equals(other: Any): Boolean = other match {
      case that: Branch[t] =>
        (that canEqual this) && (this.elem == that.elem) && (this.left == that.left) && (this.right == that.right)
      case _ => false
    }
    override def hashCode: Int = (elem, left, right).##
    def canEqual(other: Any) = other.isInstanceOf[Branch[_]]
  }
}

/*
import EqualExp._
import scala.collection.mutable
val p1, p2 = new Point(1,2)
val q = new Point(2,3)
val p2a : Any = p2
val coll = mutable.HashSet(p1)

p1 equals p2a
coll contains p2
coll contains p2a


val pAnon = new Point(1,1) {override val y =2} // anonymous sub class
pAnon equals p1 // false if you don't define canEqual

// equivalence for parameterized type
val b1 = new Branch[List[String]](Nil, EmptyTree, EmptyTree)
val b2 = new Branch[List[Int]](Nil, EmptyTree, EmptyTree)
 */
