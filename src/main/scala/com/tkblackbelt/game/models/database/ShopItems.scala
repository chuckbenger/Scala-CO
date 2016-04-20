package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB._
import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.models.flatfile.StaticItems

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession
import com.tkblackbelt.game.packets.{BuyItem, ItemInfo, ItemInformation}

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
 * Interact with the shop items table
 */
object ShopItems {

  lazy val shops = db.withDynSession(Shops.list().groupBy(_.shopid).toMap)

  val sellReduction = 3

  /**
   * Buy an item from a shop
   * @param buy the item buy event
   * @return returns the bought item
   */
  def buy(buy: BuyItem, gold: Int): Option[ItemInformation] = {
    shops.get(buy.shopID) match {
      case Some(shop) => shop.find(_.itemid == buy.itemID) match {
        case Some(shopItem) if hasEnoughMoney(shopItem.itemid, gold) => ItemInfo(shopItem.itemid)
        case _ => None
      }
      case _ => None
    }
  }

  /**
   * Returns whether the client has enough money to buy an item
   * @param itemID the id of the item they're buying
   * @param gold the gold the client has
   */
  def hasEnoughMoney(itemID: Int, gold: Int) = StaticItems(itemID) match {
    case Some(item) => item.cost <= gold
    case _ => false
  }

}
