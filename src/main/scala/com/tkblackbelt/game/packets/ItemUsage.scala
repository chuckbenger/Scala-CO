package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.packets.ItemUsage.genTimer
import com.tkblackbelt.game.util.Timestamp


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
 * The Item Usage packet used for interacting with the warehouse and equiping/un-equiping/updating items.
 * @see <a href="http://conquerwiki.com/wiki/Item_Usage_Packet#Version_4267">http://conquerwiki.com/wiki/Item_Usage_Packet#Version_4267</a>
 */
object ItemUsage {

  val packetType: Short = 1009
  val genTimer          = 0

  def apply(data: ByteString, origData: ByteString): Packet = {
    val iter = data.iterator
    val id = iter.getInt
    val param1 = iter.getInt
    val mode = iter.getInt
    val timestamp = iter.getInt

    mode match {
      case ItemModes.Ping                   => Ping(id, param1, timestamp)
      case ItemModes.BuyItem                => BuyItem(id, param1, timestamp)
      case ItemModes.RemoveInventory        => DropItem(id, timestamp, origData)
      case ItemModes.SellItem               => SellItem(id, param1, timestamp)
      case ItemModes.DropGold               => DropGold(id)
      case ItemModes.EquipItem              => EquipItem(id, param1.toByte)
      case ItemModes.UnEquipItem            => UnEquipItem(id, param1.toByte)
      case ItemModes.ShowWarehouseMoney     => ShowWarehouseMoney(id, 0)
      case ItemModes.DepositWarehouseMoney  => DepositWarehouseMoney(id, param1)
      case ItemModes.WithdrawWarehouseMoney => WithdrawWarehouseMoney(id, param1)
      case x                                => UnhandledItem(id, param1, mode, timestamp)
    }
  }

}

class ItemUsage(id: Int, param1: Int, mode: Int, timestamp: Int) extends Packet(ItemUsage.packetType) {

  override def deconstructed: ByteString = deconstruct {
    _.putInt(id)
      .putInt(param1)
      .putInt(mode)
      .putInt(if (timestamp == genTimer) Timestamp.generate() else timestamp)
  }

  override def toString: String = s"${super.toString} ($id, $param1, $mode, $timestamp)"
}

/**
 * Unhandled Item Usage
 */
case class UnhandledItem(id: Int, param1: Int, mode: Int, timestamp: Int) extends ItemUsage(id, param1, mode, timestamp)

/**
 * Sent by the client every few seconds. Simply reply with the same packet.
 * This mode is responsible for setting the clients ping
 */
case class Ping(id: Int, param1: Int, timestamp: Int) extends ItemUsage(id, param1, ItemModes.Ping, timestamp)

/**
 * Sent when the client buys an item from an npc shop
 */
case class BuyItem(shopID: Int, itemID: Int, timestamp: Int) extends ItemUsage(shopID, itemID, ItemModes.BuyItem, timestamp)

/**
 * Client selling item to an npc shop
 */
case class SellItem(shopID: Int, itemID: Int, timestamp: Int) extends ItemUsage(shopID, itemID, ItemModes.SellItem, timestamp)

/**
 * Client drops an item into the world
 */
case class DropItem(uid: Int, timestamp: Int, origData: ByteString) extends ItemUsage(uid, 0, ItemModes.RemoveInventory, timestamp) {
  override lazy val deconstructed: ByteString = origData
}

/**
 * Client drops gold into the world
 */
case class DropGold(amount: Int) extends ItemUsage(amount, 0, ItemModes.DropGold, genTimer)

/**
 * Remove an item with a specific uid from the client inventory
 */
case class RemoveFromInventory(uid: Int) extends ItemUsage(uid, 0, ItemModes.RemoveInventory, (System.currentTimeMillis() / 10000L).toInt)

/**
 * Equip an item
 */
case class EquipItem(uid: Int, position: Byte) extends ItemUsage(uid, 0, ItemModes.EquipItem, genTimer)

/**
 * Unequipt an item
 */
case class UnEquipItem(uid: Int, position: Byte) extends ItemUsage(uid, position, ItemModes.UnEquipItem, genTimer)

/**
 * Client opened warehouse and want to see the wharehouse money
 */
case class ShowWarehouseMoney(wharehouseID: Int, money: Int) extends ItemUsage(wharehouseID, money, ItemModes.ShowWarehouseMoney, genTimer)

/**
 * Deposit money into a warehouse
 */
case class DepositWarehouseMoney(warehouseID: Int, money: Int) extends ItemUsage(warehouseID, money, ItemModes.DepositWarehouseMoney, genTimer)

/**
 * Withdraw money from a warehouse
 */
case class WithdrawWarehouseMoney(warehouseID: Int, money: Int) extends ItemUsage(warehouseID, money, ItemModes.WithdrawWarehouseMoney, genTimer)

/**
 * Available item modes
 */
object ItemModes {
  val BuyItem                = 1
  val SellItem               = 2
  val RemoveInventory        = 3
  val EquipItem              = 4
  val SetEquipPosition       = 5
  val UnEquipItem            = 6
  val ShowWarehouseMoney     = 9
  val DepositWarehouseMoney  = 10
  val WithdrawWarehouseMoney = 11
  val DropGold               = 12
  val RepairItem             = 14
  val UpdateDurability       = 17
  val RemoveEquipment        = 18
  val UpgradeDragonball      = 19
  val UpgradeMeteor          = 20
  val ShowVendingList        = 21
  val AddVendingItem         = 22
  val RemoveVendingItem      = 23
  val BuyVendingItem         = 24
  val UpdateArrowCount       = 25
  val ParticleEffect         = 26
  val Ping                   = 27
}