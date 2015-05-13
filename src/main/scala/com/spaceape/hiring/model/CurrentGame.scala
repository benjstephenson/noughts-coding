// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring.model

class CurrentGame(player1: String, player2: String) {

  var lastTurn = player1

	// There's definitely a better way to do this....
	var board = List(List(0, 0, 0), List(0, 0, 0), List(0, 0, 0))

  def isMoveValid(move: Move): Boolean = {
    lastTurn != move.playerId
  }

}

