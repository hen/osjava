/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of Genjava-Core nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
package com.generationjava.random;

import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import com.generationjava.collections.ClassMap;
import org.apache.commons.lang.RandomStringUtils;

public class RandomObject implements RandomMaker {

    private Map registry = new ClassMap(new HashMap());
    public RandomObject() {
        RandomNumber rnd = new RandomNumber();
        registry.put(Number.class, rnd);
        registry.put(Integer.TYPE, rnd);
        registry.put(Double.TYPE, rnd);
        registry.put(Float.TYPE, rnd);
        registry.put(Long.TYPE, rnd);
        registry.put(Short.TYPE, rnd);
        registry.put(Byte.TYPE, rnd);
        registry.put(Date.class, new RandomDate());
        registry.put(Object.class, new RandomBean());
    }

    public Object makeInstance(Class clss) {
        // check this first so Bean doesn't get Object.class ones.
        if(clss == Object.class) {
            return new Object();
        } 
        if(clss == String.class) {
            // hack until RandomStringMaker/RandomStringUtils hooked up
            return RandomStringUtils.randomAlphanumeric(RandomNumber.randomInt(50));
        }
        RandomMaker maker = (RandomMaker)registry.get(clss);
        if(maker != null) {
            return maker.makeInstance(clss);
        }
        return null;
    }

    public void addRandomMaker(Class clss, RandomMaker maker) {
        registry.put(clss, maker);
    }

}
