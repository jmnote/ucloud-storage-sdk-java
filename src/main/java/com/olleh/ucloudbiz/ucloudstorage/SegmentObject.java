package com.olleh.ucloudbiz.ucloudstorage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.http.HttpException;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesInvalidNameException;

import com.rackspacecloud.client.cloudfiles.IFilesTransferCallback; 

class SegmentObject {
	protected long segmentSize = 10485760L;
	protected FilesClientExt fclient;
	protected TicketPool tp;
	
	protected SegmentObject(FilesClientExt fclient) {
		this.fclient = fclient;		
		tp = TicketPool.getInstance();
	}
	
	protected SegmentObject(FilesClientExt fclient, long segmentSize, int concurrent) {
		this.fclient = fclient;	
		if(segmentSize > 10485760L) this.segmentSize = segmentSize;
		if(concurrent > 1) tp = TicketPool.getInstance(concurrent);		
	}	
	
	protected boolean storeObjectSegmented(String containerName, File obj) throws IOException, 
	                                                                          HttpException, 
	                                                                          FilesException {
		return storeObjectSegmentedAs(containerName, obj, null, null, null);
	}
	
	protected boolean storeObjectSegmented(String containerName, File obj, String contentType) throws IOException, 
	                                                                                           HttpException, 
	                                                                                           FilesException {
		return storeObjectSegmentedAs(containerName, obj, contentType, null, null);
	}

	
	protected boolean storeObjectSegmentedAs(String containerName, File obj, String objName) throws IOException, 
	                                                                                            HttpException, 
	                                                                                            FilesException,
	                                                                                            FilesInvalidNameException {
		return storeObjectSegmentedAs(containerName, obj, null, objName, null, null);
	}
	
	protected boolean storeObjectSegmentedAs(String containerName, File obj, String contentType, String objName) throws IOException, 
	                                                                                                          HttpException, 
	                                                                                                          FilesException,
	                                                                                                          FilesInvalidNameException {
		return storeObjectSegmentedAs(containerName, obj, contentType, objName, null, null);
	}
	
	protected boolean storeObjectSegmentedAs(String containerName, File obj, String contentType, 
	                                         String objName, Map<String, String> objmeta) throws IOException, 
	                                         													 HttpException, 
	                                                                                             FilesException, 
	                                                                                             FilesInvalidNameException {
		return storeObjectSegmentedAs(containerName, obj, contentType, objName, objmeta, null);
	}
	
	protected boolean storeObjectSegmentedAs(String containerName, File obj, String contentType, 
	                                         String objName, Map<String, String> objmeta, 
	                                         IFilesTransferCallback callback) throws IOException, 
	                                         										 HttpException, 
	                                                                                 FilesException, 
	                                                                                 FilesInvalidNameException {
	    String segmentsContainer = containerName + "_segments";
	    if(!fclient.containerExists(segmentsContainer)) fclient.createContainer(segmentsContainer);	  
	    if(!fclient.containerExists(containerName)) 
	    	throw new FileNotFoundException("Container: " + containerName + " did not exist");
	    if(obj == null) 
			throw new FileNotFoundException("No File!");
		if(objName == null) objName = obj.getName();
		if(contentType == null) contentType = "application/octet-stream";
		if(objmeta == null) objmeta = new Hashtable<String, String>(); 
		long fsize = obj.length();
		long currentTime = System.currentTimeMillis();	
		String objPath = objName + "/" + currentTime + "/" + fsize + "/";	
		String NewManifest = segmentsContainer + "/" + objPath;
		
		FilesObjectMetaDataExt objectMeta = fclient.objectExists(containerName, objName);  // name checking
		String OldManifest = null;
		
		if(objectMeta == null) {
			if(fsize < segmentSize) {
				fclient.storeObjectAs(containerName, obj, contentType, objName, callback);
		    	return true;
	    	}
			else {
				if(fclient.createManifestObject(containerName, contentType, objName, NewManifest, objmeta, callback))
					return uploadSegment(containerName, obj, contentType, objName, objmeta, objPath, NewManifest, callback);
				else 
					return false;
			}
		}
		else {
			OldManifest = objectMeta.getObjectManifest();
			System.out.println("OldManifest : " + OldManifest); // debug
			if(OldManifest == null) {
				if(fsize < segmentSize) {
					fclient.storeObjectAs(containerName, obj, contentType, objName, callback);
		    		return true;		
				}
				else {
					if(fclient.createManifestObject(containerName, contentType, objName, NewManifest, objmeta, callback))
						return uploadSegment(containerName, obj, contentType, objName, objmeta, objPath, NewManifest, callback);
					else 
						return false;		
				}			
			}
			else {
				if(deleteOnlySegments(containerName, objName, OldManifest)) {
					if(fsize < segmentSize) {
						fclient.storeObjectAs(containerName, obj, contentType, objName, callback);
		    			return true;	
					}
					else {
						if(fclient.updateObjectMetadataAndManifest(containerName, objName, objmeta, NewManifest)) 				 
							return uploadSegment(containerName, obj, contentType, objName, objmeta, objPath, NewManifest, callback);
						else
							return false;
					}
				}
				else return false;					
			}
		}
	}
	
