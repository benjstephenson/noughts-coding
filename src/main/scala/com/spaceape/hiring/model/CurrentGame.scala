// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring.model

import scala.util.Random

class CurrentGame(player1: String, player2: String) {

	val id: String = Random.alphanumeric.take(10).mkString
  var lastTurn = player1

	// There's definitely a better way to do this....
	var board = List(List(0, 0, 0), List(0, 0, 0), List(0, 0, 0))

	def getId: String = { id }

	def getState: GameState = {
		new GameState(Some(player1), true)
	}

  def isMoveValid(move: Move): Boolean = {
    lastTurn != move.playerId
  }

}

