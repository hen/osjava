/*
 * Copyright (c) 2004, Henri Yandell
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
 * + Neither the name of OSJava nor the names of its contributors 
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

package org.osjava.taglib.trail;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class BreadCrumbs {

    private List trail = new ArrayList();
    private List normalizedTrail = new ArrayList();

    // needs lots of thought, this current version breaks after 
    // the first norm

    // i need to be able to look at the http-referer too
    // if someone hits back once or twice etc, then chooses a 
    // sibling, i have to be able to see that

    // also have to fix the .do/.jsp problem. servlet filter?

    public void addToTrail(String referer, BreadCrumb bc) {
        trail.add(bc);

        //    if crumb equals last crumb, do not add to norm
        if( !normalizedTrail.isEmpty() ) {
            BreadCrumb last = (BreadCrumb) normalizedTrail.get( normalizedTrail.size() - 1 );
            if(bc.equals(last)) {
                return;
            }
        }

        //    if crumb can be found in the trail, remove all entries 
        //      after the crumb, and don't add to norm
        if(normalizedTrail.contains(bc)) {
            int idx = normalizedTrail.indexOf(bc);
            normalizedTrail = normalizedTrail.subList(0, idx + 1);
            return;
        }

        //    if crumb's referer can be found in the trail, remove 
        //      all entries after the crumb's referer, then add to norm
        if(referer != null) {
            Iterator itr = normalizedTrail.iterator();
            while(itr.hasNext()) {
                BreadCrumb tmp = (BreadCrumb) itr.next();
                if(tmp.getUrl().equals(referer)) {
                    int idx = normalizedTrail.indexOf(tmp);
                    normalizedTrail = normalizedTrail.subList(0, idx + 1);
                    break;
                }
            }
        }

        normalizedTrail.add( bc );
    }

    public Iterator iterateTrail() {
        return trail.iterator();
    } 

    public Iterator iterateNormalizedTrail() {
        return normalizedTrail.iterator();
    }

}
