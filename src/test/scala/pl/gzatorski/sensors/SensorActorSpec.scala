package pl.gzatorski.sensors

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class SensorActorSpec extends TestKit(ActorSystem("SensorSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {
  
  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "An Sensor actor" must {

    "handle single result" in {
      val actor = TestActorRef[SensorActor]
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(50)))
      actor ! GetStatistics()

      expectMsg(PartialComputation(ComputationResult("s1", Some(50), Some(50), 50, 1, 0)))
    }

    "handle NaN as Nones" in {
      val actor = TestActorRef[SensorActor]
      actor ! ProcessSingleMeasurement(Measurement("s1", None))
      actor ! GetStatistics()

      expectMsg(PartialComputation(ComputationResult("s1", None, None, 0, 0, 1)))
    }

    "sum values" in {
      val actor = TestActorRef[SensorActor]
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(5)))
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(10)))
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(15)))

      actor ! GetStatistics()

      expectMsg(PartialComputation(ComputationResult("s1", Some(5), Some(15), 30, 3, 0)))
    }

    "count failed and successful events" in {
      val actor = TestActorRef[SensorActor]
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(0)))
      actor ! ProcessSingleMeasurement(Measurement("s1", None))
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(100)))
      actor ! ProcessSingleMeasurement(Measurement("s1", None))
      actor ! ProcessSingleMeasurement(Measurement("s1", Some(9)))
      actor ! ProcessSingleMeasurement(Measurement("s1", None))
      
      actor ! GetStatistics()

      expectMsg(PartialComputation(ComputationResult("s1", Some(0), Some(100), 109, 3, 3)))
    }
    
  }
}