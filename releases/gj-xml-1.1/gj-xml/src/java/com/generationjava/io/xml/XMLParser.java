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
package com.generationjava.io.xml;

import java.io.Reader;
import java.io.IOException;
import java.util.Stack;

/**
 * A tiny parser of xml text. It is intended to deal with simple 
 * config files.
 */
public class XMLParser {

    // various states in which the parser may be.
    private static final int VACUUM = 1;
    private static final int IN_TAG = 2;
    private static final int IN_TAG_NAME = 3;
    private static final int CLOSING_TAG = 9;
    private static final int SLASH = 4;
    private static final int ATTR_NAME = 5;
    private static final int END_ATTR_NAME = 6;
    private static final int ATTR_VALUE = 7;
    private static final int END_ATTR_VALUE = 8;
    private static final int START_COMMENT = 10;
    private static final int IN_COMMENT = 11;
    // special state to handle the DOCTYPE 'tag'
    private static final int IN_DOCTYPE = 12;
    private static final int IN_DTD = 13;

    public XMLParser() {
    }
    
    /**
     * Parse an XML text read in from a Reader.
     * Returns the root node of the xml text.
     */
    public XMLNode parseXML(Reader reader) throws IOException {
        int state = VACUUM;
        int x = 1;   // current character number 
        int y = 1;   // current line number
        int rch = 0;
        char ch;
        boolean pi = false;
        
        StringBuffer tag_name = new StringBuffer();
        StringBuffer attr_name = new StringBuffer();
        StringBuffer attr_value = new StringBuffer();
        StringBuffer value = new StringBuffer();

        XMLNode root = null;
        XMLNode node = null;
        
        
        // the current parent.
        XMLNode parent = null;
        
        // Stack is used to remember the hierarchy of parents.
        Stack stack = new Stack();
        
        while( (rch = reader.read()) != -1) {
            ch = (char)rch;
            // QUERY: Should newlines only be allowed in a VACUUM ?
            // error messaging
            x++;
            if(ch == '\n') {
                y++;
                x = 1;
                continue;
            }
            
            switch(state) {
              case VACUUM: {
                  if(ch == '<') {
                    pi = false;
                    state = IN_TAG_NAME;
                    if( value.length() != 0 ) {
                        node = new XMLNode(tag_name.toString());
                        node.setPlaintext(value.toString().trim());
                        parent.addNode(node);
                        value.setLength(0);
                    }
                  } else
                  if( (ch == ' ') || (ch == '\t') ) {
                    if(value.length() != 0) {
                        value.append(ch);
                    }
                    continue;
                  } else
                  if(ch == '\r') {   // part of a newline.
                    if(value.length() != 0) {
                        value.append(ch);
                    }
                    continue;
                  } else {
                    value.append(ch);
                  }
              }
              break;
              
              case START_COMMENT: {
                  if(ch == '-') {
                      // feels bad to do this here.
                      ch = (char)reader.read();
                      if(ch == '-') {
                          state = IN_COMMENT;
                      } else {
                          value.append("!-");
                          value.append(ch);
                      }
                  } else {
                      tag_name.append('!');
                      tag_name.append(ch);
                      state = IN_TAG_NAME;
                  }
              }
              break;

              case IN_COMMENT: {
                  if(ch == '-') {
                      // feels bad to do this here.
                      ch = (char)reader.read();
                      if(ch == '-') {
                          ch = (char)reader.read();
                          if(ch == '>') {
                              node = new XMLNode();
                              node.setPlaintext(value.toString().trim());
                              node.setComment(true);
                              if(parent == null) {
                                  parent = new XMLNode();
                                  parent.setInvisible(true);
                                  root = parent;
                              }
                              parent.addNode(node);
                              value.setLength(0);
                              state = VACUUM;
                          } else {
                              value.append("--");
                              value.append(ch);
                          }
                      } else {
                          value.append("-");
                          value.append(ch);
                      }
                  } else {
                      value.append(ch);
                  }
              }
              break;

              case IN_DOCTYPE: {
                  if(ch == '[') {
                      value.append(ch);
                      state = IN_DTD;
                  } else
                  if(ch == '>') {
                      node = new XMLNode("!DOCTYPE");
                      node.setPlaintext(value.toString());
                      node.setDocType(true);
                      if(parent == null) {
                          parent = new XMLNode();
                          parent.setInvisible(true);
                          root = parent;
                      }
                      parent.addNode(node);
                      value.setLength(0);
                      state = VACUUM;
                  } else {
                      value.append(ch);
                  }
              }
              break;

              case IN_DTD: {
                  if(ch == ']') {
                      value.append(ch);
                      state = IN_DOCTYPE;
                  } else {
                      value.append(ch);
                  }
              }

              case IN_TAG_NAME: {
                  if( (ch == '!') && (tag_name.length() == 0) ) {
                    state = START_COMMENT;
                  } else
                  if( !pi && (ch == '?') && (tag_name.length() == 0) ) {
                    pi = true;
                  } else
                  if( (ch == '/') && (tag_name.length() == 0) ) {
                    // closing tag
                    parent = (XMLNode)stack.pop();
                    state = CLOSING_TAG;
                  } else
                  if( ((ch == ' ') || (ch == '\t') || (ch == '>') || (ch == '/')) || (ch == '?') ) {
                    if("!DOCTYPE".equals(tag_name.toString())) {
                        state = IN_DOCTYPE;
                        tag_name.setLength(0);
                        continue;
                    }
                    if(pi) {
                      if(parent == null) {
                          parent = new XMLNode();
                          parent.setInvisible(true);
                          root = parent;
                      }
                    }
                    node = new XMLNode(tag_name.toString());
                    node.setPI(pi);
                    pi = false;
                    if(root == null) {
                        root = node;
                    }
                    if(parent != null) {
                        parent.addNode(node);
                    }
                    tag_name.setLength(0);
                    if( (ch == '/') || (ch == '?') ) {
                        state = SLASH;
                    } else
                    if(ch == '>') {
                        state = VACUUM;
                        stack.push(parent);
                        parent = node;
                    } else {
                        state = IN_TAG;
                    }
                  } else {
                    tag_name.append(ch);
                  }
              }
              break;
              
              case IN_TAG: {
                  if((ch == ' ') || (ch == '\t') ) {
                    continue;
                  } else
                  if(ch == '>') {
                    state = VACUUM;
                    stack.push(parent);
                    parent = node;
                  } else
                  if( (ch == '/') || (ch == '?') ) {
                    // empty tag
                    state = SLASH;
                  } else {
                    state = ATTR_NAME;
                    attr_name.setLength(1);
                    attr_name.setCharAt(0,ch);
                  }
              }
              break;
                            
              case SLASH: {
                  if(ch == '>') {
                    state = VACUUM;
                  } else {
                    state = IN_TAG;
                  }
              }
              break;
              
              case CLOSING_TAG: {
                  if(ch == '>') {
                      state = VACUUM;
                  } else {
                      continue;
                  }
              }
              break;
                            
              case ATTR_NAME: {
                  if((ch == ' ') || (ch == '\t') ) {
                    node.addAttr(attr_name.toString(), attr_name.toString());
                    state = IN_TAG;
                  } else
                  if(ch == '=') {
                    state = END_ATTR_NAME;
                  } else {
                    attr_name.append(ch);
                  }
              }
              break;
              
              case END_ATTR_NAME: {
                  if(ch == '"') {
                    state = ATTR_VALUE;
                    attr_value.setLength(0);
                  } else {
                    state = ATTR_NAME;
                  }
              }
              break;
              
              case ATTR_VALUE: {
                  if(ch == '"') {
                    node.addAttr(attr_name.toString(), attr_value.toString());
                    state = IN_TAG;
                  } else {
                    attr_value.append(ch);
                  }
              }
              break;
            }
        }
        return root;
    }

}
