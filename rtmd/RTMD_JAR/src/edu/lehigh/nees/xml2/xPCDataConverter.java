package edu.lehigh.nees.xml2;

import java.io.*;
import java.nio.*;
import edu.lehigh.nees.util.FileHandler;
import edu.lehigh.nees.util.ProgressPopup;
import edu.lehigh.nees.util.filefilter.*;


/********************************* 
 * xPCDataConverter
 * <p>
 * Convert xPC Binary Files to CSV File using xPC XML file
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 19 Oct 10  T. Marullo  Rework from original
 * 30 Aug 12  T. Marullo  Moved Time to first column
 * 
 ********************************/
public class xPCDataConverter {

	// Variables	
	private DataInputStream data;
	private PrintStream out = null;
	private String outfile;
	private String xmlfile;
	private String xpcfile;
	private boolean isDone;		
	
	
	/** Create a new xPCDataConverter Object *
	 * 
	 * @param xmlfile_ xPC XML File
	 * @param outfile_ CSV Output file
	 */
	public xPCDataConverter(String xmlfile_, String outfile_, String xpcfile_) {							
		xmlfile = xmlfile_;
		outfile = outfile_;
		xpcfile = xpcfile_;
		
		// Open the Data file
		openDataFile(true);
	}
	
	/** Create a new xPCDataConverter Object with dialog boxes to get files */
	public xPCDataConverter(boolean isDataFileLocal) {
		out = null;			
		
		// Get the input file name    	
        if ((xmlfile = FileHandler.getFilePath("Open xPC XML File", new XMLFileFilter())) == null) {    	        	
        	return;
        }        
        
    	// Get the output file name    	
        if ((outfile = FileHandler.getFilePath("Open Output CSV File", new CSVFileFilter())) == null) {
        	return;
        }
        
		// Open the Data file        
        openDataFile(isDataFileLocal);
	}
	
	private boolean openDataFile(boolean isDataFileLocal) {
		// Open the Data File and get header information
		try {      
			if (isDataFileLocal)
				data = new DataInputStream(new BufferedInputStream(new FileInputStream(xmlfile.replace(xmlfile, xpcfile))));
			else
				data = new DataInputStream(new BufferedInputStream(new FileInputStream(FileHandler.getFilePath("Open xPC Data File"))));
			if (data == null)
				return false;
			data.skipBytes(9);
			int headerSize = data.readUnsignedByte()*256;
			data.skipBytes(headerSize-10);					
		} catch (Exception e) {e.printStackTrace(); return false;}
		return true;
	}
	
	/** Convert the Data files to CSV format */
	public boolean convert() {
		if (xmlfile == null || outfile == null || data == null) {
			javax.swing.JOptionPane.showMessageDialog(null, "No files opened for conversion");
			return false;
		}
		
		// Start progress bar
		ProgressPopup progressBar = new ProgressPopup("Converting", "XPC->CSV");
		progressBar.setIndeterminate(true);
		Thread progressBarThread = new Thread(progressBar);
		progressBarThread.start();
		
		// Open XML file
		XMLxPCConfig xml = (new ReadxPCXMLConfig(new File(xmlfile))).getxPCConfig();
			
		// Open Output File
		try {
			out = new PrintStream(new FileOutputStream(new File(outfile)));
		} catch (Exception e) {e.printStackTrace(); return false;}
		
		// Write Header
		//String s = "";
		String s = "Time,";
		for (int i = 0; i < xml.getNumChannels(); i++) {
			s = s + xml.getName(i) + " (" + xml.getUnits(i) + "),";
		}
		//out.println(s + "Time (s)");				
		out.println(s);
		
		// Get xPC Data
		// Go through all xPC Read Blocks and get data from files			
		while (isDone == false) {
			s = "";	
			for (int i = 0; i < xml.getNumChannels(); i++) {
				if (xml.getisDAQ(i).equals("False"))
					// Sim-Ctrl Block
					s = s + (getData(data)*Double.parseDouble(xml.getGain(i))) + ",";
				else {
					// DAQ Block needs to be converted to a 16-Bit 2-Cs Int					
					double daqval = getData(data);
					daqval = daqval/65536;
					if (daqval > 0x7FFF)
						daqval = -(((int)daqval ^ 0xFFFF) + 1);
					daqval = ((((((10000.0 / Double.parseDouble(xml.getGain(i)))/32768.0) * daqval) * Double.parseDouble(xml.getVoltageSlope(i))) + Double.parseDouble(xml.getVoltageOffset(i))) * Double.parseDouble(xml.getEUSlope(i))) + Double.parseDouble(xml.getEUOffset(i));					
					s = s + daqval + ",";
				}
			}			
			
			// Get the time and swap to first column
			// Skip the last line because it's garbage numbers
			if (isDone != true) {
				// Remove last ,
				s = s.substring(0,s.length()-1);
				s = getData(data) + "," + s;
				out.println(s);
				//out.println(s + getData(data));
			}										
		}	
		
		// Close files
		try {data.close();} catch (Exception e) {e.printStackTrace();}		
		out.close();	
		
		// Stop Progress Bar
		progressBar.setIsDone(true);    	
		
		return true;
	}
	
	private double getData(DataInputStream data) {
		byte b[] = new byte[8];
		double value;
	     
	    try {
	    	for (int i = 0; i < 8; i++) 
	    		b[7-i] = data.readByte();	    		
    		ByteBuffer buf = ByteBuffer.wrap(b);
    		value = buf.getDouble();
    		return value;
    	} catch (Exception e) {isDone = true;} // Set isDone if end of file
	    
		return (double)9999999;					     
	}
	
	public static void main(String[] args) 
	 {
		xPCDataConverter xpcconv = new xPCDataConverter(false);
		xpcconv.convert();
		System.out.println("Conversion Completed");
		System.exit(0);
	 }		
}

