package Project2.MessageBroker


import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.DeadLetter

class DeadLetterChannel extends Actor with ActorLogging {
  var messageList: List[Any] = List.empty

  def receive: Receive = {
        
    case  deadLetter: DeadLetter =>
      log.warning("Received Dead Letter: {}", deadLetter.message)
      messageList = messageList :+ deadLetter.message
    case dead: Any =>
      log.info(s"Incorrect Message: $dead")
      messageList = messageList :+ dead
  }
}


