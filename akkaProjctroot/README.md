### Akka notes

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
