trait TraitA
trait TraitB
class ClassA
class ClassB

// class Trait extends ClassA with TraitA with TraitB  // traitは幾つでもミックスインできる

trait Pa{
  // def hi(): Unit
  def hi(): Unit = println("P-> hi")
}
trait C1 extends Pa{
  override def hi(): Unit = {
    super.hi()
    println("C1-> hi")
  }
}
trait C2 extends Pa{
  override def hi(): Unit = {
    super.hi()
    println("C2-> hi")
  }
}
class Trait extends C1 with C2

trait A {
  val foo: String
}

trait B extends A {
  lazy val bar = foo + " World"
}

class C extends B {
  val foo = "Hello"

  def printBar(): Unit = println(bar)
}

object Trait {
  def main(args: Array[String]): Unit = {
    val a = new Trait
    a.hi()
  }
}
