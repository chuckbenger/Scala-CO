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
 * The Guild Information Packet or Guild Status Packet is sent every time you open the guild window in the client.
 * @see <a href="http://conquerwiki.com/wiki/Guild_Information_Packet#Version_4267">http://conquerwiki.com/wiki/Guild_Information_Packet#Version_4267</a>
 */
object GuildInformation {

  val packetType: Short = 1106

}


case class GuildInformation(guildID: Int, donation: Int, fund: Int, members: Int, rank: Byte, leaderName: String) extends Packet(GuildInformation.packetType) {

  override def deconstructed: ByteString = deconstruct {
    _.putInt(guildID)
    .putInt(donation)
    .putInt(fund)
    .putInt(members)
    .putByte(rank)
    .putBytes(leaderName.getBytes.padTo(16, 0.toByte))
  }
}
