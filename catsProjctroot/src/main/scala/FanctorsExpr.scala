import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object FanctorsExpr {
  def run(): Unit = {
    val future: Future[String] =
      Future(123)
        .map(n => n + 1)
        .map(n => n * 2)
        .map(n => s"${n}!")

    Await.result(future, 1.second)
  }
}
