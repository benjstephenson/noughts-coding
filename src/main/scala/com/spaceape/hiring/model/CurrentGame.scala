// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring.model

import scala.util.Random

class CurrentGame(player1: String, player2: String) {

	val id: String = Random.alphanumeric.take(2).mkString
  var currentTurn = player1

	// There's definitely a better way to do this....
	var board = Array(Array("0", "0", "0"), Array("0", "0", "0"), Array("0", "0", "0"))

	def getId: String = { id }

	def getState: GameState = {
		printBoard
		if (checkRowWin(0, player1))
			new GameState(Some(player1), true)
		else
			new GameState(Some(player2), true)

		board.
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


	private def checkRowWin(row: Int, playerId: String): Boolean = {
		board(row).forall(_ == playerId)
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

