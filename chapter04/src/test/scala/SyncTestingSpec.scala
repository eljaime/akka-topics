package basics

import akka.actor.testkit.typed.scaladsl.BehaviorTestKit
import akka.actor.testkit.typed.scaladsl.TestInbox
import akka.actor.testkit.typed.CapturedLogEvent
import akka.actor.testkit.typed.Effect.{
  NoEffects,
  Scheduled,
  Spawned
}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.exceptions.TestFailedException
import common.SimplifiedManager
import common.SimplifiedWorker
import org.slf4j.event.Level

import scala.concurrent.duration.DurationInt

class SyncTestingSpec extends AnyWordSpec with Matchers {

  "When performing sync testing, an actor" must {

    "only add effects if it has spawned a worker" in {
      val testKit = BehaviorTestKit(SimplifiedManager())
      testKit.expectEffect(NoEffects)
      testKit.run(SimplifiedManager.CreateChild("adan"))
      testKit.expectEffect(Spawned(SimplifiedWorker(), "adan"))
    }

    "get forwarded a message from a manager" in {
      val testKit = BehaviorTestKit(SimplifiedManager())
      val probe = TestInbox[String]()
      testKit.run(
        SimplifiedManager.Forward("message-to-parse", probe.ref))
      probe.expectMessage("message-to-parse")
      probe.hasMessages shouldBe false
    }

    "record the log" in {
      val testKit = BehaviorTestKit(SimplifiedManager())
      testKit.run(SimplifiedManager.Log)
      testKit.logEntries() shouldBe Seq(
        CapturedLogEvent(Level.INFO, "it's done"))
    }

    "fail to schedule a message since BehaviorTestKit can't deal with scheduling" in {
      intercept[TestFailedException] {
        val testKit = BehaviorTestKit(SimplifiedManager())
        testKit.run(SimplifiedManager.ScheduleLog)
        testKit.expectEffect(
          Scheduled(1.seconds, testKit.ref, SimplifiedManager.Log))
        testKit.logEntries() shouldBe Seq(
          CapturedLogEvent(Level.INFO, "it's done"))
      }
    }
  }
}
