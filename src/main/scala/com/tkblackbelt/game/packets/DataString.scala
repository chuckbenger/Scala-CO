package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.constants.StringTypes
import StringTypes._
import com.tkblackbelt.game.server.client.GuildActor.MemberInfo
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast

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
object DataString {

  val packetType: Short = 1015

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val identifier = iter.getInt
    val mode = iter.getByte
    val stringCount = iter.getByte

    def nextString(len: Int) = {
      val bytes = Array.ofDim[Byte](len)
      iter.getBytes(bytes)
      new String(bytes).trim
    }

    val strings = (for {
      i <- 0 to stringCount
      stringLen = iter.getByte
    } yield {
      nextString(stringLen)
    }).toList

    mode match {
      case StringTypes.guildMemberList => GuildMemberList(0, List.empty)
      case x                           => BasicStrings(identifier, mode, strings)
    }
  }
}

abstract class DataString extends Packet(DataString.packetType) {

  val identifier: Int
  val stringType: Byte
  val strings   : List[String]

  override def deconstructed: ByteString = deconstruct { b =>
    b.putInt(identifier)
      .putByte(stringType)
      .putByte(strings.length.toByte)

    strings.foreach { string =>
      b.putByte(string.length.toByte)
        .putBytes(string.getBytes)
    }

    b.putByte(0)
      .putShort(0)

  }

  override def toString: String = super.toString + s"($identifier, $stringType, ${strings.mkString(", ")}))"
}

case class BasicStrings(identifier: Int, stringType: Byte, strings: List[String]) extends DataString

case class BasicString(identifier: Int, stringType: Byte, string: String) extends DataString {
  val strings = List(string)
}

case class GuildName(guildID: Int, name: String) extends DataString with SectorBroadCast {
  val identifier = guildID
  val stringType = guildName
  val strings = List(name)
}

case class GuildMemberList(guildID: Int, memberInfo: List[MemberInfo]) extends DataString {
  val identifier = guildID
  val stringType = guildMemberList
  val strings    = memberInfo.map(_.toString)
}












