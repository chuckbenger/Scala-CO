package com.tkblackbelt.game.server.npc.scripts

import java.lang.reflect.Method

import akka.actor.ActorRef
import com.tkblackbelt.game.GameServerMain
import com.tkblackbelt.game.packets.NpcCommands
import com.tkblackbelt.game.packets.NpcCommands._
import com.tkblackbelt.game.server.client.handler.server.NpcCommandHandler.ServerNpcCommand
import com.tkblackbelt.game.server.npc.NPCActor.{NpcEvent, NpcInteraction}
import com.tkblackbelt.game.util.ReflectHelper
import com.tkblackbelt.java.npc.{Response, ResponseInit}


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
 * Extend this class if you wish to implement an npcs interaction.
 * Two core attributes are used to build up an npc.
 * @see Response.java
 * @see ResponseInit.java
 */
abstract class NpcScript extends ReflectHelper {

  val id: List[Int]
  val log           = GameServerMain.system.log
  val NO_LINK_BACK  = NpcCommands.NO_LINK_BACK
  /**
   * @see noLinkFound
   */
  val NO_LINK_FOUND = 1000
  /**
   * When the class is instantiated a list of available npc commands are handled
   */
  private val npcMethods = {

    val init = getMethodsFor(classOf[ResponseInit])
    val responses = getMethodsFor(classOf[Response])

    (0 to responses.size + 1).zip(init ++ responses).toMap
  }
  var npc : ActorRef = ActorRef.noSender
  var face: Int      = 0

  /**
   * Initialize the scripts face and actor ref
   * @param actor the scripts actor ref
   * @param newFace the npcs face
   */
  def init(actor: ActorRef, newFace: Int) = {
    npc = actor
    face = newFace
    this
  }

  /**
   * Handle an npc interaction. This will be called during initial interaction and link commands
   * @param event the npc event parameters
   * @param client an implicit client actor
   * @return
   */
  def handle(implicit event: NpcInteraction, client: ActorRef)

  /**
   * Handle a callback event from a client actor. This will be called when a internal message to sent to the actor
   * @param cmd the command being received
   * @param client the client who sent the action
   */
  def handleCallback(implicit cmd: ServerNpcCommand, client: ActorRef) {
    Error(s"No callback for $cmd")
  }

  /**
   * Returns the link id of a method
   * @param name the methods name
   * @return a link id
   */
  def method(name: String) = {
    npcMethods.find { case (id: Int, meth: Method) => meth.getName == name + "$1"} match {
      case Some(x) => x._1
      case None    =>
        log.info(s"Failed to get method name $name for npc $npc))")
        NO_LINK_FOUND
    }
  }

  /**
   * Handle an npc interaction
   * @param link the methods link id
   * @return
   */
  def method(link: Int) = {
    npcMethods.get(link) match {
      case Some(method) => method
      case None         => npcMethods(NO_LINK_FOUND) //Return no link method
    }
  }

  /**
   * Send a message to the client, usually we're just sending the Say, Face, etc packets
   * @param msg the message to send
   */
  def sendToClient(msg: AnyRef)(implicit client: ActorRef, event: NpcEvent) = client ! msg

  /**
   * Optional prompt
   * @param predicate the expression to check
   * @param text the text to say
   * @param txt the text for the input textbox
   * @param link the link back id
   */
  def PromptIf(predicate: => Boolean, text: String, txt: String, link: Int)(implicit client: ActorRef, event: NpcEvent) {
    if (predicate)
      Prompt(text, txt, link)
  }

  /**
   * Prompt for an input field
   * @param text the text to say
   * @param txt the text for the input box
   * @param link the linkback for the textbox
   * @param client
   * @return
   */
  def Prompt(text: String, txt: String, link: Int)(implicit client: ActorRef, event: NpcEvent) {
    Say(text)
    Input(txt, link)
    Link("Let me think it over.", NO_LINK_BACK)
    Finish
  }

  /**
   * Add a input control to the dialog
   * @param text the label for the input
   * @param link the link back id
   */
  def Input(text: String, link: Int)(implicit client: ActorRef, event: NpcEvent) = client ! InputCmd(text, link)

  /**
   * Ask a yes no question if the predicate is true
   * @param predicate the expression to check
   * @param text the question text
   * @param yesLink the id to yes if yes is pressed
   * @param yesTxt the yes text
   * @param noTxt the no text
   */
  def QuestionIf(predicate: => Boolean, text: String, yesLink: Int, yesTxt: String = "Yes", noTxt: String = "No")(implicit client: ActorRef, event: NpcEvent) {
    if (predicate)
      Question(text, yesLink, yesTxt, noTxt)
  }

  /**
   * Ask a yes no question
   * @param text the question text
   * @param yesLink the id to yes if yes is pressed
   * @param yesTxt the yes text
   * @param noTxt the no text
   */
  def Question(text: String, yesLink: Int, yesTxt: String = "Yes", noTxt: String = "No")(implicit client: ActorRef, event: NpcEvent) {
    Say(text)
    Link(yesTxt, yesLink)
    Link(noTxt, NO_LINK_BACK)
    Finish
  }

  /**
   * Say something to the client
   * @param text the text to say
   */
  def Say(text: String)(implicit client: ActorRef, event: NpcEvent) = client ! SayCmd(text)

  /**
   * Add a option to the dialog
   * @param text the options text
   * @param link the link back id
   */
  def Link(text: String, link: Int)(implicit client: ActorRef, event: NpcEvent) = client ! LinkCmd(text, link)

  /**
   * Finish a npc dialog event
   * @param client
   */
  def Finish(implicit client: ActorRef, event: NpcEvent) {
    Face(face)
    client ! FinishCmd
  }

  /**
   * Set the face that the dialog will show
   * @param face the face id
   */
  def Face(face: Int)(implicit client: ActorRef, event: NpcEvent) = client ! FaceCmd(face)

  /**
   * Wrapper around the error function to give it a better for non error messages
   * @param msg the message to send
   * @param client the clients actor
   * @return
   */
  def Info(msg: String)(implicit client: ActorRef, event: NpcEvent) {
    Error(msg)
  }

  /**
   * Request some information from the client
   */
  def request(cmd: ServerNpcCommand)(implicit client: ActorRef, event: NpcEvent) = client ! cmd

  /**
   * Prompt for a list of options
   * @param text to display
   * @param options the options
   */
  def Options(text: String, noChoiceText: String, options: (String, Int)*)(implicit client: ActorRef, event: NpcEvent) {
    Say(text)
    options.foreach(opt => Link(opt._1, opt._2))
    Link(noChoiceText, NO_LINK_BACK)
    Finish
  }

  /**
   * No link method found
   * @param event
   * @param client
   * @return
   */
  @Response
  private def noLinkFound(implicit client: ActorRef, event: NpcInteraction) {
    Error(s"${event.npcID} doesn't have handler for link ${event.linkID}")
    log.debug(s"${event.npcID} doesn't have handler for link ${event.linkID}")
  }

  /**
   * Display a error message to the client
   * @param msg the msg to send
   * @param client the client
   * @return
   */
  def Error(msg: String)(implicit client: ActorRef, event: NpcEvent) {
    Say(msg)
    Link("OK", NO_LINK_BACK)
    Finish
  }
}
