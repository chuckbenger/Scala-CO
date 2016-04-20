package com.tkblackbelt.game.server.client.handler.packet

import com.tkblackbelt.game.models.database.ShopItems
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.ItemUsageHandler.DropThisItem
import com.tkblackbelt.game.server.map.FloorActor.FloorEvent
import com.tkblackbelt.game.util.ActorHelper
import com.tkblackbelt.game.server.GameMessages._

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

object ItemUsageHandler {
  case class DropThisItem(item: ItemInformation, x: Int, y: Int)
}

/**
 * Handles a client use of items
 */
trait ItemUsageHandler extends Client with ActorHelper {

  def handleItemUsage(item: ItemUsage) = item match {
    case ping: Ping                      => send(ping)
    case buy: BuyItem                    => buyItem(buy)
    case drop: DropItem                  => dropItem(drop)
    case sell: SellItem                  => sellItem(sell)
    case DropGold(amount)                => dropGold(amount)
    case EquipItem(itemID, pos)          => inventory.equip(itemID, pos)
    case UnEquipItem(itemID, pos)        => inventory.unEquip(pos)
    case ShowWarehouseMoney(id, _)       => warehouse.sendGoldAt(id)
    case DepositWarehouseMoney(id, amt)  => char.money -= warehouse.addGoldTo(id, amt)
    case WithdrawWarehouseMoney(id, amt) => char.money += warehouse.removeGoldFrom(id, amt)
    case x                               => unhandledMsg(x)
  }

  /**
   * Handles a clients npc item buy transaction
   */
  def buyItem(buy: BuyItem) {
    ShopItems.buy(buy, char.money) match {
      case Some(item) if inventory.add(item) != None =>
        char.money -= item.staticItem.cost
      case None => send(MsgNotEnoughMoney)
    }
  }

  /**
   * Sell an item to a npc
   * @param sell the item to be sold
   */
  def sellItem(sell: SellItem) {
    inventory.remove(sell.itemID) match {
      case Some(item) => char.money += item.staticItem.salePrice
      case _          => send(MsgItemSellFailed)
    }
  }

  /**
   * Handle a clients attemp to drop an item
   */
  def dropItem(drop: DropItem) {
    inventory.remove(drop.uid) match {
      case Some(item) => sendSector(FloorEvent(DropThisItem(item, char.x, char.y)))
      case x          => unhandledMsg(x)
    }
  }

  /**
   * Drops gold into the world
   * @param amount the amount to drop
   */
  def dropGold(amount: Int) {
    if (char.money >= amount) {
      char.money -= amount
      sendSector(FloorEvent(DropThisItem(new GoldItem(amount), char.x, char.y)))
    }
  }


}


























