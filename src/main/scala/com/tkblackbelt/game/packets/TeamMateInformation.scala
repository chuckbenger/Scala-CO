package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.models.Character

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
 * This is the Team Member Info packet, it is sent primarily when a member joins a team or is attacked to update its vitals.
 * @see <a href="http://conquerwiki.com/wiki/Teammate_Information_Packet#Version_4267">http://conquerwiki.com/wiki/Teammate_Information_Packet#Version_4267</a>
 */
object TeamMateInformation {

  val packetType: Short = 1026
  val showName = 1

  def fromChar(c: Character) =
    TeamMateInformation(showName, c.name, c.id, c.model, c.hp.toShort, c.hp.toShort)

}

case class TeamMateInformation(flag: Int, name: String, uid: Int, model: Int, maxHP: Short, hp: Short) extends Packet(TeamMateInformation.packetType) {

  override def deconstructed: ByteString = deconstruct {
    _.putByte(0)
    .putByte(flag.toByte)
    .putShort(0)
    .putBytes(name.getBytes.padTo(16, 0.toByte))
    .putInt(uid)
    .putInt(model)
    .putShort(maxHP)
    .putShort(hp)
  }

  override def toString: String = super.toString + s"($flag, $name, $uid, $model, $maxHP, $hp)"
}


















