package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.StatusUpdate.Types._
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

/**
 * The Entity Status packet, also known as the "Update packet" is used to change the appearance(in some cases), certain values unique to
 * a character (such as level, stat points, exp) and show active abilities (in other cases). This packet can be used to send 1 status update, or many.
 * @see <a href="http://conquerwiki.com/wiki/Update_Packet#Version_4267">http://conquerwiki.com/wiki/Update_Packet#Version_4267</a>
 */
object StatusUpdate {

  val packetType: Short = 1017

  object Types {
    val HP            = 0
    val MaxHP         = 1
    val Mana          = 2
    val Money         = 4
    val Experience    = 5
    val PKPoints      = 6
    val Job           = 7
    val Stamina       = 9
    val StatPoints    = 11
    val Model         = 12
    val Level         = 13
    val Spirit        = 14
    val Vitality      = 15
    val Strength      = 16
    val Agility       = 17
    val GuildDonation = 20
    val KOSeconds     = 22
    val raiseFlag     = 26
    val Hairstyle     = 27
    val XPCircle      = 28
    val LocationPoint = 255.toByte
  }
}


abstract class StatusUpdate extends Packet(StatusUpdate.packetType) {

  val uid       : Int
  val count     : Int
  val updateType: Int
  val value     : Int

  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putInt(count)
      .putInt(updateType)
      .putInt(value)
      .putInt(0)
      .putInt(0)
  }

  override def toString: String = super.toString + s"($uid, $count, $updateType, $value)"
}


/**
 * Updates the clients gold value
 */
case class UpdateMoney(uid: Int, value: Int) extends StatusUpdate {
  val count      = 1
  val updateType = Money
}

/**
 * Update the clients hp
 */
case class UpdateHP(uid: Int, value: Int) extends StatusUpdate {
  val count      = 1
  val updateType = HP
}

case class RaiseFlag(uid: Int, value: Int) extends StatusUpdate with SectorBroadCast {
  val count = 1
  val updateType = raiseFlag
}














