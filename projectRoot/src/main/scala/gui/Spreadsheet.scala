package gui

import swing._

class Model(val height: Int, val width: Int) {
  case class Cell(row: Int, column: Int)
  val cells = Array.ofDim[Cell](height, width)
  for (i <- 0 until height; j <- 0 until width)
    cells(i)(j) = new Cell(i, j)
}

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

}

object SpreadsheetExpr extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "ScalaSheet"
    contents = new Spreadsheet(100, 26)
  }
}
