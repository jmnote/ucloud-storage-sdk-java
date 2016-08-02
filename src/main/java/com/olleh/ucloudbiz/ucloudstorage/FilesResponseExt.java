package com.olleh.ucloudbiz.ucloudstorage;

import java.util.ArrayList;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import com.rackspacecloud.client.cloudfiles.FilesResponse;

/**
 * <p>
 * 이 클래스는 FilesResponse 확장 클래스로 static website, container logging 및 manifest 파일에 관련된
 * 메소드를 가지고 있다. 
 * </p>
 * 
 * @see	<A HREF="../../../../com/rackspacecloud/client/cloudfiles/FilesResponse.html"><CODE>FilesResponse</CODE></A>
 * 
 * @author KT 클라우드스토리지팀
 */

public class FilesResponseExt extends FilesResponse {
	
	protected static ArrayList<String> TRUE_VALUE = new ArrayList<String>();
    public FilesResponseExt (HttpResponse response) {
	    super(response);
	    TRUE_VALUE.add("true");
	    TRUE_VALUE.add("1");   
	    TRUE_VALUE.add("yes"); 
	    TRUE_VALUE.add("on");  
	    TRUE_VALUE.add("t");   
	    TRUE_VALUE.add("y");   
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
	    Header indexPageHeader = getResponseHeader(FilesConstantsExt.X_CONTAINER_WEB_INDEX); 
	    if (indexPageHeader != null)
          return indexPageHeader.getValue();
        return null;
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
	    Header errorPageHeader = getResponseHeader(FilesConstantsExt.X_CONTAINER_WEB_ERROR); 
	    if (errorPageHeader != null)
          return errorPageHeader.getValue();
        return null;
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
	    Header webListingHeader = getResponseHeader(FilesConstantsExt.X_CONTAINER_WEB_LISTINGS); 
	    if (webListingHeader != null) {
			String value = webListingHeader.getValue();
			return TRUE_VALUE.contains(value);
      	}
        return false;
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
	    Header CssHeader = getResponseHeader(FilesConstantsExt.X_CONTAINER_WEB_CSS); 
	    if (CssHeader != null)
          return CssHeader.getValue();
        return null;
    }
    
    /**
    * <p>
    * container에 대한 logging 설정상태를 조회한다. 
    * </p>
    *
    * @return 설정되어 있으면 true, 미 설정시 false
    *
    */    
    public boolean getContainerLogging() {
	    Header loggingHeader = getResponseHeader(FilesConstantsExt.X_CONTAINER_ACCESS_LOGGING); 
	    if (loggingHeader != null) {
          	String value = loggingHeader.getValue();
			return TRUE_VALUE.contains(value);
		}
        return false;
    }

	/**
    * <p>
    * 분할업로드된 파일에 대한 manifest 파일의 헤더(X-Object-Manifest)값을 가져온다.
    * 이 헤더 값을 이용하여 분할업로드된 파일을 삭제하거나 업데이트를 한다.
    * </p>
    *
    * @return X-Object-Manifest 헤더의 값, 헤더가 없을 경우 null
    *
    */    
    public String getObjectManifest() {
		Header manifest = getResponseHeader(FilesConstantsExt.X_OBJECT_MANIFEST); 
		if(manifest != null) {
			return manifest.getValue();
		}   
		return null;
    }   
}
