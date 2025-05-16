import akka.Done
import akka.NotUsed
import akka.actor.typed.ActorSystem
import akka.actor.typed.javadsl.Behaviors
import akka.stream.scaladsl.Flow
import akka.stream.scaladsl.Keep
import akka.stream.scaladsl.RunnableGraph
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import scala.concurrent.Future

object Main extends App {

  implicit val system = ActorSystem(Behaviors.empty, "runner")
  system
  var fakeDB: List[String] = List()
  def storeDB(value: String) =
    fakeDB = fakeDB :+ value
  var deadLetterDB: List[String] = List()
  def deadLetterDB(value: String) =
    deadLetterDB = deadLetterDB :+ value

  val producer: Source[String, NotUsed] = Source(List("uno","dos","tres", "cuatro", "cinco", "seis"))
  
  val processor: Flow[String, String, NotUsed] =
    Flow[String].filter(_ contains "o")
  
  val consumer: Sink[String, Future[Done]] =
    Sink.foreach(i => storeDB(i))

  val consumer: Sink[String, Future[Done]] =
    Sink.foreach(i => storeDB(i))

  val deadLetters: Sink[String, Future[Done]] =
    Sink.foreach(i => deadLetterDB(i))

  val blueprint: RunnableGraph[scala.concurrent.Future[akka.Done]] =
    producer.via(processor).toMat(consumer)(Keep.right)

  val future: Future[Done] = blueprint.run()

  future.onComplete { result =>
    fakeDB.foreach(i => println("Matches o " + i))
    system.terminate
  }(system.executionContext)
}
