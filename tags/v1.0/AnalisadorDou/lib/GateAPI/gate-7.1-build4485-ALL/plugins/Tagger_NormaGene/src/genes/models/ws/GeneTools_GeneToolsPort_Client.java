
package genes.models.ws;

/**
 * Please modify this class to meet your needs
 * This class is not complete
 */

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;

/**
 * This class was generated by Apache CXF 2.4.1
 * 2011-08-05T11:07:54.799+01:00
 * Generated source version: 2.4.1
 * 
 */
public final class GeneTools_GeneToolsPort_Client {

    private static final QName SERVICE_NAME = new QName("http://ws.models.genes/", "GeneToolsService");

    private GeneTools_GeneToolsPort_Client() {
    }

    public static void main(String args[]) throws java.lang.Exception {
        URL wsdlURL = GeneToolsService.WSDL_LOCATION;
        if (args.length > 0 && args[0] != null && !"".equals(args[0])) { 
            File wsdlFile = new File(args[0]);
            try {
                if (wsdlFile.exists()) {
                    wsdlURL = wsdlFile.toURI().toURL();
                } else {
                    wsdlURL = new URL(args[0]);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
      
        GeneToolsService ss = new GeneToolsService(wsdlURL, SERVICE_NAME);
        GeneTools port = ss.getGeneToolsPort();  
        
        {
        System.out.println("Invoking getBioCreativeResults...");
        java.lang.String _getBioCreativeResults_text = "";
        boolean _getBioCreativeResults_goCat = false;
        double _getBioCreativeResults_thresh = 0.0;
        try {
            genes.models.ws.MyMap _getBioCreativeResults__return = port.getBioCreativeResults(_getBioCreativeResults_text, _getBioCreativeResults_goCat, _getBioCreativeResults_thresh);
            System.out.println("getBioCreativeResults.result=" + _getBioCreativeResults__return);

        } catch (IOException_Exception e) { 
            System.out.println("Expected exception: IOException has occurred.");
            System.out.println(e.toString());
        }
            }
        {
        System.out.println("Invoking getGenesTagger...");
        java.lang.String _getGenesTagger_txt = "";
        double _getGenesTagger_thresh = 0.0;
        try {
            java.lang.String _getGenesTagger__return = port.getGenesTagger(_getGenesTagger_txt, _getGenesTagger_thresh);
            System.out.println("getGenesTagger.result=" + _getGenesTagger__return);

        } catch (IOException_Exception e) { 
            System.out.println("Expected exception: IOException has occurred.");
            System.out.println(e.toString());
        }
            }

        System.exit(0);
    }

}
