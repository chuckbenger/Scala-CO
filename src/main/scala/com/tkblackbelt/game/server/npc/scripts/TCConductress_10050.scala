package com.tkblackbelt.game.server.npc.scripts

import akka.actor.ActorRef
import com.tkblackbelt.game.constants.MapIDs
import com.tkblackbelt.game.models.database.{GamePortals, RespawnPoints}
import com.tkblackbelt.game.server.client.ClientHandler.TeleportTo
import com.tkblackbelt.game.server.npc.NPCActor.NpcInteraction
import com.tkblackbelt.java.npc.{Response, ResponseInit}

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
 * Responsible for teleporting a character in TwinCity to different cities at a defined cost
 * @see TCConductress_10050#cost
 */
class TCConductress_10050 extends NpcScript {

  lazy val market = RespawnPoints.respawnPoints.get(MapIDs.Market).get
  lazy val ape    = GamePortals.findPortal(MapIDs.ApeMoutain, MapIDs.TwinCity)
  lazy val desert = GamePortals.findPortal(MapIDs.DesertCity, MapIDs.TwinCity)
  lazy val bi     = GamePortals.findPortal(MapIDs.BirdIsland, MapIDs.TwinCity)
  lazy val pc     = GamePortals.findPortal(MapIDs.PhoenixCastle, MapIDs.TwinCity)
  val id  = List(10050)
  val cost = 100

  override def handle(implicit event: NpcInteraction, client: ActorRef) {
    method(event.linkID).invoke(this, event, client)


    @ResponseInit
    def init() {
      Options(
        "Welcome to the Twin City Conductress. Where would you like to go?",
        "No thanks",
        "Ape Mountain" -> method("toApe"),
        "Bird Island" -> method("toBI"),
        "Desert City" -> method("toDC"),
        "Phoenix Castle" -> method("toPC"),
        "Market" -> method("toMarket")
      )
    }

    @Response
    def toApe() =
      sendToClient(TeleportTo(ape.toMap, ape.toX, ape.toY))

    @Response
    def toBI() =
      sendToClient(TeleportTo(bi.toMap, bi.toX, bi.toY))

    @Response
    def toDC =
      sendToClient(TeleportTo(desert.toMap, desert.toX, desert.toY))

    @Response
    def toPC =
      sendToClient(TeleportTo(pc.toMap, pc.toX, pc.toY))

    @Response
    def toMarket =
      sendToClient(TeleportTo(market.revivemapid, market.revivex, market.revivey))

  }
}

























