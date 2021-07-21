package docs.akka.typed

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import akka.actor.testkit.typed.scaladsl.LogCapturing
import org.scalatest.wordspec.AnyWordSpecLike

object FSMExprSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike with LogCapturing {

  /** sbt "Test / testOnly *FSMExprSpec" */

  import akka.actor.typed.{ActorRef, Behavior}
  import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer}

  import fsmExpr._

  "FSM expr" must {
    "work" in {
      val buncher = spawn(Buncher())
      val probe   = createTestProbe[Buncher.Batch]()

      buncher ! Buncher.SetTarget(probe.ref)
      buncher ! Buncher.Queue(42)
      buncher ! Buncher.Queue(43)
      probe.expectMessage(Buncher.Batch(Seq(42, 43)))

      buncher ! Buncher.Queue(44)
      buncher ! Buncher.Flush
      probe.expectMessage(Buncher.Batch(Seq(44)))

      buncher ! Buncher.Queue(45)
      probe.expectMessage(Buncher.Batch(Seq(45)))
    }
  }
}
