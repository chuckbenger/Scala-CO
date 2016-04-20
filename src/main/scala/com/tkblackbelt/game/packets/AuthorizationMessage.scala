package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder

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
 * The Auth Message Packet is sent by the client during initial Authentication, it is sent to the game server,
 * the game server uses the keys to determine which account to link to the client.
 * It is also sent by the auth server when you get your username/password wrong or the server is down for example,
 * the packet contains the string of bytes which identify the message to display.
 *
 * @see <a href="http://conquerwiki.com/wiki/Authorization_Message_Packet#Version_4267">http://conquerwiki.com/wiki/Authorization_Message_Packet#Version_4267</a>
 */
object AuthorizationMessage {

  val packetType: Short = 1052

  def apply(bs: ByteString): AuthorizationMessage = {
    val iter = bs.iterator
    val uid = iter.getInt
    val token = iter.getInt
    val message: Array[Byte] = Array.ofDim(16)

    iter getBytes message

    AuthorizationMessage(uid, token, new String(message).trim)
  }

}

/**
 * @param uid the account id connected to the game account
 * @param token the login token
 * @param message the message sent
 */
case class AuthorizationMessage(uid: Int, token: Int, message: String) extends Packet(AuthorizationMessage.packetType) {


  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putInt(token)
      .putBytes(message.getBytes.padTo(16, 0.toByte))
  }

  override def toString: String = s"${super.toString} ($uid $token $message)"
}



















