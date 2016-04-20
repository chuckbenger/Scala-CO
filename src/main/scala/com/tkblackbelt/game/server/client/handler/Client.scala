package com.tkblackbelt.game.server.client.handler

import akka.actor.{Actor, ActorRef}
import com.tkblackbelt.core.Packet
import com.tkblackbelt.game.models._
import com.tkblackbelt.game.packets._
import com.tkblackbelt.game.server.client.ClientHandler.{EnteringMap, TeleportTo}
import com.tkblackbelt.game.server.map.MapActor.ClientGetMySector
import com.tkblackbelt.game.server.map.WorldManagerActor._
import com.tkblackbelt.game.server.map.MapSectorActor.UnSubscribeMe
import com.tkblackbelt.game.util.MathHelper

import scala.collection.mutable

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
trait Client extends Actor {

  //Vars
  var team          = ActorRef.noSender
  var currentMap    = ActorRef.noSender
  var currentSector = ActorRef.noSender
  var tradeBroker   = ActorRef.noSender

  var lastNpc = 0

  //Vals
  val mobsInView                       = mutable.Map[Int, (Int, Int)]()
  val npcsInView                       = mutable.Map[Int, (Int, Int)]()
  val clientsSpawned                   = mutable.HashSet[Int]()
  val pendingFriends: mutable.Set[Int] = mutable.Set.empty

  implicit val char: Character
  val connection: ActorRef
  val mapManager: ActorRef
  val inventory  = new Inventory(char.id, self)
  val friendList = new FriendList(char.id, self)
  val warehouse  = new WareHouse(char.id, self)
  val guild      = new Guild(char.id, char.name, self, mapManager)


  override def preStart() {
    super.preStart()
    inventory.load()
    friendList.load()
    guild.load()
    friendList.sendFriendStatus()
    moveToMap(char.map)
    mapManager ! SubscribeToWorld(self, char.id, char.name)
  }

  /**
   * Move client to a new map
   * @param mapID the id of the map
   */
  def moveToMap(mapID: Int) = {
    mapManager ! SubscribeToMap(self, mapID, char.name)
  }

  /**
   * Unsubscribed from the current user map
   */
  def unSubscribeFromCurrentMap() = {
    if (currentMap != ActorRef.noSender) {
      npcsInView clear()
      mobsInView clear()
      clientsSpawned clear()
      mapManager ! UnSubscribeFromMap(self, currentMap, char.id, char.x, char.y, char.map, char.name)
      currentMap = ActorRef.noSender
    }
  }

  /**
   * Unsubscibe the client from the world
   */
  def unSubscribeFromWorld() = {
    mapManager ! UnSubscribeFromWorld(self, char.id, char.name)
  }

  /**
   * Unsubscribe from the current users sector
   */
  def unSubscribeFromCurrentSector() = {
    sendSector(UnSubscribeMe)
    currentSector = ActorRef.noSender
  }


  /**
   * Moves to a map connected to the portal
   * @param teleport the teleport info
   */
  def moveToMapFrom(teleport: TeleportTo) {
    moveToMap(teleport.map)
    unSubscribeFromCurrentMap()
    unSubscribeFromCurrentSector()
    char.previousmap = Some(char.map)
    char.map = teleport.map
    char.x = teleport.x
    char.y = teleport.y

  }

  /**
   * Movement from one map to another is complete
   * @param mapMove
   */
  def mapMoveComplete(mapMove: MovedToMap) {
    currentMap = mapMove.mapActor
    if (currentMap != ActorRef.noSender) {
      sendMap(ClientGetMySector(self, char.x, char.y))
      sendMap(EnteringMap(EntitySpawn.fromCharacter(char, inventory, guild)))
      send(PositionRequest.result(char.id, char.x, char.y, char.map))
    } else
      disconnect()
  }

  /**
   * Called when the clients sector has changed
   */
  def sectorAdjusted() {
    //sendSector(GetSurroundings(self, char.x, char.y))
  }

  /**
   * Move to a new map through a portal
   * @param portal the portal entered
   */
  def moveToMapFrom(portal: GamePortal) {
    moveToMapFrom(TeleportTo(portal.toMap, portal.toX, portal.toY))
  }

  /**
   * Adds an entity to the clients screen
   * @param entity the entity to add
   */
  def addCharacter(entity: EntitySpawn) {
    val notMe = entity.id != char.id
    if (notMe) {

      val notAlreadySpawned = !clientsSpawned.contains(entity.id) || entity.force
      val canSee = isInView(entity.x, entity.y)

      if (notAlreadySpawned && canSee) {
        clientsSpawned add entity.id
        send(entity)
      }
    }
  }

  /**
   * Remove a character from the screen
   * @param id the id of the character
   */
  def removeCharacter(id: Int, x: Int, y: Int) {
    if (clientsSpawned remove id)
      send(RemoveEntity(id, x, y))
  }

  /**
   * Add a mob to the clients view
   * @param mob the mob to spawn
   */
  def addMob(mob: MonsterSpawn) {
    val notAlreadySpawned = !mobsInView.contains(mob.id)
    val canSee = isInView(mob.x, mob.y)
    if (notAlreadySpawned && canSee) {
      mobsInView += (mob.id ->(mob.x, mob.y))
      send(mob)
    }
  }

  /**
   * Add an npc to the clients view
   * @param npc the npc to add
   */
  def addNPC(npc: SpawnNPC) {
    if (isInView(npc.x, npc.y) && !(npcsInView contains npc.id)) {
      npcsInView += npc.id ->(npc.x, npc.y)
      send(npc)
    }
  }

  /**
   * Adjust what's in the clients view
   */
  def adjustView() {
    npcsInView.filter { case (id, pos) => !isInView(pos._1, pos._2)}.foreach { case (id, _) => npcsInView.remove(id)}
    mobsInView.filter { case (id, pos) => !isInView(pos._1, pos._2)}.foreach { case (id, _) => mobsInView.remove(id)}
  }

  /**
   * Returns whether set of coords are in view
   */
  def isInView(x: Int, y: Int): Boolean = MathHelper.isInRenderDistance(x, y, char.x, char.y)


  /**
   * Checks a condition and calls the true of false function
   * @param test the test
   * @param isTrue the function if true
   * @param isFalse the function if false
   */
  def conditional(test: Boolean)(isTrue: => Unit)(isFalse: => Unit) = test match {
    case true  => isTrue
    case false => isFalse
  }

  /**
   * Returns whether the client has a team
   */
  def hasTeam = team != ActorRef.noSender

  def send(p: Packet)

  /**
   * Send a message to the current map
   */
  def sendMap(msg: AnyRef) {
    if (currentMap != ActorRef.noSender) {
      currentMap ! msg
    }
  }

  /**
   * Send a message to the current sector
   */
  def sendSector(msg: AnyRef) {
    if (currentSector != ActorRef.noSender) {
      currentSector ! msg
    }
  }

  /**
   * Send a message to the clients team
   */
  def sendTeam(msg: AnyRef) {
    if (team != ActorRef.noSender)
      team ! msg
  }

  def sendTrade(msg: AnyRef) {
    if (isTrading) {
      tradeBroker ! msg
    }
  }

  /**
   * Send a message directly to a player
   */
  def sendPlayer(id: Int, msg: AnyRef) {
     getPlayer(id) ! msg
  }

  /**
   * Get a players actor
   * @param id the players id
   */
  def getPlayer(id: Int) = {
    context.actorSelection(s"../../*/player${id.toString}")
  }

  def isTrading = tradeBroker != ActorRef.noSender

  def disconnect()

}




















