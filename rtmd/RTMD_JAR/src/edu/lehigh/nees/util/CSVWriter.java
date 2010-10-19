package edu.lehigh.nees.util;

/********************************* 
 * CSV Writer
 * <p>
 * Creates a CSV file with a header and data is appended after each write
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 15 Sep 05  T. Marullo  Initial
 *  3 Nov 05  T. Marullo  Added setCSVFile(String) to set a filename explicitly
 *  7 Mar 06  T. Marullo  Changed function name for setCSVFile to open
 * 
 ********************************/
import java.io.*;
import javax.swing.JFileChooser;

public class CSVWriter {
    PrintStream toFile;        
    
    /** Creates a new instance of csvWriter */
    public CSVWriter() {    	 	    
    }       
    
    /** Write the header of the CSV file based on a String array */
    public void writeHeader(String[] header) {
    	// Empty string to hold header
    	String s_header = "";
    	
    	// Create the header string in a CSV format
    	for (int j = 0; j < header.length; j++) {
    		// Append the string with the new object title
    		s_header = s_header + header[j];
    		// If the object isn't the last one, add a comma
    		if (j < header.length - 1) 
    			s_header = s_header + ",";
    	}
    	
    	// Write the header
        toFile.println(s_header);
    }
    
    /** Write the header of the CSV file based on a comma separated String  */
    public void writeHeader(String header) {
    	String[] headerArray = header.split(",");
    	
    	// Empty string to hold header
    	String s_header = "";
    	
    	// Create the header string in a CSV format
    	for (int j = 0; j < headerArray.length; j++) {
    		// Append the string with the new object title
    		s_header = s_header + headerArray[j];
    		// If the object isn't the last one, add a comma
    		if (j < headerArray.length - 1) 
    			s_header = s_header + ",";
    	}
    	
    	// Write the header
        toFile.println(s_header);
    }
    
    /** Write a set of values to the CSV file based on an array */
    public void write(double[] values) {
    	// Empty string to hold values
    	String s_values = "";
    	
    	// Create the values string in a CSV format
    	for (int j = 0; j < values.length; j++) {
    		// Append the string with the new object title
    		s_values = s_values + Double.toString(values[j]);
    		// If the object isn't the last one, add a comma
    		if (j < values.length - 1) 
    			s_values = s_values + ",";
    	}
    	
    	// Write the values
        toFile.println(s_values);
    }
    
    /** Write a set of values to the CSV file based on a comma separated string */
    public void write(String values) {
    	String[] valuesArray = values.split(",");
    	
    	// Empty string to hold values
    	String s_values = "";
    	
    	// Create the values string in a CSV format
    	for (int j = 0; j < valuesArray.length; j++) {
    		// Append the string with the new object title
    		s_values = s_values + valuesArray[j];
    		// If the object isn't the last one, add a comma
    		if (j < valuesArray.length - 1) 
    			s_values = s_values + ",";
    	}
    	
    	// Write the values
        toFile.println(s_values);
    }

    /** Close the file */
    public void close() {
         toFile.close();
    }
    
    /** Set a CSV file*/
    public boolean open() {        
        try{
            JFileChooser fc = new JFileChooser("/");            
            int returnVal = fc.showDialog(null,"Set CSV File");
            if(returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {                
                toFile = new PrintStream(new FileOutputStream(fc.getSelectedFile()));            
                return true;
            }
            else
                return false;
        } catch (Exception e) {
            System.err.println(e);
        }
        return false;
    }
    
    /** Define the File explicitly */
    public boolean open(String s) {
    	try {
    		toFile = new PrintStream(new FileOutputStream(new File(s)));
    		return true;
    	} catch (Exception e) {System.err.println(e); return false;}    	
    }
    
    /** Main */
    public static void main(String[] args) {
    	CSVWriter csv = new CSVWriter();
    	
    	csv.open();
    	
    	String[] names = {"first,second"};
    	double val1 = 2;
    	double val2 = 4;
    	double[] values = new double[2];
    	values[0] = val1;
    	values[1] = val2;
    	
    	csv.writeHeader(names);
    	csv.write(values);
    	val1 = 5;
    	val2 = 10;
    	values[0] = val1;
    	values[1] = val2;
    	csv.write(values);
    	
    	csv.close();
    	
    	System.exit(0);
    }
    
}