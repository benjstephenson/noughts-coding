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
import scala.util.Random


object NoughtsTest {
	@ClassRule def rule = new DropwizardAppRule[NoughtsConfiguration](classOf[NoughtsApplication], "test.yml")
}

class NoughtsTest extends JUnitSuite with Matchers {

  val baseUrl = "http://localhost:8080/game"
	var players = Array[Array[String]]()

  val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)

  def initGame = {

    val p1 = Random.alphanumeric.take(2).mkString
    val p2 = Random.alphanumeric.take(2).mkString

    players = players :+ Array(p1, p2)

    val response = Unirest.post(baseUrl)
      .queryString("player1Id", p1)
      .queryString("player2Id", p2)
      .asString()

    response
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
  def newGameReturnsGameId {
    val response = initGame
    response.getStatus should be (Status.OK.getStatusCode)
  }

	@Test
	def testPlayer1Win {
    val gameId = initGame.getBody
    val p = players.last
    runMoves(gameId, Seq(
      Move(p(0), 0, 0),
      Move(p(1), 1, 0),
      Move(p(0), 0, 1),
      Move(p(1), 1, 1),
      Move(p(0), 0, 2)))

    getState(gameId) should be (GameState(Some(p(0)), true))

	}

	@Test
	def testPlayer2Win {
    val gameId = initGame.getBody
    val p = players.last
    runMoves(gameId, Seq(
      Move(p(0), 0, 0),
      Move(p(1), 1, 0),
      Move(p(0), 0, 1),
      Move(p(1), 1, 1),
      Move(p(0), 2, 2),
      Move(p(1), 1, 2)))

    getState(gameId) should be (GameState(Some(p(1)), true))

	}


  @Test
  def testPlayerDraw {
    val gameId = initGame.getBody
    val p = players.last
    runMoves(gameId, Seq(
      Move(p(0), 0, 0),
      Move(p(1), 0, 1),
      Move(p(0), 0, 2),
      Move(p(1), 1, 0),
      Move(p(0), 1, 2),
      Move(p(1), 1, 1),
      Move(p(0), 2, 0),
      Move(p(1), 2, 2),
      Move(p(0), 2, 1)))

    getState(gameId) should be (GameState(None, true))

  }


  @Test
  def testStartGameAgainstOpenOpponentReturns403 {
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


  @Test
  def testGetGameForUnknownIdReturns404 {
    val response = Unirest.get(s"$baseUrl/123456").asString()
    response.getStatus should be (Status.NOT_FOUND.getStatusCode)
  }

  @Test
  def testMakeMoveForUnknownIdReturns404 {
    val response = Unirest.put(s"$baseUrl/123456").asString()
    response.getStatus should be (Status.NOT_FOUND.getStatusCode)
  }

  @Test
  def testInvalidMoveReturns403 {
    val gameId = initGame.getBody
    val p = players.last
    runMoves(gameId, Seq(
      Move(p(0), 0, 0),
      Move(p(1), 0, 1)))

    val response = Unirest.put(s"$baseUrl/$gameId")
      .header("Content-Type", "application/json")
      .body(objectMapper.writeValueAsString(Move(p(0), 0, 1)))
      .asString()

    response.getStatus should be (Status.FORBIDDEN.getStatusCode)

  }
}
//  vim: set ts=2 sw=2 tw=0 fdm=marker et :
