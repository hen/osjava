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

    public void addToTrail(BreadCrumb bc) {
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

        //    if crumb's referrer can be found in the trail, remove 
        //      all entries after the crumb's referrer, then add to norm

        normalizedTrail.add( bc );
    }

    public Iterator iterateTrail() {
        return trail.iterator();
    } 

    public Iterator iterateNormalizedTrail() {
        return normalizedTrail.iterator();
    }

}
