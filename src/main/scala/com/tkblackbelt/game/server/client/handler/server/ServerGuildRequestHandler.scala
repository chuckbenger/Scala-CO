package com.tkblackbelt.game.server.client.handler.server

import akka.actor.ActorRef
import com.tkblackbelt.game.packets.EntitySpawn
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.server.ServerGuildRequestHandler._
import com.tkblackbelt.game.server.client.handler.server.ServerHandler.ForceRespawn
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

object ServerGuildRequestHandler {
  trait ServerGuildEvent
  case object KickedFromGuild extends ServerGuildEvent
  case class YourGuildIs(guild: ActorRef) extends ServerGuildEvent
  case object GuildDisbanded extends ServerGuildEvent
  case object Disband extends ServerGuildEvent
  case class GuildCreated(id: Int) extends ServerGuildEvent
  case object NewGuildStarted extends ServerGuildEvent
  case object GuildRankChanged extends ServerGuildEvent
}

/**
 * Handle internally sent server guild messages
 */
trait ServerGuildRequestHandler extends Client with ActorHelper {

  def handleServerGuildRequest(event: ServerGuildEvent) = event match {

    case YourGuildIs(actor)   => guild.setGuildActor(actor)
    case KickedFromGuild      => guild.kicked(); self ! ForceRespawn
    case Disband              => guild.disband()
    case GuildDisbanded       => guild.load(initial = false)
    case create: GuildCreated => mapManager ! create
    case NewGuildStarted      => guild.load(initial = false)
    case GuildRankChanged     => guild.load(initial = false)
    case x                    => unhandledMsg(x)
  }

}
