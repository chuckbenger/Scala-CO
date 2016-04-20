package com.tkblackbelt.game.models

import akka.actor.ActorRef
import com.tkblackbelt.game.constants.WareHouseNPCIDs._
import com.tkblackbelt.game.models.Tables.WarehousesRow
import com.tkblackbelt.game.models.WareHouse._
import com.tkblackbelt.game.models.database.{GameItems, Wharehouses}
import com.tkblackbelt.game.packets.{ItemInfo, ItemInformation, ShowWarehouseMoney, WareHouseItems}
import com.tkblackbelt.game.server.GameMessages._
import scala.collection.mutable
import com.tkblackbelt.game.GameServerMain.system.log

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

object WareHouse {
  val noChange = 0
  val maxSlots = 20
}

/**
 * Class for managing a clients WareHouse
 * @param charID the client id
 * @param handler the actor handling client events
 */
class WareHouse(charID: Int, handler: ActorRef) {

  private var warehouse = Wharehouses(charID).getOrElse(WarehousesRow(0, None))

  private lazy val warehouseItems = Map(
    TcWareHouse -> getWarehouseItemsAt(TcWareHouse),
    MarketWareHouse -> getWarehouseItemsAt(MarketWareHouse),
    DCWareHouse -> getWarehouseItemsAt(DCWareHouse),
    PCWareHouse -> getWarehouseItemsAt(PCWareHouse),
    BIWhareHouse -> getWarehouseItemsAt(BIWhareHouse),
    ACWhareHouse -> getWarehouseItemsAt(ACWhareHouse),
    AdventureWhareHouse -> getWarehouseItemsAt(AdventureWhareHouse)
  )

  def save() = Wharehouses.update(warehouse)


  /**
   * Get the warehouse items at a specific warehouse
   * @param id the warehouse id
   */
  def getWarehouseItemsAt(id: Int) = {
    val items = mutable.Map[Int, ItemInformation]()
    GameItems.getWareHouseItems(charID, id).foreach(item => items += item.itemuid -> ItemInfo(item))
    items
  }

  /**
   * Get the gold in a whare house
   * @param warehouseID the warehouse npc
   * @return
   */
  def getGoldAt(warehouseID: Int): Int = warehouseID match {
    case TcWareHouse         => warehouse.twin
    case MarketWareHouse     => warehouse.market
    case DCWareHouse         => warehouse.desert
    case PCWareHouse         => warehouse.phoenix
    case BIWhareHouse        => warehouse.bird
    case ACWhareHouse        => warehouse.ape
    case AdventureWhareHouse => warehouse.stone
  }

  /**
   * Add gold to the warehouse
   * @param warehouseID the warehouse to add gold to
   * @param amount the amount to add
   * @return new amount
   */
  def addGoldTo(warehouseID: Int, amount: Int)(implicit char: Character): Int = {
    if (char.money >= amount) {
      updateGoldAt(warehouseID, amount)
      amount
    } else {
      handler ! MsgNotEnoughMoney
      noChange
    }
  }

  /**
   * Remove gold from the warehouse
   * @param warehouseID the warehouse to remove gold from
   * @param amount the amount to remove
   * @return new amount
   */
  def removeGoldFrom(warehouseID: Int, amount: Int): Int = {
    if (getGoldAt(warehouseID) >= amount) {
      updateGoldAt(warehouseID, -amount)
      amount
    } else {
      handler ! MsgNotEnoughMoney
      noChange
    }
  }

  /**
   * Sends the amount of gold at warehouse x to the client
   * @param warehouseID the warehouse requesting gold at
   */
  def sendGoldAt(warehouseID: Int) = handler ! ShowWarehouseMoney(warehouseID, getGoldAt(warehouseID))

  /**
   * Send the items at a warehouse to the client
   * @param warehouseID the warehouse id
   */
  def sendItemsAt(warehouseID: Int) = handler ! WareHouseItems(warehouseID, warehouseItems(warehouseID).values)

  /**
   * Withdraw an item for the warehouse
   * @param id the warehouse id
   * @param itemID the item to withdraw
   * @param inventory
   */
  def withdrawItem(id: Int, itemID: Int, inventory: Inventory) {
    warehouseItems.get(id) match {
      case Some(items) if !inventory.isFull => items.remove(itemID) match {
        case Some(item) => {
          inventory.add(item)
          sendItemsAt(id)
        }

        case None => handler ! MsgItemNotThere(itemID)
      }

      case None => handler ! MsgInventoryIsFull
    }
  }

  /**
   * Add an item to the warehouse from the invetory
   * @param id the id of the warehouse
   * @param itemID the items id
   * @param inventory the clients inventory
   */
  def addItemFromInventory(id: Int, itemID: Int, inventory: Inventory): Option[ItemInformation] = {
    if (!isFull(id)) {
      inventory.remove(itemID, persist = false) match {
        case Some(item) => addItem(id, item)
        case None       =>
          log.error(s"Failed to add $itemID to clients $inventory")
          None
      }
    } else {
      handler ! MsgWareHouseIsFull
      None
    }
  }

  /**
   * Add an item to the warehouse
   * @param warehouseID the warehouse id
   * @param item the item to add
   */
  def addItem(warehouseID: Int, item: ItemInformation): Option[ItemInformation] = {
    val warehouseItem = item.copy(position = warehouseID)
    warehouseItems.get(warehouseID) match {
      case Some(items) if items.size < maxSlots =>
        items += warehouseItem.uid -> warehouseItem
        GameItems.update(charID, warehouseItem)
        sendItemsAt(warehouseID)
        Some(warehouseItem)
      case None                                 =>
        handler ! MsgWareHouseIsFull
        None
    }
  }

  /**
   * Returns wether a warehouse is full
   * @param warehouseID the warehouse to check
   */
  def isFull(warehouseID: Int) = warehouseItems.get(warehouseID) match {
    case Some(items) => items.size >= maxSlots
    case _           => true
  }

  /**
   * Update the gold at a warehouse
   * @param warehouseID the warehouse to update
   * @param amount the amount to update it by
   * @return the new amount
   */
  private def updateGoldAt(warehouseID: Int, amount: Int): Int = {
    val newAmount = getGoldAt(warehouseID) + amount
    warehouse = warehouseID match {
      case TcWareHouse         => warehouse.copy(twin = newAmount)
      case MarketWareHouse     => warehouse.copy(market = newAmount)
      case DCWareHouse         => warehouse.copy(desert = newAmount)
      case PCWareHouse         => warehouse.copy(phoenix = newAmount)
      case BIWhareHouse        => warehouse.copy(bird = newAmount)
      case ACWhareHouse        => warehouse.copy(ape = newAmount)
      case AdventureWhareHouse => warehouse.copy(stone = newAmount)
    }
    save()
    sendGoldAt(warehouseID)
    getGoldAt(warehouseID)
  }

}



























