package org.cyberiantiger.mudclient.ui;

import java.util.List;
import java.util.ArrayList;

public class Line {

    public LineSegment first = null;

    public Line() {
    }

    public void overwriteString(int offset,int attr,String str) {
	LineSegment segment = new LineSegment(str, attr);
	overwriteSegment(offset,segment);
    }

    public void overwriteString(int offset,int attr,char[] chars,int off,int len) 
    {
	LineSegment segment = new LineSegment(chars,off,len, attr);
	overwriteSegment(offset,segment);
    }

    public void overwriteSegment(int offset,LineSegment segment) {
	if(first == null) {
	    if(offset != 0) {
		first = new LineSegment(offset,ConsoleModel.DEFAULT);
		first.next = segment;
	    } else {
		first = segment;
	    }
	} else {
	    if(offset == 0) {
		segment.next = first.deleteChars(segment.len);
		first = segment;
	    } else {
		first.overwriteSegment(offset,segment);
	    }
	}
    }

    public String toString() {
	StringBuffer ret = new StringBuffer();
	LineSegment seg = first;
	while(seg != null) {
	    ret.append(seg.data,seg.offset,seg.len);
	    seg = seg.next;
	}
	return ret.toString();
    }
}
