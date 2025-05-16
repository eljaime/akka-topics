package com.manning

import akka.actor.typed.ActorSystem

object WalletApp extends App {

  val guardian: ActorSystem[Int] =
    ActorSystem(Wallet(), "wallet")

  for (i <- 1 to 10) {
    guardian ! i
  }

  println("Press ENTER to terminate")
  scala.io.StdIn.readLine()
  guardian.terminate()

}
