package com.tkblackbelt.game.models

import akka.actor.ActorRef
import com.tkblackbelt.game.models.Inventory._
import com.tkblackbelt.game.models.database.GameItems
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.GameMessages._
import com.tkblackbelt.game.GameServerMain.system.log
import scala.collection.mutable

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

object Inventory {
  val maxSize = 40
}

/**
 * Model for handling a client inventory
 * @param charId the charid for the inventory
 */
class Inventory(charId: Int, handler: ActorRef) {

  val items     = mutable.Map[Int, ItemInformation]()
  val equipment = new Equipment(charId, handler)

  def inventory = items.filter(_._2.position == ItemPositions.Inventory)

  /**
   * Get an item a specific position
   * @param id the item id
   */
  def apply(id: Int) = items.get(id)

  /**
   * Number of spots left
   */
  def spaceLeft = maxSize - inventory.size

  /**
   * Returns whether the clients inventory is full
   */
  def isFull = spaceLeft == 0

  /**
   * Load the clients inventory from the database
   * @return a future for when the load is complete
   */
  def load() = {
    GameItems.getOnHandItems(charId).map(item => item.itemuid -> ItemInfo(item)).foreach(item => items += item)
    items.values.filter(_.position != ItemPositions.Inventory).foreach(item => equipment.updateFromPosition(item.position, Some(item)))
  }

  /**
   * Save the inventory to the database
   * @return returns a future for when it completes
   */
  def save() = {
    GameItems.update(charId, items.values.toList)
  }

  /**
   * Send the inventory items to the client
   */
  def send() {
    items.foreach(handler ! _._2)
  }

  /**
   * Equips an item with validations
   * @param id the id of the item to equip
   * @param position the position it will be bound to
   */
  def equip(id: Int, position: Byte)(implicit char: Character) {
    items.get(id) match {
      case Some(item) => equip(item, position)
      case None       => println(s"Failed to equip item $id. ")
    }
  }

  /**
   * Equip an item at a postion
   * @param item the item to equip
   * @param pos it's position
   */
  private def equip(item: ItemInformation, pos: Byte) {
    unEquip(pos)
    val toEquip = item.copy(position = pos)
    remove(item.uid, persist = false)
    add(toEquip, persist = false)
    handler ! RemoveFromInventory(toEquip.uid)
    GameItems.update(charId, toEquip)
    equipment.updateFromPosition(pos, Some(toEquip))
  }

  /**
   * Unequip and item
   * @param position the position of the item to unequip
   */
  def unEquip(position: Byte) {
    items.find(_._2.position == position) match {
      case Some(item) => {
        val toInventory = item._2.copy(position = ItemPositions.Inventory)
        remove(item._1, persist = false)
        add(toInventory, persist = false)
        handler ! UnEquipItem(toInventory.uid, position)
        GameItems.update(charId, toInventory)
        equipment.updateFromPosition(position, None)
      }
      case None       =>
    }
  }

  /**
   * Add a item to the inventory
   */
  def add(item: ItemInformation, persist: Boolean = true): Option[ItemInformation] = {
    if (!isFull) {
      val addItem = item.copy(position = ItemPositions.Inventory)
      items.put(addItem.uid, addItem)
      items.get(addItem.uid) match {
        case Some(newItem) =>
          if (persist)
            GameItems.update(charId, newItem)
          handler ! newItem
          Some(newItem)
        case _ =>
          log.error(s"Failed to add $item to $charId inventory.")
          None
      }
    } else {
      handler ! MsgInventoryIsFull
      None
    }
  }

  /**
   * Remove a specific item info
   */
  def remove(item: ItemInformation): Option[ItemInformation] =
    remove(item.uid)


  /**
   * Remove an item for a specific uid
   */
  def remove(id: Int, persist: Boolean = true): Option[ItemInformation] = {
    items.remove(id) match {
      case Some(item) =>
        if (persist)
          GameItems.deleteItem(item.uid)
        handler ! RemoveFromInventory(item.uid)
        Some(item)
      case _          => None
    }
  }
}
























