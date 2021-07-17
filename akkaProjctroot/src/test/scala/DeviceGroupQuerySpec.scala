import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.wordspec.AnyWordSpecLike
import scala.concurrent.duration._
import scala.collection.immutable.Map

class DeviceGroupQuerySpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {
  import iot._

  "DeviceGroupQuery actor" must {

    "return temperature value for working devices" in {
      val requester = createTestProbe[DeviceManager.RespondAllTemperatures]()

      val device1 = createTestProbe[Device.Command]()
      val device2 = createTestProbe[Device.Command]()

      val deviceIdToActor = Map("device1" -> device1.ref, "device2" -> device2.ref)

      val queryActor = spawn(
        DeviceGroupQuery(
          deviceIdToActor,
          requestId = 1,
          requester = requester.ref,
          timeout = 200.millis
        )
      )

      // expectMessageType: Expect a message of type T to arrive within max or fail.
      // -> Device actor expects Device.ReadTemperature type message from DeviceGroupQuery actor to receive.
      device1.expectMessageType[Device.ReadTemperature]
      device2.expectMessageType[Device.ReadTemperature]

      queryActor ! DeviceGroupQuery.WrappedRespondTemperature(
        Device.RespondTemperature(requestId = 0, "device1", Some(2.0))
      )

      // device2.stop()

      requester.expectMessage(
        DeviceManager.RespondAllTemperatures(
          requestId = 1,
          temperatures = Map(
            "device1" -> DeviceManager.Temperature(2.0),
            "device2" -> DeviceManager.DeviceTimedOut // or DeviceManager.DeviceNotAvailable
          )
        )
      )
    }
  }
}
