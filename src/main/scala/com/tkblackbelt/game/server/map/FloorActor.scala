package com.tkblackbelt.game.server.map

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.handler.packet.ItemDropHandler.PickupItemEvent
import com.tkblackbelt.game.server.client.handler.packet.ItemUsageHandler.DropThisItem
import com.tkblackbelt.game.server.client.handler.server.ServerStatusUpdateHandler.{AddItemToInventory, AddMoneyToInventory}
import com.tkblackbelt.game.server.map.FloorActor._
import com.tkblackbelt.game.util.{ActorHelper, MathHelper}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.duration._

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

object FloorActor {

  val DistanceToPickup = 2
  val ItemRemovalTime  = 30.seconds

  case class RemoveItem(uid: Int)
  case class FloorEvent(msg: AnyRef)

  case class GetFloorItems(client: ActorRef)
  case class FloorItem(item: ItemInformation, drop: FloorItemDrop) {
    def canBePickedUpAt(x: Int, y: Int) = MathHelper.distanceBetween(x, y, drop.x, drop.y) <= DistanceToPickup
  }
}

/**
 * Actor for managing the item floor of a a sector
 */
class FloorActor extends Actor with ActorLogging with ActorHelper {

  import log._

  val floorItems: mutable.Map[Int, FloorItem] = mutable.Map.empty

  def receive = {
    case x: FloorEvent => handleFloorEvent(x)
    case RemoveItem(uid) => removeItem(uid)
    case GetFloorItems(client) => floorItems.values.foreach(item => client ! item.drop)
    case x => unhandledMsg(x)
  }

  /**
   * Handles a received floor event
   */
  def handleFloorEvent(event: FloorEvent) = event.msg match {
    case pickup: PickupItemEvent => tryToPickup(pickup)

    case drop: DropThisItem => {
      val item = drop.item.withNextUID
      val itemDrop = FloorItemDrops(item.uid, item.id, drop.x, drop.y)
      val floorItem = item.uid -> FloorItem(item, itemDrop)
      floorItems += floorItem
      context.parent ! floorItem._2.drop
      scheduleItemForRemoval(item.uid)
    }
  }

  /**
   * Schedule an item to be removed from the floor in x seconds
   */
  def scheduleItemForRemoval(uid: Int) {
    context.system.scheduler.scheduleOnce(FloorActor.ItemRemovalTime, self, RemoveItem(uid))
  }

  /**
   * Attempt to pickup a floor item
   * @param pickup the pickup attempt
   */
  def tryToPickup(pickup: PickupItemEvent) {
    floorItems.get(pickup.item.uid) match {
      case Some(item) if item.canBePickedUpAt(pickup.x, pickup.y) => {
        val item = removeItem(pickup.item.uid)
        item.get.item match {
          case gold: GoldItem => pickup.client ! AddMoneyToInventory(gold.amount)
          case item: ItemInformation => pickup.client ! AddItemToInventory(item)
        }
      }
      case None => debug(s"$pickup can't be picked up")
    }
  }

  /**
   * Remove an item from the floor
   */
  def removeItem(uid: Int) = {
    val item = floorItems.remove(uid)
    item match {
      case Some(i) => context.parent ! RemoveFloorItem(i.drop.uid, i.drop.id, i.drop.x, i.drop.y)
      case None => //Item already pickup up
    }
    item
  }

}



























