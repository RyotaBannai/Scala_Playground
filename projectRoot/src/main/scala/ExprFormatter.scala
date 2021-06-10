import Element.elem

sealed abstract class Expr

/** case classes */
case class Var(name: String) extends Expr
case class Num(num: Double) extends Expr
// Unary 1 被演算子
case class UnOp(operator: String, arg: Expr) extends Expr
// Binary 2 被演算子
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

class ExprFormatter {
  // 優先順位の昇順でグループにまとめた演算子を格納
  private val opGroups = Array(
    Set("|", "||"),
    Set("&", "&&"),
    Set("^"),
    Set("==", "!="),
    Set("<", "<=", ">", "=>"),
    Set("+", "-"),
    Set("*", "%")
  )
  // 演算子から優先順位を導き出すマップ
  // 2重ループで combination のようなことが実現できる
  private val precedence = {
    val assocs = for {
      i <- 0 until opGroups.length
      op <- opGroups(i)
    } yield op -> i // key -> value // Vector Tye
    assocs.toMap
  }

  // 単行演算子の優先順位はどの二項演算子よりも優先度が高い !, -, +
  private val unaryPrecedence = opGroups.length
  private val fractionPrecedence = -1 // 徐算演算子は特別な値を与える

  private def format(e: Expr, enclPrec: Int): Element = e match {
    case Var(name) => elem(name)
    case Num(num) =>
      def stripDot(s: String) =
        if (s endsWith ".0") s.substring(0, s.length - 2) else s
      elem(stripDot(num.toString))
    case UnOp(op, arg) => elem(op) beside format(arg, unaryPrecedence)
    case BinOp("/", left, right) =>
      val top = format(left, fractionPrecedence)
      val bot = format(right, fractionPrecedence)
      val line = elem('-', top.width max bot.width, 1)
      val frac = top above line above bot
      if (enclPrec != fractionPrecedence) frac
      else elem(" ") beside frac beside elem(" ")
    // arg が二項演算であれば括弧で囲む
    // 現在の演算子(opPrec)の優先順位が、この二項演算式を被演算子とする式(enclPrec)の優先順位より低ければ、oper を括弧で囲んで返す
    case BinOp(op, left, right) =>
      val opPrec = precedence(op)
      val l = format(left, opPrec)
      val r = format(right, opPrec)
      val oper = l beside elem(" " + op + " ") beside r
      if (enclPrec <= opPrec) oper
      else elem("(") beside oper beside elem(")")
  }

  def format(e: Expr): Element = format(e, 0)
}

object ExprFormatter {}
