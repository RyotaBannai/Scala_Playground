package gui

import swing._

class SwingPlay {}
object SwingPlay extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "First Swing App"
    val button = new Button {
      text = "Click me"
    }
    listenTo(button)
    var nClicks = 0
    // add handlers to reactions. (install handler on to the top of stack)
    // 'b' references button object
    // swing execute the the handler added to reactions first!
    // remove handler from the stack by '-=' method.
    reactions += { case event.ButtonClicked(b) =>
      println("Clicked..")
      nClicks += 1
      lable.text = "number of button clicks: " + nClicks
    }

    val lable = new Label {
      text = "No button clicks registered"
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents += button
      contents += lable
      border = Swing.EmptyBorder(30, 30, 10, 30)
    }
  }

}
// new Frame {
//   title = "Hello world"

//   contents = new FlowPanel {
//     contents += new Label("Launch rainbows:")
//     contents += new Button("Click me") {
//       reactions += { case event.ButtonClicked(_) =>
//         println("All the colours!")
//       }
//     }
//   }

//   pack()
//   centerOnScreen()
//   open()
// }
