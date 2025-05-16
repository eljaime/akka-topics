package questionwithpayload

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.duration.SECONDS
import scala.util.{ Failure, Random, Success }

object Manager {

  sealed trait Command

  final case class Delegate(texts: List[Request]) extends Command

  final case class Report(outline: String) extends Command

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      implicit val timeout: Timeout = Timeout(1, SECONDS)

      def auxCreateRequest(request: Request)(
          replyTo: ActorRef[Worker.Response])(
          customer: ActorRef[Customer.Command]): Worker.Dispatch =
        Worker.Dispatch(request, replyTo, customer)

      Behaviors.receiveMessage { message =>
        message match {
          case Delegate(requests) =>
            requests.foreach { request =>
              val workerActorId = Random.nextInt(100)
              val worker: ActorRef[Worker.Command] =
                context.spawn(Worker(), s"worker-${workerActorId}")

              val customerActorId = Random.nextInt(100)
              val customer: ActorRef[Customer.Command] =
                context.spawn(
                  Customer(),
                  s"customer-${customerActorId}")

              // Correct usage of context.ask
              context.ask(
                worker,
                (replyTo: ActorRef[Worker.Response]) =>
                  auxCreateRequest(request)(replyTo)(customer)) {
                case Success(_) =>
                  Report(
                    s"$request dispatched by ${worker.path.name}")
                case Failure(ex) =>
                  Report(
                    s"reading '$request' has failed with [${ex.getMessage}]")
              }
            }
            Behaviors.same

          case Report(outline) =>
            context.log.info(outline)
            Behaviors.same
        }
      }
    }
}
