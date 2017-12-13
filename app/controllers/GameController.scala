package controllers

import javax.inject.{Inject, Singleton}

import de.htwg.mps.minesweeper.controller.GameControllerExport
import play.api.mvc.{AbstractController, ControllerComponents}


@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  def index = Action {
    val gameController = GameControllerExport.controller

    gameController.restartGame(10,5,30)
    println(gameController.game.grid())
    Ok(views.html.game(gameController))
  }

}


