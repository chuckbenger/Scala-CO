package com.tkblackbelt.game.packets

import akka.util.ByteString
import com.tkblackbelt.core.Packet
import com.tkblackbelt.core.global.Globals.byteOrder

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
 * The Package Packet, used to interact with any number of packages, such as warehouses and sashes.
 * According to the leaked EO source there is a union at offset 12, however since the union uses types which are different sizes in memory I have decided to list them as two separate structures,
 * this process is not recommended without extremely careful allocation of memory.
 *
 * @see <a href="http://conquerwiki.com/wiki/Package_Packet#Version_4267">http://conquerwiki.com/wiki/Package_Packet#Version_4267</a>
 */
object PackagePacket {

  val packetType: Short = 1102

  def apply(data: ByteString): Packet = {
    val iter = data.iterator
    val id = iter.getInt
    val mode = iter.getByte
    iter.getByte   // junk
    iter.getShort  // junk
    val uid = iter.getInt
    mode match {
      case Modes.Show => ShowWarehouseItems(id)
      case Modes.DepositItem => DepositItemWarehouse(id, uid)
      case Modes.WithdrawItem => WithdrawItemWarehouse(id, uid)
      case x => new PackagePacket(id, mode, uid)
    }
  }

  object Modes {
    val Show         = 0
    val DepositItem  = 1
    val WithdrawItem = 2
  }
}

class PackagePacket(id: Int, mode: Int, uid: Int) extends Packet(PackagePacket.packetType) {

  override def toString: String = s"${super.toString} ($id, $mode, $uid)"
}

case class ShowWarehouseItems(id: Int) extends PackagePacket(id, PackagePacket.Modes.Show, 0)

case class DepositItemWarehouse(id: Int, uid: Int) extends PackagePacket(id, PackagePacket.Modes.DepositItem, uid)

case class WithdrawItemWarehouse(id: Int, uid: Int) extends PackagePacket(id, PackagePacket.Modes.WithdrawItem, uid)