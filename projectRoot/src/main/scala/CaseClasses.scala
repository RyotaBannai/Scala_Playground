abstract class Expr

/** case classes */
case class Var(name: String) extends Expr
case class Num(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr

object CaseClasses {

  /** Simplify Operators.
    * @example simplifyTop(UnOp("-", UnOp("-", Var("name")))) => Var(name)
    */
  // pattern => statement
  def simplifyTop(expr: Expr): Expr = expr match {
    case UnOp("-", UnOp("-", e)) => e // 負の負は元のまま
    case BinOp("+", e, Num(0))   => e // 0 の加算は元のまま
    case BinOp("*", e, Num(1))   => e // 1 の乗算は元のまま；
    case _                       => expr // wildcard
  }
}
