### Akka notes

- The Actor’s `mailbox` is essentially `a message queue with ordering semantics`.
  - The order of multiple messages sent from the `same Actor` is `preserved`, but `can be interleaved` with messages sent by `another Actor`.
- Actor Architecture:
  - For example, if we create an actor named `someActor` from the `user guardian` with `context.spawn(someBehavior, "someActor")`, its reference will include the path `/user/someActor`.
