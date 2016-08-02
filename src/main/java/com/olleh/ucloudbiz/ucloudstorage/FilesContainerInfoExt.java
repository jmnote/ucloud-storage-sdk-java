package com.olleh.ucloudbiz.ucloudstorage;

import java.util.Map;
import java.util.HashMap;
import org.apache.http.HttpException;

import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesContainerInfo;

/**
 * <p>
 * 이 클래스는 FilesContainerInfo 확장 클래스로 static website 및 container logging에 관련된
 * 메소드를 가지고 있다. 
 * </p>
 * 
 * @see	<A HREF="../../../../com/rackspacecloud/client/cloudfiles/FilesContainerInfo.html"><CODE>FilesContainerInfo</CODE></A>,
 *      <A HREF="../../../../com/kt/client/ucloudstorage/FilesClientExt.html#getContainerInfoExt(java.lang.String)"><CODE>getContainerInfoExt(String)</CODE></A>
 * 
 * @author KT 클라우드스토리지팀
 */

public class FilesContainerInfoExt extends FilesContainerInfo {
	
    private String webIndex;
    private String webError;
    private boolean webListings;
    private String webCss; 
    private boolean loggingStatus;

    public FilesContainerInfoExt(String name, int containerCount, long totalSize,
                          String webIndex, String webError, boolean webListings,
                          String webCss, boolean loggingStatus) {
    	super(name, containerCount, totalSize);
    	this.webIndex = webIndex;
    	this.webError = webError;
    	this.webListings = webListings;
    	this.webCss = webCss;
    	this.loggingStatus = loggingStatus;
    }
      
    /**
    * <p>
    * static website의 index파일 정보를 가져온다.
    * </p>
    *
    * @return index 파일, 미 구성시 null
    *
    */ 
    public String getWebIndex() {
    	return webIndex;
    }
    
    /**
    * <p>
    * static website의 error 파일에 대한 설정된 suffix 가져온다.
    * </p>
    *
    * @return error suffix, 미 구성시 null
    *
    */ 
    public String getWebError() {
		return webError;    
	}
	
	/**
    * <p>
    * HTML 파일 listing에 대한 구성정보를 가져온다.
    * </p>
    *
    * @return 구성시 true, 미 구성시 false
    *
    */    
    public boolean getWebListings() {
		return webListings;    
    }
    
    /**
    * <p>
    * style sheet에 대한 구성정보를 가져온다.
    * </p>
    *
    * @return style sheet 파일, 미 구성시 null
    *
    */
    public String getWebCss() {
		return webCss;    
    }
    
    /**
    * <p>
    * container에 대한 logging 설정상태를 조회한다. 
    * </p>
    *
    * @return 설정되어 있으면 true, 미 설정시 false
    *
    */
    public boolean getLoggingStatus() {
		return loggingStatus;    
    }    
    
    /**
    * <p>
    * container에 설정되어 있는 static website 구성정보를 가져온다.
    * </p>
    *
    * @return static website 구성정보
    *
    */
    public Map<String, String> getStaticWebsiteConfig() {
	    HashMap<String, String> hm = new HashMap<String, String>();
	    hm.put(FilesConstantsExt.X_CONTAINER_WEB_INDEX, webIndex);
	    hm.put(FilesConstantsExt.X_CONTAINER_WEB_ERROR, webError);
	    hm.put(FilesConstantsExt.X_CONTAINER_WEB_LISTINGS, Boolean.toString(webListings));
	    hm.put(FilesConstantsExt.X_CONTAINER_WEB_CSS, webCss);
	    return hm;
    }
}