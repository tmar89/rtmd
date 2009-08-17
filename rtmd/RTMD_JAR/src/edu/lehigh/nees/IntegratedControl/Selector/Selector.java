package edu.lehigh.nees.IntegratedControl.Selector;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import edu.lehigh.nees.scramnet.*;
import edu.lehigh.nees.xml.*;
import edu.lehigh.nees.util.*;

/********************************* 
 * Selector
 * <p>
 * This program is the overall front end for a user to run an RTMD program
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 16 Aug 05  T. Marullo  Initial
 * 22 Apr 09  T. Marullo  Rework
 * 
 * TODO:
 * Rework ICC
 * 
 ********************************/
public class Selector extends JFrame implements ActionListener, Runnable {
	/** Variables **/
	protected static final long serialVersionUID = -9141480871166287530L;
	// SCRAMNet
	protected ScramNetIO scr = new ScramNetIO();
	protected static final int simBitLocation = 0;		// SCRAMNet location
	protected static final int pauseBitLocation = 62;	// SCRAMNet location
	protected static final int stopBitLocation = 60;	// SCRAMNet location
	// Buttons	
	protected JButton clearSCRButton;
	protected JButton rampZeroButton;
	protected JButton simbitButton;
	protected JButton pausebitButton;
	protected JButton stopbitButton;
	protected JButton iccButton;
	protected JButton autoxPCConvertButton;
	protected JButton xpcConvertButton;
	protected JButton csvDecimatorButton;
	protected JButton dapcsvConvertButton;
	protected JButton dapButton;	

	// Panels
	protected SCRAMNetSpy spyPanel;
    // Icons
    protected ImageIcon clearSCRIcon = new ImageIcon("/RTMD/res/clearSCR.png");
    protected ImageIcon rampZeroIcon = new ImageIcon("/RTMD/res/rampZero.png");
    protected ImageIcon simbitOffIcon = new ImageIcon("/RTMD/res/simButtonOff.png");
    protected ImageIcon simbitOnIcon = new ImageIcon("/RTMD/res/simButtonOn.png");
    protected ImageIcon pausebitOffIcon = new ImageIcon("/RTMD/res/pauseButtonOff.png");
    protected ImageIcon pausebitOnIcon = new ImageIcon("/RTMD/res/pauseButtonOn.png");
    protected ImageIcon stopbitOffIcon = new ImageIcon("/RTMD/res/stopButtonOff.png");
    protected ImageIcon stopbitOnIcon = new ImageIcon("/RTMD/res/stopButtonOn.png");
    protected ImageIcon iccIcon = new ImageIcon("/RTMD/res/icc.png");    
    protected ImageIcon autoxPCIcon = new ImageIcon("/RTMD/res/autoxpc.png");
    protected ImageIcon xPCIcon = new ImageIcon("/RTMD/res/xpcconv.png");
    protected ImageIcon csvIcon = new ImageIcon("/RTMD/res/csv.png");
    protected ImageIcon csvdapIcon = new ImageIcon("/RTMD/res/csvdap.png");    
    protected ImageIcon dapIcon = new ImageIcon("/RTMD/res/dap.png");
    // Ramp to Zero Frame
    protected CommandZero commandZeroFrame = new CommandZero();
    // Integrated Control Configurator Frame
    protected XMLGenerator iccFrame = new XMLGenerator();
    // Thread for background task
    protected Thread backgroundThread;
    