	private boolean uploadSegment(String containerName, File obj, String contentType, 
	                              String objName, Map<String, String> objmeta, 
	                              String objPath, String manifest,
	                              IFilesTransferCallback callback) throws IOException, 
	                                                                      HttpException, 
	                                                                      FilesException {
	    FileInputStream fis = new FileInputStream(obj);
	    BufferedInputStream bis = new BufferedInputStream(fis);
	    
	    long fsize = obj.length();
	    long leftSize = segmentSize;
	    long passedByte = 0L;
	   	
	    if(fsize < segmentSize) {
		    fclient.storeObjectAs(containerName, obj, contentType, objName);
		    return true;
	    }	
	        
	    long currentTime = System.currentTimeMillis();
	    String segmentsContainer = containerName + "_segments";
	    
 	    byte[] buffer = new byte[4096];
 	    int readBytes;
	 	int segNum = 0;
	 	ByteArrayOutputStream baos = new ByteArrayOutputStream();
	 	
 	    while((readBytes = bis.read(buffer))!= -1) {
        	if(leftSize <= readBytes) {
	        	System.out.println("upload : " + segNum); //debug
	        	String ticket = tp.getTicket();
        	    baos.write(buffer,0, (int) leftSize);
        	    byte[] data = baos.toByteArray();
        	    String segmentID = objPath + String.format("%08d", segNum);
        	    
        	    while(ticket == null) {
	        	    try {
						Thread.currentThread().sleep(100); 
					}
					catch(InterruptedException e) {
						fclient.createManifestObject(containerName, contentType, objName, manifest, objmeta, callback);
			    	}
					tp.refreshPool();
					ticket = tp.getTicket();
				}
				
        	    SegmentUploader suder = new SegmentUploader(segmentsContainer, segmentID, data, 
        	    											contentType, fclient, ticket, callback);
        	    suder.start();
        	    passedByte = passedByte + suder.getSize();
        	    baos.close();
        	    baos = new ByteArrayOutputStream();
        	    if(readBytes != leftSize) {
        	        baos.write(buffer, (int) leftSize, (int) (readBytes - leftSize));
        	    }
        	    leftSize = segmentSize - baos.size();
        	    ++segNum;
        	} else {
        	    baos.write(buffer, 0, readBytes);
        	    leftSize -= readBytes;
        	}
		}
		
		byte[] data = baos.toByteArray();
		if(passedByte + data.length == fsize) {
			if(data.length == 0) {
				do {                                                                                    
					if(SegmentUploader.isFinished()) break;                                                                             	
					try {Thread.currentThread().sleep (1000);} catch (InterruptedException e) {;}          
				} while(true);                                                                 
			}
			else {
				String ticket = tp.getTicket();
				System.out.println("upload : " + segNum); //debug
				String segmentID = objPath + String.format("%08d", segNum);
				
				while(ticket == null) {
					try {
						Thread.currentThread().sleep(100); 
					}
					catch(InterruptedException e) {}
					tp.refreshPool();
					ticket = tp.getTicket();
				}
            	SegmentUploader suder = new SegmentUploader(segmentsContainer, segmentID, data, contentType, 
            												fclient, ticket, callback);
            	suder.start();
            	
            	do {
		    		if(SegmentUploader.isFinished()) break;
		    		try {Thread.currentThread().sleep (1000);} catch (InterruptedException e) {;}    
	        	} while(true);  
        	}   
		} 
		return SegmentUploader.isSucceeded();  
	}
	
	protected boolean deleteSegmentsManifest(String containerName, String objName) throws IOException, 
																			              FileNotFoundException, 
																			              HttpException {
		if(FilesClientExt.isValidContainerName(containerName) && FilesClientExt.isValidObjectName(objName)) {
			return deleteSegmentsManifest(containerName, objName, null);
		}
		else {
			if(!FilesClientExt.isValidObjectName(objName)) {
				throw new FilesInvalidNameException(objName);
    		}
    		else {
    			throw new FilesInvalidNameException(containerName);
    		}
		}
	}
		
