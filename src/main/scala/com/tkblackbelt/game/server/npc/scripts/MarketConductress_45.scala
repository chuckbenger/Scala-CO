package com.tkblackbelt.game.server.npc.scripts

import akka.actor.ActorRef
import com.tkblackbelt.game.constants.MapIDs
import com.tkblackbelt.game.models.database.{GamePortals, RespawnPoints}
import com.tkblackbelt.game.server.client.ClientHandler.TeleportToPreviousMap
import com.tkblackbelt.game.server.npc.NPCActor.NpcInteraction
import com.tkblackbelt.java.npc.{ResponseInit, Response}

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
 * Market conductress will teleport a client to his previous map at no cost :)
 */
class MarketConductress_45 extends NpcScript {

  lazy val market = RespawnPoints.respawnPoints.get(MapIDs.Market).get
  lazy val ape    = GamePortals.findPortal(MapIDs.ApeMoutain, MapIDs.TwinCity)
  lazy val desert = GamePortals.findPortal(MapIDs.DesertCity, MapIDs.TwinCity)
  lazy val bi     = GamePortals.findPortal(MapIDs.BirdIsland, MapIDs.TwinCity)
  lazy val pc     = GamePortals.findPortal(MapIDs.PhoenixCastle, MapIDs.TwinCity)
  val id = List(45)

  override def handle(implicit event: NpcInteraction, client: ActorRef) {
    method(event.linkID).invoke(this, event, client)

    @ResponseInit
    def init() {
      Options(
        "Hello! Would you like to leave the market?",
        "No",
        "Yes" -> method("returnToPreviousMap")
      )
    }

    /**
     * Returns the client to their previous map
     */
    @Response
    def returnToPreviousMap(){
      sendToClient(TeleportToPreviousMap)
    }

  }


}

























