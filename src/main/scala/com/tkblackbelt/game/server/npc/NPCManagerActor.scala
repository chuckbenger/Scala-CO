package com.tkblackbelt.game.server.npc

import akka.actor.SupervisorStrategy.Restart
import akka.actor._
import com.tkblackbelt.game.models.database.Npcs
import com.tkblackbelt.game.server.map.MapActor.GetSurroundings
import com.tkblackbelt.game.server.npc.NPCActor.NpcInteraction
import com.tkblackbelt.game.server.npc.NPCManagerActor.AreYouInViewOf
import com.tkblackbelt.game.util.ActorHelper

import scala.concurrent.duration._

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

object NPCManagerActor {
  case class AreYouInViewOf(sender: ActorRef, x: Int, y: Int)
}

/**
 * Manages the npc's for a single map
 * @param map the map the manage npc's for
 */
class NPCManagerActor(map: Int) extends Actor with ActorLogging with ActorHelper {


  import log._

  /**
   * Startup all the npc's
   */
  override def preStart() {
    Npcs.npcs.values.filter(_.mapid.get == map).foreach { npc =>
      context.actorOf(Props.apply(new NPCActor(npc)), npc.npctype.toString)
    }
  }

  /**
   * If an npc crashes restart it
   */
  override def supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1.minute) {
    case _ => Restart
  }

  def receive = {

    case dialog: NpcInteraction => sendToNpc(dialog, dialog.npcID)
    //Ask each npc if they are in view of the client
    case GetSurroundings(client, id, x, y) => context.children.foreach(_ ! AreYouInViewOf(client, x, y))
    case x => unhandledMsg(x)
  }

  def sendToNpc(msg: AnyRef, npcID: Int) {
    context.child(npcID.toString).getOrElse(None) match {
      case npc: ActorRef => npc ! msg
      case None => debug(s"$npcID not found on $map $msg")
    }
  }
}




























