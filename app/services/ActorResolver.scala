package services

import javax.inject.{Inject, Singleton}

import akka.actor.{ActorRef, ActorSelection, ActorSystem}
import com.typesafe.config.ConfigFactory

import scala.concurrent.Await
import scala.concurrent.duration._

@Singleton
class ActorResolver @Inject()(implicit actorSystem: ActorSystem) {

  private val config = ConfigFactory.load()
  private val controllerActorName = config.getString("akka.minesweeper.controllerActor")
  private val publisherActorName = config.getString("akka.minesweeper.publisherActor")

  def resolveGameController(): ActorRef = {
    val gameController: ActorSelection = actorSystem.actorSelection(controllerActorName)
    Await.result(gameController.resolveOne(5.seconds), 5.seconds)
  }

  def resolvePublisher(): ActorRef = {
    val publisher: ActorSelection = actorSystem.actorSelection(publisherActorName)
    Await.result(publisher.resolveOne(5.seconds), 5.seconds)
  }
}