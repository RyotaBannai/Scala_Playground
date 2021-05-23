import Switch.{asia_or_not, countryList}

object Switch2 {
  def main(args: Array[String]): Unit = {
    // asia_or_not(countryList)
    first_last()
  }

  def first_last(): Unit = {
    for (i <- 1 to 1000) {
      new scala.util.Random(new java.security.SecureRandom()).alphanumeric
        .take(5)
        .toList match {
        case List(a, b, c, d, _) =>
          println(List(a, b, c, d, a).mkString)
        //case_ =>

      }
    }
  }
}
