package controllers

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.mps.minesweeper.api.{Game, GameResult, Player}
import de.htwg.mps.minesweeper.api.events._
import play.api.Logger
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
      webSocketOut ! gridEvent("GridUpdate", g.grid)
    case GameUpdate(g) =>
      webSocketOut ! gameStatusEvent("GameUpdate", g)
      webSocketOut ! gridEvent("GridUpdate", g.grid)
    case GameWon(g) => webSocketOut ! gameStatusEvent("GameWon", g)
    case GameLost(g) => webSocketOut ! gameStatusEvent("GameLost", g)
    case PlayerUpdate(p) => webSocketOut ! playerEvent("PlayerUpdate", p)

    case t:JsValue => Logger.trace(t.toString)
  }

  private def gridEvent(name: String, grid: GridModel): JsValue =
    Json.toJson(WebSocketEvent(name, views.html.grid(grid).body))

  private def gameStatusEvent(name: String, game: GameModel): JsValue =
    Json.toJson(WebSocketEvent(name, game))

  private def playerEvent(name: String, player: PlayerModel): JsValue =
    Json.toJson(WebSocketEvent(name, player))

  private implicit val jsonWebSocketEventString: Writes[WebSocketEvent[String]] =
    (webSocketEvent: WebSocketEvent[String]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonWebSocketEventPlayer: Writes[WebSocketEvent[PlayerModel]] =
    (webSocketEvent: WebSocketEvent[PlayerModel]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonWebSocketEventGame: Writes[WebSocketEvent[GameModel]] =
    (webSocketEvent: WebSocketEvent[GameModel]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonPlayer: Writes[PlayerModel] =
    (player: PlayerModel) => Json.obj(
    "history" -> player.history
  )

  private implicit val jsonGame: Writes[GameModel] =
    (game: GameModel) => Json.obj(
      "running" -> game.running,
      "gameResult" -> game.gameResult
    )

  private implicit val jsonGameResult: Writes[GameResult] =
    (gameResult: GameResult) => Json.obj(
    "score" -> gameResult.score,
    "win" -> gameResult.win
  )

  override def postStop(): Unit = {
    publisherActor ! DeregisterObserver
  }

}