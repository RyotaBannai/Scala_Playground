import math.{E, Pi}

object PatternMatchExp {

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

  // 定数パターンにマッチさせる
  def describeValue(x: Any) = x match {
    case 5       => "five"
    case true    => "truth"
    case "hello" => "hi!"
    case Nil     => "the empty list" //  Nil 以外にも List() にもマッチ. Array() にはマッチしない
    case _       => "something else"
  }

  val pi = Pi
  def unreachableCode(x: Any) = x match {
    case `pi` => "this is just value matches anything wo backquote."
    case _    => "unreachable case."
  }

  def generalSize(x: Any) = x match {
    case s: String    => s.length
    case m: Map[_, _] => m.size
    case _            => -1
  }

  def isStringArray(x: Any) = x match {
    case a: Array[String] => "yes"
    case _                => "no"
  }

  // Pattern guard begins with if after alternative.
  /*
   * => simplifyAdd(BinOp("+", Var("x"), Var("x")))
   * <= BinOp(*,Var(x),Num(2.0))
   */
  def simplifyAdd(e: Expr) = e match {
    case BinOp("+", x, y) if x == y => BinOp("*", x, Num(2))
    case _                          => e
  }

  /*
   * => simplifyAll(UnOp("-", UnOp("-", UnOp("-", UnOp("-", Var("name"))))))
   * <= Var(name)
   */
  def simplifyAll(expr: Expr): Expr = expr match {
    case UnOp("-", UnOp("-", e)) => simplifyAll(e)
    case BinOp("+", e, Num(0))   => simplifyAll(e)
    case BinOp("*", e, Num(1))   => simplifyAll(e)
    case UnOp(op, e)             => UnOp(op, simplifyAll(e))
    case BinOp(op, l, r)         => BinOp(op, simplifyAll(l), simplifyAll(r))
    case _                       => expr
  }

  // uncheck 'match may not be exhaustive' warning for sealed case class
  def describe(e: Expr): String = (e: @unchecked) match {
    case Num(_) => "a number"
    case Var(_) => "a variable"
  }

  /*
   * show(capitals get "France")
   * show(capitals get "North Pole")
   */
  def capitals = Map("France" -> "Paris", "Japan" -> "Tokyo")
  def show(x: Option[String]) = x match {
    case Some(s) => s
    case None    => "?"
  }

  /*
   * case sequence example.
   */
  val withDefault: Option[Int] => Int = {
    case Some(x) => x
    case None    => 0
  }

  /*
   * => second.isDefinedAt(List()) <= false
   * => second.isDefinedAt(List(1,2,3)) <= true
   */
  val second: PartialFunction[List[Int], Int] = { case x :: y :: _ =>
    y
  }

  // => for(Some(fruit) <- results) println(fruit) <= Apple\nOrange
  val results = List(Some("Apple"), None, Some("Orange"))

}
