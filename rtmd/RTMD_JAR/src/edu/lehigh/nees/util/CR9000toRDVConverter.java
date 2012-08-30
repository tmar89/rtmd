package edu.lehigh.nees.util;

import java.io.*;
import edu.lehigh.nees.util.filefilter.*;
import javax.swing.JOptionPane;

/********************************* 
 * CR9000toRDVConverter
 * <p>
 * Convert between CR9000 ASCII file to CSV RDV file
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 23 Aug 11  T. Marullo  Initial
 * 
 ********************************/
public class CR9000toRDVConverter {		
    
    /** Creates a new instance of DAPCSVConverter */
    public CR9000toRDVConverter(boolean useDialog) {
    	if (useDialog) {	    		    		    	   
	    	// Get the input file name
	    	String inputFileName;
	        if ((inputFileName = FileHandler.getFilePath("Open RTDAQ DAT File", new DATFileFilter())) == null)
	        	return;
	    	
	    	// Get the output file name
	    	String outputFileName;
	        if ((outputFileName = FileHandler.getFilePath("Open CSV File", new CSVFileFilter())) == null)
	        	return;
	        
	        String trialName = JOptionPane.showInputDialog("Enter Unique Trial Name\n(Optional: Only if nesting Trials)\nHit OK for either option");
	
	        // Convert to File
	        CR9000toRDVConverter(inputFileName, outputFileName, trialName);
	        
	        // Push to RBNB server?	        
	        int choice = JOptionPane.showOptionDialog(null, "Send to RBNB Server?", "RDV Converter", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);
	        if (choice == JOptionPane.YES_OPTION) {
	        	String rbnbName = JOptionPane.showInputDialog("Enter Archive Name", "Tsunami");	        	
		        try { 
		        	Process p = Runtime.getRuntime().exec("java -jar \"C:\\Program Files\\Data Turbine Utilities\\dtu.jar\" FileToRbnb -S \""+rbnbName+"\" -s 128.180.53.5 -f \"" + outputFileName + "\"");		        	
		        } catch (Exception ex) {ex.printStackTrace();}
	            JOptionPane.showMessageDialog(null, "Data sent to RBNB server");
	        }
	        
	        // Delete CSV file
	        File f = new File(outputFileName);
	        f.delete();
    	}	    	    
    }
    
    /** Convert from DAT to CSV ASCII */
    public void CR9000toRDVConverter(String inputFileName, String outputFileName, String trialName) {        
    	// Open output file
        PrintStream outputFile = FileHandler.openOutputFile(outputFileName);
        
        // Get file information
        int numLines = FileHandler.getNumberOfLines(inputFileName);                               
		
		// Create progress bar
		ProgressPopup progressBar = new ProgressPopup("Converting", "RTDAQ DAT->CSV");
		progressBar.setMaximum(numLines);
		progressBar.setMinimum(0);		
		Thread progressBarThread = new Thread(progressBar);		
		progressBarThread.start();
				   			
        try {
        	// Open file 
            BufferedReader input = FileHandler.openInputFile(inputFileName);        
            int i;
            int progress = 0;
            String s;
            try {	
            	// Start progress bar            	
            	progressBar.setValue(progress);            	            	
    
            	// Time Format: 2001-10-11T12:12:55.12345
                                
				// Skip first line
				s = input.readLine();
				
				// Get header Line
				s = input.readLine();
				String[] channels = s.split(",");	
				// Remove quotes
				for (i = 0; i < channels.length; i++) {
					channels[i] = channels[i].replaceAll("\"", "");
					// Add trial name to channels if used
					if (!trialName.isEmpty())
						channels[i] = trialName.concat("/"+channels[i]);										
				}
								
				// Get Units Line
				s = input.readLine();
				String[] units = s.split(",");		
				// Remove quotes
				for (i = 0; i < units.length; i++)
					units[i] = units[i].replaceAll("\"", "");
				
				// Get Recording Type Line
				s = input.readLine();
				String[] rectype = s.split(",");		
				// Remove quotes
				for (i = 0; i < rectype.length; i++)
					rectype[i] = rectype[i].replaceAll("\"", "");
				
				// Write Time
				outputFile.print("Time,");		
				
				// Write headers + unites				
				for (i = 1; i < channels.length; i++) {
					if (i < channels.length-1) 
						outputFile.print(channels[i] + " (" + units[i] + ") (" + rectype[i] + "),");					
					else 
						outputFile.print(channels[i] + " (" + units[i] + ") (" + rectype[i] + ")");
				}
				
				// New Line
				outputFile.println();
			
				// Read and write data 
                while ((s = input.readLine()) != null) {
                	channels = s.split(",");	
                	channels[0] = channels[0].replace(" ", "T");
                	channels[0] = channels[0].replaceAll("\"", "");
                	s = channels[0] + s.substring(s.indexOf(','));
					outputFile.println(s);
					// Increment progress bar
					progressBar.setValue(progress++);
				}                 
            } catch (Exception e) {progressBar.setIsDone(true);}
            try {
                input.close();
                outputFile.close();
                progressBar.setIsDone(true);
                /*progressBar.dispose();*/
            } catch (Exception e) {e.printStackTrace();progressBar.setIsDone(true);}
        } catch (Exception e) {e.printStackTrace();progressBar.setIsDone(true);}                    
    }                   
  
    public static void main(String[] args) {
    	new CR9000toRDVConverter(true);    	 
    	System.exit(0);
    }    
}
