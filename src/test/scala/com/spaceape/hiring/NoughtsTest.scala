package com.spaceape.hiring

import javax.ws.rs.core.Response.Status

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.spaceape.hiring.model.{Move, GameState}
import io.dropwizard.testing.junit.DropwizardAppRule
import org.scalatest.junit.JUnitSuite

import org.junit.Test
import org.junit.ClassRule
import com.mashape.unirest.http.Unirest
import org.scalatest._


object NoughtsTest {
	@ClassRule def rule = new DropwizardAppRule[NoughtsConfiguration](classOf[NoughtsApplication], "test.yml")
}

class NoughtsTest extends JUnitSuite with Matchers {

  val baseUrl = "http://localhost:8080/game"

  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)

  def initGame(player1Id: String, player2Id: String) = {
    val response = Unirest.post(baseUrl)
      .queryString("player1Id", player1Id)
      .queryString("player2Id", player2Id)
      .asString()

    if(response.getStatus != Status.OK.getStatusCode) {
      throw new RuntimeException(s"${response.getStatus} when creating game: ${response.getBody}")
    }

    response.getBody
    //objectMapper.readValue(response.getBody, classOf[String])
  }

  def runMoves(gameId: String, moves: Seq[Move]) = {
    moves.foreach(move => {
      val response = Unirest.put(s"$baseUrl/$gameId")
        .header("Content-Type", "application/json")
        .body(objectMapper.writeValueAsString(move))
        .asString()

      if(response.getStatus != Status.ACCEPTED.getStatusCode) {
        throw new RuntimeException(s"${response.getStatus} when making move: ${response.getBody} does not match expected ${Status.ACCEPTED.getStatusCode}")
      }
    })
  }


  def getState(gameId: String) = {
    val response = Unirest.get(s"$baseUrl/$gameId").asString()

    if(response.getStatus != Status.OK.getStatusCode) {
      throw new RuntimeException(s"${response.getStatus} when getting state: ${response.getBody}")
    }

    objectMapper.readValue(response.getBody, classOf[GameState])
  }

	@Test
	def testPlayer1Win {
    val gameId = initGame("7", "8")
    runMoves(gameId, Seq(
      Move("7", 0, 0),
      Move("8", 1, 0),
      Move("7", 0, 1),
      Move("8", 1, 1),
      Move("7", 0, 2)))

    getState(gameId) should be (GameState(Some("7"), true))

	}

	@Test
	def testPlayer2Win {
    val gameId = initGame("3", "4")
    runMoves(gameId, Seq(
      Move("3", 0, 0),
      Move("4", 1, 0),
      Move("3", 0, 1),
      Move("4", 1, 1),
      Move("3", 2, 2),
      Move("4", 1, 2)))

    getState(gameId) should be (GameState(Some("4"), true))

	}


  @Test
  def testPlayerDraw {
    val gameId = initGame("5", "6")
    runMoves(gameId, Seq(
      Move("5", 0, 0),
      Move("6", 0, 1),
      Move("5", 0, 2),
      Move("6", 1, 0),
      Move("5", 1, 2),
      Move("6", 1, 1),
      Move("5", 2, 0),
      Move("6", 2, 2),
      Move("5", 2, 1)))

    getState(gameId) should be (GameState(None, true))

  }


  @Test
  def testCantStartGameAgainstOpenOpponent {
    Unirest.post(baseUrl)
      .queryString("player1Id", "1")
      .queryString("player2Id", "2")
      .asString()


    val response = Unirest.post(baseUrl)
      .queryString("player1Id", "1")
      .queryString("player2Id", "2")
      .asString()

      response.getStatus should be (Status.FORBIDDEN.getStatusCode)
  }
}
//  vim: set ts=2 sw=2 tw=0 fdm=marker et :
