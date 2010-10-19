package edu.lehigh.nees.util;

import edu.lehigh.nees.util.filefilter.CSVFileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/********************************* 
 * CSVDecimator
 * <p>
 * Decimate a CSV file
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 22 Mar 06  T. Marullo  Initial
 *  6 Apr 07  T. Marullo  Converted to Applet
 *  6 Nov 08  T. Marullo  Allowed to run as Application
 * 
 ********************************/
public class CSVDecimator extends JFrame implements ActionListener {
	
    private static final long serialVersionUID = 3492850886789702570L;
    
	/** Creates a new instance of CSVDecimator */
    public CSVDecimator() { 
    	super("CSV Decimator");
    	
    	// Initialize the decimate variable to 1 lines (no decimating) 
        decimate = 1;
        linesToSkip = 0;       
        init();
        pack();
        setVisible(true);
    }
    
    public void init() {    	
    	this.setSize(225,270);
    	this.getContentPane().setLayout(null);    	
    	this.setLocationRelativeTo(null);
    	
    	// CSV File 
    	openCSVFileButton = new JButton("Open CSV File");
    	openCSVFileButton.setBounds(10,10,200,25);
    	openCSVFileButton.addActionListener(this);
    	openCSVFileButton.setActionCommand("open");
    	this.getContentPane().add(openCSVFileButton);
    	
    	// Analyze
    	analyzeButton = new JButton("Analyze CSV File");
    	analyzeButton.setBounds(10,40,200,25);
    	analyzeButton.setActionCommand("analyze");
    	analyzeButton.addActionListener(this);
    	this.getContentPane().add(analyzeButton);
    	
    	// Decimation value
    	JLabel label = new JLabel("Decimation value: ");
    	label.setBounds(10,70,150,25);
    	this.getContentPane().add(label);
    	decimateTextField = new JTextField("1");    	
    	decimateTextField.setBounds(150,70,50,25);
    	decimateTextField.setHorizontalAlignment(JTextField.RIGHT);    	    	
    	this.getContentPane().add(decimateTextField);
    	
    	// Lines to skip value
    	label = new JLabel("Initial lines to skip: ");
    	label.setBounds(10,100,150,25);
    	this.getContentPane().add(label);
    	linesToSkipTextField = new JTextField("1");    	
    	linesToSkipTextField.setBounds(150,100,50,25);
    	linesToSkipTextField.setHorizontalAlignment(JTextField.RIGHT);    	
    	this.getContentPane().add(linesToSkipTextField);
    	
    	// Columns to keep
    	label = new JLabel("Columns to keep: ");
    	label.setBounds(10,130,150,25);
    	this.getContentPane().add(label);
    	columnsToIncludeTextField = new JTextField("");    	
    	columnsToIncludeTextField.setBounds(150,130,50,25);
    	columnsToIncludeTextField.setHorizontalAlignment(JTextField.RIGHT);    	
    	this.getContentPane().add(columnsToIncludeTextField);
    	
    	// Output File
    	openOutputFileButton = new JButton("Set Output File");
    	openOutputFileButton.setBounds(10,160,200,25);
    	openOutputFileButton.setActionCommand("set");
    	openOutputFileButton.addActionListener(this);
    	this.getContentPane().add(openOutputFileButton);
    	
    	// Decimate
    	decimateButton = new JButton("Decimate");
    	decimateButton.setBounds(10,190,200,25);
    	decimateButton.setActionCommand("decimate");
    	decimateButton.addActionListener(this);
    	this.getContentPane().add(decimateButton);    	       
    }
    
    /** Action Event handler for this class *
     */
    public void actionPerformed(ActionEvent arg0) {    	
    	if (arg0.getActionCommand().equals("open"))	
    		inputFileName = FileHandler.getFilePath("Open Input CSV File",new CSVFileFilter());
    	
    	if (arg0.getActionCommand().equals("analyze"))	
    		analyze();
    	
    	if (arg0.getActionCommand().equals("set"))
    		outputFileName = FileHandler.getFilePath("Set Output CSV File",new CSVFileFilter());
    		
		if (arg0.getActionCommand().equals("decimate"))	
			decimate();
	}      

