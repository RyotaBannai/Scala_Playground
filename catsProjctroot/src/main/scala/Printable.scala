// Type class
trait Printable[A] {
  def format(value: A): String
}

// Instance
object PrintableInstances {
  implicit val stringFormatter: Printable[String] =
    new Printable[String] {
      def format(value: String): String = value
    }

  implicit val intFormatter: Printable[Int] =
    new Printable[Int] {
      def format(value: Int): String = value.toString
    }

  implicit val catFormatter: Printable[Cat] =
    new Printable[Cat] {
      def format(value: Cat): String = {
        val name = Printable.format(value.name)
        val age = Printable.format(value.age)
        val color = Printable.format(value.color)
        s"$name is a $age year-old $color cat."
      }
    }
}

// Use
object Printable {
  def format[A](value: A)(implicit p: Printable[A]): String = p.format(value)
  def print[A](value: A)(implicit p: Printable[A]): Unit = println(
    format(value)
  )
}

/*
import PrintableSyntax._
import PrintableInstances._
1.print
> 1
 */
object PrintableSyntax {
  implicit class PrintableOps[A](value: A) {
    def format(implicit p: Printable[A]): String = p.format(value)
    def print(implicit p: Printable[A]): Unit = println(format(p))
  }
}
/*
val cat = Cat("Dog", 11, "black")
Printable.print(cat)
cat.print
 */
final case class Cat(name: String, age: Int, color: String)
