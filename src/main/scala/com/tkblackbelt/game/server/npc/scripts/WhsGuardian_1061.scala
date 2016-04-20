package com.tkblackbelt.game.server.npc.scripts

import akka.actor.ActorRef
import com.tkblackbelt.game.models.database.Wharehouses
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

class WhsGuardian_1061 extends NpcScript {

  val id = List(1061)


  override def handle(implicit event: NpcInteraction, client: ActorRef) {
    method(event.linkID).invoke(this, event, client)


    @ResponseInit
    def init() {
      val option = hasPassword(event.char.id) match {
        case true  => "Remove Password." -> method("enterPasswordRemove")
        case false => "Put password in my WhareHouse." -> method("enterPassword")
      }
      Options(
        "Hello! What can I do for you?",
        "Let me thing",
        option
      )
    }

    @Response
    def enterPassword =
      Prompt("Please put your password. Min characters: 4, and Max characters: 10. Just numbers is permitted.", "", method("setPassword"))

    @Response
    def enterPasswordRemove =
      Prompt("Please put your password. Min characters: 4, and Max characters: 10. Just numbers is permitted.", "", method("removePassword"))


    @Response
    def setPassword {
      if (!event.message.matches( """(\d\d\d\d)""")) {
        Options(
          "Invalid password format. Min characters: 4, and Max characters: 4. Just numbers is permitted.",
          "I'll try later",
          "Let me try again" -> method("enterPassword")
        )
      } else {
        Info("Your password has been set")
        Wharehouses.setPassword(event.char.id, Some(event.message))
      }
    }

    @Response
    def removePassword {
      if (checkPassword(event.char.id, Some(event.message))) {
        Info("Your password has been removed")
        Wharehouses.setPassword(event.char.id, None)
      } else {
        Options(
          "Your old password was incorrect",
          "I'll try later",
          "Let me try again" -> method("enterPasswordRemove")
        )
      }
    }

    def hasPassword(id: Int) = Wharehouses(id).get.password != None

    def checkPassword(charID: Int, password: Option[String]) = Wharehouses(charID).get.password == password
  }
}




















