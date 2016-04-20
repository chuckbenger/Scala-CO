package com.tkblackbelt.game.server.map

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import com.tkblackbelt.game.conf.GameConfig
import com.tkblackbelt.game.models.GamePortal
import com.tkblackbelt.game.models.database.GamePortals
import com.tkblackbelt.game.packets.{CreateTeam, PortalJump}
import com.tkblackbelt.game.server.client.TeamActor
import com.tkblackbelt.game.server.client.TeamActor.{AddMemberToTeam, AskLeaderToJoin}
import com.tkblackbelt.game.server.client.handler.packet.TeamActionHandler.TeamJoinRequest
import com.tkblackbelt.game.server.client.handler.server.ServerTeamActionHandler.YourLeaderOfTeam
import com.tkblackbelt.game.server.map.MapActor._
import com.tkblackbelt.game.server.map.MapSectorActor.Setup
import com.tkblackbelt.game.server.map.SubscribeHelpers._
import com.tkblackbelt.game.server.mob.MobManagerActor
import com.tkblackbelt.game.server.npc.NPCActor.NpcInteraction
import com.tkblackbelt.game.server.npc.NPCManagerActor
import com.tkblackbelt.game.util.ActorHelper

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

object MapActor {

  trait BroadCastable
  trait MapBroadCast extends BroadCastable
  trait WorldBroadCast extends BroadCastable
  trait SectorBroadCast extends BroadCastable
  trait MobBroadCast extends SectorBroadCast
  class PlayerBroadCast(val name: String, val msg: AnyRef) extends BroadCastable

  case class GetSurroundings(client: ActorRef, charID: Int, x: Int, y: Int)
  case class ValidPortalJump(portal: GamePortal)
  case object InvalidPortalJump
  class GetMySector(val client: ActorRef, val x: Int, val y: Int)
  case class ClientGetMySector(char: ActorRef, charX: Int, charY: Int) extends GetMySector(char, charX, charY)
  case class MobGetMySector(mob: ActorRef, mobX: Int, mobY: Int) extends GetMySector(mob, mobX, mobY)
  case class YourSectorIs(sector: ActorRef)
}

/**
 * Actor for handling a single map
 * @param mapID the id of the map
 * @param mapEvents the event bus to send global messages to
 */
class MapActor(mapID: Int, mapEvents: WorldEventBus) extends Actor with ActorLogging with ActorHelper {

  import log._

  val dmap = DMaps.dmaps(mapID.toShort)

  //Actors
  val npcManager = startNpcManager
  val portals    = getPortalsForMap
  val mobManager = startMobManager
  val sectors    = startSectors

  publishToSectors(Setup)

  def receive = {
    case msg: GetMySector         => publishToSectors(msg)
    case port: PortalJump         => handlePortalJump(port)
    case npcEvent: NpcInteraction       => npcManager ! npcEvent
    case x: BroadCastable         => publish(x)
    case team: CreateTeam         => startTeam(team)
    case team: TeamJoinRequest    => requestTeamJoin(team)
    case x                        => unhandledMsg(x)
  }

  /**
   * Starts up a new team actor
   * @param team
   */
  def startTeam(team: CreateTeam) {
    val teamID = TeamActor.nextID
    val teamActor = context.actorOf(Props.apply(new TeamActor(team.uid, teamID, mapEvents)), TeamActor.name(team.uid))
    teamActor ! AddMemberToTeam(team.uid, sender)
    sender ! YourLeaderOfTeam(teamActor, teamID)
    debug(s"Team $teamID created")
  }

  /**
   * Request to join a team
   * @param join
   */
  def requestTeamJoin(join: TeamJoinRequest) =
    context.actorSelection(TeamActor.name(join.teamID)) ! AskLeaderToJoin(join.charID, sender)


  /** ¬
    * Handles a portal jump
    * @param portalJump the portal jump to handle
    */
  def handlePortalJump(portalJump: PortalJump) {
    portals.find(_.isValidToJumpTo(portalJump.x, portalJump.y)) match {
      case Some(portal) => sender ! ValidPortalJump(portal)
      case None         => {
        sender ! InvalidPortalJump
        debug(s"Invalid portal jump $portalJump")
      }
    }
  }

  /**
   * Publish an event to all subscribers of this map
   * @param msg the message to send
   */
  def publish(msg: BroadCastable) = msg match {
    case map: MapBroadCast       => mapEvents.publish(MessageEvent(ToMap(Some(mapID.toString)), (msg)))
    case world: WorldBroadCast   => mapEvents.publish(MessageEvent(ToAllPlayers, (msg)))
    case player: PlayerBroadCast => mapEvents.publish(MessageEvent(ToPlayer(Some(player.name)), (player)))
    case x                       => unhandledMsg(x)
  }

  /**
   * Send a message to all map sectors
   */
  def publishToSectors(msg: AnyRef) = sectors.foreach(_ ! msg)

  /**
   * Split the map into n sectors and spawn a actor for each sector
   */
  def startSectors =
    dmap.splitIntoSectors(GameConfig.Settings.sectorsX, GameConfig.Settings.sectorsY).map { sector =>
      context.actorOf(Props.apply(new MapSectorActor(sector._1, sector._2, mapID, self, npcManager, mobManager, mapEvents)), sector._1)
    }

  /**
   * Returns all portals used on this map
   */
  def getPortalsForMap = GamePortals.portals.filter(_._1 == mapID).map(_._2)

  /**
   * Starts this maps mob manager actor
   */
  def startMobManager = context.actorOf(Props.apply(new MobManagerActor(mapID, self)), "MobManager")

  /**
   * Starts this maps npc manager
   */
  def startNpcManager = context.actorOf(Props.apply(new NPCManagerActor(mapID)), "NPCS")

}























