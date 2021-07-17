import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import scala.concurrent.duration._
import scala.collection.immutable.Map

class DeviceGroupWithQuerySpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import iot._

  "DeviceGroup actor" must {

    "be able to collect temperatures from all active devices" in {
      val registeredProve = createTestProbe[DeviceManager.DeviceRegistered]()
      val groupActor      = spawn(DeviceGroup("group"))

      groupActor ! DeviceManager.RequestTrackDevice("group", "device1", registeredProve.ref)
      val deviceActor1 = registeredProve.receiveMessage().device

      groupActor ! DeviceManager.RequestTrackDevice("group", "device2", registeredProve.ref)
      val deviceActor2 = registeredProve.receiveMessage().device

      groupActor ! DeviceManager.RequestTrackDevice("group", "device3", registeredProve.ref)
      val deviceActor3 = registeredProve.receiveMessage().device

      val recordProve = createTestProbe[Device.TemperatureRecorded]()
      deviceActor1 ! Device.RecordTemperature(requestId = 0, 1.0, recordProve.ref)
      recordProve.expectMessage(Device.TemperatureRecorded(requestId = 0))

      deviceActor2 ! Device.RecordTemperature(requestId = 1, 2.0, recordProve.ref)
      recordProve.expectMessage(Device.TemperatureRecorded(requestId = 1))
      // No temperature for device3

      val allTempProbe = createTestProbe[DeviceManager.RespondAllTemperatures]()
      groupActor ! DeviceManager.RequestAllTemperatures(
        requestId = 0,
        groupId = "group",
        allTempProbe.ref
      )
      allTempProbe.expectMessage(
        DeviceManager.RespondAllTemperatures(
          requestId = 0,
          temperatures = Map(
            "device1" -> DeviceManager.Temperature(1.0),
            "device2" -> DeviceManager.Temperature(2.0),
            "device3" -> DeviceManager.TemperatureNotAvailable
          )
        )
      )

    }
  }
}
