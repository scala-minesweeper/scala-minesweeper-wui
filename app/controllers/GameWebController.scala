package controllers

import javax.inject.{Inject, Singleton}

import akka.actor.ActorRef
import de.htwg.mps.minesweeper.api.events._
import play.api.mvc._
import services.{ActorResolver, WebSocketActorFactory}

@Singleton
class GameWebController @Inject()(actorResolver: ActorResolver,
                                  webSocketActorFactory: WebSocketActorFactory,
                                  cc: ControllerComponents) extends AbstractController(cc) {

  val publisherActor: ActorRef = actorResolver.resolvePublisher()
  val gameActor: ActorRef = actorResolver.resolveGameController()

  def socket: WebSocket = webSocketActorFactory.create(publisherActor, gameActor)

  def game = Action {
    Ok(views.html.game())
  }

  def openField(col: Int, row: Int) = Action {
    gameActor ! OpenField(col, row)
    Ok
  }

  def newGame(cols: Int, rows: Int, bombs: Int) = Action {
    gameActor ! StartGame(cols, rows, bombs)
    Ok
  }

  def toggleState(col: Int, row: Int) = Action {
    gameActor ! ToggleField(col, row)
    Ok
  }

}


