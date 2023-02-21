package Project0.Lab4

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, AllForOneStrategy, Kill, OneForOneStrategy, Props, SupervisorStrategy}



class SplitActor(nextActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case message: String =>
      val myArray = message.split("\\s+")
      nextActor ! myArray
      //log.info(s"$self")
    case message =>
      throw new Exception("Not fitting type")
      //log.info("Can't process anything other than strings")

  }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }

  }

class SwappingActor(nextActor: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case message: Array[String] =>
      val result = message.map(word => word.toLowerCase())
      var list  = List[String]()
      for(i <- result)
        {
          var string = i;
          var c = i.length - 1
          while(c != -1)
            {

              if (i(c)=='m')
                string=string.patch(c, "n", 1)
                c-=1
              else if (i(c)=='n')
                string=string.patch(c, "m", 1)
                c-=1
              else c-=1
            }
            list = list :+ string;

        }
      nextActor ! list
    case message => log.info("I just accept arrays of strings")

  }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }

}

class JoinerActor extends Actor with ActorLogging{
  override def receive: Receive =
    {
      case message: List[String] =>
        val result = message.mkString(" ")
        println(result)
      case message => log.info("I only accept lists of strings")

    }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }



}
class SupervisorActor extends Actor with ActorLogging {

  var joinerActor: ActorRef = _
  var swappingActor: ActorRef = _
  var splitActor: ActorRef = _

  override def preStart() : Unit = {
    joinerActor = context.actorOf(Props[JoinerActor](), "joinerActor")

    swappingActor = context.actorOf(Props(new SwappingActor(joinerActor)),"swappingActor")
    splitActor = context.actorOf(Props(new SplitActor(swappingActor)),"splitActor")


  }
  override def receive: Receive = {
    case message: String => splitActor ! message
    case message => log.info("I can't process this message")
  }

    override def supervisorStrategy: SupervisorStrategy = AllForOneStrategy() {
      case _: Exception => SupervisorStrategy.Restart
    }
}

object Main extends App {
  val system = ActorSystem("ProcessingLine")

  val supervisor = system.actorOf(Props[SupervisorActor](), "supervisor")
  var selection = system.actorSelection("akka://ProcessingLine/user/supervisor/splitActor")

  supervisor ! "You monster!"
  Thread.sleep(1000)
  selection ! Kill
  Thread.sleep(1000)
  supervisor ! "Maybe"
  Thread.sleep(1000)

  system.terminate()
}
