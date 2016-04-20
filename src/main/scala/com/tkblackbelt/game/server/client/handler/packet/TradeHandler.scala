package com.tkblackbelt.game.server.client.handler.packet

import akka.actor.{ActorRef, Props}
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.TradeBrokerActor
import com.tkblackbelt.game.server.client.TradeBrokerActor.{AcceptTradeRequest, AddItemToTrade, AddMoneyToTrade}
import com.tkblackbelt.game.server.client.handler.Client
import com.tkblackbelt.game.server.client.handler.packet.TradeHandler.NewTradeEvent
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast
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

object TradeHandler {
  trait TradeEvent extends SectorBroadCast
  case class NewTradeEvent(charID: Int, trade: NewTrade, tradeBroker: ActorRef) extends TradeEvent

  object Messages {

  }
}

/**
 * Used to handle trade requests from the client
 */
trait TradeHandler extends Client with ActorHelper {

  def handleTrade(trade: Trade) = trade match {
    case newTrade: NewTrade     => {
      if (!isTrading) {
        send(MsgTradeRequestSent)
        tradeBroker = context.actorOf(Props.apply(new TradeBrokerActor(self, char.id, inventory.spaceLeft)))
        sendSector(NewTradeEvent(char.id, newTrade, tradeBroker))
      } else {
        sendTrade(AcceptTradeRequest(self, char.id, inventory.spaceLeft))
      }
    }
    case RequestGoldAdd(amount) => sendTrade(AddMoneyToTrade(char.id, amount, char.money))

    case RequestItemAdd(itemID) => {
      inventory.items.get(itemID) match {
        case Some(item) => sendTrade(AddItemToTrade(char.id, item))
        case None       => sendTrade(CloseTrade())
      }
    }
    case trade: Trade           => sendTrade(trade)
    case x                      => unhandledMsg(x)
  }
}












