
package sd.clt.ws;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "FileServerImplWSService", targetNamespace = "http://srv.tp1.sd/", wsdlLocation = "http://localhost:8080/FileServer?wsdl")
public class FileServerImplWSService
    extends Service
{

    private final static URL FILESERVERIMPLWSSERVICE_WSDL_LOCATION;
    private final static WebServiceException FILESERVERIMPLWSSERVICE_EXCEPTION;
    private final static QName FILESERVERIMPLWSSERVICE_QNAME = new QName("http://srv.tp1.sd/", "FileServerImplWSService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8080/FileServer?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        FILESERVERIMPLWSSERVICE_WSDL_LOCATION = url;
        FILESERVERIMPLWSSERVICE_EXCEPTION = e;
    }

    public FileServerImplWSService() {
        super(__getWsdlLocation(), FILESERVERIMPLWSSERVICE_QNAME);
    }

    public FileServerImplWSService(WebServiceFeature... features) {
        super(__getWsdlLocation(), FILESERVERIMPLWSSERVICE_QNAME, features);
    }

    public FileServerImplWSService(URL wsdlLocation) {
        super(wsdlLocation, FILESERVERIMPLWSSERVICE_QNAME);
    }

    public FileServerImplWSService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, FILESERVERIMPLWSSERVICE_QNAME, features);
    }

    public FileServerImplWSService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public FileServerImplWSService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns FileServerImplWS
     */
    @WebEndpoint(name = "FileServerImplWSPort")
    public FileServerImplWS getFileServerImplWSPort() {
        return super.getPort(new QName("http://srv.tp1.sd/", "FileServerImplWSPort"), FileServerImplWS.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns FileServerImplWS
     */
    @WebEndpoint(name = "FileServerImplWSPort")
    public FileServerImplWS getFileServerImplWSPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://srv.tp1.sd/", "FileServerImplWSPort"), FileServerImplWS.class, features);
    }

    private static URL __getWsdlLocation() {
        if (FILESERVERIMPLWSSERVICE_EXCEPTION!= null) {
            throw FILESERVERIMPLWSSERVICE_EXCEPTION;
        }
        return FILESERVERIMPLWSSERVICE_WSDL_LOCATION;
    }

}
