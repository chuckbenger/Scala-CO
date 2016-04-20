package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.packets.CharacterCreation

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession
import com.tkblackbelt.game.models.Character
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future

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


object GameCharacters {

  def create(req: CharacterCreation) = true

  /**
   * Get a character by char id
   * @param charID the character of the id to get
   * @return
   */
  def apply(charID: Int): Option[Character] =
    db.withDynSession {
      Characters.filter(x => x.charid is charID).firstOption match {
        case Some(c) => Some(toCharacter(c))
        case None    => None
      }
    }


  /**
   * Get's a players character based on their account id
   * @param id the player account id
   * @return the game characters saved state
   */
  def getByAccount(id: Int): Option[Character] = db withDynSession {
    Characters.filter(x => x.account is id.toString).firstOption match {
      case Some(c) => Some(toCharacter(c))
      case None    => None
    }
  }


  /**
   * Updates a characters state in the database
   * @param char the character to update
   * @return returns whether the update succeeded
   */
  def update(char: Character) = Future {
    db withDynSession {
      Characters.filter(x => x.charid is char.id).map(c => c).update(toCharacterRow(char))
    }
  }

  /**
   * Converts a character object to a character row
   * @param c the character the convert
   * @return the converted object
   */
  def toCharacterRow(c: Character) =
    CharactersRow(c.id, c.name, c.account, c.server, c.spouse, c.level, c.exp, c.str, c.dex, c.spi, c.vit, c.hp, c.mp, c.pkpoints,
      c.statpoints, c.money, c.cpoints, c.vpoints, c.whmoney, c.hairstyle, c.model, c.map,
      c.mapinstance, c.x, c.y, c.status2, c.reborn, c.isgm, c.nobility,
      c.playerClass, c.ispm, c.firstlog, c.dbexpused, c.exppotiontime, c.exppotionrate, c.previousmap, c.houseid, c.housetype)


  /**
   * Converts the database row to a scala character object
   * @param c the character row to convert
   */
  private def toCharacter(c: CharactersRow) = {
    new Character(
      c(0).asInstanceOf[Int],
      c(1).asInstanceOf[String],
      c(2).asInstanceOf[String],
      c(3).asInstanceOf[String],
      c(4).asInstanceOf[String],
      c(5).asInstanceOf[Int],
      c(6).asInstanceOf[Long],
      c(7).asInstanceOf[Int],
      c(8).asInstanceOf[Int],
      c(9).asInstanceOf[Int],
      c(10).asInstanceOf[Int],
      c(11).asInstanceOf[Int],
      c(12).asInstanceOf[Int],
      c(13).asInstanceOf[Int],
      c(14).asInstanceOf[Int],
      c(15).asInstanceOf[Int],
      c(16).asInstanceOf[Int],
      c(17).asInstanceOf[Int],
      c(18).asInstanceOf[Int],
      c(19).asInstanceOf[Int],
      c(20).asInstanceOf[Int],
      c(21).asInstanceOf[Int],
      c(22).asInstanceOf[Int],
      c(23).asInstanceOf[Int],
      c(24).asInstanceOf[Int],
      c(25).asInstanceOf[String],
      c(26).asInstanceOf[Int],
      c(27).asInstanceOf[Int],
      c(28).asInstanceOf[Int],
      c(29).asInstanceOf[Int],
      c(30).asInstanceOf[Int],
      c(31).asInstanceOf[Int],
      c(32).asInstanceOf[Option[java.sql.Timestamp]],
      c(33).asInstanceOf[Option[Int]],
      c(34).asInstanceOf[Option[Int]],
      c(35).asInstanceOf[Option[Int]],
      c(36).asInstanceOf[Option[Int]],
      c(37).asInstanceOf[Option[Int]]
    )
  }
}
