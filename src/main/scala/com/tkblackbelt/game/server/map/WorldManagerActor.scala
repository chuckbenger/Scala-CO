package com.tkblackbelt.game.server.map

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import akka.util.Timeout
import com.tkblackbelt.game.models.database.{GameGuilds, GameMaps}
import com.tkblackbelt.game.server.client.ClientHandler.{Disconnect, LeavingMap}
import com.tkblackbelt.game.server.client.GuildActor
import com.tkblackbelt.game.server.client.handler.server.ServerGuildRequestHandler.{NewGuildStarted, GuildCreated, YourGuildIs}
import com.tkblackbelt.game.server.map.WorldManagerActor._
import com.tkblackbelt.game.server.map.SubscribeHelpers._
import com.tkblackbelt.game.util.ActorHelper
import scala.concurrent.ExecutionContext.Implicits._

import scala.concurrent.duration._

/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 charlesbenger
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

object WorldManagerActor {
  case class SubscribeToWorld(client: ActorRef, id: Int, charName: String)
  case class UnSubscribeFromWorld(client: ActorRef, id: Int, charName: String)
  case class SubscribeToMap(client: ActorRef, map: Int, charName: String)
  case class MovedToMap(mapActor: ActorRef)
  case class UnSubscribeFromMap(client: ActorRef, mapRef: ActorRef, id: Int, x: Int, y: Int, map: Int, charName: String)
  case class GetMyGuild(client: ActorRef, guildID: Int)

}


/**
 * Manages each game map
 */
class WorldManagerActor extends Actor with ActorLogging with ActorHelper {

  val mapEvents = new WorldEventBus
  implicit val timeout = new Timeout(1000 * 10)

  /**
   * Restart a map if it crashes
   */
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
    case _ => Restart
  }

  override def preStart() {
    startMaps()
    startGuilds()
  }

  /**
   * Startup the map actors
   */
  def startMaps() =
    GameMaps.maps.foreach(map => context.actorOf(Props.apply(new MapActor(map, mapEvents)), map.toString))

  /**
   * Startup the guild actors
   */
  def startGuilds() =
    GameGuilds.all.foreach(guild => context.actorOf(Props(new GuildActor(guild, mapEvents)), GuildActor.actorName(guild.guildid)))

  /**
   * Startup a guild actor
   * @param id the guilds if
   * @return the actor reference
   */
  def startGuild(id: Int) = {
    GameGuilds(id) match {
      case Some(info) => context.actorOf(Props(new GuildActor(info, mapEvents)), GuildActor.actorName(id))
      case None => ActorRef.noSender
    }
  }


  def receive = {
    case SubscribeToWorld(client, id, charName) =>
      mapEvents.subscribe(client, ToAllPlayers)
      mapEvents.subscribe(client, ToPlayer(Some(charName)))
      mapEvents.subscribe(client, ToPlayer(Some(id.toString)))


    case SubscribeToMap(client, map, charName) =>
      context.child(map.toString) match {
        case Some(mapRef) =>
          mapEvents.subscribe(client, ToMap(Some(mapRef.path.name)))
          client ! MovedToMap(mapRef)
        case None         => client ! Disconnect
      }

    case UnSubscribeFromMap(client, mapRef, id, x, y, map, charName) =>
      mapEvents.unsubscribe(client, ToMap(Some(mapRef.path.name)))
      mapRef ! LeavingMap(id, x, y)

    case UnSubscribeFromWorld(client, id, charName) =>
      mapEvents.unsubscribe(client, ToAllPlayers)
      mapEvents.unsubscribe(client, ToPlayer(Some(charName)))
      mapEvents.unsubscribe(client, ToPlayer(Some(id.toString)))

    case GetMyGuild(client, guildID) =>
      context.actorSelection(GuildActor.actorName(guildID)) resolveOne() onComplete {
        actor =>
          client ! YourGuildIs(actor.get)
      }

    case GuildCreated(guildID) =>
      if (startGuild(guildID) != ActorRef.noSender)
        sender ! NewGuildStarted


    case x => unhandledMsg(x)
  }
}

























