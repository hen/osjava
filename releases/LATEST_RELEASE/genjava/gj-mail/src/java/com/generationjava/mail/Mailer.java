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
 * + Neither the name of Genjava-Core nor the names of its contributors 
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
package com.generationjava.mail;

import java.util.Properties;
import java.util.Map;
import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.generationjava.util.Interpolator;
import com.generationjava.namespace.SimpleNamespace;

// make it easy to mail things.
// one other idea is to allow a body only to be passed in, 
// with the headers already set within that.
public class Mailer {

    static public Mailer getInstance(String smtp) {
        return new Mailer(smtp);
    }

    private String smtp;

    public Mailer(String smtp) {
        this.smtp = smtp;
    }

    public Mailer() {
        this("127.0.0.1");
    }

    public void mail(String body) throws MessagingException {
        mail(body, null);
    }

    public void mail(String body, Map values) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtp);

        Session session = Session.getDefaultInstance(props, null);
        MimeMessage msg = new MBoxMimeMessage(session);
//        session.setDebug(true);

        if(values != null) {
            body = Interpolator.interpolate(body, new SimpleNamespace(values));
        }

        msg.setText(body);

        Transport.send(msg);
    }

}
