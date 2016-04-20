package com.tkblackbelt.game.server.mob

import akka.actor.{Actor, ActorLogging, ActorRef, Cancellable}
import com.tkblackbelt.game.constants.ActionIDs
import com.tkblackbelt.game.models.MonsterSpawnInfo
import com.tkblackbelt.game.models.database.Mobs
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.handler.packet.InteractionHandler.{AttackEvent, MagicAttackEvent, PhysicalAttackEvent}
import com.tkblackbelt.game.server.map.InvalidMapCoord
import com.tkblackbelt.game.server.map.MapActor.{GetSurroundings, MobGetMySector, YourSectorIs}
import com.tkblackbelt.game.server.map.MapSectorActor.{HandledInteractionEvent, HandledInteraction, SectorInteractionEvent}
import com.tkblackbelt.game.server.mob.GenericMobActor.{DefaultMobAI, MobAIEvent, MobDied}
import com.tkblackbelt.game.util.{ActorHelper, Directions, MathHelper}

import scala.concurrent.ExecutionContext.Implicits._
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

object GenericMobActor {
  trait MobAIEvent
  object DefaultMobAI extends MobAIEvent
  object MobDied
  case class MobMoved(ref: ActorRef, id: Int, x: Int, y: Int)
}

abstract class GenericMobActor extends Actor with ActorLogging with ActorHelper {

  val id          : Int
  val spawn       : MonsterSpawnInfo
  val spawnManager: ActorRef
  val map         : ActorRef

  var currentSector           = ActorRef.noSender
  var aiSchedule: Cancellable = null

  val mobInfo      = Mobs.monsters.getOrElse(spawn.info.id, Mobs.monsters.get(1).get)
  val coord        = spawn.nextCoord.getOrElse(InvalidMapCoord)
  val aiTimerMaxMS = 1500
  val aiTimerMinMS = 1000
  val deathDelay   = 5000.milliseconds
  val mob          = getMob.orNull
  var stopping     = coord == InvalidMapCoord

  def receive = {
    case x if stopping                 => //do nothing stopping
    case getReq: GetSurroundings       => if (isInView(getReq.x, getReq.y)) handlePlayerJump(getReq)
    case (YourSectorIs(sector))        => currentSector = sector; spawnToSector()
    case event: SectorInteractionEvent => if (isDirectedAtMe(event.interaction.target)) handleInteraction(event)
    case ai: MobAIEvent                => doAI(ai)
    case x                             => unhandledMsg(x)
  }

  /**
   * Send the spawn to sector to broadcast it to all clients
   */
  def spawnToSector() {
    sendSector(mob)
    sendSector(Action(mob.id, mob.x, mob.y, 0, ActionIDs.sit))
  }

  /**
   * Called when an AI event is scheduled
   */
  def doAI(event: MobAIEvent)

  /**
   * Returns a new mob attack packet
   * @param targetID the id of the person being attacked
   * @param x the targets x
   * @param y the targets y
   */
  def attackPhysical(targetID: Int, x: Int, y: Int) = {
    val packet = new PhysicalAttack(0, mob.id, targetID, x, y, mobInfo.attack.getValueBetween)
    HandledInteractionEvent(PhysicalAttackEvent(packet, self, mob.id, mob.x, mob.y, packet.damage, targetID))
  }

  /**
   * Create a new magic attack packet
   * @param id the id of the target
   * @param x target x
   * @param y target y
   * @param spell spell to use

   */
  def attackMagical(id: Int, x: Int, y: Int, spell: Int, level: Int) = {
    val damage = mobInfo.attack.getValueBetween
    val packet = MagicAttack(id, x, y, spell, level, MagicAttackTarget(id, damage))
    HandledInteractionEvent(MagicAttackEvent(packet, self, mob.id, mob.x, mob.y, damage, id))
  }

  /**
   * Sends a message to the map to get this mobs sector
   */
  def requestMySector() =
    map ! MobGetMySector(self, coord.x, coord.y)

  /**
   * Send a message to this mobs sector
   */
  def sendSector(msg: Any) =
    if (currentSector != ActorRef.noSender)
      currentSector ! msg

