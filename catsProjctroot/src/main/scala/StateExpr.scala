import cats.data.State
import cats.syntax.applicative._ // for pure

object StateExpr {
  val step1 = State[Int, String] { num =>
    val ans = num + 1
    (ans, s"Result of step1: $ans")
  }

  val step2 = State[Int, String] { num =>
    val ans = num * 2
    (ans, s"Result of step2: $ans")
  }

  val both = for {
    a <- step1
    b <- step2
  } yield (a, b)

  val (state, result) = both.run(20).value
}

/** post-order expression @example '1 2 +'
  * 1. when we see a number, we push it onto the stack
  * 2. when we see an operator, we pop two operands off the stack,
  *    operate on them, and push the result in their place
  */
object PostOrderCalc {
  type CalcState[A] = State[List[Int], A]

  def evalOne(sym: String): CalcState[Int] = sym match {
    case "+" => operator(_ + _)
    case "-" => operator(_ - _)
    case "*" => operator(_ * _)
    case "/" => operator(_ / _)
    case num => operand(num.toInt)
  }

  def operand(num: Int): CalcState[Int] =
    State[List[Int], Int] { stack =>
      (num :: stack, num)
    }

  def operator(func: (Int, Int) => Int): CalcState[Int] =
    State[List[Int], Int] {
      // pop two operands off the stack
      case b :: a :: tail =>
        val ans = func(a, b)
        (ans :: tail, ans)
      case _ => sys.error("Failed")
    }
  /*
     evalOne("42").runA(Nil).value
     // Int = 42
     evalOne("42").runS(11 :: 22 :: 33 :: Nil).value
     // List[Int] = List(42, 11, 22, 33)
   */

  def program = for {
    _ <- evalOne("1")
    _ <- evalOne("2")
    ans <- evalOne("+")
  } yield ans
  /*
   program.runA(Nil).value
   // Int = 3
   */

  // We can use evalAll to conveniently evaluate multi-stage expressions:
  def evalAll(inputs: List[String]): CalcState[Int] =
    inputs.foldLeft(0.pure[CalcState]) { (acc, head) =>
      acc.flatMap(_ => evalOne(head))
    }
  /*
    val multistageProgram = evalAll(List("1", "2", "+", "4", "5"))
    multistageProgram.runS(Nil).value
    List[Int] = List(5, 4, 3)

    val biggerProgram = for {
     _ <- evalAll(List("1","2","+"))
     _ <- evalAll(List("3","4","+"))
     ans <- evalOne("*")
     } yield ans

     biggerProgram.runA(Nil).value
     Int = 21
   */

  def evalInput(input: String): Int =
    evalAll(input.split(" ").toList).runA(Nil).value

  /*
    evalInput("1 2 + 3 4 + *")
    Int = 21
   */
}
