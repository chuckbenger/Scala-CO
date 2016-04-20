package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder
import com.tkblackbelt.game.server.map.MapActor.SectorBroadCast

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
 * Client to client trades
 *
 * @see <a href="http://conquerwiki.com/wiki/Trade_Packet#Version_4267">http://conquerwiki.com/wiki/Trade_Packet#Version_4267</a>
 */
object Trade {

  val packetType: Short = 1056

  def apply(data: ByteString): Packet = {

    import com.tkblackbelt.game.packets.Trade.Modes._

    val iter = data.iterator
    val uid = iter.getInt
    val mode = iter.getByte
    mode match {
      case RequestNewTrade         => NewTrade(uid)
      case RequestCloseTrade       => CloseTrade(uid)
      case ShowTradeWindow         => ShowWindow(uid)
      case CloseTradeWindowSuccess => CloseWindowSuccess(uid)
      case CloseTradeWindowFail    => CloseWindowFail(uid)
      case RequestAddItemToTrade   => RequestItemAdd(uid)
      case RequestAddMoneyToTrade  => RequestGoldAdd(uid)
      case DisplayMoney            => DisplayGold(uid)
      case DisplayMoneyAdd         => DisplayGoldAdd(uid)
      case RequestCompleteTrade    => ReqCompleteTrade(uid)
      case ReturnItem              => ItemReturn(uid)
      case x                       => new Trade(uid, mode)
    }
  }

  object Modes {
    val RequestNewTrade        : Byte = 1
    val RequestCloseTrade      : Byte = 2
    val ShowTradeWindow        : Byte = 3
    val CloseTradeWindowSuccess: Byte = 4
    val CloseTradeWindowFail   : Byte = 5
    val RequestAddItemToTrade  : Byte = 6
    val RequestAddMoneyToTrade : Byte = 7
    val DisplayMoney           : Byte = 8
    val DisplayMoneyAdd        : Byte = 9
    val RequestCompleteTrade   : Byte = 10
    val ReturnItem             : Byte = 11
  }
}


class Trade(val uid: Int, val mode: Byte) extends Packet(Trade.packetType) {

  override def deconstructed: ByteString = deconstruct {
    _.putInt(uid)
      .putByte(mode)
  }

  def copy(id: Int) = new Trade(id, mode)

  override def toString: String = super.toString + s"($uid, $mode)"
}

case class NewTrade(id: Int = 0) extends Trade(id, Trade.Modes.RequestNewTrade) with SectorBroadCast
case class CloseTrade(id: Int = 0) extends Trade(id, Trade.Modes.RequestCloseTrade)
case class ShowWindow(id: Int = 0) extends Trade(id, Trade.Modes.ShowTradeWindow)
case class CloseWindowSuccess(id: Int = 0) extends Trade(id, Trade.Modes.CloseTradeWindowSuccess)
case class CloseWindowFail(id: Int = 0) extends Trade(id, Trade.Modes.CloseTradeWindowFail)
case class RequestItemAdd(id: Int = 0) extends Trade(id, Trade.Modes.RequestAddItemToTrade)
case class RequestGoldAdd(id: Int = 0) extends Trade(id, Trade.Modes.RequestAddMoneyToTrade)
case class DisplayGold(id: Int = 0) extends Trade(id, Trade.Modes.DisplayMoney)
case class DisplayGoldAdd(id: Int = 0) extends Trade(id, Trade.Modes.DisplayMoneyAdd)
case class ReqCompleteTrade(id: Int = 0) extends Trade(id, Trade.Modes.RequestCompleteTrade)
case class ItemReturn(id: Int = 0) extends Trade(id, Trade.Modes.ReturnItem)




















