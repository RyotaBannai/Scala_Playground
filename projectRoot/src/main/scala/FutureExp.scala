import concurrent.ExecutionContext.global
import concurrent.Future

class FutureExp {}
object FutureExp {
  implicit val ec: scala.concurrent.ExecutionContext =
    scala.concurrent.ExecutionContext.global

  def catchException = {
    val fut = Future { Thread.sleep(100); 21 / 0 }
    val result = fut.value match {
      case Some(x) => x
      case _       => None
    }
  }
  def filtering = {
    val fut = Future { 42 }
    // Success(42)
    val valid = fut.filter(res => res > 0)
    // Failure(java.util.NoSuchElementException: Future.filter predicate is not satisfied)
    val inValid = fut.filter(res => res < 0)

    // Future は widthFilter メソッドも持っているため、for も使える
    val validWithFor = for (res <- fut if res > 0) yield res

    // Collect
    val validWithCollect = fut collect { case res if res > 0 => res + 46 }
  }

  def fallbackToFut = {
    val successFut = Future { Thread.sleep(100); 21 / 1 }
    val failureFut = Future { Thread.sleep(100); 21 / 0 }
    failureFut.fallbackTo(successFut)
  }

  // fallbackTo に渡される失敗は基本的に無視される(SP623)
  val failedFallback =
    (Future { Thread.sleep(100); 21 / 0 }).fallbackTo(Future {
      val res = 42; require(res < 0); res
    })

  // result of recovering is Success type.
  // 以下の場合、もしエラー内容が ArithmeticException でない場合、recover されずもとの Failure type が返される(SP623)
  val recoverWhenFailed = (Future { Thread.sleep(100); 21 / 0 }) recover {
    case ex: ArithmeticException => -1
  }
}
