package com.tkblackbelt.game.models

import akka.actor.ActorRef
import com.tkblackbelt.game.packets.ItemInformation
import com.tkblackbelt.game.packets.ItemPositions._
import com.tkblackbelt.game.server.GameMessages._
import com.tkblackbelt.game.structures.{NoRange, ValueRange}

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
 * Manages a clients current equipment
 * @param charID the characters id
 * @param handler the event handler
 */
class Equipment(charID: Int, handler: ActorRef) {

  private var _attackRange : ValueRange = NoRange
  private var _defence     : Int        = 0
  private var _mdefence    : Int        = 0
  private var _dodge       : Int        = 0
  private var _dex         : Int        = 0
  private var _mattackRange: ValueRange = NoRange

  private var _head : Option[ItemInformation] = None
  private var _armor: Option[ItemInformation] = None
  private var _neck : Option[ItemInformation] = None
  private var _rwep : Option[ItemInformation] = None
  private var _lwep : Option[ItemInformation] = None
  private var _ring : Option[ItemInformation] = None
  private var _boots: Option[ItemInformation] = None

  def physicalAttack = _attackRange.getValueBetween

  def magicAttack = _mattackRange.getValueBetween

  /**
   * Calculates the total attack range for a characters equipment
   */
  private def calculateAttack() {

    val rwepRange = rwep match {
      case Some(weapon) => (weapon.getPhysicalAttackRange, weapon.getMagicAttackRange)
      case None         => (NoRange, NoRange)
    }

    val lwepRange = lwep match {
      case Some(weapon) => (weapon.getPhysicalAttackRange, weapon.getMagicAttackRange)
      case None         => (NoRange, NoRange)
    }

    val armorAttack = armor match {
      case Some(armor) => ValueRange(armor.staticItem.magicAttack, armor.staticItem.magicAttack)
      case None        => ValueRange(0, 0)
    }

    _attackRange = rwepRange._1 + lwepRange._1
    _mattackRange = rwepRange._2 + lwepRange._2 + armorAttack
    handler ! MsgAttackUpdated(_attackRange, _mattackRange)
  }

  /**
   * Calculate the total defense range for characters equipment
   */
  private def calculateDefense() {

    val armorDefence = armor match {
      case Some(armor) => (armor.staticItem.defenseAdd, armor.staticItem.mDefenseAdd)
      case None        => (0, 0)
    }

    val neckDefense = neck match {
      case Some(neck) => (neck.staticItem.defenseAdd, neck.staticItem.mDefenseAdd)
      case None       => (0, 0)
    }

    val headDefence = head match {
      case Some(head) => (head.staticItem.defenseAdd, head.staticItem.mDefenseAdd)
      case None       => (0, 0)
    }

    val totalDefence = armorDefence._1 + neckDefense._1 + headDefence._1
    val totalmDefence = armorDefence._2 + neckDefense._2 + headDefence._2

    _defence = totalDefence
    _mdefence = totalmDefence
    handler ! MsgDefenceUpdated(_defence, _mdefence)
  }

  /**
   * Calculate the characters dodge
   */
  private def calculateDodge() {
    _dodge = boots match {
      case Some(boots) => boots.staticItem.dodgeAdd
      case None        => 0
    }
    handler ! MsgDodgeUpdated(_dodge)
  }

  /**
   * Calculates the characters dexterity
   */
  private def calculateDex() {
    _dex = ring match {
      case Some(ring) => ring.staticItem.dexAdd
      case None       => 0
    }
    handler ! MsgDexterityUpdate(_dex)
  }

  /**
   * Update the equipment at a specific position
   * @param position the position of the equipment
   * @param equip the item to equip
   */
  def updateFromPosition(position: Int, equip: Option[ItemInformation]) = position match {
    case Head     => head = equip
    case Armor    => armor = equip
    case Necklace => neck = equip
    case Right    => rwep = equip
    case Left     => lwep = equip
    case Ring     => ring = equip
    case Boots    => boots = equip
  }

  def head = _head

  def head_=(eqiup: Option[ItemInformation]) = {
    _head = eqiup
    calculateDefense()
  }

  def armor = _armor

  def armor_=(eqiup: Option[ItemInformation]) = {
    _armor = eqiup
    calculateDefense()
    calculateAttack()
  }

  def neck = _neck

  def neck_=(eqiup: Option[ItemInformation]) = {
    _neck = eqiup
    calculateDefense()
  }

  def rwep = _rwep

  def rwep_=(eqiup: Option[ItemInformation]) = {
    _rwep = eqiup
    calculateAttack()
  }

  def lwep = _lwep

  def lwep_=(eqiup: Option[ItemInformation]) = {
    _lwep = eqiup
    calculateAttack()
  }

  def ring = _ring

  def ring_=(eqiup: Option[ItemInformation]) = {
    _ring = eqiup
    calculateDex()
  }

  def boots = _boots

  def boots_=(eqiup: Option[ItemInformation]) = {
    _boots = eqiup
    calculateDodge()
  }
}




