  /**
   * Called when a mob receives a player jump event
   */
  def handlePlayerJump(getSurround: GetSurroundings) {
    getSurround.client ! mob
  }

  /**
   * Handles a client interaction with a mob
   */
  def handleInteraction(interaction: SectorInteractionEvent) = interaction.interaction match {
    case attack: AttackEvent => handleAttack(attack, interaction.sector)
    case x                   => unhandledMsg(x)
  }

  /**
   * Handles a client attacking this mob
   */
  def handleAttack(attack: AttackEvent, sector: ActorRef) = attack match {
    case physical: PhysicalAttackEvent =>
      val physicalAttack = new PhysicalAttack(0, attack.id, mob.id, attack.x, attack.y, attack.damage)
      takeHit(attack.id, attack.damage)
      sector ! HandledInteraction(physicalAttack)
    case x                             => unhandledMsg(x)
  }

  /**
   * Take damage from a client
   * @param attacker the attackers id
   * @param damage the damage take
   */
  def takeHit(attacker: Int, damage: Int) {
    mob.hp -= damage
    if (mob.hp < 0)
      die(attacker)

  }

  /**
   * Kill the mob
   * @param attacker the person who killed the mob
   */
  def die(attacker: Int) {
    stopping = true
    sendSector(Death(attacker, mob.id, mob.x, mob.y))
    spawnManager ! MobDied
    context.system.scheduler.scheduleOnce(deathDelay, currentSector, RemoveEntity(mob.id, mob.x, mob.y))
    context.stop(self)
  }

  /**
   * Move the mob towards point
   */
  def moveTowards(x: Int, y: Int, run: Boolean = false) {
    val move = Directions.getDirection(mob.x, mob.y, x, y)
    mob.x += move._2.xPayload
    mob.y += move._2.yPayload
    sendSector(MonsterMove(mob.id, move._1, run))
  }

  /**
   * Return whether a mob is in view of another x, y point
   */
  def isInView(x: Int, y: Int) = MathHelper.isInRenderDistance(x, y, mob.x, mob.y)

  /**
   * Returns whether a x, y point is within the mobs view distance
   */
  def isNear(x: Int, y: Int) =
    MathHelper.distanceBetween(x, y, mob.x, mob.y) <= mobInfo.viewDistance

  /**
   * Returns whether an event is directed at this mob
   * @param id
   */
  def isDirectedAtMe(id: Int) = id == mob.id

  /**
   * Returns whether the mob can attack the current agro
   */
  def canAttack(x: Int, y: Int) =
    MathHelper.distanceBetween(x, y, mob.x, mob.y) <= mobInfo.attackRange

  /**
   * Schedules an ai event
   * @param msg the message to send to the mob
   */
  def runAiOnce(msg: MobAIEvent = DefaultMobAI) {
    val after = (aiTimerMinMS + Random.nextInt(aiTimerMaxMS - aiTimerMinMS)).milliseconds
    context.system.scheduler.scheduleOnce(after, self, msg)
  }

  /**
   * Starts the ai
   * @param msg
   */
  def startAI(msg: MobAIEvent = DefaultMobAI) {
    val after = (aiTimerMinMS + Random.nextInt(aiTimerMaxMS - aiTimerMinMS)).milliseconds
    aiSchedule = context.system.scheduler.schedule(100.microseconds, after, self, msg)
  }

  /**
   * Returns whether the mob ai is currently running
   */
  def isAIRunning = aiSchedule != null && !aiSchedule.isCancelled

  /**
   * Cancel the ai event
   */
  def cancelAI() {
    if (aiSchedule != null)
      aiSchedule.cancel()
  }

  /**
   * Returns a new mob built from the spawn information
   */
  def getMob = coord match {
    case InvalidMapCoord => context.stop(self); None
    case _               =>
      requestMySector()
      Some(MonsterSpawn(id, mobInfo.mesh, coord.x, coord.y, mobInfo.name, mobInfo.hp, mobInfo.level.toByte, 3))
  }

}






















