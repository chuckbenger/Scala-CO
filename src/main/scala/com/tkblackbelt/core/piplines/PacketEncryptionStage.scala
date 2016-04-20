package com.tkblackbelt.core.piplines

import akka.io.{PipePair, PipelineContext, PipelineStage}
import akka.util.ByteString
import com.tkblackbelt.java.encryption.Cryptographer

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

class PacketEncryptionStage(crypto: Cryptographer) extends PipelineStage[PipelineContext, ByteString, ByteString, ByteString, ByteString] {


  override def apply(ctx: PipelineContext) = new PipePair[ByteString, ByteString, ByteString, ByteString]  {

    val commandPipeline = (bs: ByteString) => ctx.singleCommand(encrypt(bs))

    val eventPipeline = (bs: ByteString) => ctx.singleEvent(decrypt(bs))

    def decrypt(bs: ByteString): ByteString = {
      val buffer = bs.toByteBuffer
      crypto.decrypt(buffer)
      buffer.position(0)
      ByteString(buffer)
    }

    def encrypt(bs: ByteString): ByteString = {
      val buffer = bs.toByteBuffer
      crypto.encrypt(buffer)
      buffer.position(0)
      ByteString(buffer)
    }
  }
}
