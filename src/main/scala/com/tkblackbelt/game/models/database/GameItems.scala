package com.tkblackbelt.game.models.database

import java.util.concurrent.atomic.AtomicInteger

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.packets.{ItemInformation, ItemPositions}

import scala.concurrent.Future
import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession
import scala.concurrent.ExecutionContext.Implicits._

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
 * Interact with the items table
 */
object GameItems {

  val startingID = getHighestID + 1

  private val itemUIDS = new AtomicInteger(startingID)

  def nextID = itemUIDS.incrementAndGet()

  /**
   * Gets a characters items
   * @param id the characters id
   * @return returns a list of items
   */
  def apply(id: Int) = {
    db.withDynSession(Items.filter(_.charid is id).list())
  }


  /**
   * Get all inventory and equipment for a character
   * @param id the character id
   */
  def getOnHandItems(id: Int) = {
    db.withDynSession(Items.filter(row => (row.charid is id) && (row.position inSetBind ItemPositions.OnHand)).list())
  }

  /**
   * Get warehouse items for a character
   * @param id the characters id
   */
  def getWareHouseItems(id: Int, warehouseID: Int)= {
    db.withDynSession(Items.filter(row => (row.charid is id) && (row.position is warehouseID)).list())
  }

  /**
   * Delete an item from the database
   * @param uid the items to deletes uid
   * @return future of event
   */
  def deleteItem(uid: Int) = Future {
    db.withDynSession(Items.filter(_.itemuid is uid).delete)
  }

  /**
   * Inserts a new item into the database
   * @param charID the character the item belongs to
   * @param item the item to add
   * @return future of event
   */
  def addItem(charID: Int, item: ItemInformation) = Future {
    db.withDynSession(Items += item.toItemsRow(charID))
  }

  /**
   * Gets the highest id currently in the database.
   * This will be used to seed the uid generator
   */
  def getHighestID: Int =
    db.withDynSession {
      val items = Items.map(_.itemuid).list
      if (items.isEmpty) 0 else items.max
    }

  /**
   * Determine whether an item already exists
   * @param id the item id
   */
  def exists(id: Int) = Items.where(_.itemuid is id).firstOption != None

  /**
   * Updates a list of items in the database
   * @param owner the owner of the items
   * @param items the items that are owned
   */
  def update(owner: Int, items: List[ItemInformation]) = Future {
    db.withDynSession {
      items.foreach(item => {
        Items.where(_.itemuid is item.uid).update(item.toItemsRow(owner))
      })
    }
  }


  /**
   * Updates an individual item
   * @param owner the owner of the item
   * @param item the item to update
   * @return future of event
   */
  def update(owner: Int, item: ItemInformation) = Future {
    db.withDynSession {
      if (exists(item.uid))
        Items.where(_.itemuid is item.uid).update(item.toItemsRow(owner))
      else
        addItem(owner, item)
    }
  }

}



















