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
 * ReadxPCXMLConfig
 * <p>
 * Reads and parses the RTMD xPC XML Configuration file
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 19 Oct 10  T. Marullo  Initial 
 * 
 ********************************/
public class ReadxPCXMLConfig{
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;
    Document doc;
    
    /** Constructor.  Reads an XML configuration file
     * and prepares it to be parsed by the available functions
     */
    public ReadxPCXMLConfig(File filename) {
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
    
    /** Retrieve the xPC configuration into an XMLxPCConfig2 object <br>
     *  Data arranged by input order. 
     */
    public XMLxPCConfig getxPCConfig() {   
    	XMLxPCConfig XMLconfig = null;
        
        // Get RTMD SCRAMNet Configuration
        try {         	
            NodeList scramnet = doc.getElementsByTagName("xPC");
            Element scramnetList = (Element)scramnet.item(0);                 
            // Get the Channels     
            NodeList channelList = scramnetList.getElementsByTagName("xPCSignal");            
            XMLconfig = new XMLxPCConfig(channelList.getLength());            
            for (int i = 0; i < channelList.getLength(); i++) {
                XMLconfig.setLocation(i,((Element)channelList.item(i)).getAttribute("Location").trim());                
                XMLconfig.setName(i,((Element)channelList.item(i)).getAttribute("Name").trim());
                XMLconfig.setUnits(i,((Element)channelList.item(i)).getAttribute("Units").trim());                
                XMLconfig.setGain(i,((Element)channelList.item(i)).getAttribute("Gain").trim());                
                XMLconfig.setisDAQ(i,((Element)channelList.item(i)).getAttribute("isDAQ").trim());
                if (((Element)channelList.item(i)).getAttribute("isDAQ").trim().equals("True")) {
	                XMLconfig.setVoltageSlope(i,((Element)channelList.item(i)).getAttribute("VoltageSlope").trim());
	                XMLconfig.setVoltageOffset(i,((Element)channelList.item(i)).getAttribute("VoltageOffset").trim());
	                XMLconfig.setEUSlope(i,((Element)channelList.item(i)).getAttribute("EUSlope").trim());
	                XMLconfig.setEUOffset(i,((Element)channelList.item(i)).getAttribute("EUOffset").trim());	                
                }
            }                       
        } catch (NullPointerException e) {e.printStackTrace();}            
        return XMLconfig;
    }
                  
    /** Print the XML configuration */
    public void print() {        
    	getxPCConfig().print();              
    }
    
    public static void main (String argv []){
    	// Get the input file name    	
    	String xmlfile;
        if ((xmlfile = FileHandler.getFilePath("Open xPC XML File", new XMLFileFilter())) == null) {    	        	
        	return;
        }                
        // Read the config
        ReadxPCXMLConfig xml = new ReadxPCXMLConfig(new File(xmlfile));
        
        // Print config
        xml.getxPCConfig().print();
        
        System.exit(0);
    }
}

