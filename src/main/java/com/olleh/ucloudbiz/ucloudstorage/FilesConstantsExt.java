package com.olleh.ucloudbiz.ucloudstorage;

import com.rackspacecloud.client.cloudfiles.FilesConstants;

/**
 * <p>
 * 이 클래스는 FilesConstants의 확장 클래스로 추가적인 클래스 필드를 갖고 있다.
 * </p>
 * <p>
 * 추가된 필드는 static website 및 container logging와 관련된 필드를 포함하여
 * 분할 업로드된 파일과 관련된 필드들이다.
 * </p>
 * 
 * @see	<A HREF="../../../../com/rackspacecloud/client/cloudfiles/FilesConstants.html"><CODE>FilesConstants</CODE></A>,
 *      <A HREF="../../../../com/kt/client/ucloudstorage/FilesClientExt.html#getContainerInfoExt(java.lang.String)"><CODE>getContainerInfoExt(String)</CODE></A>,
 *      <A HREF="../../../../com/kt/client/ucloudstorage/FilesClientExt.html#getObjectMetaDataExt(java.lang.String, java.lang.String)"><CODE>getObjectMetaDataExt(String, String)</CODE></A>
 * 
 * @author KT 클라우드스토리지팀
 */

public class FilesConstantsExt extends FilesConstants
{
    public static final String X_CONTAINER_LOG_DELIVERY = "X-Container-Meta-Access-Log-Delivery";
    public static final String X_CONTAINER_WEB_INDEX    = "X-Container-Meta-Web-Index";
	public static final String X_CONTAINER_WEB_ERROR    = "X-Container-Meta-Web-Error";
	public static final String X_CONTAINER_WEB_LISTINGS = "X-Container-Meta-Web-Listings";
	public static final String X_CONTAINER_WEB_CSS = "X-Container-Meta-Web-Listings-Css";
	public static final String X_CONTAINER_ACCESS_LOGGING = "X-Container-Meta-Access-Log-Delivery";
	public static final String X_CONTAINER_READ  = "X-Container-Read";
	public static final String X_OBJECT_MANIFEST = "X-Object-Manifest";
}