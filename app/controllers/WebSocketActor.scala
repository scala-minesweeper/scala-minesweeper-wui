package controllers

import akka.actor.{Actor, ActorRef, Props}
import de.htwg.mps.minesweeper.controller._
import de.htwg.mps.minesweeper.model.field.Field
import de.htwg.mps.minesweeper.model.grid.Grid
import play.api.libs.json._

object WebSocketActor {
  def props(webSocketOut: ActorRef, publisherActor: ActorRef, controllerActor: ActorRef) =
    Props(new WebSocketActor(webSocketOut, publisherActor, controllerActor))
}

class WebSocketActor(webSocketOut: ActorRef, publisherActor: ActorRef, controllerActor: ActorRef) extends Actor {

  publisherActor ! RegisterObserver

  case class WebSocketEvent[T](name: String, body: T)

  override def receive: Receive = {
    case FieldChanged(_, _, _, g) => webSocketOut ! gridEvent("FieldChanged", g)
    case GridChanged(g) =>  webSocketOut ! gridEvent("GridChanged", g)
    case GameStart(g) => webSocketOut ! gridEvent("GameStart", g)
    case GameStatus(g) => webSocketOut ! gridEvent("GameStatus", g.grid())
  }

  private def gridEvent(name: String, grid: Grid): JsValue =
    Json.toJson(WebSocketEvent(name, views.html.grid(grid).body))

  private implicit val jsonWebSocketEventWriter: Writes[WebSocketEvent[String]] =
    (webSocketEvent: WebSocketEvent[String]) => Json.obj(
      "name" -> webSocketEvent.name,
      "value" -> webSocketEvent.body
    )

  private implicit val jsonGridWriter: Writes[Grid] =
    (grid: Grid) => Json.obj(
    "fields" -> grid.nestedFields
  )

  private implicit val jsonFieldWriter: Writes[Field] =
    (field: Field) => Json.obj(
    "value" -> field.toString(),
    "state" -> field.fieldState.toString()
  )

  override def postStop(): Unit = {
    publisherActor ! DeregisterObserver
  }

}