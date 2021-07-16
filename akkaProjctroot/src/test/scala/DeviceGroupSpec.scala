//#device-read-test
import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike

class DeviceGroupSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import iot._

  "DeviceGroup actor" must {

    "be able to register a device actor" in {
      val probe      = createTestProbe[DeviceManager.DeviceRegistered]()
      val groupActor = spawn(DeviceGroup("group"))

      groupActor ! DeviceManager.RequestTrackDevice("group", "device1", probe.ref)
      val registered1  = probe.receiveMessage()
      val deviceActor1 = registered1.device

      groupActor ! DeviceManager.RequestTrackDevice("group", "device1", probe.ref)
      val registeredTheSame = probe.receiveMessage()
      deviceActor1 should ===(registeredTheSame.device)

      // another deviceId
      groupActor ! DeviceManager.RequestTrackDevice("group", "device2", probe.ref)
      val registered2  = probe.receiveMessage()
      val deviceActor2 = registered2.device

      // Check that the device actor are working
      val recordProbe = createTestProbe[Device.TemperatureRecorded]()
      val readProbe   = createTestProbe[Device.RespondTemperature]()

      // control first device
      deviceActor1 ! Device.RecordTemperature(requestId = 1, 24.0, recordProbe.ref)
      recordProbe.expectMessage(Device.TemperatureRecorded(requestId = 1))

      deviceActor1 ! Device.ReadTemperature(requestId = 2, readProbe.ref)
      val res1 = readProbe.receiveMessage()
      res1.requestId should ===(2)
      res1.value should ===(Some(24.0))

      // control second device
      deviceActor2 ! Device.RecordTemperature(requestId = 3, 55.0, recordProbe.ref)
      recordProbe.expectMessage(Device.TemperatureRecorded(requestId = 3))

      deviceActor2 ! Device.ReadTemperature(requestId = 4, readProbe.ref)
      val res2 = readProbe.receiveMessage()
      res2.requestId should ===(4)
      res2.value should ===(Some(55.0))

    }
    //#device-read-test
  }
}
