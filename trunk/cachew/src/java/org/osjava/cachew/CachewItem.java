/*
 * Created on Jun 10, 2005
 */
package org.osjava.cachew;

import java.util.Date;

/**
 * @author hen
 */
public class CachewItem {
	
	private byte[] bytes;
	private String contentType;
	private Date creationTime;
	
	/**
	 * @return Returns the bytes.
	 */
	public byte[] getBytes() {
		return bytes;
	}
	/**
	 * @param bytes The bytes to set.
	 */
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	/**
	 * @return Returns the contentType.
	 */
	public String getContentType() {
		return contentType;
	}
	/**
	 * @param contentType The contentType to set.
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	/**
	 * @return Returns the creationTime.
	 */
	public Date getCreationTime() {
		return creationTime;
	}
	/**
	 * @param creationTime The creationTime to set.
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}
}
