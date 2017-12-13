package controllers

import javax.inject.{Inject, Singleton}

import play.api.mvc.{AbstractController, ControllerComponents}

import de.htwg.mps.minesweeper.controller.GameControllerExport

@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  def index = Action {
    val gameController = GameControllerExport.controller

    gameController.restartGame(10,10,30)
    println(gameController.game)
    Ok(gameController.game.grid().toString)
  }

}


