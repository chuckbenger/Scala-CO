package com.tkblackbelt.game.server.client.handler.packet

import com.tkblackbelt.game.packets.{Associate, RequestFriend}
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.AssociateHandler.SendFriendRequest
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast
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

object AssociateHandler {
  trait AssociateEvent
  case class SendFriendRequest(uid: Int, uidFrom: Int, nameFrom: String) extends AssociateEvent with SectorBroadCast
}

/**
 * Handler for associations such as friend requests, enemies, etc
 */
trait AssociateHandler extends Client with ActorHelper {


  /**
   * Handles an association packet
   */
  def handleAssociation(asc: Associate) = asc match {
    case RequestFriend(uid, charName) => {
      pendingFriends += uid
      sendSector(SendFriendRequest(uid, char.id, char.name))
    }
    case x => unhandledMsg(x)
  }

}



































