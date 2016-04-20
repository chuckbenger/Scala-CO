package com.tkblackbelt.bot

import akka.actor.ActorRef
import akka.io.Tcp.Closed
import com.tkblackbelt.core.{IncomingPacket, Packet}
import com.tkblackbelt.game.models.Character
import com.tkblackbelt.game.server.client.ClientHandler
import com.tkblackbelt.game.server.client.ClientHandler.{Disconnect, Persist, SendPacket}

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

object AIClientHandler {
  case object DoYourAI
  var packets = BigInt(0)
  var chatCOunter = BigInt(0)
}

class AIClientHandler(override val char: Character,
  override val connection: ActorRef,
  override val mapManager: ActorRef) extends ClientHandler(char, connection, mapManager) {


  def defaultReceive(x: Any) = x match {
    case p: IncomingPacket => handlePacket(p.packet)
    case Persist           => persist()
    case Closed            => onDisconnect()
    case Disconnect        => disconnect()
    case m: Any            => handleServerMessage(m)
    //case x                 => unhandledMsg(x)
  }

  override def send(p: Packet) = {
    AIClientHandler.packets += 1
    if (connection != ActorRef.noSender) {

      connection ! SendPacket(p)
    }
  }

  override def preStart {
    moveToMap(char.map)
  }

}
