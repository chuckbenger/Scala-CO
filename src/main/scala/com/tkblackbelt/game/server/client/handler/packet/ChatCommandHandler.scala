package com.tkblackbelt.game.server.client.handler.packet

import com.tkblackbelt.bot.AIStressTestClient
import com.tkblackbelt.game.models.database.RespawnPoints
import com.tkblackbelt.game.models.flatfile.StaticItems
import com.tkblackbelt.game.packets.SystemMessages.{CommandNotFound, NotAGM, SystemMessage}
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.Stats
import com.tkblackbelt.game.server.client.ClientHandler.{TeleportTo}
import com.tkblackbelt.game.server.client.handler.Client
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

object ChatCommandHandler {
  object Messages {
    lazy val invalidItemCommand = new SystemMessage("Invalid Item command")
  }
}

/**
 * Handles chat commands sent by the client
 */
trait ChatCommandHandler extends Client with ActorHelper {

  import com.tkblackbelt.game.server.client.handler.packet.ChatCommandHandler.Messages._

  /**
   * Handles an incoming chat command from the client
   * @param command the command to handle
   */
  def handleChatCommand(command: ChatCommand) {

    if (char.isgm != 1)
      send(NotAGM)
    else
      command.message.toUpperCase.takeWhile(_ != ' ') match {
        case "/DC"         => disconnect()
        case "/SPAWN"      => send(MonsterSpawn(77, 605, char.x + 10, char.y, "BirdMan", 10000, 100, 3))
        case "/TEL"        => handleTeleportRequest(command.message.split(" "))
        case "/SEC"        => send(new SystemMessage(currentSector.path.name))
        case "/MAP"        => send(new SystemMessage(currentMap.path.name))
        case "/NPC"        => send(new SystemMessage(npcsInView.mkString(", ")))
        case "/CHARS"      => send(new SystemMessage(clientsSpawned.mkString(", ")))
        case "/ME"         => send(new SystemMessage(char.toString))
        case "/STRESS"     => spawnStressTest(command.message.split(" "))
        case "/STRESS_OFF" => AIStressTestClient.stop()
        case "/INV"        => send(new SystemMessage(inventory.items.map(_._2.uid).mkString(", ")))
        case "/RM"         => send(RemoveFromInventory(189))
        case "/ITEM"       => createItem(command.message.split(" "))
        case "/STATS"      => send(SystemMessage(Stats.toString))
        case _             => send(CommandNotFound)
      }
  }

  /**
   * Teleports a client a given position
   * @param args the teleport arguments
   */
  def handleTeleportRequest(args: Array[String]) {
    args.length match {
      case 4 => self ! TeleportTo(args(1).toInt, args(2).toInt, args(3).toInt)
      case 2 => {
        RespawnPoints.respawnPoints.get(args(1).toInt) match {
          case Some(rev) => self ! TeleportTo(rev.revivemapid, rev.revivex, rev.revivey)
          case None      => send(CommandNotFound)
        }
      }
      case _ => send(CommandNotFound)
    }
  }

  /**
   * Launch some bot clients
   * @param args
   */
  def spawnStressTest(args: Array[String]) {
    val numberOfClients = args(1).toInt
    val spreadOut = args(2).toInt
    val map = args.length >= 4 match {
      case true  => Some(args(3).toInt)
      case false => None
    }
    AIStressTestClient.start(context.system, mapManager, numberOfClients, spreadOut, map)
    send(new SystemMessage(s"$numberOfClients bots started"))
  }

  /**
   * Creates a item for the client
   */
  def createItem(args: Array[String]) = {
    if (args.length >= 5) {
      val id = args(1).toInt
      val sock1 = args(2).toByte
      val sock2 = args(3).toByte
      val plus = args(4).toByte
      StaticItems(id) match {
        case Some(item) => {
          val newItem = ItemInformation(id, item.maxDura, item.maxDura, ItemInfoModes.Default, ItemPositions.Inventory, sock1, sock2, 0, 0, plus)
          inventory.add(newItem)
        }
        case _          => send(invalidItemCommand)
      }
    } else {
      send(invalidItemCommand)
    }
  }

}


















