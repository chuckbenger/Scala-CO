package com.tkblackbelt.bot

import akka.actor._
import com.tkblackbelt.bot.AIClientHandler.DoYourAI
import com.tkblackbelt.core.IncomingPacket
import com.tkblackbelt.game.models.Character
import com.tkblackbelt.game.models.database.{GameCharacters, GameMaps}
import com.tkblackbelt.game.packets.{Avatar, Talk}
import com.tkblackbelt.game.server.client.ClientHandler.Disconnect

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

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

object AIStressTestClient {

  val runningBots: mutable.HashSet[(Cancellable, ActorRef)] = mutable.HashSet.empty

  def start(system: ActorSystem, mapManager: ActorRef, bots: Int, dev: Int, map: Option[Int]) = Future {

    system.log.info(s"Starting $bots bots")

    if (runningBots.size > 0) {
      stop()
      Thread.sleep(2000)
    }

    val baseChar: Character = GameCharacters.getByAccount(1).get
    val actors = generateRandomCharactersFrom(baseChar, bots, dev, map).foreach(char => {
      val actor = system.actorOf(Props(new AIStressTestClient(char, Actor.noSender, mapManager)))
      val schedule = system.scheduler.schedule(5 seconds, 5 second, actor, DoYourAI)
      Thread.sleep(1)
      runningBots += (schedule -> actor)
    })

    system.log.info(s"$bots bots created")
  }

  def stop() {
    println("Stopping bots")
    runningBots.foreach { bot =>
      bot._1.cancel()
      bot._2 ! Disconnect
    }
    runningBots.clear()
  }


  def generateRandomCharactersFrom(char: Character, number: Int, deviation: Int, map: Option[Int]) = {
    (1 to number).map(i => {
      val newChar = char.copy
      newChar.id += i
      map match {
        case Some(map) => newChar.map = map
        case None      => newChar.map = GameMaps.maps(Random.nextInt(GameMaps.maps.length))
      }

      newChar.x += Random.nextInt(deviation)
      newChar.y += Random.nextInt(deviation)
      newChar.name += i.toString
      newChar
    })
  }
}

class AIStressTestClient(override val char: Character,
  override val connection: ActorRef,
  override val mapManager: ActorRef) extends AIClientHandler(char, connection, mapManager) {

  override def receive = {
    case DoYourAI => {
      val xOffset = 5 * (if (Random.nextBoolean()) 1 else -1)
      val yOffset = 5 * (if (Random.nextBoolean()) 1 else -1)
      self ! new IncomingPacket(Avatar(char.id, char.x + xOffset, char.y + yOffset))
      self ! new IncomingPacket(new Talk(char.name, "", "Hello :) Total chat packets sent " + AIClientHandler.chatCOunter + " Total packets sent = " + AIClientHandler.packets ))
      AIClientHandler.chatCOunter += 1
    }

    case x => defaultReceive(x)
  }
}













































