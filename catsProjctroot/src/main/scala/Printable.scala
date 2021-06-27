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
}

// Use
object Printable {
  def print[A](value: A)(implicit p: Printable[A]): Unit = println(
    p.format(value)
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
    def print(implicit p: Printable[A]): Unit = println(
      p.format(value)
    )
  }
}
