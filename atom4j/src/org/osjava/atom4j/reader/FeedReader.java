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
package org.osjava.atom4j.reader;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osjava.atom4j.digester.ContentRuleSet;
import org.osjava.atom4j.digester.GeneratorRuleSet;
import org.osjava.atom4j.digester.LinkRuleSet;
import org.osjava.atom4j.digester.PersonRuleSet;
import org.osjava.atom4j.pojo.Feed;

import java.io.InputStream;

/**
 * Created on Feb 14, 2004
 * @author Lance
 */
public class FeedReader extends AtomReader
{        
    private static Log logger = 
       LogFactory.getFactory().getInstance(FeedReader.class);
       
    private Feed feed = null;
    public void setFeed(Feed _feed)
    {
        feed = _feed;
    }
    public Feed getFeed() 
    {
        return feed;
    }

    protected void initDigester()
    {
        digester = new Digester();
        digester.push(this);
        //digester.setDebug(0);
        digester.setValidating(false);
        
        configureFeedDigester();
    }

    /**
     * @return
     */
    protected void configureFeedDigester()
    {
        String feedPath = "feed";
        digester.addObjectCreate(feedPath, Feed.class.getName());
        
        digester.addBeanPropertySetter(feedPath + "/id");
        
        digester.addCallMethod(feedPath + "/modified",  "setModified", 0);

        digester.addRuleSet(new ContentRuleSet(feedPath + "/title", "setTitle"));

        digester.addRuleSet(new LinkRuleSet(feedPath + "/link"));

        digester.addRuleSet(new PersonRuleSet(feedPath + "/author", "setAuthor"));

        digester.addRuleSet(new PersonRuleSet(feedPath + "/contributor", "addContributor"));

        digester.addRuleSet(new ContentRuleSet(feedPath + "/tagline", "setTagline"));

        digester.addRuleSet(new GeneratorRuleSet(feedPath + "/generator"));

        digester.addRuleSet(new ContentRuleSet(feedPath + "/copyright", "setCopyright"));

        digester.addRuleSet(new ContentRuleSet(feedPath + "/info", "setInfo"));

        // add Entry reading rules to this Feed digester
        configureEntryDigester(digester, feedPath + "/"); 

        digester.addSetNext(feedPath, "setFeed", Feed.class.getName());
    }

    /**
     * @param xml
     */
    public FeedReader(String xml)
    {
        super(xml);
        parse();
    }

    /**
     * @param in
     */
    public FeedReader(InputStream in)
    {
        super(in);
        parse();
    }

}
