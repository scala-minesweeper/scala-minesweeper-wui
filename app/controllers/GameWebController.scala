package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSelection, ActorSystem}
import akka.stream.Materializer
import de.htwg.mps.minesweeper.controller._
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class GameWebController @Inject()(implicit actorSystem: ActorSystem, cc: ControllerComponents, materializer: Materializer)
  extends AbstractController(cc) {

  private val publishController: ActorSelection =
    actorSystem.actorSelection("akka.tcp://minesweeper@127.0.0.1:5555/user/publisher")
  private val gameController: ActorSelection =
    actorSystem.actorSelection("akka.tcp://minesweeper@127.0.0.1:5555/user/controller")

  def socket: WebSocket = WebSocket.accept[JsValue, JsValue] { _ =>
    val publisherActor: ActorRef = Await.result(publishController.resolveOne(5.seconds), 5.seconds)
    val gameActor: ActorRef = Await.result(gameController.resolveOne(5.seconds), 5.seconds)
    ActorFlow.actorRef(out => WebSocketActor.props(out, publisherActor, gameActor))
  }

  def game = Action {
    Ok(views.html.game())
  }

  def openField(col: Int, row: Int) = Action {
    gameController ! OpenField(col, row)
    Ok
  }

  def newGame(cols: Int, rows: Int, bombs: Int) = Action {
    gameController ! StartGame(cols, rows, bombs)
    Ok
  }

  def toggleState(col: Int, row: Int) = Action {
    gameController ! ToggleField(col, row)
    Ok
  }

}


