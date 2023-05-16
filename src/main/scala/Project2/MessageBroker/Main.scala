package Project2.MessageBroker
import akka.actor.{ActorSystem, Props}
import akka.io.Tcp.Bind
import akka.io.{IO, Tcp}
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._


import java.net.InetSocketAddress
import scala.concurrent.Await


object Message extends App {
      val system = ActorSystem("TelnetSystem")
      val telnetActor = system.actorOf(Props[TelnetActor](), "TelnetActor")
}
