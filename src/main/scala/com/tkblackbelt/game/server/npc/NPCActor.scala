package com.tkblackbelt.game.server.npc

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.tkblackbelt.game.models.{Character, NPC}
import com.tkblackbelt.game.models.Tables.CharacterGuildRow
import com.tkblackbelt.game.packets.{NpcCommands, SpawnNPC}
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler.ServerNpcCommand
import com.tkblackbelt.game.server.npc.NPCActor.NpcInteraction
import com.tkblackbelt.game.server.npc.NPCManagerActor.AreYouInViewOf
import com.tkblackbelt.game.server.npc.scripts.NpcScripts
import com.tkblackbelt.game.util.{Benchmark, ActorHelper}

import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

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

object NPCActor {
  trait NpcEvent
  case class NpcInteraction(char: Character, guild: CharacterGuildRow, npcID: Int, client: ActorRef, linkID: Int = 0, message: String = "") extends NpcEvent {
    def isInitial = linkID == 0
  }


}

/**
 * Npc actor
 * @param npc the npc's model
 */
class NPCActor(npc: NPC) extends Actor with ActorLogging with ActorHelper {

  val script = NpcScripts.get(npc.npctype).init(self, npc.face)

  def receive = {

    case event: NpcInteraction =>
      if (event.linkID != NpcCommands.NO_LINK_BACK)
        Future(Benchmark.time("NPC")(script.handle(event, event.client)))

    case event: ServerNpcCommand =>
      Future(Benchmark.time("NPC")(script.handleCallback(event, event.client)))


    case AreYouInViewOf(client, x, y) => if (npc.isInView(x, y)) client ! SpawnNPC.fromNPC(npc)

    case x => unhandledMsg(x)
  }
}
