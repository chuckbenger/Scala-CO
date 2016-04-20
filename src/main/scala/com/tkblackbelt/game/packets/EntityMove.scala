package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals._
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast
import com.tkblackbelt.game.util.Directions

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
 * The EntityMove packet, also known as the Walk packet, this packet is used by all entitys that can move, such as Players, Monsters and if you wish NPCs.
 * @see <a href="http://conquerwiki.com/wiki/Entity_Move_Packet#Version_4267">http://conquerwiki.com/wiki/Entity_Move_Packet#Version_4267</a>
 */
object EntityMove {

  val packetType: Short = 1005

  def apply(data: ByteString, origData: ByteString): Packet = {
    val iter = data.iterator
    val id = iter.getInt
    val direction = ((iter.getByte & 0xFF) % 8).toByte
    val running = iter.getByte == 1
    val payload = Directions.getChange(direction)

    new EntityMove(id, direction, running, payload.xPayload, payload.yPayload, origData)
  }

}

/**
 * @param id The entity that moved
 * @param direction the direction they moved
 * @param running whether they are running
 */
class EntityMove(val id: Int, val direction: Byte, val running: Boolean, val xPayload: Int, val yPayload: Int, val data: ByteString) extends Packet(EntityMove.packetType) with SectorBroadCast {

  override def deconstructed: ByteString = data

  override def toString: String = s"${super.toString} ($id, $direction, $running) ${data.mkString(" ")}"
}

case class MonsterMove(uid: Int, dir: Byte, run: Boolean) extends EntityMove(uid, dir, run, 0, 0, ByteString.empty) {
  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putByte(dir)
      .putByte(if (run) 1 else 0)
  }
}















































