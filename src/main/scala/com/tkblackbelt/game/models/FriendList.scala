package com.tkblackbelt.game.models

import akka.actor.ActorRef
import com.tkblackbelt.game.models.Tables.FriendsRow
import com.tkblackbelt.game.models.database.{Friend, GameCharacters}
import com.tkblackbelt.game.packets.{AddOnlineFriend, AssociateInfo}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import com.tkblackbelt.game.server.map.SubscribeHelpers._

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

class FriendList(charID: Int, handler: ActorRef) {

  val friends = mutable.Map[Int, FriendsRow]()

  def getEventCoords = friends.values.map(friend => ToFriend(Some(friend.friendid.toString)))

  /**
   * Load the characters friend list
   */
  def load() {
    Friend.apply(charID).foreach(f => friends += f.friendid -> f)
  }

  /**
   * Add a new friend
   * @param uid their uid
   * @param name their name
   * @return
   */
  def add(uid: Int, name: String) = {
    val newFriend = FriendsRow(0, charID, uid, name)
    friends.put(newFriend.friendid, newFriend)
    friends.get(newFriend.friendid) match {
      case Some(friend) => {
        Friend.add(newFriend)
        sendFriendOnline(newFriend)
        Some(friend)
      }
      case _ => None
    }
  }

  /**
   * Sends the details of a specific friend to the client
   * @param id
   */
  def sendFriendDetails(id: Int) {
    friends.get(id) match {
      case Some(friend) => Future {
        GameCharacters.apply(id) match {
          case Some(char) => handler ! AssociateInfo(char)
          case _ =>
        }
      }
      case None => //friend doesn't exist
    }
  }

  /**
   * Send the status of all friends to the client
   */
  def sendFriendStatus() = friends.values.foreach(sendFriendOnline)

  /**
   * Send a message to the client notifying their friend is online
   */
  private def sendFriendOnline(f: FriendsRow) = handler ! AddOnlineFriend(f.friendid, f.friendname)

}



























