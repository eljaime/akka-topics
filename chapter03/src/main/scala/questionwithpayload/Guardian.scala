package questionwithpayload

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

object Guardian {

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      val manager: ActorRef[Manager.Command] =
        context.spawn(Manager(), "manager-1")
      Behaviors.receiveMessage { message =>
        manager ! Manager.Delegate(
          List(
            Request(
              "0001",
              "Greetings from Spain",
              "Hola $firstName, como estas"),
            Request(
              "0002",
              "Greetings from UK",
              "Tally-ho $firstName, how do you do")))
        Behaviors.same
      }
    }

  sealed trait Command

  final case object Start extends Command
}
