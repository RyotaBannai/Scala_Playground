import scala.io.Source

class XmlExpr {}
object XmlExpr {
  // val readmeText: Iterator[String] = Source.fromResource("readme.txt").getLines
  // Coca cola thermometer
  abstract class CCTherm {
    val description: String
    val yearMade: Int
    val dateObtained: String
    val bookPrice: Int
    val purchasePrice: Int
    val condition: Int
    override def toString = description

    def toXML = <cctherm>
      <description version="1.0">{description}</description>
      <yearMade>{yearMade}</yearMade>
      <dateObtained>{dateObtained}</dateObtained>
      <bookPrice>{bookPrice}</bookPrice>
      <purchasePrice>{purchasePrice}</purchasePrice>
      <condition>{condition}</condition>
    </cctherm>
  }

  def fromXML(node: scala.xml.Node): CCTherm = new CCTherm {
    val description = (node \ "description").text
    val yearMade = (node \ "yearMade").text.toInt
    val dateObtained = (node \ "dateObtained").text
    val bookPrice = (node \ "bookPrice").text.toInt
    val purchasePrice = (node \ "purchasePrice").text.toInt
    val condition = (node \ "condition").text.toInt
  }

  // check 20.5 for anonymous class with trait.(SP559)
  def create = new CCTherm {
    val description = "hot dog #5"
    val yearMade = 1952
    val dateObtained = "march 14, 2006"
    val bookPrice = 2199
    val purchasePrice = 500
    val condition = 9
  }
}
/*
// ref https://stackoverflow.com/questions/27360977/how-to-read-files-from-resources-folder-in-scala
import XmlExpr._
val x = create.toXML
scala.xml.XML.save("src/main/resources/test.xml", x)

// getClass.getResource("/data.xml") はコンパイル後のパスを取得.
 */
