import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class AskSpec
    extends ScalaTestWithActorTestKit
    with AnyWordSpecLike
    with Matchers {

  import simplequestion.Manager

  "the message on dead letters" should {
    "be referencing to the sender of the ask" in {

      val manager = spawn(Manager(), "manager-1")
      manager ! Manager.Delegate(
        List("task-a", "task-b", "task-c", "task-d"))

      Thread.sleep(3000)
    }
  }
}
