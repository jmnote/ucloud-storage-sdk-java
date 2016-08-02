package com.olleh.ucloudbiz.ucloudstorage;

import org.apache.http.HttpException;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import org.apache.log4j.Logger;

import com.rackspacecloud.client.cloudfiles.FilesClient;
import com.rackspacecloud.client.cloudfiles.FilesObject;
import com.rackspacecloud.client.cloudfiles.FilesContainer;
import com.rackspacecloud.client.cloudfiles.FilesException;
import com.rackspacecloud.client.cloudfiles.wrapper.RequestEntityWrapper;


/**
 * <p>
 * 이 클래스는 FilesContainer의 확장 클래스로 static website 및 container logging에 관련된
 * 메소드를 가지고 있다. 한가지 주의할 것은 이 클래스의 인스턴스를 생성하는 것으로 스토리지 상에
 * container가 생성되는 것은 아니다. 스토리지상에 있는 container에 대한 참조객체로써 사용하는 것이
 * 바람직하다.
 * </p>
 * 
 * @see	<A HREF="../../../../com/rackspacecloud/client/cloudfiles/FilesContainer.html"><CODE>FilesContainer</CODE></A>
 * 
 * @author KT 클라우드스토리지팀
 */

public class FilesContainerExt extends FilesContainer
{
	protected FilesClientExt clientExt;
	
    /**
     * FilesClientExt의 객체생성으로 스토리지상에 container를 생성하지 않는다.
     *  
     * @param containerName  container 이름
     * @param objs           container에 저장되어 있는 파일 리스트
     * @param clientExt      http client
     */
    public FilesContainerExt(String containerName, List<FilesObject> objs, FilesClientExt clientExt)
    {
        super(containerName, objs, clientExt);
    }

    /**
     * @param containerName   container 이름
     * @param clientExt       http client
     */
    public FilesContainerExt(String containerName, FilesClientExt clientExt)
    {
        super(containerName, clientExt);
    }
    
   /**
    * <p>
    * container에 대한 정보를 가져온다.
    * </p>
    *
    * @return 성공하면 FilesContainerInfoExt, 실패하면 null
    *
    */  
    public FilesContainerInfoExt getInfoExt() throws HttpException, IOException, FilesException {
        if (clientExt != null)
        {
            return clientExt.getContainerInfoExt(this.name);
        }
        else
        {
            logger.fatal("This container does not have a valid client !");
        }
        return null;
    }

    /**
    * <p>
    * container를 static website로 구성한다. 자세한 서비스 설명은 <A HREF="https://ucloudbiz.olleh.com/manual/ucloud_storage_Static_Web_service_user_guide.pdf"><CODE>ucloud storage Static Web 서비스 이용 가이드</CODE></A>를 참고한다.
    * </p>
    *
    * @param config
    *            구성정보
    * @return 성공하면 true
    *
    */  
    public boolean enableStaticWebsiteConfig(Map<String, String> config) throws IOException, 
                                                                             FilesException, 
                                                                             HttpException {
	    return this.clientExt.enableStaticWebsiteConfig(this.name, config);
    }
    
    /**
    * <p>
    * static website로 구성된 container를 불활성화시킨다. 그러나 이전 구성 정보는 삭제되지 않는다. 만일 새로운 
    * 구성정보로 업데이를 원할 경우 enableStaticWebsiteConfig를 이용한다.
    * 자세한 서비스 설명은 <A HREF="https://ucloudbiz.olleh.com/manual/ucloud_storage_Static_Web_service_user_guide.pdf"><CODE>ucloud storage Static Web 서비스 이용 가이드</CODE></A>를 참고한다.
    * </p>
    *
    * @return 성공하면 true
    *
    */  
    public boolean disableStaticWebsiteConfig() throws IOException, FilesException, HttpException {  
	    return this.clientExt.disableStaticWebsiteConfig(this.name);
    }
    
    /**
    * <p>
    * container에 대한 접근로그를 저장한다. 자세한 서비스 설명은 <A HREF="https://ucloudbiz.olleh.com/manual/ucloud_storage_log_save_service_user_guide.pdf"><CODE>ucloud storage 로그 저장 서비스 이용 가이드</CODE></A>를 참고한다.
    * </p>
    *
    * @param active 설정(true/false)
    *            
    * @return 성공하면 true
    *
    */
    public boolean setContainerLogging(boolean active) throws IOException, 
                                                              FilesException,
                                                              HttpException {
	   	return this.clientExt.setContainerLogging(this.name, active); 	    
    }    
}
