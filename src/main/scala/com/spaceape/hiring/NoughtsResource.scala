// vim: set ts=2 sw=2 tw=78 fdm=marker noet :
package com.spaceape.hiring

import javax.ws.rs._
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.Response.Status

import com.spaceape.hiring.model.{GameState, Move, CurrentGame};

@Path("/game")
@Produces(Array(MediaType.APPLICATION_JSON))
@Consumes(Array(MediaType.APPLICATION_JSON))
class NoughtsResource() {

	//eurgh...
	var currentGame: Option[CurrentGame] = None

  @POST
  def createGame(@QueryParam("player1Id") player1: String, @QueryParam("player2Id") player2: String): String = {
    currentGame = Some(new CurrentGame(player1, player2))
		currentGame.get.getId
  }

  @GET
  @Path("/{gameId}")
  def getGame(@PathParam("gameId") gameId: String): GameState = {
    currentGame.get.getState
  }


  @PUT
  @Path("/{gameId}")
  def makeMove(@PathParam("gameId") gameId: String, move: Move): Response = {
    Response.status(Status.ACCEPTED).build()
  }
}
