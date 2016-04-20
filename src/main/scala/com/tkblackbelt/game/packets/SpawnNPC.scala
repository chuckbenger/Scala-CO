package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.models.NPC

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
 * Spawns an npc on the clients screen
 * @see <a href="http://conquerwiki.com/wiki/Npc_Spawn_Packet#Version_4267">http://conquerwiki.com/wiki/Npc_Spawn_Packet#Version_4267</a>
 */
object SpawnNPC {

  val packetType: Short = 2030

  /**
   * Return spawn packet from an npc model
   * @param npc the npc model
   */
  def fromNPC(npc: NPC) =
    SpawnNPC(npc.npctype, npc.x.get, npc.y.get, npc.subtype.get, npc.notnpctype.get, npc.flag)
}

case class SpawnNPC(id: Int, x: Int, y: Int, npcType: Int, direction: Int, interaction: Int) extends Packet(SpawnNPC.packetType) {


  override def deconstructed: ByteString = deconstruct {
    _.putInt(id)
      .putShort(x.toShort)
      .putShort(y.toShort)
      .putShort(npcType.toShort)
      .putShort(direction.toShort)
      .putInt(interaction.toShort)
  }

  override def toString: String = s"${super.toString} ($id $x $y $npcType $direction $interaction)"
}