package gui

import swing._
import event._
import util.parsing.combinator._

class Model(val height: Int, val width: Int) {
  case class Cell(row: Int, column: Int) {
    var formula: Formula = Empty
    override def toString = formula.toString
  }
  val cells = Array.ofDim[Cell](height, width)
  for (i <- 0 until height; j <- 0 until width)
    // それぞれのセルに Cell のインスタンスを追加.
    cells(i)(j) = new Cell(i, j)
} // end of Model

class Spreadsheet(val height: Int, val width: Int) extends ScrollPane {
  val cellModel = new Model(height, width)
  import cellModel._

  val table = new Table(height, width) {
    rowHeight = 25
    autoResizeMode = Table.AutoResizeMode.Off
    showGrid = true
    gridColor = new java.awt.Color(150, 150, 150)

    reactions += { case TableUpdated(table, rows, column) =>
      // 単一のセルを編集したら 'Range 1 to 1' のようになる.
      for (row <- rows)
        cells(row)(column).formula = FormulaParsers.parse(userData(row, column))
    }

    override protected def rendererComponent(
        isSelected: Boolean,
        focused: Boolean,
        row: Int,
        column: Int
    ): Component = if (hasFocus) new TextField(userData(row, column))
    else
      // フォーカスが当たっていないセルは編集できないため label
      new Label(cells(row)(column).toString) {
        xAlignment = Alignment.Right
      }

    def userData(row: Int, column: Int): String = {
      // this(x, y) は constructor 呼び出しではなく、現在の Table の apply メソッドを呼び出す
      val v = this(row, column)
      if (v == null) "" else v.toString
    }
  }

  // 行番号を表示.
  val rowHeader = new ListView((0 until height) map (_.toString)) {
    fixedCellWidth = 30
    fixedCellHeight = table.rowHeight
  }
  viewportView = table
  rowHeaderView = rowHeader
} // end of Spreadsheet

trait Formula

/** A3 などのセル座標 */
case class Coord(row: Int, column: Int) extends Formula {
  override def toString = ('A' + column).toChar.toString + row
}
case class Range(c1: Coord, c2: Coord) extends Formula {
  override def toString = c1.toString + ":" + c2.toString
}
case class Number(value: Double) extends Formula {
  override def toString = value.toString
}

/** 賛成数、Total などのテキストによるラベル */
case class Textual(value: String) extends Formula {
  override def toString = value
}

/** sum(A1, A2) などの関数適用 */
case class Application(function: String, arguments: List[Formula])
    extends Formula {
  override def toString = function + arguments.mkString("(", ",", ")")
}
object Empty extends Textual("")

object FormulaParsers extends RegexParsers {
  def ident: Parser[String] = """[a-zA-Z_]\w*""".r // 先頭が数字以外の識別子
  def decimal: Parser[String] = """-?\d+(\.\d*)?""".r // 小数点以下はオプショナル
  def cell: Parser[Coord] = """[a-zA-Z_]\d+""".r ^^ { s =>
    val column = s.charAt(0).toUpper - 'A' // 最大 26 カラムしか読み取ることができないが..
    val row = s.substring(1).toInt
    Coord(row, column)
  }
  def range: Parser[Range] = cell ~ ":" ~ cell ^^ { case c1 ~ ":" ~ c2 =>
    Range(c1, c2)
  }
  def number: Parser[Number] = decimal ^^ (d => Number(d.toDouble))
  def application: Parser[Application] =
    ident ~ "(" ~ repsep(expr, ",") ~ ")" ^^ { case f ~ "(" ~ ps ~ ")" =>
      Application(f, ps)
    }
  def expr: Parser[Formula] = range | cell | number | application
  // 先頭が　= から始まるもの以外. = から始まるものは数式と見なす.
  def textual: Parser[Textual] = """[^=].*""".r ^^ Textual
  def formula: Parser[Formula] = number | textual | "=" ~> expr

  def parse(input: String): Formula = parseAll(formula, input) match {
    case Success(result, next) => result
    case f: NoSuccess          => Textual("[" + f.msg + "]")
  }
} // end of FormulaParsers

object SpreadsheetExpr extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "ScalaSheet"
    contents = new Spreadsheet(100, 26)
  }
}
