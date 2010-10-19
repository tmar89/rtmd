package edu.lehigh.nees.scramnet;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import java.text.DecimalFormat;
import edu.lehigh.nees.IntegratedControl.Ramp.ToZeroRampGenerator;

/********************************* 
 * SCRAMNet CommandZero
 * <p>
 * Zero the selected command blocks in SCRAMNet using a Ramp
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 *  2 Mar 06  T. Marullo   Initial
 * 21 Apr 09  T. Marullo   Fixed some sawtoothing effects.  Now runs smooth
 *  						
 ********************************/
public class CommandZero extends JDialog implements ActionListener, Runnable {
	/** Variables */
	private static final long serialVersionUID = -4520876572241672062L;	
	// Textboxes
	private JLabel label;
	private JTextField[] addressTextField;
	private JTextField[] valueTextField; 
	private JTextField rateTextField;
	private JLabel alertLabel;
	// Checkboxes
	private JCheckBox[] blockCheckBox;	 
	// Buttons
	private JButton zeroButton;
	private JButton selectAllButton;
	private JButton unselectAllButton;
	// Counter objects
	private long previousTickCount;
	private long currentTickCount;
	// SCRAMNet
	private ScramNetIO scr;
	// Formatter
	private DecimalFormat format = new DecimalFormat();   
	// Thread for updating
	private Thread updateThread;
	private Thread zeroThread;
  
	/** New CommandZero object */
	public CommandZero() {		
		// SCRAMNet
		scr = new ScramNetIO();
		scr.initScramnet();				
		
		// Number Formatter
		format.setMaximumFractionDigits(4);
		
		// Initialize the timer counters
		currentTickCount = scr.readGlobalCounter();
		previousTickCount = scr.readGlobalCounter();
		
		init();			
	}
	  
