package Project0.Lab3
import akka.actor.{Actor, ActorSystem, Props}

object app extends App {

  class EchoActor extends Actor  {
    
    def receive = {
      case message => println(message)

    }
  }

    val system = ActorSystem("SimpleSystem")
    val actor = system.actorOf(Props[EchoActor](),"EchoActor")

    actor ! 42
    actor ! "Hey There"
    actor ! Map{10 -> "Hello"}

    system.terminate()
  

}