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
 * Sent by the client in response to the NEW_ROLE string when the account being used to login doesn't have a character associated with it on the target server.
 * On successfully handling the Character Creation Packet the server responds with a Chat Packet with the message "ANSWER_OK" sent to "ALLUSERS" with the type being ChatType.Dialog.
 * The client will then disconnect and return to the login screen, or in later versions of the client proceed with the typical login procedure.
 *
 * @see <a href="http://conquerwiki.com/wiki/Character_Creation_Packet#Version_4267">http://conquerwiki.com/wiki/Character_Creation_Packet#Version_4267</a>
 */
object CharacterCreation {

  val packetType: Short = 1001

  def apply(bs: ByteString): CharacterCreation = {
    val iter = bs.iterator
    val accountName = Array.ofDim[Byte](16)
    val characterName = Array.ofDim[Byte](16)
    val password = Array.ofDim[Byte](16)

    iter getBytes accountName getBytes characterName getBytes password

    val model = iter.getShort
    val charClass = iter.getShort
    val uid = iter.getInt

    CharacterCreation(new String(accountName).trim,
      new String(characterName).trim,
      new String(password).trim,
      model,
      charClass,
      uid)
  }

}

/**
 * Character creation packet
 * @param accountName the login account name
 * @param name the characters name
 * @param password the login account password
 * @param model the characters model
 * @param acctClass the characters class
 * @param uid the account unique id
 */
case class CharacterCreation(accountName: String,
                             name: String,
                             password: String,
                             model: Short,
                             acctClass: Short,
                             uid: Int) extends Packet(CharacterCreation.packetType) {

  override def toString: String = s"${super.toString} ($uid $accountName $name $password $model $acctClass $uid)"
}