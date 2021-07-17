### Akka notes

- `Test`:
  1. `sbt` on console.
  2. `test` inside sbt repl or `"Test / testOnly *YourTestClassName"` if you'd prefer to run single test.
- The Actor’s `mailbox` is essentially `a message queue with ordering semantics`.
  - The order of multiple messages sent from the `same Actor` is `preserved`, but `can be interleaved` with messages sent by `another Actor`.
- Actor Architecture:
  - For example, if we create an actor named `someActor` from the `user guardian` with `context.spawn(someBehavior, "someActor")`, its reference will include the path `/user/someActor`.
- `Stop an actor`:
  - The recommended pattern is to return `Behaviors.stopped()` inside the actor to stop itself, `usually as a response to some user defined stop message` or `when the actor is done with its job`.
  - `Stopping a child actor`:
    - Technically possible by calling `context.stop(childRef)` `from the parent`, but it’s not possible to stop `arbitrary (non-child) actors` this way.
- `Failure handling:
  - The default `supervisor strategy` is `to stop the child`. If you don’t define the strategy `all failures result in a stop`.
- In the world of actors, protocols take the place of interfaces. While it is not possible to formalize general protocols in the programming language, we can compose their most basic element, `messages`. So, we will `start by identifying the messages` we will want to send to device actors.
  - Typically, `messages fall into categories, or patterns`.
    - One of them is `the request-respond message pattern`
- `It can be difficult to determine the definition of the Succuss of Delivery`:
  - `The guarantee of delivery` does not translate to `the domain level guarantee`. We only want to report success once the order has been actually fully processed and persisted. The only entity that can report success is the application itself, since only it has any understanding of the domain guarantees required. `No generalized framework can figure out the specifics of a particular domain and what is considered a success in that domain`.
- `Device manager hierarchy`: we will model the `device manager component` as an actor tree with three levels:
  - `The top level supervisor actor` represents the system component for devices. It is also the entry point to `look up and create device group and device actors`.
  - At the next level, `group actors` each `supervise the device actors` for one group id (e.g. one home). They also provide services, such as `querying temperature readings` from all of the available devices in their group.
  - `Device actors` manage all `the interactions with the actual device sensors`, such as storing temperature readings.
- `Death Watch feature`: allows an actor to watch another actor and be notified if the other actor is stopped. Unlike `supervision`, watching is not limited to parent-child relationships, any actor can watch any other actor as long as it knows the `ActorRef`
  - After a watched actor stops, the watcher receives a `Terminated(actorRef) signal` which also contains the `reference to the watched actor`. The watcher can either handle this message explicitly or will fail with a `DeathPactException`. This latter is useful if the actor can no longer perform its own duties after the watched actor has been stopped. In our case, the group should still function after one device have been stopped, so we need to handle the `Terminated(actorRef) signal`.
- `Dealing with possible scenarios with Iot devices`:
  - `Scenarios`:
    - When a query arrives, the group actor takes a `snapshot` of the existing device actors and will only ask those actors for the temperature.
    - Actors that start up `after` the query arrives are ignored.
    - If an actor in the `snapshot` stops during the query without answering, we will report the fact that it stopped to teh sender of the query message.
  - Device actor states with respect to a temperature query:
    - It has a temperature available: `Temperature`.
    - It has responded, but has no temperature available yet: `TemperatureNotAvailable`.
    - It has stopped before answering: `DeviceNotAvailable`.
    - It did not respond before the deadline: `DeviceTimedOut`.
- `ActorContext is not thread safe`, thus
  - must not accessed by threads from `scala.concurrent.Future` callbacks
  - must not be shared between several actor instances
- When the `guardian` actor stops this will stop the `ActorSystem`.
