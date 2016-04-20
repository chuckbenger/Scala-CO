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
object Friend {

  /**
   * Gets a characters friends
   * @param uid the uid of the character
   */
  def apply(uid: Int) = {
    db.withDynSession(Friends.where(_.charid is uid).list())
  }


  def add(row: FriendsRow) = Future {
    db.withDynSession(Friends += row)
  }

  /**
   * Delete a characters friend
   * @param uid the character that wants a a friend removed
   * @param uidFriend the uid of the friend
   * @return a future of the event
   */
  def delete(uid: Int, uidFriend: Int) = Future {
    db.withDynSession(Friends.where(row => (row.charid is uid) && (row.friendid is uidFriend)).delete)
  }

}





















