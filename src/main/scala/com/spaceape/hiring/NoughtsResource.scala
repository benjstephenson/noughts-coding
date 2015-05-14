package com.spaceape.hiring

import javax.ws.rs._
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import com.spaceape.hiring.model.{GameState, Move, Game};

@Path("/game")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
class NoughtsResource() {

	//eurgh...
	var gameList = Array[Game]()

  @POST
  def createGame(@QueryParam("player1Id") player1: String, @QueryParam("player2Id") player2: String): Response = {
		val game = new Game(player1, player2)

		gameList.find { g =>
			(g.player1 == player1 && g.player2 == player2) ||
			(g.player1 == player2 && g.player2 == player1)
		} match {
			case Some(existing) => {
				Response.status(Status.FORBIDDEN).build
			}
			case None => {
				gameList = gameList :+ game
				Response.ok(game.getId).build
			}
		}
  }

  @GET
  @Path("/{gameId}")
  def getGame(@PathParam("gameId") gameId: String): GameState = {
		gameList.find {
			_.getId == gameId
		} match {
			case Some(currentGame) => {
				val state = currentGame.getState
				if (state.gameOver) {
					gameList = gameList diff List(currentGame)
				}

				state
			}
			case None => new GameState(Some("1"), true)
		}
  }


  @PUT
  @Path("/{gameId}")
  def makeMove(@PathParam("gameId") gameId: String, move: Move): Response = {

		val currentGame = gameList.find {
			_.getId == gameId
		}

		if (currentGame.get.applyMove(move))
			Response.status(Status.ACCEPTED).build
		else
			Response.status(Status.FORBIDDEN).build
  }
}
//  vim: set ts=2 sw=2 tw=78 fdm=marker noet :
