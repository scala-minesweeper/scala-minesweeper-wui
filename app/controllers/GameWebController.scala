package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import akka.stream.Materializer
import de.htwg.mps.minesweeper.controller._
import de.htwg.mps.minesweeper.model.grid.{Grid, MinesweeperGrid}
import play.api.libs.json.JsValue
import play.api.libs.streams.ActorFlow
import play.api.mvc._

class GameWebControllerActor(actorSystem: ActorSystem, receiveImpl: Receive) extends Actor {

  private val publishController: ActorSelection =
    actorSystem.actorSelection("akka.tcp://minesweeper@127.0.0.1:5555/user/publisher")

  publishController ! RegisterObserver

  override def receive: Receive = receiveImpl

}

@Singleton
class GameWebController @Inject()(implicit actorSystem: ActorSystem, cc: ControllerComponents, materializer: Materializer) extends AbstractController(cc) {

  var grid: Grid = MinesweeperGrid(1, 1, 1)
  private val publishController: ActorSelection =
    actorSystem.actorSelection("akka.tcp://minesweeper@127.0.0.1:5555/user/publisher")

  def socket: WebSocket = WebSocket.accept[JsValue, JsValue] { _ =>
    ActorFlow.actorRef(out => Props(new MyWebSocketActor(out, publishController)))
  }

  val webController: ActorRef = actorSystem.actorOf(Props(new GameWebControllerActor(actorSystem, {
    case FieldChanged(_, _, _, g) => grid = g
    case GridChanged(g) => grid = g
    case GameStart(g) => grid = g
  })))

  val gameController: ActorSelection = actorSystem.actorSelection("akka.tcp://minesweeper@127.0.0.1:5555/user/controller")

  def game = Action {
    Ok(views.html.game(grid))
  }

  def openField(col: Int, row: Int) = Action {
    gameController ! OpenField(col, row)
    Ok(views.html.game(grid))
  }

  def newGame(cols: Int, rows: Int, bombs: Int) = Action {
    gameController ! StartGame(cols, rows, bombs)
    Ok(views.html.game(grid))
  }

  def toggleState(col: Int, row: Int) = Action {
    gameController ! ToggleField(col, row)
    Ok(views.html.game(grid))
  }

}