	/** Initialize the Button Graphics */
	public void init() {	
		// Set form layout
		Container contentPane = getContentPane();
	    contentPane.setLayout(new BorderLayout());
	
	    // Window parameters
	    this.setSize(600,500);
	    this.setTitle("SCRAMNet Command Zero");
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(null);
        this.setResizable(false);     
        this.setAlwaysOnTop(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);             
        // Set up UI Look and Feel
        try {                        
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {System.out.println("Error setting Look and Feel: " + e);}

        // Set up arrays for all 30 blocks
        addressTextField = new JTextField[30];
        valueTextField = new JTextField[30];
        blockCheckBox = new JCheckBox[30];
        
        // Set up header       
        // left column
		label = new JLabel("Name");
	    label.setBounds(30,5,125,25);	    
	    this.getContentPane().add(label);
	    label = new JLabel("Address");
	    label.setBounds(160,5,55,25);
	    label.setHorizontalAlignment(JTextField.RIGHT);
	    this.getContentPane().add(label);
	    label = new JLabel("Value");
	    label.setBounds(220,5,55,25);
	    label.setHorizontalAlignment(JTextField.RIGHT);
	    this.getContentPane().add(label);
        // right column
		label = new JLabel("Name");
	    label.setBounds(330,5,125,25);	    
	    this.getContentPane().add(label);
	    label = new JLabel("Address");
	    label.setBounds(460,5,55,25);
	    label.setHorizontalAlignment(JTextField.RIGHT);
	    this.getContentPane().add(label);
	    label = new JLabel("Value");
	    label.setBounds(520,5,55,25);
	    label.setHorizontalAlignment(JTextField.RIGHT);
	    this.getContentPane().add(label);
		    
		
	    // Create Textfields
        for (int i = 0; i < 30; i++) {
        	addressTextField[i] = new JTextField(Integer.toString(i));
        	valueTextField[i] = new JTextField("0");
        	blockCheckBox[i] = new JCheckBox();

        	switch (i) {
        		case 0:	// Simulation Running
        			// Label
        			label = new JLabel("Simulation Running");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 1:	// Displacement Command 1
        			// Label
        			label = new JLabel("Displacement cmd 1");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 2:	// Velocity Command 1
        			// Label
        			label = new JLabel("Velocity cmd 1");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;        			
        		case 3:	// Acceleration Command 1
        			// Label
        			label = new JLabel("Accel cmd 1");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 4:	// Force Command 1
        			// Label
        			label = new JLabel("Force cmd 1");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 5:	// Force Derivative Command 1
        			// Label
        			label = new JLabel("Force Deriv cmd 1");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 6:	// Displacement Command 2
        			// Label
        			label = new JLabel("Displacement cmd 2");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 7:	// Velocity Command 2
        			// Label
        			label = new JLabel("Velocity cmd 2");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;        			
        		case 8:	// Acceleration Command 2
        			// Label
        			label = new JLabel("Accel cmd 2");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 9:	// Force Command 2
        			// Label
        			label = new JLabel("Force cmd 2");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 10:	// Force Derivative Command 2
        			// Label
        			label = new JLabel("Force Deriv cmd 2");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 11:	// Displacement Command 3
        			// Label
        			label = new JLabel("Displacement cmd 3");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 12:	// Velocity Command 3
        			// Label
        			label = new JLabel("Velocity cmd 3");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;        			
        		case 13:	// Acceleration Command 3
        			// Label
        			label = new JLabel("Accel cmd 3");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 14:	// Force Command 3
        			// Label
        			label = new JLabel("Force cmd 3");
        		    label.setBounds(30,30+(i*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(160,30+(i*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(220,30+(i*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(5,30+(i*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 15:	// Force Derivative Command 3
        			// Label
        			label = new JLabel("Force Deriv cmd 3");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;        		
        		case 16:	// Displacement Command 4
        			// Label
        			label = new JLabel("Displacement cmd 4");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 17:	// Velocity Command 4
        			// Label
        			label = new JLabel("Velocity cmd 4");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 18:	// Acceleration Command 4
        			// Label
        			label = new JLabel("Accel cmd 4");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 19:	// Force Command 4
        			// Label
        			label = new JLabel("Force cmd 4");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 20:	// Force Derivative Command 4
        			// Label
        			label = new JLabel("Force Deriv cmd 4");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 21:	// Displacement Command 5
        			// Label
        			label = new JLabel("Displacement cmd 5");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 22:	// Velocity Command 5
        			// Label
        			label = new JLabel("Velocity cmd 5");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 23:	// Acceleration Command 5
        			// Label
        			label = new JLabel("Accel cmd 5");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 24:	// Force Command 5
        			// Label
        			label = new JLabel("Force cmd 5");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 25:	// Force Derivative Command 5
        			// Label
        			label = new JLabel("Force Deriv cmd 5");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(false);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 26:	// Custom 1
        			// Label
        			label = new JLabel("Custom 1");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(true);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 27:	// Custom 2
        			// Label
        			label = new JLabel("Custom 2");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(true);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 28:	// Custom 3
        			// Label
        			label = new JLabel("Custom 3");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(true);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		case 29:	// Custom 4
        			// Label
        			label = new JLabel("Custom 4");
        		    label.setBounds(330,30+((i-15)*25),125,25);        		    
        		    this.getContentPane().add(label);
        		    // Address TextField        		    
        		    addressTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    addressTextField[i].setBounds(460,30+((i-15)*25),55,25);
        		    addressTextField[i].setEditable(true);
        		    this.getContentPane().add(addressTextField[i]);
        		    // Value TextField        		    
        		    valueTextField[i].setHorizontalAlignment(JTextField.RIGHT);
        		    valueTextField[i].setBounds(520,30+((i-15)*25),55,25);
        		    valueTextField[i].setEditable(false);
        		    this.getContentPane().add(valueTextField[i]); 
        		    // Block CheckBox        		    
	                blockCheckBox[i].setBounds(305,30+((i-15)*25),20,20);
        		    this.getContentPane().add(blockCheckBox[i]); 
        			break;
        		default:
        	}
        }
        	
    	// Select All Button
    	selectAllButton = new JButton("Select All");
    	selectAllButton.setBounds(180,410,95,25);
    	selectAllButton.addActionListener(this);
    	this.getContentPane().add(selectAllButton);
    	
    	// Unselect All Button
    	unselectAllButton = new JButton("Unselect All");
    	unselectAllButton.setBounds(180,440,95,25);
    	unselectAllButton.addActionListener(this);	        
    	this.getContentPane().add(unselectAllButton);
    	
    	// Rate TextField
    	// Label
		label = new JLabel("Ramping Rate");
	    label.setBounds(350,410,125,25);       		    
	    this.getContentPane().add(label);
	    // Label
		label = new JLabel("ms (Ticks)");
	    label.setBounds(400,440,125,25);       		    
	    this.getContentPane().add(label);
	    // Rate TextField   		    
	    rateTextField = new JTextField("1024");
	    rateTextField.setHorizontalAlignment(JTextField.RIGHT);
	    rateTextField.setBounds(350,440,45,25);
	    rateTextField.setEditable(true);		    
	    this.getContentPane().add(rateTextField);		    
	    // Zero Button
    	zeroButton = new JButton("Zero Ramp");
    	zeroButton.setBounds(480,410,95,60);
    	zeroButton.addActionListener(this);	        
    	this.getContentPane().add(zeroButton);
    	// Counter Alert
    	alertLabel = new JLabel("Set Pulsar SCRAMNet On");
    	alertLabel.setHorizontalAlignment(JTextField.LEFT);
    	alertLabel.setBounds(15,440,200,25); 
    	alertLabel.setForeground(Color.RED);
    	alertLabel.setVisible(false);
	    this.getContentPane().add(alertLabel);
           
	    // Create a Thread to update the Button state
	    updateThread = new Thread(this);
	    updateThread.start();	    
	  }  		  
	  
		public void actionPerformed(ActionEvent arg0) {
			Object source = arg0.getSource();			
			
			/** Select All Button Event */
			if (source == selectAllButton) {
				for (int i = 0; i < 30; i++) {
					  blockCheckBox[i].setSelected(true);
				}
			}
			  
			  /** Unselect All Button Event */
			if (source == unselectAllButton) {
				for (int i = 0; i < 30; i++) {
					blockCheckBox[i].setSelected(false);
				}
			}
			  
			  /** Zero Button Event */
			if (source == this.zeroButton) {
				  zeroButton.setEnabled(false);				  
				  
				  // Create To Zero Ramp generators
				  ToZeroRampGenerator[] toZero = new ToZeroRampGenerator[30];		  		 		  
				  
				  // Get Rate
				  int lengthOfRamp = 1024;	// default value
				  // Check if it has a value that is a number		 
				  if (!rateTextField.getText().equals(""))	// get updated value 		  
					  lengthOfRamp = Integer.parseInt(rateTextField.getText().trim());
				  else
					  rateTextField.setText("1024");
				  // Can't be less than 1 second
				  if (lengthOfRamp < 1024) {
					  lengthOfRamp = 1024;
					  rateTextField.setText("1024");
				  }
		          
				  // Create a separate runnable class to perform the action
				  class clearSCRAMNet implements Runnable {
					  private ToZeroRampGenerator[] toZero;
					  private long baseTick;
					  private int lengthOfRamp;
					  private float[] currentCommand = new float[30];
					  public clearSCRAMNet(ToZeroRampGenerator[] _toZero, int _lengthOfRamp) {
						  toZero = _toZero;
						  lengthOfRamp = _lengthOfRamp;
					  }
					  public void run() {
						// Read the current SCRAMNet command data            
				          for (int i = 0; i < 30; i++) {
				        	  if (blockCheckBox[i].isSelected() == true) {
				        		  toZero[i] = new ToZeroRampGenerator();
				        		  if (!addressTextField[i].getText().equals("")) {
					        		  valueTextField[i].setText(format.format(scr.readFloat(Integer.parseInt(addressTextField[i].getText().trim()))));
					        		  currentCommand[i] = scr.readFloat(Integer.parseInt(addressTextField[i].getText().trim()));
				        		  }
				        	  }
				          }
						  
				          baseTick = scr.readGlobalCounter();
				          long nextTick = baseTick;
				          long count = 1;                             
				          // Ramp Selected down to 0
				          while (count <= lengthOfRamp) {            
				        	  for (int i = 0; i < 30; i++) {
				        		  if (blockCheckBox[i].isSelected() == true) {
				        			  if (!addressTextField[i].getText().equals(""))
				        				  scr.writeFloat(Integer.parseInt(addressTextField[i].getText().trim()), (float)toZero[i].generate(currentCommand[i], lengthOfRamp, count));        			  
				        		  }        		  
				        	  }
				              // Wait for next tick
				              nextTick = scr.readGlobalCounter();
				              while(nextTick == scr.readGlobalCounter()) {}              
				        	  //Increment the ramp to the next tick value to try to maintain speed
				              count = 1 + nextTick - baseTick;                                
				          }
				          
				          zeroButton.setEnabled(true); 
					  }
				  }
					
					// Run action 
					zeroThread = new Thread(new clearSCRAMNet(toZero, lengthOfRamp));
					zeroThread.start();				  		          		          
			  }
		}
  
	  /** Updates the values */
	  public void run() {
		  while (true) {
			  // Update values		  
			  for (int i = 0; i < 30; i++) {
				  if (!addressTextField[i].getText().equals(""))
					  valueTextField[i].setText(format.format(scr.readFloat(Integer.parseInt(addressTextField[i].getText().trim()))));
			  }
			  
			  // Check if the SCRAMNet Global Counter is on
			  currentTickCount = scr.readGlobalCounter();	
			  if (currentTickCount == previousTickCount) {
				  zeroButton.setEnabled(false);
				  alertLabel.setVisible(true);
			  }
			  else {
				  zeroButton.setEnabled(true);
				  alertLabel.setVisible(false);
			  }
			  
			  // Save the tick count for next check
			  previousTickCount = currentTickCount;
			  
			  try {
				  Thread.sleep(250);				  
			  } catch (Exception e) {}
		  }
	  } 	  	 

	  /** Main Process */
	  public static void main(String[] args) {
		  try {
			  CommandZero cz = new CommandZero();		
			  cz.setVisible(true);
		  } catch (Exception e) {e.printStackTrace();}
	  }
} 
