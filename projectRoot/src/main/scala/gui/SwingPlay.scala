package gui

import swing._

class SwingPlay {}
object SwingPlay extends SimpleSwingApplication {
  def top = new MainFrame {
    title = "First Swing App"
    contents = new Button {
      text = "Click me"
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
