package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.Interaction.Modes
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast
import com.tkblackbelt.game.util.Timestamp

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
 * The Interact packet is most commonly used for direct melee/archer attacks, but also used for certain player to player actions, such as marriage.
 * @see <a href="http://conquerwiki.com/wiki/Interact_Packet#Version_4267">http://conquerwiki.com/wiki/Interact_Packet#Version_4267</a>
 */
object Interaction {

  val packetType: Short = 1022

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val timer = iter.getInt
    val uid = iter.getInt
    val tid = iter.getInt
    val tx = iter.getShort
    val ty = iter.getShort
    val mode = iter.getByte
    val damage = iter.getInt
    mode match {
      case Modes.PhysicalAttack => PhysicalAttack(timer, uid, tid, tx, ty, damage)
      case x                    => UnhandledInteraction(timer, uid, tid, tx, ty, mode, damage)
    }
  }

  object Modes {
    val None           : Byte = 0
    val PhysicalAttack : Byte = 2
    val RequestMarriage: Byte = 8
    val AcceptMarriage : Byte = 9
    val SendFlowers    : Byte = 13
    val Death          : Byte = 14
    val RushAttack     : Byte = 20
    val MagicAttack    : Byte = 21
    val WeaponReflect  : Byte = 23
    val DashEffect     : Byte = 24
    val ArcherAttack   : Byte = 25
    val MagicReflect   : Byte = 26
  }

}

abstract class Interaction extends Packet(Interaction.packetType) with SectorBroadCast {

  val timer   : Int
  val uid     : Int
  val targetID: Int
  val targetX : Int
  val targetY : Int
  val mode    : Byte
  val damage  : Int

  override def deconstructed: ByteString = deconstruct {
    _.putInt(Timestamp.generate())
      .putInt(uid)
      .putInt(targetID)
      .putShort(targetX)
      .putShort(targetY)
      .putInt(mode)
      .putInt(damage)
  }

  override def toString: String = s"${super.toString} ($uid, $targetID, $targetX, $targetY, $mode, $damage)"
}

case class UnhandledInteraction(timer: Int, uid: Int, targetID: Int, targetX: Int, targetY: Int, mode: Byte, damage: Int) extends Interaction

case class PhysicalAttack(timer: Int, uid: Int, targetID: Int, targetX: Int, targetY: Int, damage: Int) extends Interaction {
  val mode = Modes.PhysicalAttack
}

case class Death(uid: Int, targetID: Int, targetX: Int, targetY: Int) extends Interaction {
  val mode = Modes.Death
  val damage = 0
  val timer = 0
}


























