package org.cyberiantiger.mudclient.ui;

public class LineSegment {

    public char[] data;
    public int offset;
    public int len;
    public int attr;
    public LineSegment next = null;

    // Create a new segment, consisting of len spaces.
    public LineSegment(int len,int attr) {
	data = new char[len];
	for (int i=0;i<len;i++) {
	    data[i] = ' ';
	}
	offset = 0;
	this.len = len;
	this.attr = attr;
    }

    // Create a new segment, with the specified str, and attributes.
    public LineSegment(String str, int attr) {
	this.len = str.length();
	this.offset = 0;
	this.data = new char[len];
	str.getChars(0,str.length(),data,0);
	this.attr = attr;
    }

    // Create a new segment, with the specified char[], and attributes.
    public LineSegment(char[] data, int offset, int len, int attr) {
	this.data = data;
	this.offset = offset;
	this.len = len;
	this.attr = attr;
    }

    public void overwriteSegment(int offset, LineSegment segment) {
	if(offset > len) {
	    if(next == null) {
		next = new LineSegment(offset-len,ConsoleModel.DEFAULT);
		next.next = segment;
	    } else {
		next.overwriteSegment(offset-len, segment);
	    }
	} else if(offset == len) {
	    if(next == null) {
		// appends to this segment
		this.next = segment;
	    } else {
		// overwrites the next segment, and maybe the one after that.
		segment.next = next.deleteChars(segment.len);
		this.next = segment;
	    }
	} else {
	    if(offset + segment.len > len) {
		// Carries on, into next segment
		segment.next = next.deleteChars(this.len - offset);
		this.next = segment;
		this.len = offset;
	    } else if(offset + segment.len == len) {
		// Overwrites the latter half of this segment.
		this.len = offset;
		segment.next = this.next;
		this.next = segment;
	    } else {
		// Splits this segment in two.
		LineSegment tail = new LineSegment(data,this.offset + offset + segment.len, len - offset - segment.len, this.attr);
		tail.next = this.next;
		segment.next = tail;
		this.next = segment;
	    }
	}
    }

    public LineSegment deleteChars(int number) {
	if(number > len) {
	    if(next != null) {
		return next.deleteChars(number - len);
	    } else {
		return null;
	    }
	} else if(number == len) {
	    return next;
	} else {
	    offset += number;
	    len -= number;
	    return this;
	}
    }

    public String toString() {
	return new String(data,offset,len);
    }
}
