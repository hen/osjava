package com.generationjava.awt;

public interface InformationListener {

    public void report(ReportEvent re);
    
    public Object request(RequestEvent re);
  
  // when lots of components are involved, we need 
  // to only multicast to those interested, not all.
  // however this is overkill in a simple system, 
  // broadcast is best.  
  // the best way seems to be for each listener to 
  // offer up a list of the messages they are interested
  // in. Then the central app may decide whether to 
  // deal with these or lump them all in together.
//    public Vector getConcerns();

}