package docs.akka.typed

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.testkit.typed.scaladsl.LogCapturing
import org.scalatest.wordspec.AnyWordSpecLike

object StashExprSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike with LogCapturing {

  /** sbt "Test / testOnly *StashExprSpec" */
  import scala.concurrent.Future
  import scala.util.Failure
  import scala.util.Success

  import akka.Done
  import akka.actor.typed.ActorRef
  import akka.actor.typed.Behavior
  import akka.actor.typed.scaladsl.ActorContext
  import akka.actor.typed.scaladsl.Behaviors
  import akka.actor.typed.scaladsl.StashBuffer

  import stashExpr._

  "Stashing expr" must {
    "illustrate stash and unstashAll" in {
      val db = new DB {
        override def save(id: String, value: String): Future[Done] = Future.successful(Done)
        override def load(id: String): Future[String]              = Future.successful("TheValue")
      }

      val dataAccess = spawn(DataAccess(id = "17", db))
      val getProbe   = createTestProbe[String]()
      dataAccess ! DataAccess.Get(getProbe.ref)
      getProbe.expectMessage("TheValue")

      val saveProbe = createTestProbe[Done]()
      dataAccess ! DataAccess.Save("UpdatedValue", saveProbe.ref)
      dataAccess ! DataAccess.Get(getProbe.ref)
      saveProbe.expectMessage(Done)
      getProbe.expectMessage("UpdatedValue")

      dataAccess ! DataAccess.Get(getProbe.ref)
      getProbe.expectMessage("UpdatedValue")
    }
  }
}
