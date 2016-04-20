package com.tkblackbelt.game.server.client.handler.server

import com.tkblackbelt.game.packets.Jump
import com.tkblackbelt.game.server.client.ClientHandler.SendMeYourEntityInfo
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.GeneralHandler.{ChangedDirection, GeneralMessage, JumpedTo}
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

/**
 * Responsible for handling general packet related server events
 */
trait ServerGeneralHandler extends Client with ActorHelper {

  /**
   * Handles a general packet related message
   * @param msg the message to handle
   */
  def handleGeneralMessage(msg: GeneralMessage) = msg match {

    //Someone jumped from position x to y
    case JumpedTo(client, id, oldX, oldY, newX, newY) => {
      if (id != char.id && !(clientsSpawned contains id)) {
        client ! SendMeYourEntityInfo(self, char.x, char.y)
      } else {
        if (isInView(newX, newY))
          send(Jump(id, oldX, oldY, newX, newY))
        else
          removeCharacter(id, oldX, oldY)
      }
    }
    //Someone changes direction
    case ChangedDirection(client, x, y, dir) => {
      if (isInView(x, y))
        send(dir)
    }
  }

}
