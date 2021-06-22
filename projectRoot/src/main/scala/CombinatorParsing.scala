package combinator_parsing

import util.parsing.combinator._

class Arith extends JavaTokenParsers {
  // プリミティブな floatingPointNumber parser
  // ~ は合成を表現
  // 繰り返し {...} は rep(...) で表現
  // オプショナル [...]は opt(...) で表現
  def expr: Parser[Any] = term ~ rep("+" ~ term | "-" ~ term)
  def term: Parser[Any] = factor ~ rep("*" ~ factor | "/" ~ factor)
  def factor: Parser[Any] = floatingPointNumber | "(" ~ expr ~ ")"
  /*
  def floatingPointNumber: Parser[String] =
    """-?(\d+(\.\d*)?|\d*\.\d+)([eE][+-]?\d+)?[fFdD]?""".r
   */
}

object MyParsers extends RegexParsers {
  val ident: Parser[String] = """[a-zA-Z_]\w*""".r
}

object ParseExpr extends Arith {
  def main(args: Array[String]) = {
    println("input; " + args(0))
    println(parseAll(expr, args(0)))
  }
}
/*
((2~List((*~(((~((3~List())~List((+~(7~List())))))~)))))~List())
() はそれぞれは一つの要素として認識される
 */
