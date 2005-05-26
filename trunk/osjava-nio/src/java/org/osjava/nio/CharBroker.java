/*
 * org.osjava.nio.CharBroker
 *
 * $Id$
 * $URL$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Copyright (c) 2003-2005, Anthony Riley, Robert M. Zigweid
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name of the OSJava-NIO nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package org.osjava.nio;

import java.nio.CharBuffer;

/**
 * @author cybertiger
 *
 * And interface to represent something which brokers chars, either via
 * the CharBuffer class, or via arrays.
 */
public interface CharBroker {
    /**
     * Do something with some chars
     *
     * @param data Some characters, this method is not guaranteed to use all 
     *        the data, it is upto the caller to deal with any chars that are
     *        left in the CharBuffer when this method returns.
     */
    public void broker(CharBuffer data);

    /**
     * Do something with some chars
     *
     * @param data Some characters, this method is not guaranteed to use all 
     *        the data, it is upto the caller to deal with any chars that are
     *        left in the CharBuffer when this method returns.
     * @param close Close socket on last write.
     */
    public void broker(CharBuffer data, boolean close);

    /**
     * Do something with some chars
     *
     * @param data Some characters, this method is not guaranteed to use all 
     *        the data, it is upto the caller to deal with any chars that are
     *        not written
     * @return the number of chars brokered.
     */
    public int broker(char[] data);

    /**
     * Do something with some chars
     *
     * @param data Some characters, this method is not guaranteed to use all 
     *        the data, it is upto the caller to deal with any chars that are
     *        not written
     * @param close Close socket on last write.
     * @return the number of chars brokered.
     */
    public int broker(char[] data, boolean close);

    /**
     * Do something with some chars
     *
     * @param data Some characters, this method is not guaranteed to use all the
     * data, it is upto the caller to deal with any chars that are not written
     * @param offset The offset into data where the chars to broker are.
     * @param len The number of chars at the offset to broker.
     * @param close Close socket on last write.
     * @return the number of chars brokered.
     */
    public int broker(char[] data, int offset, int len, boolean close);

    /**
     * Do something with some chars
     *
     * @param str Some characters, this method is not guaranteed to use all the
     * data, it is upto the caller to deal with any chars that are not used.
     * @return the number of chars brokered
     */
    public int broker(CharSequence str);

    /**
     * Do something with some chars
     *
     * @param str Some characters, this method is not guaranteed to use all the
     * data, it is upto the caller to deal with any chars that are not used.
     * @param close Close socket on last write.
     * @return the number of chars brokered
     */
    public int broker(CharSequence str, boolean close);

    /**
     * Do something with some chars
     *
     * @param str Some characters, this method is not guaranteed to use all the
     * data, it is upto the caller to deal with any chars that are not used.
     * @param offset The offset into data where the chars to broker are.
     * @param len The number of chars at the offset to broker.
     * @return the number of chars brokered
     */
    public int broker(CharSequence str, int offset, int len);

    /**
     * Do something with some chars
     *
     * @param str Some characters, this method is not guaranteed to use all the
     * data, it is upto the caller to deal with any chars that are not used.
     * @param offset The offset into data where the chars to broker are.
     * @param len The number of chars at the offset to broker.
     * @param close Close socket on last write.
     * @return the number of chars brokered
     */
    public int broker(CharSequence str, int offset, int len, boolean close);

    /**
     * Test if this CharBroker is closed
     *
     * @return true if this CharBroker is closed
     */
    public boolean isClosed();

}
