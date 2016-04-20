package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.FloorItemDrops.Modes._
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
 * The Drop Item Packet is used to drop items on the floor, to place a trap, and to pickup items.
 * @see <a href="http://conquerwiki.com/wiki/Drop_Item_Packet#Version_4267">http://conquerwiki.com/wiki/Drop_Item_Packet#Version_4267</a>
 */
object FloorItemDrops {

  val packetType: Short = 1101

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val uid = iter.getInt
    val id = iter.getInt
    val x = iter.getShort
    val y = iter.getShort
    val mode = iter.getByte

    mode match {
      case Modes.Pickup => PickupItem(uid, id, x, y)
      case _            => UnhandledPickup(uid, id, x, y, mode)
    }
  }

  def apply(uid: Int, id: Int, x: Int, y: Int) = VisibleItem(uid, id, x, y)

  object Modes {
    val Visible      : Byte = 1
    val Remove       : Byte = 2
    val Pickup       : Byte = 3
    val CastTrap     : Byte = 10
    val SendTrap     : Byte = 11
    val RemoveTrap   : Byte = 12
    val ScreenEffects: Byte = 13
  }
}


abstract class FloorItemDrop extends Packet(FloorItemDrops.packetType) with SectorBroadCast {

  val uid : Int
  val id  : Int
  val x   : Int
  val y   : Int
  val mode: Byte

  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putInt(id)
      .putShort(x)
      .putShort(y)
      .putByte(mode)
  }

  override def toString: String = super.toString + s"($uid, $id, $x, $y, $mode )"
}

case class UnhandledPickup(uid: Int, id: Int, x: Int, y: Int, mode: Byte) extends FloorItemDrop

case class PickupItem(uid: Int, id: Int, x: Int, y: Int) extends FloorItemDrop {
  val mode = Pickup

  def remove() = RemoveFloorItem(uid, id, x, y)
}

case class RemoveFloorItem(uid: Int, id: Int, x: Int, y: Int) extends FloorItemDrop {
  val mode = Remove
}

case class VisibleItem(uid: Int, id: Int, x: Int, y: Int) extends FloorItemDrop {
  val mode = Visible
}






















