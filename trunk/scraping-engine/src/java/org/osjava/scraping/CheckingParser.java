/*
 * Copyright (c) 2003, Henri Yandell
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
 * + Neither the name of Scabies nor the names of its contributors 
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
package org.osjava.scraping;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;
import org.osjava.oscube.container.Result;
import org.osjava.oscube.container.NullResult;

import org.osjava.oscube.container.Header;
import org.osjava.oscube.service.store.*;

public abstract class CheckingParser extends AbstractParser {

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        Header header = parseHeader(page, cfg, session);
        Store store = StoreFactory.getStore(cfg, session);
        try {
            boolean found = store.exists(header, cfg, session);
            if(found) {
                return new NullResult();
            } else {
                return parseBody(page, header, cfg, session);
            }
        } catch(StoringException se) {
            return new NullResult();
        }
    }

    public abstract Header parseHeader(Page page, Config cfg, Session session) throws ParsingException;
    public abstract Result parseBody(Page page, Header header, Config cfg, Session session) throws ParsingException;

}