	private boolean deleteSegmentsManifest(String containerName, String objName, String objectManifest) throws IOException, 
																							    HttpException, 
																							    FilesException {
		if(!fclient.containerExists(containerName)) 
			throw new FileNotFoundException("Container: " + containerName + " did not exist");
		String segmentContainer = null;
		String path = null;
		StringTokenizer st = null;
		
		if(objectManifest == null) {
			FilesObjectMetaDataExt objectMeta = fclient.objectExists(containerName, objName);
			if(objectMeta != null) {
				String manifest = objectMeta.getObjectManifest();
				if(manifest != null) {
					st = new StringTokenizer(manifest,"/");
					segmentContainer = st.nextToken();
					path = manifest.substring(manifest.indexOf("/") + 1);
				}
				else {
					fclient.deleteObject(containerName, objName);
					return true;
				}
			}
			else { throw new FileNotFoundException("Container: " + containerName + " did not have object " + objName); }
		}
		else {
			st = new StringTokenizer(objectManifest,"/");
			segmentContainer = st.nextToken();
			path = objectManifest.substring(objectManifest.indexOf("/") + 1);			
		}
		if(!fclient.containerExists(segmentContainer)) 
			throw new FileNotFoundException("Container: " + segmentContainer + " did not exist");
		List<FilesObject> objects = fclient.listObjects(segmentContainer, path);
		if(objects.size() == 0) { SegmentRemover.setFinished(true);	}
		else {
			Iterator<FilesObject> i = objects.iterator();
			int j = 0;
			while(i.hasNext()) {
				String ticket = tp.getTicket();
				String segmentID = i.next().getName();
				
				System.out.println("deleted : " + j);
				
				while(ticket == null) {
					try {
						Thread.currentThread().sleep(100); 
					}
					catch(InterruptedException e) {}
					tp.refreshPool();
					ticket = tp.getTicket();
				}
				
				SegmentRemover remover = new SegmentRemover(segmentContainer, segmentID, fclient, ticket);
				remover.start();
				++j;
			} 	
		}
		do {
			if(SegmentRemover.isFinished()) {
				if(SegmentRemover.isSucceeded()) 
					fclient.deleteObject(containerName, objName);
				break;
		    }   
		    try {Thread.currentThread().sleep (1000);} catch (InterruptedException e) {;}    
	    } while(true);     
   		return SegmentRemover.isSucceeded(); 
	}
	
	private boolean deleteOnlySegments(String containerName, String objName, String objectManifest) throws IOException, 
																							    HttpException, 
																							    FilesException {
		if(!fclient.containerExists(containerName)) 
			throw new FileNotFoundException("Container: " + containerName + " did not exist"); 
		String segmentContainer = null;
		String path = null;
		StringTokenizer st = null;
		
		if(objectManifest == null) {
			FilesObjectMetaDataExt objectMeta = fclient.objectExists(containerName, objName);
			if(objectMeta != null) {
				String manifest = objectMeta.getObjectManifest();
				if(manifest != null) {
					st = new StringTokenizer(manifest,"/");
					segmentContainer = st.nextToken();
					path = manifest.substring(manifest.indexOf("/") + 1);
				}
				else {
					fclient.deleteObject(containerName, objName);
					return true;
				}
			}
			else { throw new FileNotFoundException("Container: " + containerName + " did not have object " + objName); }
		}
		else {
			st = new StringTokenizer(objectManifest,"/");
			segmentContainer = st.nextToken();
			path = objectManifest.substring(objectManifest.indexOf("/") + 1);			
		}
		if(!fclient.containerExists(segmentContainer)) 
			throw new FileNotFoundException("Container: " + segmentContainer + " did not exist");
		List<FilesObject> objects = fclient.listObjects(segmentContainer, path);
		if(objects.size() == 0) { SegmentRemover.setFinished(true);	}
		else {
			Iterator<FilesObject> i = objects.iterator();
			int j = 0;
			while(i.hasNext()) {
				String ticket = tp.getTicket();
				String segmentID = i.next().getName();
				
				System.out.println("deleted : " + j);
				
				while(ticket == null) {
					try {
						Thread.currentThread().sleep(100); 
					}
					catch(InterruptedException e) {}
					tp.refreshPool();
					ticket = tp.getTicket();
				}
				SegmentRemover remover = new SegmentRemover(segmentContainer, segmentID, fclient, ticket);
				remover.start();
				++j;
			} 	
		}
		do {
			if(SegmentRemover.isFinished()) {
				if(SegmentRemover.isSucceeded()) break;
		    }   
		    try {Thread.currentThread().sleep (1000);} catch (InterruptedException e) {;}    
	    } while(true);     
   		return SegmentRemover.isSucceeded(); 
	}
}