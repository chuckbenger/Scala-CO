package com.tkblackbelt.game.server.client.handler.packet

import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.handler.Client
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
trait GuildRequestHandler extends Client with ActorHelper {

  def handleGuildRequest(request: GuildRequest) =
    if (guild.isMember) request match {

      case RequestInfoGuild(charID)
        if charID == char.id => guild.sendGuildInfo()

      case RequestGuildName => guild.sendName()

      case DonateGuild(amt) =>
        if (char.money >= amt) {
          char.money -= amt
          guild.donate(amt)
        }

      case LeaveGuild(_) =>
        if (guild.leave())
          sendSector(EntitySpawn.fromCharacter(char, inventory, guild, force = true))

      case AcceptJoinGuildRequest(charID: Int) => guild.acceptGuildMember(charID, getPlayer(charID))

      case x => unhandledMsg(x)

    } else request match {
      case r: RequestJoinGuild => sendPlayer(r.charID, r.copy(charID = char.id))
      case x                   => unhandledMsg(x)
    }
}
