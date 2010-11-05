/**
 * 
 */
package edu.lehigh.nees.util;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import edu.lehigh.nees.util.filefilter.*;
import edu.lehigh.nees.util.FileHandler;
import java.util.Properties;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
 
public class LimitsGenerator extends JPanel implements ActionListener {    
	private static final long serialVersionUID = 3807198237888009520L;	
	private static final int frameWidth = 600;
	private static final int frameHeight = 520;
	private static final double panelPercentage = 0.65;
	private static final Stroke basicStroke = new BasicStroke(2);
	private static final Stroke dashedStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2}, 0);
	private Properties properties = new Properties();
	
	
	/* 1px = 5mm */
	/* 200px = 1000mm */
	public int ratio = 5;
	
	// Actuator and Piston graphics
	public double pistonLength = 1000; //mm
	public double actPosition = 0; //mm
	public double actMean = 0; //mm
	public Point actStart = new Point(50,60);
    public Point actEnd = new Point(actStart.x+200,actStart.y+50);
    public Point pistonStart = new Point(actEnd.x,actStart.y+10);
    public Point pistonEnd = new Point(actEnd.x+((int)pistonLength/ratio)/2,pistonStart.y+30);
    public int pistonResize = 0;
    
    // LVDT graphics (start at act body)
    public double LVDTlength = 1000; //mm
    public double LVDTposition = 0; //mm
	public Point LVDTStart = new Point(actEnd.x,140);
    public Point LVDTEnd = new Point(LVDTStart.x+((int)LVDTlength/ratio),LVDTStart.y+10);            
    
    // Inputs
    JTextField pistonLengthInput, actPositionInput, actMeanInput, LVDTlengthInput, LVDTpositionInput;
    JComboBox actUnitsBox;
    JToggleButton showLVDTButton;
    
    // Displays
    JTextField actPhysicalRetractionLimit, actPhysicalExtensionLimit, actCmdRetractionLimit, actCmdExtensionLimit;    
	JTextField actDSPRetractionLimit, actDSPExtensionLimit, actDispExtensionLimit, actDispRetractionLimit;
    JTextField LVDTretractionLimit, LVDTextensionLimit;
    JTextField commandValue, DSPValue;
    
    public JMenuBar generateMenuBar() {
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("File");
		JMenuItem openitem = new JMenuItem("Open");
		JMenuItem saveitem = new JMenuItem("Save");
		openitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				openConfiguration();
			}});
		saveitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				saveConfiguration();
			}});
		// Organize the Menu Bar		
		filemenu.add(openitem);
		filemenu.add(saveitem);
		menubar.add(filemenu);
		return menubar;
	}
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);        
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        /** Actuator Section */
        // Adjust Piston Length                        
        pistonEnd = new Point(actEnd.x+((int)pistonLength/ratio)/2,pistonStart.y+30);
                        
        // Draw Actuator Body Rectangle in mm pixels
        g2.setPaint(Color.blue);
        g2.fill(new Rectangle2D.Double(actStart.x,actStart.y,actEnd.x-actStart.x,actEnd.y-actStart.y));     
        // Draw Actuator Piston in mm pixels
        g2.setPaint(Color.gray);
        g2.fill(new Rectangle2D.Double(pistonStart.x,pistonStart.y,pistonEnd.x-pistonStart.x-pistonResize,pistonEnd.y-pistonStart.y));
        // Top global lines
        g2.drawLine(actEnd.x+(int)pistonLength/ratio/2, actStart.y-5, actEnd.x+(int)pistonLength/ratio/2, actStart.y-2); // Center tick
        g2.setStroke(dashedStroke);                       
        g2.drawLine(actEnd.x, actStart.y-5, actEnd.x, actEnd.y+35); // Left End        
        g2.drawLine(actEnd.x+(int)pistonLength/ratio, actStart.y-5, actEnd.x+(int)pistonLength/ratio, actEnd.y+35); // Right End        
        // Top global range numbers
        g2.drawChars(new char[]{'0'}, 0, 1, actEnd.x+(int)pistonLength/ratio/2-3, actStart.y-10); // Zero
        g2.drawChars(Double.toString(pistonLength/2).toCharArray(), 0, Double.toString(pistonLength/2).length()-2, pistonStart.x-15, actStart.y-10); // Right
        g2.drawChars(Double.toString(-pistonLength/2).toCharArray(), 0, Double.toString(pistonLength/2).length()-1, pistonStart.x+(int)pistonLength/ratio-15, actStart.y-10); // Left
        // Actuator Position Line
        g2.drawLine(pistonEnd.x-(int)actPosition/ratio, actStart.y-30, pistonEnd.x-(int)actPosition/ratio, actEnd.y+35);        
        // Actuator Position number
        g2.drawChars(Double.toString(actPosition).toCharArray(), 0, Double.toString(actPosition).length(), pistonEnd.x-(int)actPosition/ratio, actStart.y-35);                
        g2.setPaint(Color.black);
        // Actuator mean
        g2.drawChars(Double.toString(actMean).toCharArray(), 0, Double.toString(actMean).length(), pistonEnd.x-pistonResize-35, pistonEnd.y-2);
        // Labels
        g2.drawChars(new char[]{'A','c','t','u','a','t','o','r',' ','R','a','n','g','e'}, 0, 14, actStart.x, actStart.y-10);
        g2.drawChars(new char[]{'A','c','t','u','a','t','o','r',' ','O','f','f','s','e','t'}, 0, 15, actStart.x, actStart.y-35);
        g2.setPaint(Color.white);
        g2.drawChars(new char[]{'D','i','s','p','l','a','c','e','m','e','n','t',' ','T','r','a','n','s','d','u','c','e','r'}, 0, 23, actStart.x, actStart.y+35);
        
        
        /** LVDT Section */
        if (showLVDTButton.isSelected()) {
        	g2.setPaint(Color.black);
        	// Adjust LVDT Length
            LVDTEnd = new Point(LVDTStart.x+((int)LVDTlength/ratio),LVDTStart.y+10);
            
            // Adjust LVDT center position relative to actuator body
            //LVDTStart = new Point(actEnd.x+((int)LVDTposition/ratio)-((int)LVDTlength/ratio)/2,100);
            //LVDTStart = new Point(actEnd.x+((int)LVDTposition/ratio),140);
            LVDTStart = new Point((actEnd.x+(int)pistonLength/ratio/2)-(int)LVDTlength/ratio/2-((int)LVDTposition/ratio),140);
            LVDTEnd = new Point(LVDTStart.x+((int)LVDTlength/ratio),LVDTStart.y+10);
        	
	        // Draw LVDT Rod in mm pixels
	        g2.setPaint(Color.black);
	        g2.fill(new Rectangle2D.Double(LVDTStart.x,LVDTStart.y,LVDTEnd.x-LVDTStart.x,LVDTEnd.y-LVDTStart.y));
	        // Draw Vertical Line at LVDT center and ends
	        g2.setStroke(basicStroke);
	        g2.drawLine(LVDTStart.x+((int)LVDTlength/ratio)/2,LVDTStart.y-2,LVDTStart.x+((int)LVDTlength/ratio)/2,LVDTEnd.y+2); // Center line
	        //g2.drawLine(LVDTStart.x, LVDTStart.y-2, LVDTStart.x, LVDTStart.y+12);        
	        //g2.drawLine(LVDTEnd.x, LVDTStart.y-2, LVDTEnd.x, LVDTStart.y+12);  
	        // Draw Numeric Indicators on LVDT
	        //g2.drawChars(Double.toString(pistonLength/2-LVDTposition).toCharArray(), 0, Double.toString(pistonLength/2-LVDTposition).length(), LVDTStart.x+((int)LVDTlength/ratio)/2-10, LVDTEnd.y+17);
	        //g2.drawChars(Double.toString((LVDTlength/2) + (pistonLength/2 - LVDTposition)).toCharArray(), 0, Double.toString((LVDTlength/2) + (pistonLength/2 - LVDTposition)).length()-2, LVDTStart.x-10, LVDTEnd.y+17);
	        //g2.drawChars(Double.toString(-((LVDTlength/2) - (pistonLength/2 - LVDTposition))).toCharArray(), 0, Double.toString(-((LVDTlength/2) - (pistonLength/2 - LVDTposition))).length()-2, LVDTEnd.x-10, LVDTEnd.y+17);
	        // Show LVDT absolute range
	        g2.drawChars(new char[]{'0'}, 0, 1, LVDTStart.x+((int)LVDTlength/ratio)/2-3, LVDTEnd.y+17); // Zero
	        g2.drawChars(Double.toString(LVDTlength/2).toCharArray(), 0, Double.toString(LVDTlength/2).length()-2, LVDTStart.x-10, LVDTEnd.y+17); // Left
	        g2.drawChars(Double.toString(-LVDTlength/2).toCharArray(), 0, Double.toString(-LVDTlength/2).length()-2, LVDTEnd.x-10, LVDTEnd.y+17); // Right
	        // Show offset from center value
	        g2.drawChars(Double.toString(actPosition-LVDTposition).toCharArray(), 0, Double.toString(actPosition-LVDTposition).length(), pistonEnd.x-(int)actPosition/ratio, LVDTStart.y-5);
	        // Show values where ends intersect LVDT
	        g2.drawChars(Double.toString(pistonLength/2-LVDTposition).toCharArray(), 0, Double.toString(pistonLength/2-LVDTposition).length()-2, pistonStart.x, LVDTStart.y-5); // Right
	        g2.drawChars(Double.toString(-pistonLength/2-LVDTposition).toCharArray(), 0, Double.toString(pistonLength/2-LVDTposition).length()-1, pistonStart.x+(int)pistonLength/ratio, LVDTStart.y-5); // Left
	        // Show text labels
	        g2.drawChars(new char[]{'R','e','s','t','r','i','c','t','e','d',' ','L','V','D','T',' ','R','a','n','g','e'}, 0, 21, actStart.x, LVDTStart.y-5);
	        g2.drawChars(new char[]{'L','V','D','T',' ','R','a','n','g','e'}, 0, 10, actStart.x, LVDTStart.y+27);
        }
    }
 
    public JPanel getUIPanel() {
    	// Create the panel to add the items too
    	JPanel panel = new JPanel();
    	panel.setLayout(null);
    	panel.setBounds(0, (int)(frameHeight*(1-panelPercentage)), frameWidth, (int)(frameHeight*panelPercentage));
    	panel.setBorder(BorderFactory.createEtchedBorder());
    	
    	int setupRow = 1; int setupCol = 10;
    	int outputcolRow = 1; int outputcol = 250;    	
    	
    	// Input section  
    	JLabel label = new JLabel("Actuator Setup");
    	label.setBounds(setupCol,25*setupRow,200,25);
    	panel.add(label);
    	setupRow++;
    	
    	// Units selection
    	label = new JLabel("Units");
    	label.setBounds(setupCol,25*setupRow,200,25);
    	panel.add(label);   
    	actUnitsBox = new JComboBox(new String[]{"mm","inches"});
    	actUnitsBox.setBounds(setupCol+124,25*setupRow,75,25);
    	panel.add(actUnitsBox);
    	setupRow++;
    	
    	// Actuator Piston Length
    	label = new JLabel("Actuator Length");
    	label.setBounds(setupCol,25*setupRow,150,25);
    	panel.add(label);
    	pistonLengthInput = new JTextField(String.valueOf(pistonLength));
    	pistonLengthInput.setBounds(setupCol+150,25*setupRow,50,25);
    	pistonLengthInput.setActionCommand("pistonLengthInput");
    	pistonLengthInput.addActionListener(this);  	
        panel.add(pistonLengthInput);
        setupRow++;
    	
    	// Actuator Position
        label = new JLabel("Actuator Zero Position");
        label.setBounds(setupCol,25*setupRow,150,25);
    	panel.add(label);
    	actPositionInput = new JTextField(String.valueOf(actPosition));
    	actPositionInput.setBounds(setupCol+150,25*setupRow,50,25);
    	actPositionInput.setActionCommand("actPositionInput");
    	actPositionInput.addActionListener(this);    
        panel.add(actPositionInput);
        setupRow++;
        
        // Actuator Mean
        label = new JLabel("Actuator Mean");
        label.setBounds(setupCol,25*setupRow,150,25);
    	panel.add(label);
    	actMeanInput = new JTextField(String.valueOf(actMean));
    	actMeanInput.setBounds(setupCol+150,25*setupRow,50,25);
    	actMeanInput.setActionCommand("actMeanInput");
    	actMeanInput.addActionListener(this);    
        panel.add(actMeanInput);
        setupRow++;
        
        // Diretion Note
    	label = new JLabel("(Extention is - , Retraction is +)");
    	label.setBounds(10,25*setupRow,200,25);
    	panel.add(label);
    	setupRow++;    	
        
        // LVDT Setup
    	label = new JLabel("External LVDT Setup");
    	label.setBounds(10,25*setupRow,200,25);
    	panel.add(label);
    	setupRow++;
    	
    	// LVDT length
        label = new JLabel("LVDT Length");
        label.setBounds(setupCol,25*setupRow,100,25);
    	panel.add(label);
    	LVDTlengthInput = new JTextField(String.valueOf(LVDTlength));
    	LVDTlengthInput.setBounds(setupCol+150,25*setupRow,50,25);
    	LVDTlengthInput.setActionCommand("LVDTlengthInput");
    	LVDTlengthInput.addActionListener(this);  
    	LVDTlengthInput.setVisible(false);
        panel.add(LVDTlengthInput);
        setupRow++;
        
        // LVDT center position relative to Actuator body
        label = new JLabel("LVDT Center wrt Act Zero Pos");
        label.setBounds(setupCol,25*setupRow,200,25);
    	panel.add(label);
    	LVDTpositionInput = new JTextField(String.valueOf("0"));
    	LVDTpositionInput.setBounds(setupCol+150,25*setupRow,50,25);
    	LVDTpositionInput.setActionCommand("LVDTpositionInput");
    	LVDTpositionInput.addActionListener(this);   
    	LVDTpositionInput.setVisible(false);
        panel.add(LVDTpositionInput);
        setupRow++;
        
        // Toggle button to show LVDT or not
        showLVDTButton = new JToggleButton("External LVDT Disabled");
        showLVDTButton.setBounds(setupCol,25*setupRow,199,25);
        showLVDTButton.setActionCommand("showLVDTButton");
        showLVDTButton.addActionListener(this);
        panel.add(showLVDTButton);
        setupRow++;
        
        // Output  section    	
    	label = new JLabel("Actuator and LVDT Range");
    	label.setBounds(outputcol,25*outputcolRow,150,25);
    	panel.add(label);    	
    	label = new JLabel("[<- Ret  |  Ext ->]");
    	label.setBounds(outputcol+205,25*outputcolRow,150,25);
    	panel.add(label);
    	outputcolRow++;
    	
        
        // Actuator Physical Stroke
    	label = new JLabel("Actuator Travel from Zero Position");
    	label.setBounds(outputcol,25*outputcolRow,200,25);
    	panel.add(label);
    	actPhysicalRetractionLimit = new JTextField(String.valueOf(pistonLength/2));
    	actPhysicalRetractionLimit.setBounds(outputcol+200,25*outputcolRow,50,25);
    	actPhysicalRetractionLimit.setEditable(false);
        panel.add(actPhysicalRetractionLimit);
        actPhysicalExtensionLimit = new JTextField(String.valueOf(-pistonLength/2));
        actPhysicalExtensionLimit.setBounds(outputcol+250,25*outputcolRow,50,25);
        actPhysicalExtensionLimit.setEditable(false);
        panel.add(actPhysicalExtensionLimit);
        outputcolRow++;
        
        // LVDT Range wrt Actuator 
    	label = new JLabel("Actuator Travel with LVDT Restriction");
    	
    	label.setBounds(outputcol,25*outputcolRow,200,25);
    	panel.add(label);
    	LVDTretractionLimit = new JTextField(String.valueOf(pistonLength/2));
    	LVDTretractionLimit.setBounds(outputcol+200,25*outputcolRow,50,25);
    	LVDTretractionLimit.setEditable(false);
    	LVDTretractionLimit.setVisible(false);
        panel.add(LVDTretractionLimit);
        LVDTextensionLimit = new JTextField(String.valueOf(-pistonLength/2));
        LVDTextensionLimit.setBounds(outputcol+250,25*outputcolRow,50,25);
        LVDTextensionLimit.setEditable(false);
        LVDTextensionLimit.setVisible(false);
        panel.add(LVDTextensionLimit);
        outputcolRow++;
        outputcolRow++;
        
        // Actuator Limits	
    	label = new JLabel("Pulsar Control Limits");
    	label.setBounds(outputcol,25*outputcolRow,350,25);
    	panel.add(label);
    	label = new JLabel("[<- Ret  |  Ext ->]");
    	label.setBounds(outputcol+205,25*outputcolRow,150,25);
    	panel.add(label);
    	outputcolRow++;
        
        // Actuator Displacement Range
    	label = new JLabel("Max Relative Disp. Limits");
    	label.setBounds(outputcol,25*outputcolRow,200,25);
    	panel.add(label);
    	actDispRetractionLimit = new JTextField(String.valueOf(pistonLength/2));
    	actDispRetractionLimit.setBounds(outputcol+200,25*outputcolRow,50,25);
    	actDispRetractionLimit.setEditable(false);    	
        panel.add(actDispRetractionLimit);
        actDispExtensionLimit = new JTextField(String.valueOf(-pistonLength/2));
        actDispExtensionLimit.setBounds(outputcol+250,25*outputcolRow,50,25);
        actDispExtensionLimit.setEditable(false);        
        panel.add(actDispExtensionLimit);
        outputcolRow++;                        
        
        // Actuator Command Limits
        label = new JLabel("Max Command Limits");
        label.setBounds(outputcol,25*outputcolRow,200,25);
    	panel.add(label);
    	actCmdRetractionLimit = new JTextField(String.valueOf(pistonLength/2));
    	actCmdRetractionLimit.setBounds(outputcol+200,25*outputcolRow,50,25);
    	actCmdRetractionLimit.setEditable(false);
        panel.add(actCmdRetractionLimit);
        actCmdExtensionLimit = new JTextField(String.valueOf(-pistonLength/2));
        actCmdExtensionLimit.setBounds(outputcol+250,25*outputcolRow,50,25);
        actCmdExtensionLimit.setEditable(false);
        panel.add(actCmdExtensionLimit);
        outputcolRow++;
        
        // Actuator DSP Limits
        label = new JLabel("Max DSP (SCRAMNet) Limits");
        label.setBounds(outputcol,25*outputcolRow,200,25);
    	panel.add(label);
    	actDSPRetractionLimit = new JTextField(String.valueOf(pistonLength/2/500));
    	actDSPRetractionLimit.setBounds(outputcol+200,25*outputcolRow,50,25);
    	actDSPRetractionLimit.setEditable(false);
        panel.add(actDSPRetractionLimit);
        actDSPExtensionLimit = new JTextField(String.valueOf(-pistonLength/2/500));
        actDSPExtensionLimit.setBounds(outputcol+250,25*outputcolRow,50,25);
        actDSPExtensionLimit.setEditable(false);
        panel.add(actDSPExtensionLimit);
        outputcolRow++;
        outputcolRow++;         
                        
        // DSP Limit Calculator
        label = new JLabel("Number to DSP Converter");
        label.setBounds(outputcol,25*outputcolRow,200,25);
    	panel.add(label);
    	commandValue = new JTextField("500");
    	commandValue.setBounds(outputcol+200,25*outputcolRow,50,25);
    	commandValue.setActionCommand("commandValue");
    	commandValue.addActionListener(this);
        panel.add(commandValue);
        DSPValue = new JTextField("1.0");
        DSPValue.setBounds(outputcol+250,25*outputcolRow,50,25);        
        DSPValue.setEditable(false);
        panel.add(DSPValue);
        
        return panel;        
    }
    
    // Actions go here
	public void actionPerformed(ActionEvent e) {			
		// Get some necessary variables
		try {
			actPosition = Double.parseDouble(actPositionInput.getText().trim());
			actMean = Double.parseDouble(actMeanInput.getText().trim());
			pistonLength = Double.parseDouble(pistonLengthInput.getText().trim());
			LVDTlength = Double.parseDouble(LVDTlengthInput.getText().trim());
			LVDTposition = Double.parseDouble(LVDTpositionInput.getText().trim());
		} catch (Exception ex) {}
		
		// Piston Length change
		if (e.getActionCommand().equals("pistonLengthInput")) {						
			pistonLengthInput.setText(String.valueOf(pistonLength));
		}
		
		// Actuator Position change
		if (e.getActionCommand().equals("actPositionInput")) {
			actPositionInput.setText(String.valueOf(actPosition));			
		}
		
		// Actuator Mean change
		if (e.getActionCommand().equals("actMeanInput")) {
			actMeanInput.setText(String.valueOf(actMean));			
		}
		
		// LVDT Length change
		if (e.getActionCommand().equals("LVDTlengthInput")) {
			LVDTlengthInput.setText(String.valueOf(LVDTlength));			
		}
		
		// LVDT Position change
		if (e.getActionCommand().equals("LVDTpositionInput")) {
			LVDTpositionInput.setText(String.valueOf(LVDTposition));			
		}
		
		// DSP Converter
		if (e.getActionCommand().equals("commandValue")) {
			DSPValue.setText(String.valueOf((Double.parseDouble(commandValue.getText())/500)));
			return;
		}
		
		// Show LVDT Button
		if (e.getActionCommand().equals("showLVDTButton")) {
			if (showLVDTButton.isSelected()) {
				showLVDTButton.setText("External LVDT Enabled");
				LVDTlengthInput.setVisible(true);
				LVDTpositionInput.setVisible(true);
				LVDTextensionLimit.setVisible(true);
				LVDTretractionLimit.setVisible(true);
			}
			else {
				showLVDTButton.setText("External LVDT Disabled");
				LVDTlengthInput.setVisible(false);
				LVDTpositionInput.setVisible(false);
				LVDTextensionLimit.setVisible(false);
				LVDTretractionLimit.setVisible(false);
			}
		}
		
		// Check if actuator position within piston size, then max or min if out of range
    	if (actPosition+actMean > pistonLength/2) {
    		actPosition = pistonLength/2;
    		actMean = 0;
    	}
    	if (actPosition+actMean < -pistonLength/2) {
    		actPosition = -pistonLength/2;
    		actMean = 0;
    	}
    	actPositionInput.setText(String.valueOf(actPosition));
    	actMeanInput.setText(String.valueOf(actMean));
    	
    	// Normalize to pixel size for redraw of piston
    	int newPositionInt = (int)actPosition/ratio + (int)actMean/ratio;
    	pistonResize = newPositionInt; 
		
		// Change actuator stroke limits
    	double newExtensionStroke = -pistonLength/2-actPosition-actMean;
    	double newRetractionStroke = pistonLength/2-actPosition-actMean;	 
    	actPhysicalExtensionLimit.setText(String.valueOf(newExtensionStroke));
    	actPhysicalRetractionLimit.setText(String.valueOf(newRetractionStroke));
    	
    	// Change Actuator Displacement Limits wrt LVDT Range 
    	double lvdtExtensionLimit = -((LVDTlength/2) - LVDTposition)-actPosition-actMean;
    	double lvdtRetractionLimit = ((LVDTlength/2) + LVDTposition)-actPosition-actMean;
    	if (newRetractionStroke < lvdtRetractionLimit)
    		lvdtRetractionLimit = newRetractionStroke;
    	if (newExtensionStroke > lvdtExtensionLimit)
    		lvdtExtensionLimit = newExtensionStroke;
    	LVDTextensionLimit.setText(String.valueOf(lvdtExtensionLimit));
    	LVDTretractionLimit.setText(String.valueOf(lvdtRetractionLimit));
    	if (java.lang.Math.abs(lvdtExtensionLimit) > LVDTlength || java.lang.Math.abs(lvdtRetractionLimit) > LVDTlength) {
    		LVDTextensionLimit.setText("RANGE");
        	LVDTretractionLimit.setText("RANGE");
    	}
    	
    	
    	// Change Actuator Displacement Limits     	
    	//double actExtensionLimit = java.lang.Math.max(-pistonLength/2,lvdtExtensionLimit)-actPosition;
    	//double actRetractionLimit = java.lang.Math.min(pistonLength/2,lvdtRetractionLimit)-actPosition;
    	double actExtensionLimit = java.lang.Math.max(newExtensionStroke,lvdtExtensionLimit)+actMean;
    	double actRetractionLimit = java.lang.Math.min(newRetractionStroke,lvdtRetractionLimit)+actMean;
    	// Check for exceeding of piston size
    	if (actExtensionLimit < -pistonLength/2) {    		
    		actExtensionLimit = -pistonLength/2;
    	}
    	if (actRetractionLimit > pistonLength/2) {
    		actRetractionLimit = pistonLength/2;
    	}
    	actDispExtensionLimit.setText(String.valueOf(actExtensionLimit));
    	actDispRetractionLimit.setText(String.valueOf(actRetractionLimit));
    	// Check if out of range of LVDT
    	if (actRetractionLimit < 0 || -actExtensionLimit < 0) {    		
    		actDispExtensionLimit.setText("RANGE");
    		actDispRetractionLimit.setText("RANGE");
    	}    	    	
    	
    	// Change Command limits
    	double newExtensionCommand = actExtensionLimit - actMean;
    	double newRetractionCommand = actRetractionLimit - actMean;
    	if (newExtensionCommand < -pistonLength/2) {    		
    		newExtensionCommand = -pistonLength/2;
    	}
    	if (newRetractionCommand > pistonLength/2) {
    		newRetractionCommand = pistonLength/2;
    	}
    	actCmdExtensionLimit.setText(String.valueOf(newExtensionCommand));
    	actCmdRetractionLimit.setText(String.valueOf(newRetractionCommand));
    	actDSPExtensionLimit.setText(String.valueOf(newExtensionCommand/500));
    	actDSPRetractionLimit.setText(String.valueOf(newRetractionCommand/500));
    	if (actDispExtensionLimit.getText().equals("RANGE") && actDispRetractionLimit.getText().equals("RANGE")) {
    		actCmdExtensionLimit.setText("RANGE");
        	actCmdRetractionLimit.setText("RANGE");
        	actDSPExtensionLimit.setText("RANGE");
        	actDSPRetractionLimit.setText("RANGE");
    	}
			
		repaint();
	}
	
	
	
	public void openConfiguration() {
		// Open File
		String configFilename = FileHandler.getFilePath("Open Configuration", new TXTFileFilter());					
		if (configFilename == null) {
			return;
		}
		
		// Write properties file.
	    try {
	        properties.load(new FileInputStream(configFilename));
	        // Load properties
			pistonLength = Double.parseDouble(properties.getProperty("pistonLength"));
			actPosition = Double.parseDouble(properties.getProperty("actPosition"));
			actMean = Double.parseDouble(properties.getProperty("actMean"));		
			LVDTlength = Double.parseDouble(properties.getProperty("LVDTlength"));
			LVDTposition = Double.parseDouble(properties.getProperty("LVDTposition"));
			
			// Change inputs 								
			pistonLengthInput.setText(String.valueOf(pistonLength));
			actPositionInput.setText(String.valueOf(actPosition));			
			actMeanInput.setText(String.valueOf(actMean));			
			LVDTlengthInput.setText(String.valueOf(LVDTlength));			
			LVDTpositionInput.setText(String.valueOf(LVDTposition));		
			
			actionPerformed(new ActionEvent(this,ActionEvent.ACTION_PERFORMED,"Enter"));
			
			//repaint();
	    } catch (IOException e2) {}					    			    
	}
	
	public void saveConfiguration() {		
		// Open File
		String configFilename = FileHandler.getFilePath("Save Configuration", new TXTFileFilter());					
		if (configFilename == null) {
			return;
		}
				
		// Save properties
		properties.setProperty("pistonLength", String.valueOf(pistonLength));
		properties.setProperty("actPosition", String.valueOf(actPosition));
		properties.setProperty("actMean", String.valueOf(actMean));		
	    properties.setProperty("LVDTlength", String.valueOf(LVDTlength));
	    properties.setProperty("LVDTposition", String.valueOf(LVDTposition));
			
    	// Write properties file.
	    try {
	        properties.store(new FileOutputStream(configFilename), null);
	    } catch (IOException e2) {}	    	    
	}
		
 
    public static void main(String[] args) {
    	// Create new Graphics Panel
        LimitsGenerator lg = new LimitsGenerator();
        lg.setBounds(0,0,frameWidth,(int)(frameHeight*(1-panelPercentage)));
        
        // Set up UI Look and Feel
        try {                        
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {System.out.println("Error setting Look and Feel: " + e);}
        
        // Set up Frame container
        JFrame f = new JFrame("RTMD Limits Configurator");        
        f.setResizable(false);
        f.getContentPane().setLayout(null);        
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);        
        f.setJMenuBar(lg.generateMenuBar());
        f.add(lg);
        f.add(lg.getUIPanel());
        f.setSize(frameWidth,frameHeight);        
        f.setLocationRelativeTo(null);        
        f.setVisible(true);
        
    }	
}