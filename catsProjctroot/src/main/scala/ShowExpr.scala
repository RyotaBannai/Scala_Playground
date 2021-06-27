import cats._, cats.syntax.show._
import java.util.Date
import cats.instances.int._ // Instance
import cats.instances.string._ // Instance

// Type Class: equivalent to Printable. In import cats._
// trait Show[A] {
//   def show(value: A): String
// }

// Instances: In cats.syntax.show._
object Instances {
//   val showInt: Show[Int] = Show.apply[Int]
//   val showString: Show[String] = Show.apply[String]

  // custom Show
  // implicit val dateShow: Show[Date] =
  //   new Show[Date] {
  //     def show(date: Date): String =
  //       s"${date.getTime}ms since the epoch."
  //   }

  // custom show using Show singleton
  implicit val dateShow: Show[Date] =
    Show.show(date => s"${date.getTime}ms since the epoch.")
  /*
  import cats._, cats.syntax.show._
  import java.util.Date
  new Date().show
   */

  implicit val catShow: Show[Cat] =
    Show.show[Cat](cat => {
      val name = cat.name.show
      val age = cat.age.show
      val color = cat.color.show
      s"$name is a $age year-old $color cat."
    })
}

/*
import cats._, cats.syntax.show._
import cats.instances.int._
val shownInt = 123.show
> shownInt: String = "123"
 */
