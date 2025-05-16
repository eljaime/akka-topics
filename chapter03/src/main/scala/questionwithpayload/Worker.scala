package questionwithpayload

import akka.actor.typed.{ ActorRef, Behavior }
import akka.actor.typed.scaladsl.Behaviors
import akka.util.Timeout

import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import scala.util.Random

object Worker {

  sealed trait Command
  final case class Dispatch(
      request: Request,
      replyTo: ActorRef[Worker.Response],
      customer: ActorRef[Customer.Command])
      extends Command

  sealed trait Response
  final case object Done extends Response

  def apply(): Behavior[Command] =
    Behaviors.setup { context =>
      implicit val timeout
          : Timeout = 3.seconds // Timeout for the ask pattern

      Behaviors.receiveMessage {
        case Dispatch(request, replyTo, customer) =>
          context.log.info(
            s"Processing request to email: ${request.recipientId}")

          // Use context.ask to send a Preferences request to the Customer actor
          context.ask(
            customer,
            Customer.Preferences(
              CustomerInformationRequest(request.recipientId),
              _)) {
            case Success(Customer.PreferencesResponse(preferences)) =>
              context.log.info(
                s"Customer preferences for ${request.recipientId}: $preferences")
              replyTo ! Worker.Done
              Behaviors.same
            case Failure(exception) =>
              context.log.error(
                s"Failed to get preferences: ${exception.getMessage}")
              replyTo ! Worker.Done
              Behaviors.same
          }

          Behaviors.same
      }
    }

  private def fakeLengthyParsing(request: Request): Unit = {
    val endTime =
      System.currentTimeMillis + Random.between(1000, 2000)
    while (endTime > System.currentTimeMillis) {}
  }

  final case class CustomerInformationRequest(recipientId: String)
}
