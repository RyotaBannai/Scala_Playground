package gui

import swing._, event._
import util.parsing.combinator._

class Model(val height: Int, val width: Int) extends Evaluator with Arithmetic {

  /** ValueChanged: Spreadsheet の reactions でも view の更新に使用
    * Cell が使うときは、内部データ(value)を書き換えるために使用
    */
  case class ValueChanged(cell: Cell) extends event.Event
  case class Cell(row: Int, column: Int) extends Publisher {
    private var f: Formula = Empty
    def formula = f
    def formula_=(f: Formula) = {
      // 先にサブを解除して、その後でサブ対象になる Cell をセットするため formula を書き換える.
      for (c <- references(formula)) deafTo(c)
      this.f = f
      for (c <- references(formula)) listenTo(c)
      value = evaluate(f)
    }

    private var v: Double = 0
    def value = v
    def value_=(w: Double) = {
      if (!(v == w || v.isNaN && w.isNaN)) {
        v = w
        // event を発行するのは、value をセットする時
        publish(ValueChanged(this))
      }
    }
    override def toString = formula match {
      case Textual(s) => s
      case _          => value.toString
    }

    // Cell が他の Cell の通知をキャッチして再計算
    reactions += { case ValueChanged(c) =>
      if (c != this)
        value = evaluate(formula)
    }
  } // end of Cell

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

    override protected def rendererComponent(
        isSelected: Boolean,
        focused: Boolean,
        row: Int,
        column: Int
    ): Component =
      // isSelected => whole row
      // focused => true when cursor is mounted on the cell.
      if (focused)
        new TextField(userData(row, column))
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

    reactions += {
      case TableUpdated(table, rows, column) =>
        // 単一のセルを編集したら 'Range 1 to 1' のようになる.
        for (row <- rows)
          cells(row)(column).formula =
            FormulaParsers.parse(userData(row, column))
      case ValueChanged(cell) => updateCell(cell.row, cell.column)
    }
    for (row <- cells; cell <- row) listenTo(cell)
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

trait Evaluator { this: Model =>
  def evaluate(e: Formula): Double = try {
    e match {
      case Coord(row, column) => cells(row)(column).value
      case Number(v)          => v
      case Textual(_)         => 0
      case Application(function, arguments) =>
        val argvals = arguments flatMap evalList
        operations(function)(argvals)
    }
  } catch {
    case ex: Exception => Double.NaN
  }

  type Op = List[Double] => Double
  val operations = new collection.mutable.HashMap[String, Op]
  private def evalList(e: Formula): List[Double] = e match {
    case Range(_, _) => references(e) map (_.value)
    case _           => List(evaluate(e))
  }

  /** 数式が参照する全てのセルの値を計算 */
  def references(e: Formula): List[Cell] = e match {
    case Coord(row, column) => List(cells(row)(column))
    case Range(Coord(r1, c1), Coord(r2, c2)) =>
      for (row <- (r1 to r2).toList; column <- c1 to c2)
        yield cells(row)(column)
    // 個々の引数式が参照するセルを一つのリストに連結
    case Application(function, arguments) => arguments flatMap references
    case _                                => List()
  }
} // end of Evaluator

trait Arithmetic { this: Evaluator =>
  operations += (
    "add" -> { case List(x, y) => x + y },
    "sub" -> { case List(x, y) => x - y },
    "div" -> { case List(x, y) => x / y },
    "mul" -> { case List(x, y) => x * y },
    "mod" -> { case List(x, y) => x % y },
    "sum" -> { xs => (0.0 /: xs)(_ + _) },
    "prod" -> { xs => (1.0 /: xs)(_ * _) },
  )
} // end of Arithmetic

object SpreadsheetExpr extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "ScalaSheet"
    contents = new Spreadsheet(100, 26)
  }
}
