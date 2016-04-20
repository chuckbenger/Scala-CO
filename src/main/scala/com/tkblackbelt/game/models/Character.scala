package com.tkblackbelt.game.models

import akka.actor.ActorRef
import com.tkblackbelt.game.constants.StatusFlags
import com.tkblackbelt.game.models.database.GameCharacters
import com.tkblackbelt.game.packets.{RaiseFlag, ItemInformation, UpdateHP, UpdateMoney}
import com.tkblackbelt.game.server.client.handler.server.ServerHandler.SendToSector


class Character(

                 var id: Int,
                 var name: String,
                 var account: String,
                 var server: String,
                 var spouse: String,
                 var level: Int,
                 var exp: Long,
                 var str: Int,
                 var dex: Int,
                 var spi: Int,
                 var vit: Int,
                 private var _hp: Int,
                 var mp: Int,
                 var pkpoints: Int,
                 var statpoints: Int,
                 private var _money: Int,
                 var cpoints: Int,
                 var vpoints: Int,
                 var whmoney: Int,
                 var hairstyle: Int,
                 var model: Int,
                 var map: Int,
                 var mapinstance: Int,
                 var x: Int,
                 var y: Int,
                 var status2: String,
                 var reborn: Int,
                 var isgm: Int,
                 var nobility: Int,
                 var playerClass: Int,
                 var ispm: Int,
                 val firstlog: Int,
                 var dbexpused: Option[java.sql.Timestamp],
                 var exppotiontime: Option[Int],
                 var exppotionrate: Option[Int],
                 var previousmap: Option[Int],
                 var houseid: Option[Int],
                 var housetype: Option[Int],
                 var direction: Int = 0
                 ) {


  private var _status = StatusFlags.None
  var handler: ActorRef = ActorRef.noSender

  def money = _money

  def money_=(amount: Int) = {
    _money = amount
    if (money < 0) _money = 0
    handler ! UpdateMoney(id, money)
    money
  }

  def hp = _hp

  def hp_=(amount: Int) = {
    _hp = amount
    if (hp < 0) _hp = 0
    handler ! UpdateHP(id, hp)
    hp
  }

  def status = _status

  def status_=(newStatus: Int) = {
    _status = newStatus
    handler ! SendToSector(RaiseFlag(id, status))
  }

  /**
   * Returns whether the player can equip a item
   * @param item the item the client wants to equip
   */
  def canEquipItem(item: ItemInformation) = {
    item.staticItem.level <= level &&
    item.staticItem.clas == playerClass
  }

  /**
   * Sets the client current position
   * @param newX the clients new x position
   * @param newY the clients new y position
   */
  def setPosition(newX: Int, newY: Int) {
    x = newX
    y = newY
  }

  /**
   * Adjusts the clients position by an offset
   * @param xOffset the x offset
   * @param yOffset the y offset
   */
  def adjustPosition(xOffset: Int, yOffset: Int) {
    x += xOffset
    y += yOffset
  }


  def save() = GameCharacters.update(this)

  def copy = new Character(id,
    name,
    account,
    server,
    spouse,
    level,
    exp,
    str,
    dex,
    spi,
    vit,
    hp,
    mp,
    pkpoints,
    statpoints,
    money,
    cpoints,
    vpoints,
    whmoney,
    hairstyle,
    model,
    map,
    mapinstance,
    x,
    y,
    status2,
    reborn,
    isgm,
    nobility,
    playerClass,
    ispm,
    firstlog,
    dbexpused,
    exppotiontime,
    exppotionrate,
    previousmap,
    houseid,
    housetype,
    direction)

}












