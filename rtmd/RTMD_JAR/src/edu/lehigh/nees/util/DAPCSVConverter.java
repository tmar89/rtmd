package edu.lehigh.nees.util;

import java.io.*;
import javax.swing.JOptionPane;
import edu.lehigh.nees.util.filefilter.*;

/********************************* 
 * DAPCSVConverter
 * <p>
 * Convert between Servotest DAP ASCII file and CSV file
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 22 Mar 06  T. Marullo  Initial
 * 
 ********************************/
public class DAPCSVConverter {		
    
    /** Creates a new instance of DAPCSVConverter */
    public DAPCSVConverter(boolean useDialog) {
    	if (useDialog) {
	    	int selection = 0;
	    	try {
	    		selection = Integer.parseInt(JOptionPane.showInputDialog(null,"1- Convert CSV to DAP\n2- Convert DAP to CSV","1"));
	    	} catch (Exception e) {return;}
	    	
	    	if (selection == 1) {    	
		    	// Get the input file name
		    	String inputFileName;
		        if ((inputFileName = FileHandler.getFilePath("Open CSV File", new CSVFileFilter())) == null)
		        	return;
		    	
		    	// Get the output file name
		    	String outputFileName;
		        if ((outputFileName = FileHandler.getFilePath("Open DAP .sdap File", new SDAPFileFilter())) == null)
		        	return;
		        
		        int freq = Integer.parseInt(JOptionPane.showInputDialog(null,"What is the frequency of the CSV file data (Hz)?","DAPCSVConverter",JOptionPane.OK_OPTION));
		
		        convertCSVtoDAP(inputFileName, outputFileName, freq);
	    	}
	    	else if (selection == 2) {    	
		    	// Get the input file name    
	    		String inputFileName;
		        if ((inputFileName = FileHandler.getFilePath("Open DAP .sdap File", new SDAPFileFilter())) == null)
		        	return;
		    	
		    	// Get the output file name
		        String outputFileName;
		        if ((outputFileName = FileHandler.getFilePath("Open CSV File", new CSVFileFilter())) == null)
		        	return;
		
		        convertDAPtoCSV(inputFileName, outputFileName);
	    	}
    	}
    }
    
    /** Convert from CSV to DAP ASCII */
    public void convertCSVtoDAP(String inputFileName, String outputFileName, int freq) {        
    	// Open output file
        PrintStream outputFile = FileHandler.openOutputFile(outputFileName);
        
        // Get file information
        int numLines = FileHandler.getNumberOfLines(inputFileName);                               
		
		// Create progress bar
		ProgressPopup progressBar = new ProgressPopup("Converting", "CSV->DAP");
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
                // Write freq
                outputFile.println(freq);
                // Write Length
                outputFile.println((numLines-1));
                                
				// Read header
				s = input.readLine();
				String[] cols = s.split(",");
				// Write # of columns
				outputFile.println((cols.length));
				// Write headers
				for (i = 0; i < cols.length; i++) {
					if (cols[i].length() > 20) 
						outputFile.println(cols[i].substring(0,19));					
					else 
						outputFile.println(cols[i]);
				}
				// Write equal amount of blanks (no units)
				for (i = 0; i < cols.length; i++) 
					outputFile.println("units");					
				// Read data and replace "," with " "
                while ((s = input.readLine()) != null) {					
					outputFile.println(s.replace(',',' '));
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
    
    /** Convert from DAP ASCII to CSV */
    public void convertDAPtoCSV(String inputFileName, String outputFileName) {        
    	// Open output file
        PrintStream outputFile = FileHandler.openOutputFile(outputFileName);                  
        
        /// Create progress bar
		ProgressPopup progressBar = new ProgressPopup("Converting", "DAP->CSV");			
		Thread progressBarThread = new Thread(progressBar);				
		
        try {
        	// Open file 
            BufferedReader input = FileHandler.openInputFile(inputFileName);        
            int i;
            int j = 0;
            String s;
            try {	
            	// Throw away first 12 lines
            	for (i = 0; i < 12; i++)
            		input.readLine();
            	
				// Read number of lines
				int numLines = Integer.parseInt(input.readLine().trim());
								
				// Read header
				s = input.readLine();					
				// Write header
				outputFile.println(s);

				// Read and throw away units				
				input.readLine();					
												
				// Initialize progress bar
				progressBar.setMaximum(numLines);
				progressBar.setMinimum(0);
            	progressBar.setValue(j);
            	progressBarThread.start();
				// Read data and replace " " with ","
                while ((s = input.readLine()) != null) {					
					outputFile.println(s);
					// Increment progress bar
					progressBar.setValue(j++);
				}                 
            } catch (Exception e) {e.printStackTrace(); progressBar.setIsDone(true);}
            try {
                input.close();
                outputFile.close();
                progressBar.setIsDone(true);
            } catch (Exception e) {e.printStackTrace();progressBar.setIsDone(true);}
        } catch (Exception e) {e.printStackTrace();progressBar.setIsDone(true);}                    
    }  
  
    public static void main(String[] args) {
    	new DAPCSVConverter(true);    	      	
    }    
}
