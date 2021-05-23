import scala.math._
// 課題：　https://scala-text.github.io/scala_text/basic.html
object Hello {

  def main(args: Array[String]): Unit = {
    println(s"Hello world, i'm ${x}")
    println(
      s"you need to pay ${floor(interest(rate_by_year, lend_amount, period_by_monnth))}"
    )
    println(lose_by_percent())
  }

  val x: Int = 26
  val rate_by_year: Double = 0.023
  val lend_amount: Int = 3950000
  val period_by_monnth: Int = 8
  val interest = (rate: Double, amount: Int, period: Int) =>
    rate * amount * period / 12

  def lose_by_percent(): Double = {
    val lose: Double = 26400
    val percent: Double = 1.6
    val sell_price: Double = 1980000.0
    val original_price: Double = (lose * 100) / percent
    var reduced_price: Double = 0

    reduced_price = sell_price - original_price - lose
    return (reduced_price * 100) / original_price
  }
}
