package iot

import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.Signal
import akka.actor.typed.ActorRef

import akka.actor.typed.scaladsl.AbstractBehavior
import akka.actor.typed.scaladsl.ActorContext
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.LoggerOps

object IotSupervisor {
  def apply(): Behavior[Nothing] = Behaviors.setup[Nothing](context => new IotSupervisor(context))
}

class IotSupervisor(context: ActorContext[Nothing]) extends AbstractBehavior[Nothing](context) {
  context.log.info("IoT Application started")

  override def onMessage(msg: Nothing): Behavior[Nothing] = {
    // No need to handle any messages
    Behaviors.unhandled
  }

  override def onSignal: PartialFunction[Signal, Behavior[Nothing]] = { case PostStop =>
    context.log.info("IoT Application stopped")
    this
  }
}

object IotApp extends App {
  ActorSystem[Nothing](IotSupervisor(), "iot-system")
}

object DeviceManager {
  def apply(): Behavior[DeviceManager.Command] =
    Behaviors.setup(context => new DeviceManager(context))

  sealed trait Command

  // Registering Device
  final case class RequestTrackDevice(
      groupId: String,
      deviceId: String,
      replyTo: ActorRef[DeviceRegistered]
  ) extends DeviceManager.Command
      with DeviceGroup.Command

  final case class DeviceRegistered(device: ActorRef[Device.Command])

  final case class RequestDeviceList(
      groupId: String,
      deviceId: String,
      replyTo: ActorRef[ReplyDeviceList]
  ) extends DeviceManager.Command
      with DeviceGroup.Command

  final case class ReplyDeviceList(device: ActorRef[Device.Command])

}

class DeviceManager(context: ActorContext[DeviceManager.Command])
    extends AbstractBehavior[DeviceManager.Command](context) {
  override def onMessage(msg: DeviceManager.Command): Behavior[DeviceManager.Command] = ???
}

object DeviceGroup {
  def apply(groupId: String): Behavior[Command] =
    Behaviors.setup(context => new DeviceGroup(context, groupId))

  sealed trait Command

  private final case class DeviceTerminated(
      device: ActorRef[Device.Command],
      groupId: String,
      deviceId: String
  ) extends Command
}

class DeviceGroup(context: ActorContext[DeviceGroup.Command], groupId: String)
    extends AbstractBehavior[DeviceGroup.Command](context) {
  import DeviceGroup._
  import DeviceManager.{DeviceRegistered, ReplyDeviceList, RequestTrackDevice, RequestDeviceList}

  private var deviceIdToActor = Map.empty[String, ActorRef[Device.Command]]

  context.log.info("DeviceGroup {} started", groupId)

  override def onMessage(msg: Command): Behavior[Command] =
    (msg: @unchecked) match {
      case trackMsg @ RequestTrackDevice(`groupId`, deviceId, replyTo) =>
        deviceIdToActor.get(deviceId) match {
          case Some(deviceActor) => replyTo ! DeviceRegistered(deviceActor)
          case None =>
            context.log.info("Creating device actor for {}", trackMsg.deviceId)

            val deviceActor = context.spawn(Device(groupId, deviceId), s"device-$deviceId")
            deviceIdToActor += deviceId -> deviceActor
            replyTo ! DeviceRegistered(deviceActor)
        }
        this
      case RequestTrackDevice(gId, _, _) =>
        context.log.info2(
          "Ignoring TrackDevice request for {}. This actor is responsible for {}.",
          gId,
          groupId
        )
        this
    }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = { case PostStop =>
    context.log.info("DeviceGroup {} stopped", groupId)
    this
  }
}

object Device {
  def apply(groupId: String, deviceId: String): Behavior[Command] =
    Behaviors.setup(context => new Device(context, groupId, deviceId))

  sealed trait Command
  // the device actor will use ActorRef when the device actor will use when replaying to the request
  final case class ReadTemperature(requestId: Long, replyTo: ActorRef[RespondTemperature])
      extends Command
  final case class RespondTemperature(requestId: Long, value: Option[Double])

  // to receive
  final case class RecordTemperature(
      requestId: Long,
      value: Double,
      replyTo: ActorRef[TemperatureRecorded]
  ) extends Command
  // for reply
  final case class TemperatureRecorded(requestId: Long)
} // end of Device object

class Device(context: ActorContext[Device.Command], groupId: String, deviceId: String)
    extends AbstractBehavior[Device.Command](context) {
  import Device._

  var lastTemperatureReading: Option[Double] = None

  context.log.info2("Device actor {}-{} started", groupId, deviceId)

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case ReadTemperature(id, replyTo) =>
        replyTo ! RespondTemperature(id, lastTemperatureReading)
        this

      case RecordTemperature(id, value, replyTo) =>
        context.log.info2("Recorded temperature reading {} with {}", value, id)
        lastTemperatureReading = Some(value)
        replyTo ! TemperatureRecorded(id)
        this
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = { case PostStop =>
    context.log.info2("Device actor {}-{} stopped", groupId, deviceId)
    this
  }
} // end of Device class
