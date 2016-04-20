package com.tkblackbelt.game.models.database

import com.tkblackbelt.game.DB.db
import com.tkblackbelt.game.constants.GuildRanks
import com.tkblackbelt.game.models.Tables
import com.tkblackbelt.game.models.Tables._
import com.tkblackbelt.game.server.client.GuildActor.MemberInfo

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
object CharacterGuilds {

  /**
   * Get a characters guild information
   * @param charID the characters id
   */
  def apply(charID: Int): Option[Tables.CharacterGuild#TableElementType] = db.withDynSession {
    CharacterGuild.where(_.charid is charID).firstOption
  }

  /**
   * Return the guild member list
   * @param guildID the guild id for the list
   * @return
   */
  def guildMemberList(guildID: Int) = Future {
    db.withDynSession {
      (for {
        (c, g) <- Characters innerJoin CharacterGuild on (_.charid === _.charid)
      } yield {
        (c.charid, c.name,  g.rank, c.level)
      }).list().map(info => MemberInfo(info._1, info._2, info._3, info._4, online = false))
    }
  }

  /**
   * Swap guild leaders
   * @param leaderID the current leaders id
   * @param newLeaderID the new leaders id
   */
  def switchLeaders(guild: Int, leaderID: Int, newLeaderID: Int) = Future {
    db.withDynSession {
      CharacterGuild.
        where(_.charid is leaderID).
        map(_.rank).
        update(GuildRanks.Member)

      CharacterGuild.
        where(_.charid is newLeaderID).
        map(_.rank).
        update(GuildRanks.Leader)

      Guilds.
        where(_.guildid is guild).
        map(_.leader).
        update(newLeaderID)
    }
  }

  /**
   * Create a new character guild mapping
   * @param charID the characters id
   * @param guildID the guilds id
   * @param rank the characters initial ranking
   * @return a future
   */
  def create(charID: Int, guildID: Int, rank: Int = GuildRanks.Member) = Future {
    db.withDynSession {
      CharacterGuild += CharacterGuildRow(charID, guildID, rank = rank)
      GameCharacters(charID)
    }
  }

  /**
   * Update a characters guild rank
   * @param charID the characters id
   * @param newRank the new rank
   * @return a future of the event
   */
  def updateRank(charID: Int, newRank: Int) = Future {
    db.withDynSession {
      CharacterGuild.where(_.charid is charID).map(_.rank).update(newRank)
    }
  }

  /**
   * Update and existing guild mapping row
   * @param row the row to update
   * @return a future
   */
  def update(row: CharacterGuildRow) = Future {
    db.withDynSession {
      CharacterGuild.where(_.charid is row.charid).update(row)
    }
  }

  /**
   * Either create a new mapping or update the existing one
   * @param row the row to update/create
   * @return a future
   */
  def createOrUpdate(row: CharacterGuildRow) = Future {
    CharacterGuilds(row.charid) match {
      case Some(mapping) => update(mapping)
      case None          => create(row.charid, row.guildid, row.rank)
    }
  }

  /**
   * Delete a character guild row
   * @param charID the characters id
   * @return a future
   */
  def delete(charID: Int) = {
    db.withDynSession {
      CharacterGuild.where(_.charid is charID).delete
    }
  }

}




















