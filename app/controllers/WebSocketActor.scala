package controllers

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.mps.minesweeper.controller._
import de.htwg.mps.minesweeper.model.Game
import de.htwg.mps.minesweeper.model.grid.Grid
import de.htwg.mps.minesweeper.model.player.Player
import de.htwg.mps.minesweeper.model.result.GameResult
import play.api.libs.json._

object WebSocketActor {
  def props(webSocketOut: ActorRef, publisherActor: ActorRef, controllerActor: ActorRef) =
    Props(new WebSocketActor(webSocketOut, publisherActor, controllerActor))
}

class WebSocketActor(webSocketOut: ActorRef, publisherActor: ActorRef, controllerActor: ActorRef) extends Actor {

  publisherActor ! RegisterObserver

  case class WebSocketEvent[T](name: String, body: T)

  override def receive: Receive = {
    case FieldUpdate(_, _, _, g) => webSocketOut ! gridEvent("FieldUpdate", g)
    case GridUpdate(g) =>  webSocketOut ! gridEvent("GridUpdate", g)
    case GameStart(g) =>
      webSocketOut ! gameStatusEvent("GameStart", g)
      webSocketOut ! gridEvent("GridUpdate", g.grid())
    case GameUpdate(g) =>
      webSocketOut ! gameStatusEvent("GameUpdate", g)
      webSocketOut ! gridEvent("GridUpdate", g.grid())
    case GameWon(g) => webSocketOut ! gameStatusEvent("GameWon", g)
    case GameLost(g) => webSocketOut ! gameStatusEvent("GameLost", g)
    case PlayerUpdate(p) => webSocketOut ! playerEvent("PlayerUpdate", p)
  }

  private def gridEvent(name: String, grid: Grid): JsValue =
    Json.toJson(WebSocketEvent(name, views.html.grid(grid).body))

  private def gameStatusEvent(name: String, game: Game): JsValue =
    Json.toJson(WebSocketEvent(name, game))

  private def playerEvent(name: String, player: Player): JsValue =
    Json.toJson(WebSocketEvent(name, player))

  private implicit val jsonWebSocketEventString: Writes[WebSocketEvent[String]] =
    (webSocketEvent: WebSocketEvent[String]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonWebSocketEventPlayer: Writes[WebSocketEvent[Player]] =
    (webSocketEvent: WebSocketEvent[Player]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonWebSocketEventGame: Writes[WebSocketEvent[Game]] =
    (webSocketEvent: WebSocketEvent[Game]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonPlayer: Writes[Player] =
    (player: Player) => Json.obj(
    "history" -> player.history
  )

  private implicit val jsonGame: Writes[Game] =
    (game: Game) => Json.obj(
      "running" -> game.isRunning,
      "gameResult" -> game.getScore
    )

  private implicit val jsonGameResult: Writes[GameResult] =
    (gameResult: GameResult) => Json.obj(
    "score" -> gameResult.getScore,
    "win" -> gameResult.win
  )

  override def postStop(): Unit = {
    publisherActor ! DeregisterObserver
  }

}