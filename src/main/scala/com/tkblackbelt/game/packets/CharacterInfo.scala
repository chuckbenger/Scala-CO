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
 * The Character Information packet, this packet is sent primarily during the login process to set the majority of your characters values.

 * @see <a href="http://conquerwiki.com/wiki/Character_Information_Packet#Version_4267">http://conquerwiki.com/wiki/Character_Information_Packet#Version_4267</a>
 */
object CharacterInfo {

  val packetType: Short = 1006
}

case class CharacterInfo(char: Character) extends Packet(CharacterInfo.packetType) {


  override def deconstructed: ByteString = deconstruct {
    _.putInt(char.id)
      .putInt(char.model)
      .putShort(311)
      .putShort(0)
      .putInt(char.money) //Gold
      .putInt(char.exp.toInt) //Exp
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putShort(char.str) //str
      .putShort(char.dex) //dex
      .putShort(char.vit) //vit
      .putShort(char.spi) //spir
      .putShort(char.statpoints) //stat
      .putShort(char.hp) //hp
      .putShort(char.mp) //MP
      .putShort(char.pkpoints) //PKP
      .putByte(char.level.toByte) //lvl
      .putByte(char.playerClass.toByte)
      .putByte(1)
      .putByte(char.reborn.toByte) //Reborn
      .putByte(1)
      .putByte(2)
      .putByte(char.name.length.toByte)
      .putBytes(char.name.getBytes)
  }


  override def toString: String = s"${super.toString} ($char)"
}
