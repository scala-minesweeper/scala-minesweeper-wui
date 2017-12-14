package controllers

import javax.inject.{Inject, Singleton}

import de.htwg.mps.minesweeper.controller.GameControllerExport
import play.api.mvc.{AbstractController, ControllerComponents}


@Singleton
class GameController @Inject()(cc: ControllerComponents) extends AbstractController(cc){

  val gameController = GameControllerExport.controller

  def game = Action {
    gameController.restartGame(10,10,30)
    println(gameController.game.grid())
    Ok(views.html.game(gameController))
  }

  def openField(col:Int, row:Int) = Action {
    println("open"+row+","+col)
    gameController.openField(col,row)
    println(gameController.game.grid())
    Ok(views.html.game(gameController))
  }

  def newGame(cols:Int, rows:Int ,bombs:Int) = Action {
    gameController.restartGame(cols,rows,bombs);
    Ok(views.html.game(gameController))
  }

}


