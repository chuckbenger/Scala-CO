package com.tkblackbelt.game.server.client.handler.server

import com.tkblackbelt.game.packets.{ItemPositions, ItemInformation, NewTrade}
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.TradeHandler.{NewTradeEvent, TradeEvent}
import com.tkblackbelt.game.server.client.handler.server.ServerTradeHandler.{AddTradeItemToInventory, AddTradeMoneyToInventory, RemoveTradeItemFromInventory, RemoveTradeMoneyFromInventory}
import com.tkblackbelt.game.util.ActorHelper

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


object ServerTradeHandler {
  case class AddTradeMoneyToInventory(amount: Int) extends TradeEvent
  case class AddTradeItemToInventory(item: ItemInformation) extends TradeEvent
  case class RemoveTradeMoneyFromInventory(amount: Int) extends TradeEvent
  case class RemoveTradeItemFromInventory(item: ItemInformation) extends TradeEvent
}

/**
 * Used to handle trade requests from the server
 */
trait ServerTradeHandler extends Client with ActorHelper {


  def handleServerTrade(trade: TradeEvent) = trade match {
    case NewTradeEvent(charID, newTrade, broker) => {
      if (newTrade.uid == char.id) {
        tradeBroker = broker
        send(NewTrade(charID))
      }
    }

    case AddTradeMoneyToInventory(amount) => char.money += amount

    case RemoveTradeMoneyFromInventory(amount) => char.money -= amount

    case AddTradeItemToInventory(item) => inventory.add(item.copy(position = ItemPositions.Inventory))

    case RemoveTradeItemFromInventory(item) => inventory.remove(item)

    case x => unhandledMsg(x)
  }

}






