    /** Creates a new instance of Selector */
    public Selector() {
    	// Set some JFrame propertie
        super("RTMD Simulation and Control Toolbar");                
        this.setResizable(false);                
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        	
        // Set up UI Look and Feel
        try {            
        	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {System.out.println("Error setting Look and Feel: " + e);}
        init();
    }
    
    /** GUI init process */
    private void init() {
    	// Set layout to Flow Layout
        this.getContentPane().setLayout(new FlowLayout());        
        
        // Initialize the SCRAMNet
        scr.initScramnet();
        
        // Generate Buttons
        // Clear SCRAMNet
        clearSCRButton = new JButton(clearSCRIcon);
        clearSCRButton.setToolTipText("Clear SCRAMNet");
        clearSCRButton.setBorder(null);
        clearSCRButton.setContentAreaFilled(false);
        clearSCRButton.setFocusPainted(false);
        clearSCRButton.addActionListener(this);
        
        // Ramp Zero
        rampZeroButton = new JButton(rampZeroIcon);
        rampZeroButton.setToolTipText("Ramp SCRAMNet Commands to Zero");
        rampZeroButton.setBorder(null);
        rampZeroButton.setContentAreaFilled(false);
        rampZeroButton.setFocusPainted(false);
        rampZeroButton.addActionListener(this);        
        commandZeroFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // Simulation Running Bit Button
        simbitButton = new JButton(simbitOffIcon);
        simbitButton.setToolTipText("Enable or Disable the Simulation Running SCRAMNet Bit");
        simbitButton.setBorder(null);
        simbitButton.setContentAreaFilled(false);
        simbitButton.setFocusPainted(false);
        simbitButton.addActionListener(this);
        
        // Pause Bit Button
        pausebitButton = new JButton(pausebitOffIcon);
        pausebitButton.setToolTipText("Enable or Disable the Pause SCRAMNet Bit");
        pausebitButton.setBorder(null);
        pausebitButton.setContentAreaFilled(false);
        pausebitButton.setFocusPainted(false);
        pausebitButton.addActionListener(this);
        
        // Stop Bit Button
        stopbitButton = new JButton(stopbitOffIcon);
        stopbitButton.setToolTipText("Enable or Disable the Stop Simulation SCRAMNet Bit");
        stopbitButton.setBorder(null);
        stopbitButton.setContentAreaFilled(false);
        stopbitButton.setFocusPainted(false);
        stopbitButton.addActionListener(this);
        
        // Spy Panel
        spyPanel = new SCRAMNetSpy(scr);     
        spyPanel.setBorder(null);
        spyPanel.setBackground(null);
        
        // Integrated Control Configuration Button
        iccButton = new JButton(iccIcon);
        iccButton.setToolTipText("RTMD Integrated Control Configurator");
        iccButton.setBorder(null);
        iccButton.setContentAreaFilled(false);
        iccButton.setFocusPainted(false);
        iccButton.addActionListener(this);
        iccFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        
        // Auto xPC Convert Drop Panel
        autoxPCConvertButton = new JButton(autoxPCIcon);
        autoxPCConvertButton.setToolTipText("Auto xPC Data Converter");        
        autoxPCConvertButton.setBorder(null);
        autoxPCConvertButton.setContentAreaFilled(false);
        autoxPCConvertButton.setFocusPainted(false);                	
        autoxPCConvertButton.addActionListener(this);       
        new FileDrop( null, autoxPCConvertButton, new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   
        		if (files.length > 0) {
        			if (files[0].getName().contains(".xml")) {
        				new xPCToCSVtoDAPHandler(files[0]);
        			}
        			else
        				javax.swing.JOptionPane.showMessageDialog(null, "Must be the xPC XML file for the binary data format");
        		}
        		else 
        			javax.swing.JOptionPane.showMessageDialog(null, "Must be the xPC XML file for the binary data format");   		
            }   
        }); 
        
        // xPC Convert Button
        xpcConvertButton = new JButton(xPCIcon);
        xpcConvertButton.setToolTipText("Convert xPC Binary Data to Text Data");
        xpcConvertButton.setBorder(null);
        xpcConvertButton.setContentAreaFilled(false);
        xpcConvertButton.setFocusPainted(false);
        xpcConvertButton.addActionListener(this);
        
        // csv Decimator
        csvDecimatorButton = new JButton(csvIcon);
        csvDecimatorButton.setToolTipText("Decimate CSV file");
        csvDecimatorButton.setBorder(null);
        csvDecimatorButton.setContentAreaFilled(false);
        csvDecimatorButton.setFocusPainted(false);
        csvDecimatorButton.addActionListener(this);
        
        // csv - dap Convert Button
        dapcsvConvertButton = new JButton(csvdapIcon);
        dapcsvConvertButton.setToolTipText("Convert CSV->DAP or DAP->CSV");
        dapcsvConvertButton.setBorder(null);
        dapcsvConvertButton.setContentAreaFilled(false);
        dapcsvConvertButton.setFocusPainted(false);
        dapcsvConvertButton.addActionListener(this);  
        
        // dap Button
        dapButton = new JButton(dapIcon);
        dapButton.setToolTipText("Start Data Analysis Package or drag DAP file(s) here");
        dapButton.setBorder(null);
        dapButton.setContentAreaFilled(false);
        dapButton.setFocusPainted(false);
        dapButton.addActionListener(this); 
        new FileDrop( null, dapButton, new FileDrop.Listener()
        {   public void filesDropped( java.io.File[] files )
            {   
        		for (int i = 0; i < files.length; i++) {        			 		
       				try { Runtime.getRuntime().exec("C:\\Dataan~1\\sDap.exe \"" + files[i] + "\""); }       				
       	            catch (Exception ex) {ex.printStackTrace();}
        		}   	
            }   
        }); 
        
