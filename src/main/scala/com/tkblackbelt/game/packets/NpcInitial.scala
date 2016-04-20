package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder

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
 * This is the packet that is sent when a NPC is initialized. Server responds with an NPC dialog packet.
 * @see <a href="http://conquerwiki.com/wiki/Npc_Initial_Packet#Version_4267">http://conquerwiki.com/wiki/Npc_Initial_Packet#Version_4267</a>
 */
object NpcInitial {

  val packetType: Short = 2031

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val id = iter.getInt
    val targetID = iter.getInt
    val action = iter.getShort
    val subAction = iter.getShort

    NpcInitial(id, targetID, action, subAction)
  }

}

/**
 * Initial NPC Dialog Text
 * @param id the npc's id
 * @param targetID the target id
 * @param action the action
 * @param subAction the sub action
 */
case class NpcInitial(id: Int, targetID: Int, action: Short, subAction: Short) extends Packet(NpcInitial.packetType) {


  override def toString: String = s"${super.toString} ($id, $targetID, $action, $subAction)"
}





























