package com.tkblackbelt.game.server.client

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.tkblackbelt.game.models.database.GameItems
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.TradeBrokerActor._
import com.tkblackbelt.game.server.client.handler.server.ServerTradeHandler.{AddTradeItemToInventory, AddTradeMoneyToInventory, RemoveTradeItemFromInventory, RemoveTradeMoneyFromInventory}
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

object TradeBrokerActor {

  case class AcceptTradeRequest(client: ActorRef, charID: Int, inventoryRoom: Int)
  case class AddMoneyToTrade(fromID: Int, amount: Int, availableGold: Int)
  case class AddItemToTrade(fromID: Int, item: ItemInformation)

  case object TradeFinished

  case class CurrentOffer(gold: Int = 0, items: List[ItemInformation] = List.empty, finalized: Boolean = false)

  class TradeState(val charID: Int, val actor: ActorRef, val inventoryRoom: Int, var offer: CurrentOffer = CurrentOffer())
}

/**
 * Actor for handling client to client trades. All trade events are handled through a broker instace.
 */
class TradeBrokerActor(actor: ActorRef, initiatorID: Int, inventoryRoom: Int) extends Actor with ActorLogging with ActorHelper {


  import log._

  var acceptor: TradeState = null
  var initiator            = new TradeState(initiatorID, actor, inventoryRoom)

  def receive = {
    case AcceptTradeRequest(client, id, inv)                             => {
      acceptor = new TradeState(id, client, inv)
      openTradeWindow()
    }
    case AddMoneyToTrade(from, amount, available) if !hasFinalized(from) =>
      if (hasEnoughMoney(amount, available))
        addMoney(from, amount)
      else closeTradeFail()

    case AddItemToTrade(from, item) if !hasFinalized(from) => addItem(from, item)

    case ReqCompleteTrade(id) => finalize(id)

    case CloseTrade(_) => closeTradeFail()
    case x             => unhandledMsg(x)
  }

  /**
   * Returns whether the client has already finalized their trade
   * @param id the client that requested the event
   */
  def hasFinalized(id: Int) = getSender(id)._1.offer.finalized

  /**
   * Returns whether both client have finalized their trade
   */
  def bothFinalized = hasFinalized(initiator.charID) && hasFinalized(acceptor.charID)

  /**
   * Returns whether the client has enough money to make the trade
   * @param amount the amount to be traded
   * @param available the amount the client has
   */
  def hasEnoughMoney(amount: Int, available: Int) = available >= amount && amount > 0

  /**
   * Add money to the trade
   * @param from who's adding the money
   * @param amount the amount they're adding
   */
  def addMoney(from: Int, amount: Int) {
    val goldAdd = DisplayGold(amount)
    val traders = getSender(from)
    val sender = traders._1
    val receiver = traders._2
    sender.offer = sender.offer.copy(gold = amount)
    receiver.actor ! goldAdd
  }

  /**
   * Adds an item to the trade
   * @param from who the item is from
   * @param item the item
   */
  def addItem(from: Int, item: ItemInformation) {
    val traders = getSender(from)
    val sender = traders._1
    val receiver = traders._2

    if (sender.offer.items.size + 1 <= sender.inventoryRoom) {
      val tradeItem = item.copy(mode = ItemInfoModes.Trade)
      sender.offer = sender.offer.copy(items = sender.offer.items :+ item)
      receiver.actor ! tradeItem
    } else
      notEnoughSpace(sender.actor)
  }

  /**
   * Finalize a clients trade
   * @param id the id of the client to finalize
   */
  def finalize(id: Int) {
    val traders = getSender(id)
    val sender = traders._1
    val receiver = traders._2
    sender.offer = sender.offer.copy(finalized = true)

    if (bothFinalized)
      finishTrade()
    else
      receiver.actor ! ReqCompleteTrade(sender.charID)
  }

  /**
   * Returns the state of the client who requested the action
   */
  def getSender(id: Int) = id match {
    case _ if id == initiator.charID => (initiator, acceptor)
    case _ if id == acceptor.charID  => (acceptor, initiator)
  }

  /**
   * Close the trade window and stop the actor
   */
  def closeTradeFail() {
    sendToBothTraders(CloseWindowFail())
    sendTradeFinished()
    sendServerMessage(MsgTradeFailed)
    context.stop(self)
  }

  /**
   * Send a message notifing that the trade partners bag is full
   */
  def notEnoughSpace(to: ActorRef) {
    to ! MsgTradeInventoryIsFull
  }

  /**
   * The trade was a success
   */
  def finishTrade() {
    logFinishedTrade()
    sendToBothTraders(CloseWindowSuccess())
    sendTrades(initiator, acceptor)
    sendTrades(acceptor, initiator)
    sendTradeFinished()
    context.stop(self)
  }

  /**
   * Sends the trades to one another
   */
  def sendTrades(from: TradeState, to: TradeState) {
    val gold = from.offer.gold

    to.actor ! AddTradeMoneyToInventory(gold)
    from.actor ! RemoveTradeMoneyFromInventory(gold)

    from.offer.items.foreach { item =>
      to.actor ! RemoveFromInventory(item.uid)
      to.actor ! AddTradeItemToInventory(item.copy(uid = GameItems.nextID))
      from.actor ! RemoveTradeItemFromInventory(item)
    }
  }

  /**
   * Displays the items offered from each client
   */
  def logFinishedTrade() {
    debug(s"${initiator.charID} offered ${initiator.offer}")
    debug(s"${acceptor.charID} offered ${acceptor.offer}")
  }

  /**
   * Opens the trade window on both clients screen
   */
  def openTradeWindow() {
    sendToBothTraders(ShowWindow())
  }

  /**
   * Notifies the clients that the trade is finished
   */
  def sendTradeFinished() {
    initiator.actor ! TradeFinished
    acceptor.actor ! TradeFinished
    sendServerMessage(MsgTradeSucceeded)
  }

  /**
   * Send a server message to both clients
   */
  def sendServerMessage(msg: Any) {
    initiator.actor ! msg
    acceptor.actor ! msg
  }

  /**
   * Send a message to both trade clients
   * @param tradeMessage the message to send
   */
  def sendToBothTraders(tradeMessage: Trade) {
    initiator.actor ! tradeMessage.copy(acceptor.charID)
    acceptor.actor ! tradeMessage.copy(initiator.charID)
  }
}























