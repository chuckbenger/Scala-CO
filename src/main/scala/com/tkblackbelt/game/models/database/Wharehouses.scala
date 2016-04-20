package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.models.Tables._

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
 * Interact with the warehouse table
 */
object Wharehouses {

  /**
   * Returns the warehouse for a client
   * @param charID the client to get it for
   */
  def apply(charID: Int) = db.withDynSession(Warehouses.where(_.id is charID).firstOption)

  /**
   * Create a new wharehouse for a client
   * @param charID the clients id
   */
  def create(charID: Int) = Future(db.withDynSession(Warehouses += WarehousesRow(charID, None)))

  /**
   * Updates a wharehouse row
   * @param row the row to update
   */
  def update(row: WarehousesRow) = Future(db.withDynSession(Warehouses.where(_.id is row.id).update(row)))


  /**
   * Set the password for a warehous
   * @param charID the character id
   * @param password the characters new password
   */
  def setPassword(charID: Int, password: Option[String]) = update(WarehousesRow(charID, password))
}
