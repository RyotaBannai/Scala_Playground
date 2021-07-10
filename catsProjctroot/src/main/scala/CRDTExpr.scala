import scala.language.postfixOps

/** CRDTs: Commutative Replicated Data Types
  */
object CRDTExpr {
  final case class GCounter(counters: Map[String, Int]) {
    def increment(machine: String, amount: Int) = {
      val value = amount + counters.getOrElse(machine, 0)
      GCounter(counters + (machine -> amount))
    }

    // def merge(that: GCounter): GCounter =
    //   GCounter(
    //     that.counters map { case (k, v) =>
    //       (k -> (v max this.counters.getOrElse(k, 0)))
    //     } toMap
    //   )

    /* get all possible element by merging two maps*/
    def merge(that: GCounter): GCounter = GCounter(
      that.counters ++ this.counters.map { case (k, v) =>
        k -> (v max that.counters.getOrElse(k, 0))
      }
    )
    def total: Int = counters.values.sum
  }
}
