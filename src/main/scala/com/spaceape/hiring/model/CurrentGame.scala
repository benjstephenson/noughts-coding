// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring.model

import scala.util.Random

class CurrentGame(player1: String, player2: String) {

	val id: String = Random.alphanumeric.take(2).mkString
  var lastTurn = player1

	// There's definitely a better way to do this....
	var board = Array(Array("", "", ""), Array("", "", ""), Array("", "", ""))

	def getId: String = { id }

	def getState: GameState = {
		new GameState(Some(player1), true)
	}

  def isMoveValid(move: Move): Boolean = {
    lastTurn != move.playerId
  }

	def applyMove(move: Move): Boolean = {
		lastTurn = move.playerId
		board(move.x)(move.y) = move.playerId
		true
	}

}

