import akka.actor.{ActorPath, ActorRef, ActorSystem}
import controllers.GameWebController
import org.scalatestplus.play._
import play.api.test.Helpers._
import play.api.test.FakeRequest
import services.{ActorResolver, WebSocketActorFactory}

class UnitSpec extends PlaySpec {

  "GameWebController" should {

    "return a valid result with action" in {
      val publisherActor:ActorRef = ActorRef.noSender
      val gameActor:ActorRef = ActorRef.noSender
      implicit val actorSystem: ActorSystem = ActorSystem("test")
      val actorResolverStub = new ActorResolver() {
        override def resolvePublisher(): ActorRef = publisherActor
        override def resolveGameController(): ActorRef = gameActor
      }
      try {
        val controller = new GameWebController(actorResolverStub, new WebSocketActorFactory(), stubControllerComponents())
        val result = controller.game(FakeRequest())
        contentAsString(result) must contain("Minesweeper Game")
      } finally {
        // always shut down actor system at the end of the test.
        actorSystem.terminate()
      }
    }
  }

}
