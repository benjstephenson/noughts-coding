// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring.model

import scala.collection.mutable.ListBuffer
import scala.util.Random

class CurrentGame(player1: String, player2: String) {

	val id: String = Random.alphanumeric.take(2).mkString
  var currentTurn = player1

	// There's definitely a better way to do this....
	var board = Array(Array("0", "0", "0"), Array("0", "0", "0"), Array("0", "0", "0"))

	// Pretty brute force way to go about pattern matching win conditions
	private val winningCombos = List(List(0, 1, 2), List(3, 4, 5), List(6, 7, 8),  //Rows
		List(0, 3, 6), List(1, 4, 7), List(2, 5, 8),                                 //Cols
		List(0, 4, 8), List(2, 4, 6)                                                 //Diags
	)


	def getId: String = { id }


	def getState: GameState = {
		printBoard

		checkForPlayerWin(player1) match {
			case Some(player1) => return new GameState(Some(player1), true)
			case None => checkForPlayerWin(player2) match {
				case Some(player2) => return new GameState(Some(player2), true)
				case None => return new GameState(None, false)
			}
		}
	}

	private def checkForPlayerWin(player: String): Option[String] = {

		// Get the board layout for the player
		var marks = new ListBuffer[Int]()
		board.flatten.zipWithIndex foreach { case (value, index) =>
			println(s"${value} ${index}")
			if (value == player) marks += index
		}

		val playerResult = marks.toList
		println(playerResult)

		if (winningCombos.contains(playerResult))
			Some(player)
		else
			None
	}


	def applyMove(move: Move): Boolean = {
		if (isMoveValid(move)) {
			System.out.println("Move valid")
			updateBoard(move)
		} else {
			System.out.println("Move not valid")
			false
		}
	}

	def printBoard = {
		System.out.println(s"${board(0)(0)}, ${board(0)(1)}, ${board(0)(2)}")
		System.out.println(s"${board(1)(0)}, ${board(1)(1)}, ${board(1)(2)}")
		System.out.println(s"${board(2)(0)}, ${board(2)(1)}, ${board(2)(2)}")
	}


  private def isMoveValid(move: Move): Boolean = {
    currentTurn == move.playerId && board(move.x)(move.y) == "0"
  }


	private def updateBoard(move: Move): Boolean = {
		if (currentTurn == player1) currentTurn = player2 else currentTurn = player1
		System.out.println(s"currentTurn is now ${currentTurn}")
		board(move.x)(move.y) = move.playerId
		true
	}

}

