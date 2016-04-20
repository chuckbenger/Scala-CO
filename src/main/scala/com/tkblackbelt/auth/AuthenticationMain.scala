package com.tkblackbelt.auth

import akka.actor.{Props, ActorSystem}
import com.tkblackbelt.auth.server.AuthenticationServerActor
import java.net.InetSocketAddress
import com.tkblackbelt.core.global.ServerConfig
import ServerConfig._
import com.tkblackbelt.core.util.HeaderPrinter

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

object AuthenticationMain extends App with HeaderPrinter {

    val system = ActorSystem("AuthenticationServer")

    system.log.info("Authentication Server Starting")

    val host = Some(config.getString("auth.host")).getOrElse("localhost")
    val port = Some(config.getInt("auth.port")).getOrElse(9958)
    val address = new InetSocketAddress(host, port)

    val server = system.actorOf(Props(new AuthenticationServerActor(address)), "AuthServer")

}
