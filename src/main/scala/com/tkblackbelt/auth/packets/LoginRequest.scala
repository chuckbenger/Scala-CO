package com.tkblackbelt.auth.packets

import com.tkblackbelt.core.Packet
import akka.util.ByteString

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


object LoginRequest {

  val packetType: Short = 1051

  def apply(bs: ByteString): LoginRequest = {
    val iter = bs.iterator
    val username: Array[Byte] = Array.ofDim(16)
    val password: Array[Byte] = Array.ofDim(16)
    val server: Array[Byte] = Array.ofDim(16)

    iter getBytes username getBytes password getBytes server

    LoginRequest(new String(username).trim, new String(password).trim, new String(server).trim)
  }

}

case class LoginRequest(username: String, password: String, server: String) extends Packet(LoginRequest.packetType) {


  override lazy val deconstructed: ByteString = deconstruct {
    _.putBytes(username.getBytes().padTo(16, 0.toByte))
    .putBytes(password.getBytes().padTo(16, 0.toByte))
    .putBytes(server.getBytes().padTo(16, 0.toByte))
  }

  override def toString: String = s"${super.toString} ($username $password $server)"
}
