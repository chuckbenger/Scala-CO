package com.tkblackbelt.java.encryption;

/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2013 charlesbenger
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import java.nio.ByteBuffer;

public final class Cryptographer
{

    private int inCounter = 0;
    private int outCounter = 0;
    private boolean usingAlternate = false;

    private static byte[] key1 = new byte[256];
    private static byte[] key2 = new byte[256];
    private byte[] key3 = new byte[256];
    private byte[] key4 = new byte[256];

    private static int[] passwordKey = {
            0xebe854bc, 0xb04998f7, 0xfffaa88c, 0x96e854bb, 0xa9915556, 0x48e44110, 0x9f32308f,
            0x27f41d3e, 0xcf4f3523, 0xeac3c6b4, 0xe9ea5e03, 0xe5974bba, 0x334d7692, 0x2c6bcf2e,
            0xdc53b74, 0x995c92a6, 0x7e4f6d77, 0x1eb2b79f, 0x1d348d89, 0xed641354, 0x15e04a9d,
            0x488da159, 0x647817d3, 0x8ca0bc20, 0x9264f7fe, 0x91e78c6c, 0x5c9a07fb, 0xabd4dcce,
            0x6416f98d, 0x6642ab5b
    };

    static
    {
        // Generate the default key
        byte i_key1 = (byte) 0x9D, i_key2 = 0x62;

        for (int i = 0; i < 256; i++) {
            key1[i] = i_key1;
            key2[i] = i_key2;
            i_key1 = (byte) ((0x0F + (byte) (i_key1 * 0xFA)) * i_key1 + 0x13);
            i_key2 = (byte) ((0x79 - (byte) (i_key2 * 0x5C)) * i_key2 + 0x6D);
        }
    }

    /**
     * Encrypt a {@code ByteBuffer}
     * @param attachment
     */
    public synchronized void encrypt(ByteBuffer attachment)
    {
        byte[] buffer = attachment.array();
        for (int i = 0, length = attachment.limit(); i < length; i++)
        {
            buffer[i] ^= 0xAB;
            buffer[i] = (byte) (((buffer[i] & 0xFF) >> 4) | ((buffer[i] & 0xFF) << 4));
            buffer[i] ^= key2[(outCounter) >> 8] ^ key1[(outCounter) & 0xFF];
            outCounter = (outCounter + 1) % (1 << 16);
        }
    }

    /**
     * Decrypt a {@code ByteBuffer}
     * @param attachment
     */
    public synchronized void decrypt(ByteBuffer attachment) {
        byte[] buffer = attachment.array();
        for (int i = 0, length = attachment.limit(); i < length; i++)
        {
            buffer[i] ^= 0xAB;
            buffer[i] = (byte) (((buffer[i] & 0xFF) >> 4) | ((buffer[i] & 0xFF) << 4));

            if(usingAlternate)
            {
                buffer[i] ^= key4[inCounter >> 8] ^ key3[inCounter & 0xFF];
            }
            else
            {
                buffer[i] ^= key2[inCounter >> 8] ^ key1[inCounter & 0xFF];
            }

            inCounter = (inCounter + 1) % (1 << 16);
        }
    }

    /**
     * Set the keys for this {@code Cryptographer}
     * @param inKey1 this is the token sent by the {@code AuthServer}
     * @param inKey2 this is the identity sent by the {@code AuthServer}
     */
    public synchronized void setKeys(long inKey1, long inKey2)
    {
        long DWordKey = ((inKey1 + inKey2) ^ 0x4321) ^ inKey1;
        long IMul = DWordKey * DWordKey;

        byte[]  XorKey =  {
                (byte) (DWordKey & 0xFF),
                (byte) ((DWordKey >> 8) & 0xFF),
                (byte) ((DWordKey >> 16) & 0xFF),
                (byte) ((DWordKey >> 24) & 0xFF),

                (byte)  (IMul & 0xFF),
                (byte) ((IMul >> 8) & 0xFF),
                (byte) ((IMul >> 16) & 0xFF),
                (byte) ((IMul >> 24) & 0xFF)
        };

        for ( int i = 0; i < 256; i++ )
        {
            key3[i] = (byte) (XorKey[i % 4] ^ key1[i]);
            key4[i] = (byte) (XorKey[i%4+4] ^ key2[i]);
        }

        usingAlternate = true;
        outCounter = 0;
    }

}
