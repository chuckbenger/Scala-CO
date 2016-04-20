package com.tkblackbelt.game.server.client.handler.server

import akka.actor.ActorRef
import com.tkblackbelt.core.Packet
import com.tkblackbelt.game.models.database.RespawnPoints
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.ClientHandler._
import com.tkblackbelt.game.server.client.GuildActor.InvitedToGuild
import com.tkblackbelt.game.server.client.TradeBrokerActor.TradeFinished
import com.tkblackbelt.game.server.client.handler.packet.AssociateHandler.AssociateEvent
import com.tkblackbelt.game.server.client.handler.packet.ChatHandler.ChatMessage
import com.tkblackbelt.game.server.client.handler.packet.GeneralHandler.GeneralMessage
import com.tkblackbelt.game.server.client.handler.packet.InteractionHandler.InteractionEvent
import com.tkblackbelt.game.server.client.handler.packet.TradeHandler.TradeEvent
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler.ServerNpcCommand
import com.tkblackbelt.game.server.client.handler.server.ServerGuildRequestHandler.ServerGuildEvent
import com.tkblackbelt.game.server.client.handler.server.ServerHandler.{ForceRespawn, SendToSector}
import com.tkblackbelt.game.server.client.handler.server.ServerStatusUpdateHandler.ServerStatusUpdate
import com.tkblackbelt.game.server.client.handler.server.ServerTeamActionHandler.ServerTeamEvent
import com.tkblackbelt.game.server.map.MapActor.{InvalidPortalJump, ValidPortalJump, YourSectorIs}
import com.tkblackbelt.game.server.map.WorldManagerActor.MovedToMap

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


object ServerHandler {
  case class SendToSector(msg: AnyRef)
  case object ForceRespawn
}

/**
 * Handles messages sent internally by the server
 */
trait ServerHandler extends ServerChatHandler
                            with ServerGeneralHandler
                            with ServerTradeHandler
                            with ServerStatusUpdateHandler
                            with ServerAssociateHandler
                            with ServerInteractionHandler
                            with ServerTeamActionHandler
                            with NpcCommandHandler
                            with ServerGuildRequestHandler {


  /**
   * Send character information to
   * @param to who will receive it
   */
  def sendInfo(to: ActorRef, force: Boolean = false) {
    to ! EntitySpawn.fromCharacter(char, inventory, guild, force)
    to ! RaiseFlag(char.id, char.status)
    guild.sendName(to)
  }


  /**
   * Handle an internal message
   * @param msg the message
   */
  def handleServerMessage(msg: Any) = msg match {

    /**
     * Send a message the clients sector
     */
    case SendToSector(secMsg) => sendSector(secMsg)

    /**
     * Handles a team event
     */
    case team: ServerTeamEvent => handleTeamAction(team)

    /**
     * Add npc to the client screen
     */
    case npc: SpawnNPC => addNPC(npc)

    /**
     * Add character to the clients screen
     */
    case entity: EntitySpawn => addCharacter(entity)

    /**
     * Add a mob to clients screen
     */
    case mob: MonsterSpawn => addMob(mob)

    /**
     * Handle an incoming chat message
     */
    case chat: ChatMessage => handleChat(chat)

    /**
     * General information handler
     */
    case general: GeneralMessage => handleGeneralMessage(general)

    /**
     * Handles updates to a clients state
     */
    case update: ServerStatusUpdate => handleStatusUpdate(update)

    /**
     * Handles a interaction event such as attack
     */
    case interaction: InteractionEvent => handleServerInteractionEvent(interaction)

    /**
     * Trade handler
     */
    case trade: TradeEvent => handleServerTrade(trade)

    /**
     * Trade has finished. Reset the broker
     */
    case TradeFinished => tradeBroker = ActorRef.noSender

    /**
     * Handles a association event such as friend requests
     */
    case asc: AssociateEvent => handleServerAssociation(asc)

    /**
     * Handles npcs interacting with a clients state (taking money, etc)
     */
    case npc: ServerNpcCommand => handleNpcCommand(npc)

    /**
     * A client is requesting entity information from nearby players
     */
    case SendMeYourEntityInfo(client, x, y) => {
      if (client != self) {
        isInView(x, y) match {
          case true  => sendInfo(client)
          case false => client ! CharacterOutOfView(char.id, char.x, char.y)
        }
      }
    }

    case mapMove: MovedToMap => mapMoveComplete(mapMove)

    case YourSectorIs(sector) => {
      //log.debug("Sector changing " + sender.path.name)
      currentSector = sector
      sectorAdjusted()
    }

    /**
     * Teleport the client from x to y
     */
    case teleport: TeleportTo => moveToMapFrom(teleport)

    /**
     * Teleport to previous map
     */
    case TeleportToPreviousMap => {
      val respawn = RespawnPoints.respawnPoints.get(char.previousmap.getOrElse(1002)).get
      moveToMapFrom(TeleportTo(respawn.mapid, respawn.revivex, respawn.revivey))
    }

    case s: ServerGuildEvent => handleServerGuildRequest(s)

    /**
     * Player was invited to a guild
     */
    case InvitedToGuild(guildActor) =>
      guild.acceptedToGuild(guildActor)
      self ! ForceRespawn

    /**
     * Force the client to respawn their entity information
     */
    case ForceRespawn =>
      if (currentSector != null)
        sendInfo(currentSector, force = true)

    /**
     * Another character is out of view
     */
    case CharacterOutOfView(id, x, y) => removeCharacter(id, x, y)

    /**
     * A player is entering the map
     */
    case EnteringMap(entity) => addCharacter(entity)

    /**
     * A player is leaving the map
     */
    case LeavingMap(id, x, y) => removeCharacter(id, x, y)

    /**
     * The map actor has confirmed the portal jump
     */
    case ValidPortalJump(portal) => moveToMapFrom(portal)

    /**
     * The map actor has declined the portal jump
     */
    case InvalidPortalJump => disconnect()

    /**
     * Disconnect the client
     */
    case Disconnect => disconnect()

    /**
     * Forward packet to client
     */
    case packet: Packet => send(packet)

    case x => unhandledMsg(x)
  }

}
