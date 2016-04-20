package com.tkblackbelt.game.server.npc.scripts

import akka.actor.ActorRef
import com.tkblackbelt.game.models.database.Wharehouses
import com.tkblackbelt.game.packets.WhareHouseOpen
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
class Wharehouse_all extends NpcScript {

  val id = List(10028,
                10027,
                10012,
                10011,
                10006,
                4101,
                4000,
                0044)


  override def handle(implicit event: NpcInteraction, client: ActorRef) {
    method(event.linkID).invoke(this, event, client)

    @ResponseInit
    def init() {
      getPassword(event.char.id) match {
        case Some(password) => Prompt("", "Password", method("checkPassword"))
        case None           => open(event.char.id)
      }
    }

    @Response
    def checkPassword() =
      if (getPassword(event.char.id).getOrElse("") == event.message)
        open(event.char.id)
      else
        Error("Invalid warehouse password")

    def open(charID: Int) = sendToClient(WhareHouseOpen(charID))

    def getPassword(charID: Int) = Wharehouses(charID).get.password

  }

}
