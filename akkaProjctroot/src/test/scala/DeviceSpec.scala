//#device-read-test
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import iot._, Device._

  "Device actor" must {

    "reply with empty reading if no temperature is known" in {
      val probe       = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("group", "device"))

      deviceActor ! ReadTemperature(requestId = 42, probe.ref)
      val response = probe.receiveMessage()
      response.requestId should ===(42)
      response.value should ===(None)
    }
    //#device-read-test
  }
}
