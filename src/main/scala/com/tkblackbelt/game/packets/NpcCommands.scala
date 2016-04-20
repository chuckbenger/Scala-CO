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
 * Npc dialog
 * @see <a href="http://conquerwiki.com/wiki/Npc_Dialog_Packet#Version_4267">http://conquerwiki.com/wiki/Npc_Dialog_Packet#Version_4267</a>
 */
object NpcCommands {

  val packetType: Short = 2032
  val NO_LINK_BACK      = 255
  val GUILD_KICK        = 358

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    iter.drop(6)
    val link = iter.getByte
    val cmdType = iter.getShort
    val strLen = iter.getByte
    val strBytes = Array.ofDim[Byte](strLen)
    iter.getBytes(strBytes)


    new NpcCommand(link, new String(strBytes).trim, cmdType)
  }


  case class SayCmd(text: String) extends NpcCommand1(NpcCommands.NO_LINK_BACK, CommandTypes.DIALOGUE, text)
  case class InputCmd(text: String, link: Int) extends NpcCommand1(link, CommandTypes.INPUT, text)
  case class LinkCmd(text: String, link: Int) extends NpcCommand1(link, CommandTypes.OPTION, text)
  case class FaceCmd(face: Int) extends NpcCommand2(2544, face, NpcCommands.NO_LINK_BACK, CommandTypes.AVATAR)
  case object FinishCmd extends NpcCommand2(0, 0, NpcCommands.NO_LINK_BACK, CommandTypes.FINISH)
}

class NpcCommand(val linkID: Int, val message: String, val cmdType: Int) extends Packet(NpcCommands.packetType)

class NpcCommand1(linkID: Int, cmdType: Int, line: String) extends NpcCommand(linkID, "", cmdType) {


  override def deconstructed: ByteString = deconstruct {
    _.putInt(0)
      .putShort(0)
      .putByte(linkID.toByte)
      .putByte(cmdType.toByte)
      .putByte(1)
      .putByte(line.length.toByte)
      .putBytes(line.getBytes)
      .putShort(0)
  }

  override def toString: String = s"${super.toString} ($linkID, $cmdType, $line)"
}


class NpcCommand2(UK1: Int, id: Int, linkBack: Int, cmdType: Int) extends NpcCommand(linkBack, "", cmdType) {


  override def deconstructed: ByteString = deconstruct {
    _.putInt(UK1)
      .putShort(id)
      .putByte(linkBack.toByte)
      .putByte(cmdType.toByte)
      .putInt(0)
  }

  override def toString: String = s"${super.toString} ($id, $UK1, $linkBack, $cmdType)"
}


object CommandTypes {
  val NONE     = 0x00
  val DIALOGUE = 0x01
  val OPTION   = 0x02
  val INPUT    = 0x03
  val AVATAR   = 0x04
  val FINISH   = 0x64
}




























