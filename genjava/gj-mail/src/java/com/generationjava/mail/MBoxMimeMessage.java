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

import java.io.InputStream;

import javax.mail.*;
import javax.mail.internet.*;

import com.generationjava.collections.CollectionsW;
import com.generationjava.io.StreamW;
import org.apache.commons.lang.StringUtils;

// one other idea is to allow a body only to be passed in, 
// with the headers already set within that.

// TODO:
//    Attachment: 1. <filename> <comment>,
//                2. .....
public class MBoxMimeMessage extends MimeMessage {

    private static java.text.DateFormat format = new java.text.SimpleDateFormat();

    public MBoxMimeMessage(Session session) {
        super(session);
    }

    public void setText(String body) throws MessagingException {
        parseMBox(this, body);
    }

    public void parseMBox(Part mimePart, String body) throws MessagingException {
// Date: 26 Feb 2001 00:11:05 +0000
// From: <email>
// To: <email>
// Subject: <subject>
// Content-Type: TEXT/PLAIN; charset=US-ASCII
// then a blank line. How to handle multi??

// multi is handled by:
// Content-Type: multipart/alternative; boundary="FLAG"
// --FLAG
// Content-Type: text/plain; charset="iso-8859-1"
// ....texxt...
// Content-Type: text/html; charset="iso-8859-1"
// Content-Transfer-Encoding: quoted-printable

        // split lines
        String[] lines = StringUtils.split(body, "\n");

        // pre process lines so that any headers that are split 
        // over a newline are joined

        int sz = lines.length;
        String multipart = null;
        String mimeType = null;
        String subtype = null;
        int i=0;  // kept for later
        for(; i<sz; i++) {
            if("".equals(lines[i].trim())) {
                break;
            }

            int colonIdx = lines[i].indexOf(":");
            if(colonIdx == -1) {
                if(!lines[i].trim().startsWith("From ")) {
                    if(!lines[i].equals("")) {
                        i--;
                    }
                    break;
                }
            } else {
                String hdr   = lines[i].substring(0, colonIdx).trim();
                String value = lines[i].substring(colonIdx+1).trim();

                if(mimePart instanceof MimeMessage) {
                    MimeMessage msg = (MimeMessage)mimePart;
                    if("Date".equals(hdr)) {
                        // TODO: Check if this is meant to be a standard of some kind
//                        msg.setSentDate( DateW.parseString(value) );
                        try {
                            msg.setSentDate( format.parse(value) );
                        } catch(java.text.ParseException pe) {
                            throw new MessagingException("Unable to parse Date: "+value);
                        }
                        continue;
                    } else
                    if("From".equals(hdr)) {
                        msg.setFrom( InternetAddress.parse(value)[0] );
                        continue;
                    } else
                    if("ReplyTo".equals(hdr)) {
                        msg.setReplyTo( InternetAddress.parse(value) );
                        continue;
                    } else
                    if("To".equals(hdr)) {
                        msg.setRecipients( MimeMessage.RecipientType.TO, InternetAddress.parse(value) );
                        continue;
                    } else
                    if("Cc".equals(hdr)) {
                        msg.setRecipients( MimeMessage.RecipientType.CC, InternetAddress.parse(value) );
                        continue;
                    } else
                    if("Bcc".equals(hdr)) {
                        msg.setRecipients( MimeMessage.RecipientType.BCC, InternetAddress.parse(value) );
                        continue;
                    } else
                    if("Attachment".equals(hdr)) {
                        // <filename> [content-type] "comment", 
                        String[] attachments = StringUtils.split(value, ",");
                        Multipart multi = new MimeMultipart();
                        mimePart.setContent(multi);
                        for(int k=0; k<attachments.length; k++) {
                            String attach = attachments[k];
                            int idx = attach.indexOf(" ");
                            int idx2 = attach.indexOf(" ", idx+1);
                            String filename = attach.substring(0,idx);
                            InputStream in = StreamW.loadFromClasspath(filename, this);
                            MimeBodyPart mbp = new MimeBodyPart();
                            mbp.setFileName(filename);
                            String type;
                            if(idx2 == -1) {
                                // no comment
                                type = attach.substring(idx);
                            } else {
                                type = attach.substring(idx+1, idx2);
                                mbp.setDescription(attach.substring(idx2+1));
                            }
                            mbp.setContent("["+attach+"]",type);
                            multi.addBodyPart(mbp);
                        }
                        continue;
                    }
                }
                if("Content-Type".equals(hdr)) {
                    if(value.startsWith("multipart/")) {
// Content-Type: multipart/mixed; boundary="FLAG"
                        subtype   = StringUtils.getNestedString(value, "/", ";");
                        multipart = StringUtils.getNestedString(value, "\"", "\"");
                        mimeType = "multipart/"+subtype;
                    } else {
                        mimePart.setHeader(hdr, value);
                        mimeType = value;
                    }
                } else
                if("Content-Transfer-Encoding".equals(hdr)) {
                    // ignore
                } else {
                    mimePart.setHeader(hdr, value);
                }
            }
        }

        String[] rest = CollectionsW.getSubArray( lines, i+1 ); 
        body = StringUtils.join(rest, "\n");

        if(multipart != null) {
            // we know the flag at which the multipart stuff will be 
            // shown. Then we have a body of text which should be set text?,
            // then a chunk of text which is made up of multiple
            //   --flag (must be at start of line)
            //   headers:\n\n
            //   body
            // 
            // everything after this list is to be ignored. 
            // if no content-type set, then don't need a \n\n to end 
            // a section, else you need one.
            //
            // 
            
            multipart = "\n--"+multipart+"\n";
            String[] chunks = StringUtils.split( body, multipart );
            MimeMultipart mu = new MimeMultipart();

            // each chunks is a perfectly fine MimePart.
            for(int j=0; j<chunks.length; j++) {
                // TODO: try looking in chunk[j] to see if we think it's a 
                // multipart....  if possible... can a MultiP contain a MP?
                BodyPart mp = new MimeBodyPart();
                parseMBox(mp, chunks[j]);
                mu.addBodyPart(mp);
            }

            mimePart.setContent( mu );
        } else {
            if(mimeType != null) {
                mimePart.setContent(body, mimeType);
            } else {
                if(mimePart == this) {
                    super.setText(body);
                } else {
                    mimePart.setText(body);
                }
            }
        }
    }

}
