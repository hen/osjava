/*
 * Created on Nov 9, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.cyberiantiger.nio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * @author rzigweid
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public interface SocketListener {

    
    public void receiveData(ByteBuffer buf);
}
