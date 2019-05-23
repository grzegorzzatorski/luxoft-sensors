package pl.gzatorski.sensors

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.Success

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import akka.pattern.ask
import akka.util.Timeout

class StatisticCounterActorSpec extends TestKit(ActorSystem("StatisticSensorSpec"))
  with ImplicitSender
  with WordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  private val dirFilePath = getClass.getResource("/reports").getPath

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "StatisticCounterActorSpec actor" must {

    "return a list of aggregated data located in one file" in {
      val actor = TestActorRef(new StatisticCounterActor(s"$dirFilePath/leader-1.csv"))
      implicit val timeout = Timeout(60 seconds)
      val future = actor ? ProcessFile()
      
      Await.result(future, 1.minutes)
      val Success(result: List[_]) = future.value.get

      val expected = List(
        ComputationResult("s2", Some(88), Some(88), 88, 1, 0),
        ComputationResult("s1", Some(10), Some(10), 10, 1, 1)
      )

      result.toSet should equal(expected.toSet)
    }
  }
}
