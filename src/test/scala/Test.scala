import Project2.MessageBroker.TelnetActor
import akka.actor.{ActorSystem, Props}
import akka.io.Tcp.{CommandFailed, Connect, Connected, PeerClosed, Received, Register}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import akka.util.ByteString
import org.scalatest.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

import scala.concurrent.duration.*
import java.net.InetSocketAddress

class TelnetActorSpec(_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("TelnetActorSpec"))

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "A TelnetActor" should {

    "connect to a Telnet server and receive data" in {
      val telnetActor = system.actorOf(Props[TelnetActor](), "TestTelnetActor")
      val remote = new InetSocketAddress("localhost", 23)
      val local = new InetSocketAddress("localhost", 0)
      val probe = TestProbe()

        probe.send(telnetActor, Connect(remote, Some(local)))
      probe.expectMsgType[Connected]

      telnetActor ! Register(probe.ref)

      probe.send(telnetActor, Received(ByteString("Hello, world!")))
      probe.expectMsg(10.second, "Hello, world!")
    }

    "fail to connect to a Telnet server" in {
      val telnetActor = system.actorOf(Props[TelnetActor](), "Test2Actor")

      // Simulate a failed connection
      telnetActor ! CommandFailed(Connect(new InetSocketAddress("localhost", 23)))
      expectNoMessage(1.second)
    }

    "close the connection to a Telnet server" in {
      val telnetActor = system.actorOf(Props[TelnetActor](), "Test3Actor")

      // Simulate the server closing the connection
      telnetActor ! PeerClosed
      expectNoMessage(1.second)
    }
  }
}