    /** Read the length of the CSV file and set decimation value *
     */
    private void analyze() {
    	if (inputFileName == null) {
    		JOptionPane.showMessageDialog(null,"Open CSV file first");
    		return;
    	}
    	
        // Check if the CSV file has a header and skip it when decimating
    	if (JOptionPane.showConfirmDialog(null,"Does this input file have a header?","CSVDecimator",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
    		linesToSkip = 1;
    	else
    		linesToSkip = 0;
    	linesToSkipTextField.setText(Integer.toString(linesToSkip));
    	
    	// Get number of columns
    	int numColumns = FileHandler.getNumberOfColumns(inputFileName);
    	columnsToInclude = "";
    	for (int i = 0; i < numColumns-1; i++)
    		columnsToInclude += Integer.toString(i+1) + ",";    	
    	columnsToInclude += Integer.toString(numColumns);
    	columnsToIncludeTextField.setText(columnsToInclude);
    	
    	// Get number of lines
		numLines = FileHandler.getNumberOfLines(inputFileName);
		decimate = Integer.parseInt(JOptionPane.showInputDialog(null,"File has " + numLines + " lines.  Excel supports 32000 max lines for graphs.\nRecommended Decimation value = " + (1+(long)((double)numLines/(double)32000)),(1+(long)((double)numLines/(double)32000))));
		decimateTextField.setText(Integer.toString(decimate));
		
		wasAnalyzed = true;
    }
    
    /** Decimate the file and only include certain columns *
     * 
     * @param columns comma delimited list of columns to include 
     */
    public void decimate() {
    	if (inputFileName == null || outputFileName == null || wasAnalyzed == false) {
    		JOptionPane.showMessageDialog(null,"You must open and set the CSV files and analyze the input file first");
    		return;
    	}
    	
    	decimate = Integer.parseInt(decimateTextField.getText());
    	linesToSkip = Integer.parseInt(linesToSkipTextField.getText());
    	columnsToInclude = columnsToIncludeTextField.getText().trim();    	
    	
    	// Get columns
    	String[] cols = columnsToInclude.split(",");
    	    	
    	// Open output file
        PrintStream outputFile = FileHandler.openOutputFile(outputFileName);
        
        // Create a progress bar
        JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(0,0,100,25);
		JFrame popup = new JFrame("CSVDecimator");
		popup.setBounds(0,0,200,70);
		popup.setLocationRelativeTo(null);
		popup.getContentPane().setLayout(new BorderLayout());
		popup.getContentPane().add(progressBar,BorderLayout.CENTER);
		JLabel label = new JLabel("Decimating");
		label.setHorizontalAlignment(JLabel.CENTER);
		popup.getContentPane().add(label,BorderLayout.NORTH);
        try {
        	// Open file 
            BufferedReader input = FileHandler.openInputFile(inputFileName);        
            int i = 0;
            String s;
            String s_new;
            String[] ss;
            try {	
            	// Initialize progress bar
            	progressBar.setMaximum(numLines);
            	progressBar.setMinimum(0);
            	progressBar.setValue(i);
            	popup.setVisible(true);
            	// Read each line
                while ((s = input.readLine()) != null) {
                	s_new = "";
                	// Split into each column
                	ss = s.split(",");
                	// Number of lines to skip
                    if (i < linesToSkip) {                       
                    	for (int k = 0; k < cols.length; k++)
                    		s_new = s_new + ss[Integer.parseInt(cols[k].trim())-1] + ",";
                    	outputFile.println(s_new);
                    }
                    else {
                    	// Output the modulus of the decimation value
                        if (i % decimate == 0) {                    
                        	for (int k = 0; k < cols.length; k++)
                        		s_new = s_new + ss[Integer.parseInt(cols[k].trim())-1] + ",";
                        	outputFile.println(s_new);
                        }                
                    }
                    i++;   
                    progressBar.setValue(i);
                }           
            } catch (Exception e) {popup.setVisible(false);}
            try {
                input.close();
                outputFile.close();
                popup.setVisible(false);
            } catch (Exception e) {e.printStackTrace();popup.setVisible(false);}
        } catch (Exception e) {e.printStackTrace();popup.setVisible(false);}
            
        JOptionPane.showMessageDialog(null,"Decimation Complete");
    }            
    
    public static void main(String args[]) {
    	new CSVDecimator();    	
    }

    
    // Variables    
    private int numLines;     
    private boolean wasAnalyzed = false;
    private JButton openCSVFileButton;
    private String inputFileName;    
    private JButton analyzeButton;
    private JButton openOutputFileButton;
    private String outputFileName;
    private JTextField decimateTextField;
    private int decimate;
    private JTextField linesToSkipTextField;
    private int linesToSkip;
    private JTextField columnsToIncludeTextField;
    private String columnsToInclude;
    private JButton decimateButton;
}
