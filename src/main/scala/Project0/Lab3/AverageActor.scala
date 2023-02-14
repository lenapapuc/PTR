package Project0.Lab3

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import akka.event.Logging

object AverageActor{

  def apply(): Behavior[Double] = {
    calculateAverage(0.0, 0)
  }

  def calculateAverage(sum: Double, count: Int): Behavior[Double] =
    Behaviors.receive { (context, message) =>
      val newSum = sum + message
      val newCount = count + 1
      val average = newSum / newCount
      println(s"Current average is $average")
      calculateAverage(newSum, newCount)
    }
}


object Task4 extends App
{
  val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "AveragingSystem")
  val actorRef: ActorRef[Double] = system.systemActorOf(AverageActor(), "average-calculator")

  actorRef ! 0
  actorRef ! 10
  actorRef ! 10
  actorRef ! 10

  system.terminate()

}
