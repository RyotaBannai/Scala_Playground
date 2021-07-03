import cats.Eval

object EvalExpr {
  // call-by-value: eager & memorized : Now
  val x = {
    println("Computing X")
    math.random()
  }

  // call-by-name: lazy & not memorized : Always
  def y = {
    println("Computing Y")
    math.random()
  }

  // call-by-need: lazy & memorized : Later
  lazy val z = {
    println("Computing Z")
    math.random()
  }

  val now = Eval.now(math.random() + 1000)
  val always = Eval.always(math.random() + 1000)
  val later = Eval.later(math.random() + 1000)

  // stack safe foldRight
  def foldRightEval[A, B](as: List[A], acc: Eval[B])(
      fn: (A, Eval[B]) => Eval[B]
  ): Eval[B] =
    as match {
      case head :: tail =>
        Eval.defer(fn(head, foldRightEval(tail, acc)(fn)))
      case Nil =>
        acc
    }

  def foldRight[A, B](as: List[A], acc: B)(fn: (A, B) => B): B =
    foldRightEval(as, Eval.now(acc)) { (a, b) =>
      b.map(fn(a, _))
    }.value

  // foldRight((1 to 100000).toList, 0L)(_ + _)
}
