package questionwithpayload

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.ActorRef

object Customer {

  sealed trait Command
  final case class Preferences(
      request: Worker.CustomerInformationRequest,
      replyTo: ActorRef[PreferencesResponse])
      extends Command

  final case class PreferencesResponse(preferences: String)

  def apply(): Behavior[Command] = Behaviors.receive {
    (context, message) =>
      message match {
        case Preferences(request, replyTo) =>
          val preferences = customerPreferences
            .getOrElse(request.recipientId, Map.empty)
            .getOrElse("preferences", "email")
          context.log.info(
            s"Customer preferences for ${request.recipientId}: $preferences")
          replyTo ! PreferencesResponse(
            preferences
          ) // Send response back to Worker
          Behaviors.same
      }
  }

  private val customerPreferences = Map(
    "0001" -> Map(
      "preferences" -> "SMS",
      "sendWindow" -> "09:00-17:00"),
    "0002" -> Map(
      "preferences" -> "Email",
      "sendWindow" -> "08:00-20:00"),
    "0003" -> Map(
      "preferences" -> "Push",
      "sendWindow" -> "10:00-18:00"))
}
