package com.tkblackbelt.game.server.client.handler.packet

import com.tkblackbelt.core.Packet
import com.tkblackbelt.game.packets._

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

/**
 * Handles packets from the client
 */
trait PacketHandler extends GeneralHandler
                            with ChatCommandHandler
                            with ChatHandler
                            with ItemUsageHandler
                            with EntityMovementHandler
                            with NpcInteractionHandler
                            with InteractionHandler
                            with ItemDropHandler
                            with TradeHandler
                            with AssociateHandler
                            with PackageHandler
                            with TeamActionHandler
                            with GuildRequestHandler
                            with DataStringHandler {


  /**
   * Handle a incoming packet
   * @param packet the packet
   */
  def handlePacket(packet: Packet) = packet match {
    case p: GeneralUpdate => handleGeneralUpdate(p)
    case p: ChatCommand   => handleChatCommand(p)
    case p: ItemUsage     => handleItemUsage(p)
    case p: Chat          => handleChat(p)
    case p: EntityMove    => handleEntityMovement(p)
    case p: NpcInitial    => handleNpcInteraction(p)
    case p: NpcCommand    => handleNpcLinkBack(p)
    case p: Interaction   => handleInteraction(p)
    case p: FloorItemDrop => handleItemDrop(p)
    case p: Trade         => handleTrade(p)
    case p: Associate     => handleAssociation(p)
    case p: PackagePacket => handlePackage(p)
    case p: TeamAction    => handleTeamAction(p)
    case p: GuildRequest  => handleGuildRequest(p)
    case p: DataString    => handleDataString(p)
    case x                => unhandledMsg(x)
  }

}
