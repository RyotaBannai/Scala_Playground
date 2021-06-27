import cats._, syntax.eq._
import java.util.Date
import cats.instances.long._ // for Eq
import cats.instances.int._ // for === method
import cats.instances.string._ // for === method

object Comparison {
  implicit val dateEq: Eq[Date] =
    Eq.instance[Date] { (date1, date2) =>
      date1.getTime === date2.getTime
    }

  implicit val catEq: Eq[Cat] =
    Eq.instance[Cat] { (cat1, cat2) =>
      (cat1.name === cat2.name) &&
      (cat1.age === cat2.age) &&
      (cat1.color === cat2.color)
    }
}

/* @example
val x = new Date()
val x: java.util.Date = Sun Jun 27 23:41:54 JST 2021

val y = new Date()
val y: java.util.Date = Sun Jun 27 23:42:00 JST 2021

x === y
> false
 */
