// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.spaceape.hiring.model.{Move, GameState, CurrentGame}
import io.dropwizard.testing.junit.DropwizardAppRule
import org.scalatest.junit.JUnitSuite

import org.junit.Test
import org.junit.ClassRule
import org.scalatest.Matchers


object CurrentGameTest {
	@ClassRule def rule = new DropwizardAppRule[NoughtsConfiguration](classOf[NoughtsApplication], "test.yml")
}

class CurrentGameTest extends JUnitSuite with Matchers {

  val baseUrl = "http://localhost:8080/game"

	@Test
	def checkPlayerCantMakeConsecutiveMoves {
		var currentGame = new CurrentGame("1", "2")
    currentGame.applyMove(new Move("1", 0, 0))
		currentGame.applyMove(new Move("1", 0, 0)) should be (false)
	}

}
