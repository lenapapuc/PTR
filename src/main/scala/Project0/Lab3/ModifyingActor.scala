package Project0.Lab3

import akka.actor.{ Actor, ActorRef, ActorSystem, PoisonPill, Props }
import akka.event.Logging
import akka.actor.typed.scaladsl.LoggerOps
import akka.pattern.gracefulStop

object app2 extends App{

  class ModifyingActor extends Actor  {

    val log = Logging(context.system, this)

    def receive = {
      case s: String => println("Received: " + s.toLowerCase)
        //log.info("Received: {}",s.toLowerCase)
      case i: Int =>  println("Received: " + (i+1))
        //log.info("Received: {}",i+1)
      case _ =>  println("I don't know how to HANDLE this!")
        //log.info("I don ’ t know how to HANDLE this !")
    }
  }
    val system = ActorSystem("Modifying")
    val actor = system.actorOf(Props[ModifyingActor](),"Modifying")

    actor ! "Hey There"
    actor ! 42
    actor ! Map {10 -> "Hello"}

    system.terminate()



}
