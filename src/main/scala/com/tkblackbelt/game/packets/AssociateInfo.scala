package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.models.{Guild, Character}

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
 * The Associate Information packet is used to display information about a friend or an enemy.
 * @see <a href="http://conquerwiki.com/wiki/Associate_Information_Packet#Version_4267">http://conquerwiki.com/wiki/Associate_Information_Packet#Version_4267</a>
 */
object AssociateInfo {

  val packetType: Short = 2033

  def apply(c: Character): Packet =
//todo add guild stuff back
    AssociateInfo(c.id, c.model, c.level.toByte, c.playerClass.toByte, c.pkpoints.toShort, 0, 50, c.spouse)


}

case class AssociateInfo(id: Int, model: Int, level: Byte, playerClass: Byte, pkp: Short, guild: Short, grank: Byte, spouse: String) extends Packet(AssociateInfo.packetType) {

  override def deconstructed: ByteString = deconstruct {
    _.putInt(id)
      .putInt(model)
      .putByte(level)
      .putByte(playerClass)
      .putShort(pkp)
      .putShort(0)
      .putByte(0)
      .putByte(0)
      .putBytes(spouse.getBytes.padTo(16, 0.toByte))
  }

  override def toString: String = super.toString + s"($id, $model, $level, $playerClass, $pkp, $guild, $grank, $spouse)"
}