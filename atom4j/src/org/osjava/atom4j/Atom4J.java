/*
 *   Copyright 2003-2004 Lance Lavandowska
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.osjava.atom4j;    

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * http://atomenabled.org
 * http://www.imc.org/atom-syntax/index.html
 * http://www.intertwingly.net/wiki/pie/FrontPage
 * 
 * Created on Aug 29, 2003
 * 
 * @author llavandowska
 */
public abstract class Atom4J
{
    public static String xmlns = "http://purl.org/atom/ns#";

    // doesn't work with the Z on the end
    public static SimpleDateFormat READ_ISO8601_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    
    public static SimpleDateFormat WRITE_ISO8601_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    public static String formatIso8601(Date date)
    {
        if (date == null) return "";
        
        return WRITE_ISO8601_FORMAT.format(date);
    }
    
    public static String simpleTag(Object value, String tagName) 
    {
        if (value == null) value = "";
        return "<" + tagName + ">" + value.toString() + "</" + tagName + ">\n";
    }
    
    public static String dateTag(Date date, String tagName)
    {
        return "<" + tagName + ">" + Atom4J.formatIso8601(date) + "</" + tagName + ">\n";
    }
}
