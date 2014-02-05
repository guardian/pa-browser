package controllers

import play.api._
import play.api.mvc._
import play.api.Play.current
import pa.{Player, TeamEventMatch, Client}
import util.{FutureZippers, ExecutionContexts}
import org.joda.time.DateMidnight
import java.net.URLDecoder
import model.{PrevResult, PA}
import play.api.templates.Html
import scala.concurrent.Future

object PlayerController extends Controller with ExecutionContexts {

  def playerCard(playerId: String) = Action.async { request =>
    FutureZippers.zip(
      Client.playerHead2Head(playerId, "1", new DateMidnight(2013, 7, 1), DateMidnight.now()),
      Client.appearances(playerId, new DateMidnight(2013, 7, 1), DateMidnight.now())
    ).map { case ((playerh2h, _), playerAppearances) =>
      Ok(views.html.player.playerCard(playerh2h, playerAppearances))
    }
  }

  def head2Head(player1Id: String, player2Id: String) = Action.async { request =>
    FutureZippers.zip(
      Client.playerHead2Head(player1Id, player2Id, new DateMidnight(2013, 7, 1), DateMidnight.now()),
      Client.appearances(player1Id, new DateMidnight(2013, 7, 1), DateMidnight.now()),
      Client.appearances(player2Id, new DateMidnight(2013, 7, 1), DateMidnight.now())
    ).map { case ((player1h2h, player2h2h), player1Appearances, player2Appearances) =>
      Ok(views.html.player.playerHead2Head(player1h2h, player2h2h, player1Appearances, player2Appearances))
    }
  }
}
