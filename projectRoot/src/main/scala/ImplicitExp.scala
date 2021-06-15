class ImplicitExp {}
object ImplicitExp {

  /** @example val i: Int = 3.5 // Not Error thank to implicit conversion
    */
  implicit def doubleToInt(x: Double) = x.toInt

  /** @example val myRectangle = 10 x 10
    */
  case class Rectangle(width: Int, height: Int)
  implicit class RectangleMarker(width: Int) {
    def x(height: Int) = Rectangle(width, height)
  }
  // implicit def RectangleMarker(width: Int) = new RectangleMarker(width) が自動生成する

  def maxListOrdering[T](elements: List[T])(ordering: Ordering[T]): T =
    elements match {
      case List()  => throw new IllegalArgumentException("empty list")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxListOrdering(rest)(ordering)
        if (ordering.gt(x, maxRest)) x
        else maxRest
    }

  /*
  Ref: https://alvinalexander.com/scala/how-sort-scala-sequences-seq-list-array-buffer-vector-ordering-ordered/
  https://kmizu.hatenablog.com/entry/2017/05/22/224622
  maxListImpParm(List(new Person(1), new Person(11), new Person(3)))
  List(new Person(1), new Person(11), new Person(3)).sorted
  maxListImpParm(List("One", "Two", "Three")) // 暗黙裏にコンパイラは String のための ordering を渡している
   */
  def maxListImpParm[T](elements: List[T])(implicit ordering: Ordering[T]): T =
    elements match {
      case List()  => throw new IllegalArgumentException("empty list")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxListImpParm(rest)(ordering)
        if (ordering.gt(x, maxRest)) x
        else maxRest
    }

  // コンテキスト境界
  def maxList[T: Ordering](elements: List[T]): T =
    elements match {
      case List()  => throw new IllegalArgumentException("empty list")
      case List(x) => x
      case x :: rest =>
        val maxRest = maxListImpParm(rest) // 暗黙のうちに (ordering) が追加される
        if (implicitly[Ordering[T]].gt(x, maxRest)) x
        else maxRest
    }

  case class Person(age: Int)
  implicit object PersonOrdering extends Ordering[Person] {
    override def compare(x: Person, y: Person): Int = {
      if (x.age < y.age) -1 else if (x.age > y.age) 1 else 0
    }
  }
}

// Implicit parameters を使うために意図的に珍しいクラス名をつける
class PreferredPrompt(val preference: String)
class PreferredDrink(val preference: String)

object Greeter {
  def greet(
      name: String
  )(implicit prompt: PreferredPrompt, drink: PreferredDrink) = {
    println("Welcome, " + name + ". The system is ready.")
    println("While you work, why not enjoy a cup of " + drink.preference)
    println(prompt.preference)
  }
}

object JoesPrefs {
  implicit val prompt = new PreferredPrompt("Yes, master> ")
  implicit val drink = new PreferredDrink("Coffee")
}

/*
// you don't have to provider the rest of parameter for curry.
import JoesPrefs._
Greeter.greet("Bob")
 */
