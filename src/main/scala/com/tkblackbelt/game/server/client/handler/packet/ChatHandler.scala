package com.tkblackbelt.game.server.client.handler.packet

import akka.actor.ActorRef
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.ChatHandler.{GuildMessage, BroadCastMessage, PlayerMessage, TalkMessage}
import com.tkblackbelt.game.server.map.MapActor.{PlayerBroadCast, SectorBroadCast, WorldBroadCast}
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

object ChatHandler {
  sealed trait ChatMessage
  case class TalkMessage(sender: ActorRef, x: Int, y: Int, chat: Chat) extends ChatMessage with SectorBroadCast
  case class BroadCastMessage(sender: ActorRef, broadCast: BroadCast) extends ChatMessage with WorldBroadCast
  case class PlayerMessage(to: String, whisper: Whisper) extends PlayerBroadCast(to, whisper) with ChatMessage
  case class GuildMessage(sender: ActorRef, guild: GuildMsg) extends ChatMessage
}


/**
 * Handles chat packets sent by the client
 */
trait ChatHandler extends Client with ActorHelper {

  /**
   * Handles an incoming chat packet from the client
   * @param chat the chat packet to handle
   */
  def handleChat(chat: Chat) = chat match {
    case talk: Talk                    => sendSector(TalkMessage(self, char.x, char.y, chat))
    case broadCast: BroadCast          => sendMap(BroadCastMessage(self, broadCast))
    case whisper: Whisper              =>
      if (whisper.from equals char.name)
        sendMap(PlayerMessage(whisper.to, whisper))
    case GuildBulletin(newMsg: String) => guild.setBulletin(newMsg)
    case msg: GuildMsg                 => guild.talk(GuildMessage(self, msg))
    case x                             => unhandledMsg(x)
  }

}
