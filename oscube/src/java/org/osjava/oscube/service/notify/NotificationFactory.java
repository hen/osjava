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
 * + Neither the name of OSJava nor the names of its contributors 
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
package org.osjava.oscube.service.notify;

import org.osjava.oscube.util.FactoryUtils;
import org.osjava.oscube.container.Session;
import com.generationjava.config.Config;

public class NotificationFactory {

    static public Notifier getSuccessNotifier(Config cfg, Session session) {
        String name = cfg.getString("notifier.success");
        Notifier notifier = (Notifier)FactoryUtils.getObject(name, "Notifier", "org.osjava.oscube.service.notify");
        if(notifier == null) {
            throw new RuntimeException("Unable to find Success Notifier. ");
        }
        return notifier;
    }

    static public Notifier getErrorNotifier(Config cfg, Session session, Exception e) {
        String name = cfg.getString("notifier.error."+e.getClass().getName());
        if(name == null) {
            name = cfg.getString("notifier.error");
        }
        Notifier notifier = (Notifier)FactoryUtils.getObject(name, "Notifier", "org.osjava.oscube.service.notify");
        if(notifier == null) {
            throw new RuntimeException("Unable to find Error Notifier. ");
        }
        return notifier;
    }

}
