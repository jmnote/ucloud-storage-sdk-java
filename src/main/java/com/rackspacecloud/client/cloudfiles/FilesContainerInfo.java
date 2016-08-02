/*
 * See COPYING for license information.
 */ 

package com.rackspacecloud.client.cloudfiles;

/**
 * Contains basic information about the container
 * 
 * @author lvaughn
 *
 */
public class FilesContainerInfo
{
    protected int objectCount;
    protected long totalSize;
    protected String name;

    /**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
     * @param containerCount The number of objects in the container
     * @param totalSize      The total size of the container (in bytes)
     */
    public FilesContainerInfo(String name, int containerCount, long totalSize)
    {
    	this.name = name;
        this.objectCount = containerCount;
        this.totalSize = totalSize;
    }

    /**
     * Returns the number of objects in the container
     * 
     * @return The number of objects
     */
    public int getObjectCount()
    {
        return objectCount;
    }

    /**
     * @return The total size of the objects in the container (in bytes)
     */
    public long getTotalSize()
    {
    	return totalSize;
    }

}
