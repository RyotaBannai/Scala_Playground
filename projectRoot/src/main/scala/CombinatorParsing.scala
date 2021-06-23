package combinator_parsing

import util.parsing.combinator._
import java.io.{FileReader}

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

/** Extends primitive matching */
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

class JSONParser extends JavaTokenParsers {
  def value: Parser[Any] =
    obj | arr | stringLiteral | floatingPointNumber | "null" | "true" | "false"
  def obj: Parser[Any] = "{" ~ repsep(member, ",") ~ "}"
  def arr: Parser[Any] = "[" ~ repsep(value, ",") ~ "]"
  def member: Parser[Any] = stringLiteral ~ ":" ~ value
}

// src/main/resources/sample.json
object ParseJsonExpr extends JSONParser {
  def main(args: Array[String]) = {
    val reader = new FileReader(args(0))
    println(parseAll(value, reader)) // 第二引数に入力リーダーを取ることができる
  }
}

class JSONParserWithMapping extends JavaTokenParsers {
  def obj: Parser[Any] = "{" ~> repsep(member, ",") <~ "}" ^^ (Map() ++ _)
  def arr: Parser[Any] = "[" ~> repsep(value, ",") <~ "]"
  def member: Parser[(String, Any)] = stringLiteral ~ ":" ~ value ^^ {
    case name ~ ":" ~ value => (name, value)
  }
  def value: Parser[Any] = (obj
    | arr
    | stringLiteral
    | floatingPointNumber ^^ (_.toDouble)
    | "null" ^^ (x => null)
    | "true" ^^ (x => true)
    | "false" ^^ (x => false))
}

// src/main/resources/sample.json
object ParseJsonAndMappingExpr extends JSONParserWithMapping {
  def main(args: Array[String]) = {
    val reader = new FileReader(args(0))
    println(parseAll(value, reader))
  }
}
