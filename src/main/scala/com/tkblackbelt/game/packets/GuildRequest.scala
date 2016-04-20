package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import GuildRequest.Modes._

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
object GuildRequest {

  val packetType: Short = 1107

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val mode = iter.getInt
    val param = iter.getInt

    mode match {
      case Modes.requestInfo => RequestInfoGuild(param)
      case Modes.requestName => RequestGuildName
      case Modes.donate      => DonateGuild(param)
      case Modes.quit        => LeaveGuild(param)
      case Modes.requestJoin => RequestJoinGuild(param)
      case Modes.acceptJoin  => AcceptJoinGuildRequest(param)
      case _                 => UnhandledGuildRequest(mode, param)
    }
  }

  object Modes {
    val requestJoin  = 1
    val acceptJoin   = 2
    val quit         = 3
    val requestName  = 6
    val ally         = 7
    val neutral      = 8
    val enemy        = 9
    val unEnemy      = 10
    val donate       = 11
    val requestInfo  = 12
    val updateGuild  = 13
    val updateBranch = 14
    val uniteSubSyn  = 15
    val uniteSyn     = 16
    val setWhiteSyn  = 17
    val setBlackSyn  = 18
    val leave        = 19
  }
}

abstract class GuildRequest extends Packet(GuildRequest.packetType) {
  val mode : Int
  val param: Int


  override def deconstructed: ByteString = deconstruct {
    _.putInt(mode)
      .putInt(param)
  }

  override def toString: String = super.toString + s"($mode, $param)"
}

case class UnhandledGuildRequest(mode: Int, param: Int) extends GuildRequest

case class RequestInfoGuild(charID: Int) extends GuildRequest {
  val mode  = requestInfo
  val param = charID
}

case object RequestGuildName extends GuildRequest {
  val mode  = requestName
  val param = 0
}

case class DonateGuild(amount: Int) extends GuildRequest {
  val mode  = donate
  val param = amount
}


case class NeutralGuild(guildID: Int) extends GuildRequest {
  val mode  = neutral
  val param = guildID
}

case class LeaveGuild(guildID: Int) extends GuildRequest {
  val mode  = quit
  val param = guildID
}

case class RequestJoinGuild(charID: Int) extends GuildRequest {
  val mode = requestJoin
  val param = charID
}

case class AcceptJoinGuildRequest(charID: Int) extends GuildRequest {
  val mode = acceptJoin
  val param = charID
}