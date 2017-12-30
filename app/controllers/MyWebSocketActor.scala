package controllers

import akka.actor.{Actor, ActorRef, ActorSelection, Props}
import de.htwg.mps.minesweeper.controller._
import de.htwg.mps.minesweeper.model.field.Field
import de.htwg.mps.minesweeper.model.grid.Grid
import play.api.libs.json._

object MyWebSocketActor {
  def props(out: ActorRef, publisherActor: ActorSelection) = Props(new MyWebSocketActor(out, publisherActor))
}

class MyWebSocketActor(wsOut: ActorRef, publisherActor: ActorSelection) extends Actor {

  publisherActor ! RegisterObserver

  override def receive: Receive = {
    case FieldChanged(_, _, _, g) => wsOut ! Json.toJson(g)
    case GridChanged(g) => wsOut ! Json.toJson(g)
    case GameStart(g) => wsOut ! Json.toJson(g)
  }

  implicit val jsonGridWriter: Writes[Grid] = (grid: Grid) => Json.obj(
    "fields" -> grid.nestedFields
  )

  implicit val jsonFieldWriter: Writes[Field] = (field: Field) => Json.obj(
    "number" -> field.toString()
  )

  override def postStop(): Unit = {
    // unregister observer when socket gets closed
    publisherActor ! DeregisterObserver
  }

  trait MessageIn {
    val name: String
  }


}