        // Add items to toolbar
        this.getContentPane().add(clearSCRButton);
        this.getContentPane().add(rampZeroButton);
        this.getContentPane().add(simbitButton); 
        this.getContentPane().add(pausebitButton); 
        this.getContentPane().add(stopbitButton);   
        this.getContentPane().add(spyPanel);
        this.getContentPane().add(iccButton);
        this.getContentPane().add(autoxPCConvertButton);
        this.getContentPane().add(xpcConvertButton);
        this.getContentPane().add(csvDecimatorButton);
        this.getContentPane().add(dapcsvConvertButton);
        this.getContentPane().add(dapButton); 
        
        // Start the background Thread process
        backgroundThread = new Thread(this);
        backgroundThread.start();
        
        pack();
    }
    
    /** Button capture **/
    public void actionPerformed(ActionEvent evt) {
    	Object source = evt.getSource();
    	
    	// Clear SCRAMNet 
    	if (source == clearSCRButton) {
    		// Confirm
    		int response = javax.swing.JOptionPane.showConfirmDialog(this, "Are you sure you want to clear the SCRAMNet Memory?");
    		if (response == javax.swing.JOptionPane.YES_OPTION) {
	    		// Clear every address
	            for (int i = 0 ; i <= 2097151; i++)
	            	scr.writeInt(i,0);
    		}
    	}
    	// Ramp to Zero program
    	else if (source == rampZeroButton) {
    		if (commandZeroFrame.isVisible())
    			commandZeroFrame.setVisible(false);
    		else
    			commandZeroFrame.setVisible(true);
    	}
    	// Sim Bit Button
    	else if (source == simbitButton) {
    		if (scr.readFloat(simBitLocation) == 1.0) {
    			scr.writeFloat(simBitLocation,0.0F);
    			simbitButton.setIcon(simbitOffIcon);
    		}
    		else {
    			scr.writeFloat(simBitLocation,1.0F);
    			simbitButton.setIcon(simbitOnIcon);
    		}       		    		    	   
    	}
    	// Pause Bit Button
    	else if (source == pausebitButton) {
    		if (scr.readFloat(pauseBitLocation) == 1.0) {
    			scr.writeFloat(pauseBitLocation,0.0F);
    			pausebitButton.setIcon(pausebitOffIcon);
    		}
    		else {
    			scr.writeFloat(pauseBitLocation,1.0F);
    			pausebitButton.setIcon(pausebitOnIcon);
    		}    		
    	}
    	// Stop Bit Button
    	else if (source == stopbitButton) {
    		if (scr.readFloat(stopBitLocation) == 1.0) {
    			scr.writeFloat(stopBitLocation,0.0F);
    			stopbitButton.setIcon(stopbitOffIcon);
    		}
    		else {
    			scr.writeFloat(stopBitLocation,1.0F);
    			stopbitButton.setIcon(stopbitOnIcon);
    		}    		
    	}   
    	// ICC Button
    	else if (source == iccButton) {
    		if (iccFrame.isVisible())
    			iccFrame.setVisible(false);
    		else
    			iccFrame.setVisible(true);
    	}  
    	
    	// Auto xPC Button
    	else if (source == autoxPCConvertButton) {
    		javax.swing.JOptionPane.showMessageDialog(null, "Drag an xPC XML file here to convert the Binary data into Text");
    	}  
    	
    	// xPC Convert Button
    	else if (source == xpcConvertButton) {
    		new xPCDataConverter();
    	}
    	
    	// csv Decimator
    	else if (source == csvDecimatorButton) {
       		new CSVDecimator();
    	} 
    	
    	// csv - dap Converter
    	else if (source == dapcsvConvertButton) {
    		new DAPCSVConverter();
    	} 
    	
    	// dap Button
    	else if (source == dapButton) {
    		try { Runtime.getRuntime().exec("C:\\Data Analysis Package\\sDAP.exe"); }
            catch (Exception ex) {ex.printStackTrace();}
    	} 
	}   
    
    /** Timer for performing background tasks **/
	public void run() {
		while (true) {
			// Set the Sim Bit Icon
			if (scr.readFloat(simBitLocation) == 1.0)
				simbitButton.setIcon(simbitOnIcon);
			else
				simbitButton.setIcon(simbitOffIcon);
			// Set the Pause Bit Icon
			if (scr.readFloat(pauseBitLocation) == 1.0)
				pausebitButton.setIcon(pausebitOnIcon);
			else
				pausebitButton.setIcon(pausebitOffIcon);
			// Set the Stop Bit Icon
			if (scr.readFloat(stopBitLocation) == 1.0)
				stopbitButton.setIcon(stopbitOnIcon);
			else
				stopbitButton.setIcon(stopbitOffIcon);
			
			// Update the Spy
			spyPanel.updateValues();
			
			// Sleep for 0.1 seconds
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
	/** Main GUI Start **/
    public static void main(String[] args) {
        Selector selector = new Selector();
        selector.setVisible(true);            
    }
}

