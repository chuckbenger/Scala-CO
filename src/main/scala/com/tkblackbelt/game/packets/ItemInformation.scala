package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.constants.CommonItemIDs._
import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.models.database.GameItems
import com.tkblackbelt.game.models.flatfile.StaticItems
import com.tkblackbelt.game.packets.SystemMessages.SystemMessage
import com.tkblackbelt.game.structures.ValueRange

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

object ItemInfo {

  val packetType: Short = 1008

  def apply(item: ItemsRow): ItemInformation =
    ItemInformation(item.itemid, item.dura.toByte, item.maxdura.toByte, ItemInfoModes.Default, item.position.toByte, item.soc1.toByte, item.soc2.toByte, 0, 0, item.plus.toByte, item.itemuid)

  /**
   * Create a new item using the default configuration for the passed item id
   * @param id the constants item id
   * @return some item or none
   */
  def apply(id: Int): Option[ItemInformation] = {
    StaticItems(id) match {
      case Some(item) => Some(ItemInformation(item.id, item.maxDura, item.maxDura, ItemInfoModes.Default, ItemPositions.Inventory, 0, 0, 0, 0, 0))
      case _ => None
    }
  }

}


case class ItemInformation(id: Int, dura: Short, maxDura: Short, mode: Short, position: Int, sock1: Byte, sock2: Byte, effect: Byte, luck: Byte, plus: Byte, uid: Int = GameItems.nextID) extends Packet(ItemInfo.packetType) {

  lazy val staticItem    = StaticItems.items.get(id).get
  lazy val pickupMessage = new SystemMessage(s"${staticItem.name} added to inventory!")

  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putInt(id)
      .putShort(dura)
      .putShort(maxDura)
      .putShort(mode)
      .putByte(position.toByte)
      .putByte(sock1)
      .putByte(sock2)
      .putByte(effect)
      .putByte(luck)
      .putByte(plus)
  }

  /**
   * Converts this item info into a table row
   */
  def toItemsRow(owner: Int) = ItemsRow(owner, uid, position, id, plus, sock1, sock2, dura, maxDura)

  def withNextUID = ItemInformation(id, dura, maxDura, mode, position, sock1, sock2, effect, luck, plus)

  def getPhysicalAttackRange = {
    val minAttack = staticItem.minDamage
    val maxAttack = staticItem.maxDamage
    ValueRange(minAttack, maxAttack)
  }

  def getMagicAttackRange = {
    val attack = staticItem.magicAttack
    ValueRange(attack, attack)
  }

  override def toString: String = super.toString + s"($uid, $id, $dura, $maxDura, $mode, $position, $sock1, $sock2, $effect, $luck, $plus)"
}

/**
 * Represents a gold amount
 * @param amount the amount the gold is for
 */
class GoldItem(val amount: Int, uid: Int = GameItems.nextID) extends ItemInformation(0, 0, 0, ItemInfoModes.Default, 0, 0, 0, 0, 0, 0, uid) {

  override val id = amount match {
    case _ if amount <= 10 && amount >= 1 => silver
    case _ if amount <= 100 && amount >= 10 => sycee
    case _ if amount <= 1000 && amount >= 100 => gold
    case _ if amount <= 10000 && amount >= 1000 => goldBar
    case _ if amount > 10000 => goldBars
  }

  override def withNextUID: ItemInformation = new GoldItem(amount, GameItems.nextID)
}


object ItemInfoModes {
  val Default: Byte = 0x01
  val Trade  : Byte = 0x02
  val Update : Byte = 0x03
  val View   : Byte = 0x04
}

object ItemPositions {
  val Inventory: Byte = 0
  val Head     : Byte = 1
  val Necklace : Byte = 2
  val Armor    : Byte = 3
  val Right    : Byte = 4
  val Left     : Byte = 5
  val Ring     : Byte = 6
  val Bottle   : Byte = 7
  val Boots    : Byte = 8

  val OnHand = List[Int](Inventory, Head, Necklace, Armor, Right, Left, Ring, Bottle, Boots)
}



















