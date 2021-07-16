//#device-read-test
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import iot._, Device._

  "Device actor" must {

    "reply with empty reading if no temperature is known" in {
      val recordProbe = createTestProbe[TemperatureRecorded]()
      val readProbe   = createTestProbe[RespondTemperature]()
      val deviceActor = spawn(Device("group", "device"))

      // first round
      deviceActor ! RecordTemperature(requestId = 1, 24.0, recordProbe.ref)
      recordProbe.expectMessage(Device.TemperatureRecorded(requestId = 1))

      deviceActor ! ReadTemperature(requestId = 2, readProbe.ref)
      val res1 = readProbe.receiveMessage()
      res1.requestId should ===(2)
      res1.value should ===(Some(24.0))

      // second round
      deviceActor ! RecordTemperature(requestId = 3, 55.0, recordProbe.ref)
      recordProbe.expectMessage(Device.TemperatureRecorded(requestId = 3))

      deviceActor ! ReadTemperature(requestId = 4, readProbe.ref)
      val res2 = readProbe.receiveMessage()
      res2.requestId should ===(4)
      res2.value should ===(Some(55.0))
    }
    //#device-read-test
  }
}
