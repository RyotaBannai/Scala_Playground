### Akka notes

- The Actor’s `mailbox` is essentially `a message queue with ordering semantics`.
  - The order of multiple messages sent from the `same Actor` is `preserved`, but `can be interleaved` with messages sent by `another Actor`.
- Actor Architecture:
  - For example, if we create an actor named `someActor` from the `user guardian` with `context.spawn(someBehavior, "someActor")`, its reference will include the path `/user/someActor`.
- `Stop an actor`:
  - The recommended pattern is to return `Behaviors.stopped()` inside the actor to stop itself, `usually as a response to some user defined stop message` or `when the actor is done with its job`.
  - `Stopping a child actor`:
    - Technically possible by calling `context.stop(childRef)` `from the parent`, but it’s not possible to stop `arbitrary (non-child) actors` this way.
