package com.tkblackbelt.game.server.client.handler.server

import akka.actor.ActorRef
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler._
import com.tkblackbelt.game.server.npc.NPCActor.NpcEvent
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

object NpcCommandHandler {
  abstract class ServerNpcCommand extends NpcEvent {
    val npc   : ActorRef
    val client: ActorRef
    val message: String = ""
    val linkID: Int
  }
  case class NpcSendMeYourID(npc: ActorRef, client: ActorRef, linkID: Int, charID: Int = 0, override val message: String = "") extends ServerNpcCommand
  case class NpcMoneyAvailable(npc: ActorRef, client: ActorRef, linkID: Int, money: Int) extends ServerNpcCommand
  case class ChangeGuildLeaderShip(npc: ActorRef, client: ActorRef, linkID: Int, requestorID: Int, name: String, msg: String = "") extends ServerNpcCommand
  case class AssignDeputy(npc: ActorRef, client: ActorRef, linkID: Int, removing: Boolean, requestorID: Int, name: String, msg: String = "") extends ServerNpcCommand
}

trait NpcCommandHandler extends Client with ActorHelper {

  def handleNpcCommand(request: ServerNpcCommand) = request match {

    case npc: NpcSendMeYourID        => npc.npc ! npc.copy(charID = char.id)
    case npc: NpcMoneyAvailable      => npc.copy(money = char.money)
    case pass: ChangeGuildLeaderShip => guild.changeLeader(pass)
    case dep: AssignDeputy           => guild.assignDeputy(dep)

    case x => unhandledMsg(x)
  }

}
