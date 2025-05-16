package com.manning

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.Behavior

object Wallet {

  var money = 0

  def apply(): Behavior[Int] =
    Behaviors.receive { (context, message) =>
      money += message
      context.log.info(s"Wallet has '$money' dollar(s)")
      Behaviors.same
    }

}
