package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import Chat.Types

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
 * The Chat packet has changed a lot over the years, it still contains 4 strings(From, To, Suffix, Message),
 * although only 3 (From, To, Message) are now used on official servers. Suffix is now ignored, people were adding "[PM]" & "[GM]" t
 * o the end of their names in order to access admin commands which were built into the client, these commands have since been removed.
 * The suffix was also used to scam people, people believed they were speaking to an official member of staff and gave out their usernames and passwords.
 * You do not need to include a suffix string within your packets, however the length is still required to be 0.
 *
 * @see <a href="http://conquerwiki.com/wiki/Chat_Packet#Version_4267">http://conquerwiki.com/wiki/Chat_Packet#Version_4267</a>
 */
object Chat {

  val packetType: Short = 1004
  val system            = "SYSTEM"

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val color = iter.getInt
    val chatType = iter.getInt
    val id = iter.getInt
    val strCount = iter.getByte

    def readNextString() = {
      val len = iter.getByte
      val bytes = Array.ofDim[Byte](len)
      iter.getBytes(bytes)
      new String(bytes).trim
    }

    val from = readNextString()
    val to = readNextString()
    val suffix = readNextString()
    val message = readNextString()

    if (message startsWith "/")
      new ChatCommand(from, to, message)
    else
      chatType match {
        case Types.talk          => Talk(from, to, message, id)
        case Types.service       => BroadCast(from, message, id)
        case Types.whisper       => Whisper(from, to, message, id)
        case Types.guildBulletin => GuildBulletin(message.take(50))
        case Types.guild         => GuildMsg(from, to, message)
        case _                   => ChatMessage(from, to, message, chatType, id)
      }
  }

  /**
   * Available Chat Types
   */
  object Types {
    val action                = 0x7d2
    val broadcast             = 0x7da
    val center                = 0x7db
    val dialog                = 0x834
    val friend                = 0x7d9
    val friendBoard           = 0x89a
    val friendsOfflineMessage = 0x83e
    val ghost                 = 0x7dd
    val guild                 = 2004
    val guildBoard            = 0x89c
    val guildBulletin         = 0x83f
    val loginInformation      = 0x835
    val minimap               = 0x83c
    val minimap2              = 0x83d
    val othersBoard           = 0x89d
    val service               = 0x7de
    val spouse                = 0x7d6
    val talk                  = 0x7d0
    val team                  = 0x7d3
    val teamBoard             = 0x89b
    val top                   = 0x7d5
    val tradeBoard            = 0x899
    val vendorHawk            = 0x838
    val website               = 0x839
    val whisper               = 0x7d1
    val yell                  = 0x7d8
  }
}

abstract class Chat extends Packet(Chat.packetType) {

  val msgID   : Int
  val from    : String
  val to      : String
  val message : String
  val chatType: Int

  override def deconstructed: ByteString = deconstruct {
    _.putByte(222.toByte)
      .putByte(222.toByte)
      .putByte(222.toByte)
      .putByte(0.toByte)
      .putInt(chatType)
      .putInt(msgID)
      .putByte(4.toByte)
      .putByte(from.length.toByte)
      .putBytes(from.getBytes)
      .putByte(to.length.toByte)
      .putBytes(to.getBytes)
      .putByte(0.toByte)
      .putByte(message.length.toByte)
      .putBytes(message.getBytes)
  }


  override def toString: String = s"${super.toString} ($msgID $from $to $message $chatType)"
}

case class ChatMessage(from: String, to: String, message: String, chatType: Int, msgID: Int) extends Chat

/**
 * Command initiated from the client
 * @param from who it's from
 * @param to who it's directed at
 * @param message the chat command
 */
case class ChatCommand(from: String, to: String, message: String) extends Chat {
  val msgID    = 0
  val chatType = 0
}

/**
 * General Talk Message
 * @param msgID the message id
 * @param from who it's from
 * @param to who it's directed at
 * @param message the message
 */
case class Talk(from: String = Chat.system, to: String, message: String, msgID: Int = 0) extends Chat {
  val chatType = Types.talk
}

/**
 * Broadcasts a message to the entire server
 * @param msgID the message id
 * @param from who it's from
 * @param message the message
 */
case class BroadCast(from: String, message: String, msgID: Int = 0) extends Chat {
  val to       = "World"
  val chatType = Types.service
}

/**
 * Clients sending a direct message to another client
 * @param msgID the message id
 * @param from who it's from
 * @param to who it's to
 * @param message the message it's self
 */
case class Whisper(from: String, to: String, message: String, msgID: Int = 0) extends Chat {
  val chatType = Types.whisper
}

/**
 * Change/set the guild bulletin message
 * @param message the new message
 */
case class GuildBulletin(message: String) extends Chat {
  val chatType = Types.guildBulletin
  val from     = ""
  val to       = ""
  val msgID    = 0
}

/**
 * Message sent to guild members
 */
case class GuildMsg(from: String, to: String, message: String) extends Chat {
  val chatType = Types.guild
  val msgID    = 0
}

/**
 * System messages that can be sent to the client
 */
object SystemMessages {
  case class SystemMessage(message: String, to: String = "", chatType: Int = Types.top) extends Chat {
    val from  = "SYSTEM"
    val msgID = 0
  }
  object NotAGM extends SystemMessage("Only GM's can use commands")
  object CommandNotFound extends SystemMessage("Command not found")


  object NewUser extends SystemMessage("NEW_ROLE", "ALL_USERS", Types.loginInformation)
  object SuccessLogin extends SystemMessage("ANSWER_OK", "ALL_USERS", Types.loginInformation)
}

case class CenterMessage(message: String) {
  val chatType = Types.center
  val from     = Chat.system
  val to       = ""
  val msgID    = 0
}














