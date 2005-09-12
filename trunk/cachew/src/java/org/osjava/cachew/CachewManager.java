/*
 * Created on Jun 10, 2005
 */
package org.osjava.cachew;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author hen
 */
public class CachewManager {
	
	private Map map = new HashMap();
	
	public CachewItem getItem(String path) {
		Cachew cachew = getCachew(path);
		return cachew.getItem(path);
	}
	
	public CachewItem createItem(String path) {
		Cachew cachew = getCachew(path);
		return cachew.createItem(path);
	}
	
	private Cachew getCachew(String path) {
		Iterator itr = map.keySet().iterator();
		while(itr.hasNext()) {
			String key = (String) itr.next();
			if(path.matches(key)) {
				return (Cachew) map.get(path);
			}
		}
		return null;
	}

}
