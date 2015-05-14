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

	val predGameById: (Game, String) => Boolean = (game, gameId) => {
		game.getId == gameId
	}

	val predGameWithPlayerCombo: (Game, String, String) => Boolean = (game, p1, p2) => {
			(game.player1 == p1 && game.player2 == p2) ||
			(game.player1 == p2 && game.player2 == p1)
	}

  @POST
  def createGame(@QueryParam("player1Id") player1: String, @QueryParam("player2Id") player2: String): Response = {

		val closure: (Option[Game]) => Response = (game) => {
			game match {
				case Some(existing) => {
					println(s"Found existing game ${existing.player1} ${existing.player2}")
					Response.status(Status.FORBIDDEN).build
				}
				case None => {
					val newGame = new Game(player1, player2)
					gameList = gameList :+ newGame
					Response.ok(newGame.getId).build
				}
			}
		}

		findGameAndExecute(predGameWithPlayerCombo(_, player1, player2), closure)
  }


  @GET
  @Path("/{gameId}")
  def getGame(@PathParam("gameId") gameId: String): Response = {

		val closure: (Option[Game]) => Response = (game) => {
			game match {
				case Some(currentGame) => {
					val state = currentGame.getState
					if (state.gameOver) {
						gameList = gameList diff List(currentGame)
					}

					Response.ok(state).build
				}
				case None => Response.ok(new GameState(Some("1"), true)).build
			}
		}

		findGameAndExecute(predGameById(_, gameId), closure)
  }


  @PUT
  @Path("/{gameId}")
  def makeMove(@PathParam("gameId") gameId: String, move: Move): Response = {

		val closure: (Option[Game]) => Response = (game) => {
			game match {
				case Some(game) => game.applyMove(move) match {
					case true  => Response.status(Status.ACCEPTED).build
					case false => Response.status(Status.FORBIDDEN).build
				}
					case _ => Response.status(Status.NOT_FOUND).build
			}
		}

		findGameAndExecute(predGameById(_, gameId), closure)
  }

	def findGameAndExecute(predicate: (Game) => Boolean,
		closure: (Option[Game]) => Response): Response = {
		val game = gameList.find { predicate(_) }
		closure(game)
	}
}
//  vim: set ts=2 sw=2 tw=78 fdm=marker noet :
