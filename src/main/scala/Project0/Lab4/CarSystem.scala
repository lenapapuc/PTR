package Project0.Lab4

import akka.actor._
import scala.util.Random

case object Failure
// Define the sensor actor trait
trait SensorActor extends Actor with ActorLogging {
  def generateReading(): Int
  def isWorking(): Boolean

  var currentReading = generateReading()
  def receive = {
    case "read" =>
      if (isWorking())
        println(currentReading)
        sender() ! currentReading
      else
        sender() ! Failure
        log.info(s"$self has failed with reading $currentReading")
  }
}

// Define the wheel sensor actor class
class WheelSensorActor extends Actor with ActorLogging {
  var wheelSensors = (1 to 4).map(_ => context.actorOf(Props[SingleWheelActor]()))
  def receive = {
    case "read" => wheelSensors.foreach(wheel => wheel ! "read")
    case reading  => context.parent ! reading
  }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }

}

class SingleWheelActor extends SensorActor {
  def generateReading(): Int = {
    Random.nextInt(101)
  }

  def isWorking(): Boolean = {
    currentReading >= 10 && currentReading <= 90
  }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }
}

// Define the motor sensor actor class
class MotorSensorActor extends SensorActor {
  def generateReading(): Int = {
    // Simulate a random sensor reading
    Random.nextInt(501)
  }

  def isWorking(): Boolean = {
    currentReading >= 50 && currentReading <= 450
  }

  override def postRestart(reason: Throwable): Unit  = {
    log.info(s"I, $self have been restarted")
  }

}

// Define the cabin sensor actor class
class CabinSensorActor extends SensorActor {
  def generateReading(): Int = {
    Random.nextInt(51)
  }

  def isWorking(): Boolean = {
    // Check if the current reading is within the expected range
    currentReading <= 45
  }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }
}

// Define the chassis sensor actor class
class ChassisSensorActor extends SensorActor {
  def generateReading(): Int = {
    // Simulate a random sensor reading
    Random.nextInt(101)
  }

  def isWorking(): Boolean = {
    // Check if the current reading is within the expected range
    currentReading >= 10
  }

  override def postRestart(reason: Throwable): Unit  =
  {
    log.info(s"I, $self have been restarted")
  }
}

class AirbagDeployer extends Actor with ActorLogging
{
  def receive =
  {
    case "deploy" => deployAirbags()
    case _ =>
  }

  def deployAirbags():Unit=
  {
    log.info("Airbags deployed")
  }
}

// Define the sensor supervisor actor class
class SensorSupervisorActor extends Actor with ActorLogging {
  val wheelSensorActor = context.actorOf(Props[WheelSensorActor](), "wheelSensor")
  val motorSensorActor = context.actorOf(Props[MotorSensorActor](), "motorSensor")
  val cabinSensorActor = context.actorOf(Props[CabinSensorActor](), "cabinSensor")
  val chassisSensorActor = context.actorOf(Props[ChassisSensorActor](), "chassisActor")
  val airbagDeployer = context.actorOf(Props[AirbagDeployer](), "airbagDeployer")



  var numFailures = 0
  def receive = {
    case "check" => context.children.foreach(child => child ! "read")
    case Failure =>
      numFailures += 1
      if (numFailures == 1)
        {
          sender() ! Kill
        }
      if(numFailures > 1 && numFailures<3) {
        airbagDeployer ! "deploy"
      }
    case response =>
  }

  override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
    case _: Exception => SupervisorStrategy.Restart
  }

}

object Main1 extends App
{
  val system = ActorSystem("CarSystem")
  val supervisor = system.actorOf(Props[SensorSupervisorActor](),"supervisor")

    supervisor ! "check"
    Thread.sleep(5000)

  system.terminate()
}
