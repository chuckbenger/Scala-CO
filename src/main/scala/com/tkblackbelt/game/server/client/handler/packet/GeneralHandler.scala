package com.tkblackbelt.game.server.client.handler.packet

import akka.actor.ActorRef
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.ClientHandler.TeleportTo
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.GeneralHandler.{ChangedDirection, JumpedTo}
import com.tkblackbelt.game.server.map.DMaps
import com.tkblackbelt.game.server.map.MapActor.{GetSurroundings, SectorBroadCast}
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
object GeneralHandler {

  sealed trait GeneralMessage

  case class JumpedTo(client: ActorRef, id: Int, oldX: Int, oldY: Int, newX: Int, newY: Int) extends GeneralMessage with SectorBroadCast

  case class ChangedDirection(client: ActorRef, x: Int, y: Int, direction: Direction) extends GeneralMessage with SectorBroadCast

}

/**
 * Handles general packets sent by the client
 */
trait GeneralHandler extends Client with ActorHelper {

  /**
   * Handles the general update packet from the client
   * @param update the requested update
   */
  def handleGeneralUpdate(update: GeneralUpdate) = update match {
    case update: Avatar            => handleJump(update)
    case dir: Direction            => {
      char.direction = dir.directionFaced
      sendSector(ChangedDirection(self, char.x, char.y, dir))
    }
    case canIJump: PortalJump      => sendMap(canIJump)
    case RetrieveSurroundings      => sendSector(GetSurroundings(self, char.id, char.x, char.y))
    case PositionRequest           => {
      sendSector(EntitySpawn.fromCharacter(char, inventory, guild))
      send(PositionRequest.result(char.id, char.x, char.y, char.map))
    }
    case friend: FriendDataRequest => friendList.sendFriendDetails(friend.id)
    case ItemsRequest(id)          => inventory.send()
    case msg: SectorBroadCast      => sendSector(msg)
    case update: UnhandledUpdate   => unhandledMsg(update)
    case sync: EntitySync          => //todo handle
    case x                         => unhandledMsg(x)
  }

  /**
   * Validates a clients jump request
   */
  private def handleJump(jump: Avatar) {
    DMaps.check(char.map, jump.newX, jump.newY) match {
      case true  => acceptJump(jump)
      case false => rejectJump(jump)
    }
  }

  /**
   * Reject the clients jump request and send them back to their old location
   */
  private def rejectJump(jump: Avatar) {
   // DMaps.dmaps(char.map).checkAndRender(jump.newX, jump.newY)
    moveToMapFrom(TeleportTo(char.map, char.x, char.y))
  }

  /**
   * Accept a clients jump request and advance them on the server
   */
  private def acceptJump(jump: Avatar) {
    sendSector(GetSurroundings(self, char.id, jump.newX, jump.newY))
    sendSector(JumpedTo(self, char.id, char.x, char.y, jump.newX, jump.newY))
    char.x = jump.newX
    char.y = jump.newY
    adjustView()
    send(jump)
  }
}















