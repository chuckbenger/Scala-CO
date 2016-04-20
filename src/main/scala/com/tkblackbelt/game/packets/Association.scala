package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.Association.Modes._

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
 * The Friend packet, used during login and to update a player when a friend goes online and offline.
 *
 * @see <a href="http://conquerwiki.com/wiki/Associate_Packet#Version_4267">http://conquerwiki.com/wiki/Associate_Packet#Version_4267</a>
 */
object Association {

  val packetType: Short = 1019


  def apply(data: ByteString): Packet = {
    val iter = data.iterator

    val nameBytes = Array.ofDim[Byte](16)
    val uid = iter.getInt
    val mode = iter.getByte
    val online = iter.getByte == 1
    val junk = iter.getShort
    iter.getBytes(nameBytes)
    val name = new String(nameBytes).trim

    mode match {
      case Modes.requestFriend => RequestFriend(uid, name)
      case x                   => new UnhandledAssociate(uid, mode, online, name)
    }
  }

  object Modes {
    val requestFriend   : Byte = 10
    val newFriend       : Byte = 11
    val setOnlineFriend : Byte = 12
    val setOfflineFriend: Byte = 13
    val removeFriend    : Byte = 14
    val addFriend       : Byte = 15
    val setOnlineEnemy  : Byte = 16
    val setOfflineEnemy : Byte = 17
    val removeEnemy     : Byte = 18
    val addEnemy        : Byte = 19
  }
}


abstract class Associate extends Packet(Association.packetType) {

  val uid   : Int
  val mode  : Byte
  val online: Boolean
  val name  : String

  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putByte(mode)
      .putByte(if (online) 1 else 0)
      .putShort(0)
      .putBytes(name.getBytes.padTo(16, 0.toByte))
  }

  override def toString: String = super.toString + s"($uid, $mode, $online, $name)"
}

case class UnhandledAssociate(uid: Int, mode: Byte, online: Boolean, name: String) extends Associate

case class RequestFriend(uid: Int, name: String) extends Associate {
  val mode   = requestFriend
  val online = false
}

case class AddOnlineFriend(uid: Int, name: String) extends Associate {
  val mode   = addFriend
  val online = true
}

case class AddOfflineFriend(uid: Int, name: String) extends Associate {
  val mode   = addFriend
  val online = false
}



















