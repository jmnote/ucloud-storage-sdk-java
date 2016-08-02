package com.olleh.ucloudbiz.ucloudstorage;

import java.util.Map;
import java.util.HashMap;
import org.apache.http.HttpException;

import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.FilesObjectMetaData;

/**
 * <p>
 * 이 클래스는 FilesObjectMetaData 확장 클래스로 분할 업로드와 관련된 manifest 파일에 대한 메소드를 가지고 있다. 
 * </p>
 * 
 * @see	<A HREF="../../../../com/rackspacecloud/client/cloudfiles/FilesObjectMetaData.html"><CODE>FilesObjectMetaData</CODE></A>,
 *		<A HREF="../../../../com/rackspacecloud/client/cloudfiles/FilesClient.html#getObjectMetaData(java.lang.String, java.lang.String)"><CODE>getObjectMetaData(String, String)</CODE></A>,
 *      <A HREF="../../../../com/kt/client/ucloudstorage/FilesClientExt.html#getObjectMetaDataExt(java.lang.String, java.lang.String)"><CODE>getObjectMetaDataExt(String, String)</CODE></A>
 * 
 * @author KT 클라우드스토리지팀
 */

public class FilesObjectMetaDataExt extends FilesObjectMetaData {
	protected String objectManifest;
	protected String contentType;
	
	public FilesObjectMetaDataExt(String mimeType, String contentLength, String eTag,
								  String lastModified, String contentType, String objectManifest) {
		
		super(mimeType, contentLength, eTag, lastModified);
		this.contentType = contentType;
		this.objectManifest = objectManifest;
	}
	
	/**
    * <p>
    * 파일에 대한 contetn type 정보를 가져온다.
    * </p>
    *
    * @return 파일의 content type, 헤더가 없을 경우 null
    *
    */ 
	public String getContentType() {
		return contentType;
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
		return objectManifest;
	}	
}