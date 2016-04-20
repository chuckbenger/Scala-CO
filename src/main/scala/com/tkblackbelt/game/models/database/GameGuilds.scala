package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.models.Tables
import com.tkblackbelt.game.models.Tables._

import scala.concurrent.Future
import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession
import scala.concurrent.ExecutionContext.Implicits._

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
 * Interact with guilds table
 */
object GameGuilds {

  /**
   * Get all guilds in the database
   * @return
   */
  def all = db.withDynSession {
    Guilds.list()
  }

  /**
   * Returns a guild
   * @param id the guilds id
   * @return the guild option
   */
  def apply(id: Int): Option[Tables.Guilds#TableElementType] = db.withDynSession {
    Guilds.where(_.guildid is id).firstOption
  }

  /**
   * Does a guild already exist with a name
   * @param name
   * @return
   */
  def nameExists(name: String) = db.withDynSession {
    Guilds.where(_.name is name).firstOption != None
  }

  /**
   * Disband a guild. This has the effect of deleting all charactor guild records. (Cascade on delete)
   * @param guildID the guild to delete
   * @return whether the delete was successful
   */
  def delete(guildID: Int) = db.withDynSession {
    Guilds.where(_.guildid is guildID).delete == 1
  }

  /**
   * Find a guild by leader id
   * @param leader the id of the leader
   * @return
   */
  def byLeader(leader: Int) = db.withDynSession {
    Guilds.where(_.leader is leader).firstOption
  }

  /**
   * Create a new guild
   * @param name the name of the guild
   * @param leader the guilds leader
   */
  def create(name: String, leader: Int) = Future {
    db.withDynSession {
      Guilds += GuildsRow(0, name, leader, bulletin = "Enter your bulletin here")
      byLeader(leader)
    }
  }

  /**
   * Update a guilds information
   * @param guild the guild row to update
   * @return a future
   */
  def update(guild: GuildsRow) = Future {
    db.withDynSession {
      Guilds.where(_.guildid is guild.guildid).update(guild)
    }
  }
}
