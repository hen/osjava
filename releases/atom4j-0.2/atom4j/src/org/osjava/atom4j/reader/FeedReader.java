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
import org.osjava.atom4j.pojo.Feed;
import org.osjava.atom4j.pojo.Generator;

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
        String feedPath = "feed";
        digester.addObjectCreate(feedPath, Feed.class.getName());
        digester.addCallMethod(feedPath + "/id",        "setId", 0);
        digester.addCallMethod(feedPath + "/modified",  "setModified", 0);

        configureContentDigester(digester, feedPath + "/title", "setTitle", "</title>");

        //TODO I think Link reading is broken (more than one)
        configureLinkDigester(digester, feedPath + "/link");

        configurePersonDigester(digester, feedPath + "/author", "setAuthor");

        configurePersonDigester(digester, feedPath + "/contributor", "addContributor");

        configureContentDigester(digester, feedPath + "/tagline", "setTagline", "</tagline>");

        digester.addObjectCreate( feedPath + "/generator", Generator.class.getName());
        digester.addSetProperties(feedPath + "/generator");
        digester.addCallMethod(feedPath + "/generator",  "setContent", 0);
        digester.addSetNext(feedPath + "/generator", "setGenerator", Generator.class.getName());

        configureContentDigester(digester, feedPath + "/copyright", "setCopyright", "</copyright>");

        configureContentDigester(digester, feedPath + "/info", "setInfo", "</info>");

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
