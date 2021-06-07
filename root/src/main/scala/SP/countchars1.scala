import scala.io.Source;

// object Main {
//   // NOTE: 行数ではなく、行数を表す数値の'桁数'を計算
//   def widthOfLength(s: String) = s.length.toString().length
//   def main(args: Array[String]): Unit = {
//     if (args.length > 0) {
//       // NOTE: getLines returns Iterator[String]
//       // Iterator は Generator と同様、一度一周すると使用できなくなる.
//       val lines = Source.fromFile(args(0)).getLines().toList
//       var longestLine =
//         lines.reduceLeft((a, b) => if (a.length > b.length) a else b)
//       var maxWidth = widthOfLength(longestLine)

//       for (line <- lines) {
//         val numSpaces = maxWidth - widthOfLength(line);
//         val padding = " " * numSpaces
//         println(padding + line.length + " | " + line)
//       }
//     } else
//       Console.err.println("please enter filename")
//   }
// }
