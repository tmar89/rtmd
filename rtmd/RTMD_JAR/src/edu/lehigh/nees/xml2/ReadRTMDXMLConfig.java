package edu.lehigh.nees.xml2;

import java.io.File;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import edu.lehigh.nees.util.FileHandler;
import edu.lehigh.nees.util.filefilter.XMLFileFilter;


/********************************* 
 * ReadRTMDXMLConfig
 * <p>
 * Reads and parses the RTMD XML Configuration file
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 19 Oct 10  T. Marullo  Initial 
 * 
 ********************************/
public class ReadRTMDXMLConfig{
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;
    Document doc;
    
    /** Constructor.  Reads an XML configuration file
     * and prepares it to be parsed by the available functions
     */
    public ReadRTMDXMLConfig(File filename) {
        try {
            docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docBuilderFactory.newDocumentBuilder();
            doc = docBuilder.parse (filename);

            // normalize text representation
            doc.getDocumentElement().normalize ();            
            
            // normalize text representation
            doc.getDocumentElement().normalize ();
        } catch (SAXParseException err) {
        System.out.println ("** Parsing error" + ", line " + err.getLineNumber () + ", uri " + err.getSystemId ());
        System.out.println(" " + err.getMessage ());

        } catch (SAXException e) {
        Exception x = e.getException ();
        ((x == null) ? e : x).printStackTrace ();

        } catch (Throwable t) {
        t.printStackTrace ();
        }
    }
    
    /** Retrieve the SCRAMNet configuration into an XMLRTMDConfig object <br>
     *  Data arranged by input order. 
     */
    public XMLRTMDConfig getRTMDConfig() {   
        XMLRTMDConfig XMLconfig = null;
        
        // Get RTMD SCRAMNet Configuration
        try {         	
            NodeList scramnet = doc.getElementsByTagName("SCRAMNet");
            Element scramnetList = (Element)scramnet.item(0);                 
            // Get the Channels     
            NodeList channelList = scramnetList.getElementsByTagName("Channel");            
            XMLconfig = new XMLRTMDConfig(channelList.getLength());            
            for (int i = 0; i < channelList.getLength(); i++) {
                XMLconfig.setLocation(i,((Element)channelList.item(i)).getAttribute("Location").trim());
                XMLconfig.setType(i,((Element)channelList.item(i)).getAttribute("Type").trim());
                XMLconfig.setName(i,((Element)channelList.item(i)).getAttribute("Name").trim());
                XMLconfig.setUnits(i,((Element)channelList.item(i)).getAttribute("Units").trim());
                XMLconfig.setStream(i,((Element)channelList.item(i)).getAttribute("Stream").trim());
                XMLconfig.setRecord(i,((Element)channelList.item(i)).getAttribute("Record").trim());
                XMLconfig.setReadWrite(i,((Element)channelList.item(i)).getAttribute("ReadWrite").trim());
                XMLconfig.setGain(i,((Element)channelList.item(i)).getAttribute("Gain").trim());                
                XMLconfig.setisDAQ(i,((Element)channelList.item(i)).getAttribute("isDAQ").trim());
                if (((Element)channelList.item(i)).getAttribute("isDAQ").trim().equals("True")) {
	                XMLconfig.setVoltageSlope(i,((Element)channelList.item(i)).getAttribute("VoltageSlope").trim());
	                XMLconfig.setVoltageOffset(i,((Element)channelList.item(i)).getAttribute("VoltageOffset").trim());
	                XMLconfig.setEUSlope(i,((Element)channelList.item(i)).getAttribute("EUSlope").trim());
	                XMLconfig.setEUOffset(i,((Element)channelList.item(i)).getAttribute("EUOffset").trim());
	                XMLconfig.setGaugeType(i,((Element)channelList.item(i)).getAttribute("GaugeType").trim());
                }
            }                       
        } catch (NullPointerException e) {e.printStackTrace();}            
        return XMLconfig;
    }
                  
    /** Print the XML configuration */
    public void print() {        
        getRTMDConfig().print();              
    }
    
    public static void main (String argv []){
    	// Get the input file name    	
    	String xmlfile;
        if ((xmlfile = FileHandler.getFilePath("Open RTMD XML File", new XMLFileFilter())) == null) {    	        	
        	return;
        }                
        // Read the config
        ReadRTMDXMLConfig xml = new ReadRTMDXMLConfig(new File(xmlfile));
        
        // Print config
        xml.getRTMDConfig().print();
        
        System.exit(0);
    }
}

