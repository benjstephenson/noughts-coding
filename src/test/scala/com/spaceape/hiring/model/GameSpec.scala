package com.spaceape.hiring

import javax.ws.rs.core.Response.Status
import com.spaceape.hiring.model._
import com.mashape.unirest.http.Unirest
import org.scalatest._

class GameSpec extends FlatSpec with Matchers {

  val baseUrl = "http://localhost:8080/game"

  "A Game" should "not allow a player to make consecutive moves" in {
		var currentGame = new Game("1", "2")
    currentGame.applyMove(new Move("1", 0, 0))
		currentGame.applyMove(new Move("1", 0, 1)) should be (false)
  }

  it should "not allow a player to reuse a square that is in play" in {
		var currentGame = new Game("1", "2")
    currentGame.applyMove(new Move("1", 0, 0))
		currentGame.applyMove(new Move("2", 0, 0)) should be (false)
  }

}
//  vim: set ts=2 sw=2 tw=0 fdm=marker et :
