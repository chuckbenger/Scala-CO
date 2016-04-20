package com.tkblackbelt.game.packets
import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.models._
import com.tkblackbelt.game.server.map.MapActor.{SectorBroadCast, MapBroadCast}

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
 * The Entity Spawn packet is used to spawn both Monsters and Characters, it does not spawn NPCs. The packet's layout changes slightly depending on which you are spawning.
 * @see <a href="http://conquerwiki.com/wiki/Entity_Spawn_Packet#Version_4267">http://conquerwiki.com/wiki/Entity_Spawn_Packet#Version_4267</a>
 */
object EntitySpawn {

  val packetType: Short = 1014

  /**
   * Returns a new spawn packet from a character
   * @param c the character to create the packet for
   */
  def fromCharacter(c: Character, inv: Inventory, guild: Guild, force: Boolean = false) = {
    val helm = inv.equipment.head match {
      case Some(head) => head.id;
      case _ => 0
    }
    val armor = inv.equipment.armor match {
      case Some(armor) => armor.id;
      case _ => 0
    }
    val rwep = inv.equipment.lwep match {
      case Some(wep) => wep.id;
      case _ => 0
    }
    val lwep = inv.equipment.rwep match {
      case Some(wep) => wep.id;
      case _ => 0
    }

    EntitySpawn(c.id, c.model, 0, guild.info.guildid, guild.info.rank, helm, armor, rwep, lwep, c.x, c.y, c.hairstyle, 0, 0, 1, c.name, force)
  }

}


case class EntitySpawn(id: Int, model: Int, status: Int, guild: Int, guildRank: Int,
                       helm: Int, armor: Int, rWeapon: Int, lWeapon: Int, x: Int,
                       y: Int, hair: Int, direction: Byte, action: Byte, showName: Byte, name: String, force: Boolean = false) extends Packet(EntitySpawn.packetType) with SectorBroadCast {


  override def deconstructed: ByteString = deconstruct {
    _.putInt(id)
      .putInt(model)
      .putInt(status)
      .putShort(guild) //Guild
      .putByte(0)
      .putByte(guildRank.toByte)
      .putInt(helm) //Helm
      .putInt(armor) //Armor
      .putInt(rWeapon) //R Weapon
      .putInt(lWeapon) //L Weapon
      .putInt(0)
      .putInt(0)
      .putShort(x)
      .putShort(y)
      .putShort(hair)
      .putByte(direction) //direction
      .putByte(action) //Action
      .putByte(showName) //str count
      .putByte(name.length.toByte)
      .putBytes(name.getBytes)
  }


  override def toString: String = s"${super.toString}"
}


case class MonsterSpawn(id: Int, mesh: Int, var x: Int, var y: Int, name: String, var hp: Int, level: Byte, direction: Byte) extends Packet(EntitySpawn.packetType) with SectorBroadCast {

  override def deconstructed: ByteString = deconstruct {
    _.putInt(id)
      .putInt(mesh)
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putInt(0)
      .putShort(hp)
      .putShort(level)
      .putShort(x)
      .putShort(y)
      .putShort(0)
      .putByte(direction)
      .putByte(100)
      .putByte(1)
      .putByte(name.length.toByte)
      .putBytes(name.getBytes)

  }
}















