package com.tkblackbelt.core

import akka.util.{ByteString, ByteStringBuilder}
import com.tkblackbelt.auth.packets.LoginRequest
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets._

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
 * Main Packet class used for mapping the raw data of a packet to a usable data structure.
 */
object Packet {

  def apply(bs: ByteString): Packet = {

    val iter = bs.iterator
    val packetType = iter.getShort
    val data = bs.drop(2)

    val packet = packetType match {
      case EntityMove.packetType           => EntityMove(data, bs)
      case LoginRequest.packetType         => LoginRequest(data)
      case AuthorizationMessage.packetType => AuthorizationMessage(data)
      case CharacterCreation.packetType    => CharacterCreation(data)
      case GeneralUpdate.packetType        => GeneralUpdate(data, bs)
      case Chat.packetType                 => Chat(data)
      case ItemUsage.packetType            => ItemUsage(data, bs)
      case NpcInitial.packetType           => NpcInitial(data)
      case NpcCommands.packetType          => NpcCommands(data)
      case Interaction.packetType          => Interaction(data)
      case FloorItemDrops.packetType       => FloorItemDrops(data)
      case Trade.packetType                => Trade(data)
      case Association.packetType          => Association(data)
      case PackagePacket.packetType        => PackagePacket(data)
      case TeamAction.packetType           => TeamAction(data)
      case GuildRequest.packetType         => GuildRequest(data)
      case DataString.packetType           => DataString(data)
      case _                               => new UnHandledPacket(packetType, data)
    }
    new IncomingPacket(packet)
  }
}


class Packet(packetType: Short) {

  /**
   * Lazily created so packet deconstructions are only created if required.
   * Override this to construct the packet.
   * @see deconstruct to assist building
   */
  def deconstructed: ByteString = ByteString.empty


  /**
   * Helper method for building a packets structure. Implicitly adds the packets type
   * @param build the function loaning the packet builder
   * @return returns the built packet
   */
  def deconstruct(build: ByteStringBuilder => Unit): ByteString = {
    val builder = ByteString.newBuilder
    builder.putShort(packetType)
    build(builder)
    builder.result()
  }

  override def toString: String = s"$packetType - (${deconstructed.mkString(" ")})  ${getClass.getName}"
}

/**
 * Unhandled packet
 */
class UnHandledPacket(packetType: Short, data: ByteString) extends Packet(packetType) {

  override def toString: String = super.toString + data.mkString(" ")
}

class IncomingPacket(val packet: Packet) extends Packet(0) {
  override def toString: String = packet.toString
}