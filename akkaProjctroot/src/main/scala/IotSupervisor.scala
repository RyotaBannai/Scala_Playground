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

object Device {
  def apply(groupId: String, deviceId: String): Behavior[Command] =
    Behaviors.setup(context => new Device(context, groupId, deviceId))

  sealed trait Command
  // the device actor will use ActorRef when the device actor will use when replaying to the request
  final case class ReadTemperature(requestId: Long, replyTo: ActorRef[RespondTemperature])
      extends Command
  final case class RespondTemperature(requestId: Long, value: Option[Double])
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
    }
  }

  override def onSignal: PartialFunction[Signal, Behavior[Command]] = { case PostStop =>
    context.log.info2("Device actor {}-{} stopped", groupId, deviceId)
    this
  }
} // end of Device class
