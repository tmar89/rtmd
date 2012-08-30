package org.nees.rbnb;

import java.io.File;
import java.lang.InterruptedException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import edu.lehigh.nees.xml2.ReadRTMDXMLConfig;
import edu.lehigh.nees.xml2.XMLRTMDConfig;
import edu.lehigh.nees.scramnet.ScramNetIO;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import com.rbnb.sapi.ChannelMap;
import com.rbnb.sapi.SAPIException;
import com.rbnb.sapi.Source;

/********************************* 
 * SCRAMNetSource
 * <p>
 * Data Turbine driver for SCRAMNet
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 16 Nov 06  T. Marullo  Initial 
 *  6 Apr 07  T. Marullo  Added units to channel names
 * 17 Aug 09  T. Marullo  Changed gain from int to float 
 * 19 Oct 10  T. Marullo  Using new xml2 services
 *  5 Dec 10  T. Marullo  Running 10 channel map pushes for every 1 frame push to improve performance
 * 15 Mar 11  T. Marullo  Updated metadata stuff 
 ********************************/
public class SCRAMNetSource extends RBNBBase {
	
	private final static String DEFAULT_XML_PATH = "turbine.xml";
	private String xmlPath = DEFAULT_XML_PATH;
	private final static String DEFAULT_RBNB_SOURCE = "RTMD";
	private String rbnbSourceName = DEFAULT_RBNB_SOURCE;
	private String[] rbnbChannelNames;
	private int rbnbChannelIndex[];
	
	private static final int DEFAULT_CACHE_SIZE = 900;
	private int cacheSize = DEFAULT_CACHE_SIZE;
	private static final int DEFAULT_ARCHIVE_SIZE = 0;
	private int archiveSize = DEFAULT_ARCHIVE_SIZE;
	private static final int DEFAULT_DELAY = 500; // Default to 500 ms between flushes 	
	private int delay = DEFAULT_DELAY; 
	private static final int DEFAULT_PUSHES = 5; // Default to 5 pushes per flush
	private int pushes = DEFAULT_PUSHES;
	
	Source source = null;
	boolean connected = false;
	Thread timerThread;
	boolean runit = false;
	private final static long RETRY_INTERVAL = 1000;
	
	XMLRTMDConfig xml = null;
		
	/** Constructor that starts the thread **/
	public SCRAMNetSource () {
       Runtime.getRuntime ().addShutdownHook (new Thread () {
          public void run () {
             try {
                disconnect ();
                System.out.println ("Shutdown hook for " + SCRAMNetSource.class.getName ());
             } catch (Exception e) {
                System.out.println ("Unexpected error closing " + SCRAMNetSource.class.getName ());
             }
          } // run ()
       }); //?addHook
    } // constructor
	
	public static void main(String[] args) {
		// start from command line
		SCRAMNetSource s = new SCRAMNetSource();
		if (s.parseArgs(args))
		{
			s.connect();
			s.startThread();
		}
	}
	
	
	/** Connect to the RBNB **/
	public void connect()
	{
		if (connected) return;
		try {
			// Create a source and connect:
			if (archiveSize > 0)
				source=new Source(cacheSize, "append", archiveSize);
			else
				source=new Source(cacheSize, "append", 0);
			System.out.println (getServer());
			//source.OpenRBNBConnection(getServer(),rbnbSourceName);
			source.OpenRBNBConnection("128.180.53.5:3333",rbnbSourceName);
			connected = true;
			System.out.println("Connecting to SCRAMNetSource with..."			
				+ "\n RBNB Server = " + getServer()
				+ "\n RBNB Source name = " + rbnbSourceName				
				+ "\n RBNB Number of channels = " + rbnbChannelNames.length
				+ "\n RBNB Frame Size = " + (rbnbChannelNames.length * 4 * pushes) + " Bytes"				
				+ "\n RBNB Sample rate = " + (1/((float)delay/(1000F * pushes))) + "Hz"
				+ "\n RBNB Flush rate = " + (1/((float)delay/1000F)) + "Hz"
				+ "\n RBNB Cache Size = " + cacheSize + " Frames (" + cacheSize*(rbnbChannelNames.length * 4 * pushes)/1000 + " KB)"			
				+ "\n RBNB Archive Size = " + archiveSize + " Frames (" + archiveSize*(rbnbChannelNames.length * 4 * pushes)/1000 + " KB)"
				+ "\n RBNB Cache Time = " + cacheSize/(1/((float)delay/1000F))/60 + " minutes"
				+ "\n RBNB Archive Time = " + archiveSize/(1/((float)delay/1000F))/60 + " minutes"
			);
		} catch (SAPIException se) { se.printStackTrace(); }
	}
	
	/** Connect from the RBNB **/
	private void disconnect() {
      if ( (cacheSize != 0 || archiveSize != 0) && source != null ) {
         source.Detach (); // close and keep cache and archive
      } else if (source != null) { // they are both zero; close and scrap
         source.CloseRBNBConnection();
      } else {}
      source = null;
		connected = false;
	}
	
	/** Command line options */
	protected Options setOptions() {
		Options opt = setBaseOptions(new Options()); 
		opt.addOption("N",true,"XML file *" + DEFAULT_XML_PATH);		
		opt.addOption("S",true,"Source name *" + DEFAULT_RBNB_SOURCE);
		opt.addOption("z",true,"cache size *" + DEFAULT_CACHE_SIZE);
		opt.addOption("Z",true,"archive size *" + DEFAULT_ARCHIVE_SIZE);
		opt.addOption("f",true,"delay *" + DEFAULT_DELAY);
		opt.addOption("p",true,"delay *" + DEFAULT_PUSHES);
		return opt;
	}
	
	/** Command line arguments */
	protected boolean setArgs(CommandLine cmd) {
		if (cmd.hasOption('N')) {
			String a=cmd.getOptionValue('N');
			if (a!=null) xmlPath=a;
		}
		if (cmd.hasOption('S')) {
			String a=cmd.getOptionValue('S');
			if (a!=null) rbnbSourceName=a;
		}
		if (cmd.hasOption('f')) {
			String a=cmd.getOptionValue('f');
			try
			{
				Integer i =  new Integer(a);
				int value = i.intValue();
				delay = value;
			}
			catch (Exception ignore) {} 
		}
		if (cmd.hasOption('p')) {
			String a=cmd.getOptionValue('p');
			try
			{
				Integer i =  new Integer(a);
				int value = i.intValue();
				pushes = value;
			}
			catch (Exception ignore) {} 
		}
		if (cmd.hasOption('z')) {
			String a=cmd.getOptionValue('z');
			if (a!=null)
			try
			{
				Integer i =  new Integer(a);
				int value = i.intValue();
				cacheSize = value;
			}
			catch (Exception ignore) {} 
		}
		if (cmd.hasOption('Z')) {
			String a=cmd.getOptionValue('Z');
			if (a!=null)
			try
			{
				Integer i =  new Integer(a);
				int value = i.intValue();
				archiveSize = value;
			}
			catch (Exception ignore) {} 
		}
      
       if ((archiveSize > 0) && (archiveSize < cacheSize)){
            System.err.println(
                "a non-zero archiveSize = " + archiveSize + " must be greater then " +
                    "or equal to cacheSize = " + cacheSize);
            return false;
       }
       
       // Parse the XML file here and get the rbnbSourceName and rbnbChannelNames
       if (getSCRAMNetChannels() == false) {
    	   return false;
       }
       
       System.out.println("\nStarting SCRAMNetSource");
			   	
	return true;
	}
	
	/** Get all the SCRAMNet channels from the xPC formatted file */
	protected boolean getSCRAMNetChannels() {
		ReadRTMDXMLConfig xmlfile = new ReadRTMDXMLConfig(new File(xmlPath));
		xml = xmlfile.getRTMDConfig();
		
		// Get the number of Channels and gather the indicies where stream is true
		int rbnbNumChannels = 0;
		for (int i = 0; i < xml.getNumChannels(); i++) {			
			if (xml.getStream(i).equals("True")) {				
				rbnbNumChannels++;				
			}
		}
		
		// Create the Channels
		rbnbChannelIndex = new int[rbnbNumChannels];
		rbnbChannelNames = new String[rbnbNumChannels];
		int j = 0;
		for (int i = 0; i < xml.getNumChannels(); i++)
			if (xml.getStream(i).equals("True")) {				
				rbnbChannelIndex[j] = i;				
				rbnbChannelNames[j] = xml.getName(i).replace('/', '\\') + " (" + xml.getUnits(i).replace('/', '\\') + ")";
				j++;
			}		

		// Get all the channels				
		//rbnbChannelNames = new String[rbnbChannelIndex.length];
		//for (int i = 0; i < rbnbChannelNames.length; i++)
		//	rbnbChannelNames[i] = xml.getName(i) + " " + xml.getUnits(i);			
				
		return true;
	}

	protected String getCVSVersionString() {		
		return (
			"$LastChangedDate$\n" +
	        "$LastChangedRevision$" +
	        "$LastChangedBy$" +
	        "$HeadURL$"
	        );
    }
	
	/** Start the main thread */
	public void startThread()
	{	
		if (!connected) return;
		
		// Use this inner class to hide the public run method
		Runnable r = new Runnable() {
			public void run() {
			  runWork();
			}
		};
		runit = true;
		timerThread = new Thread(r, "Timer");
		timerThread.start();
		System.out.println("SCRAMNetSource: Started thread");
	}

	/** Stop the main thread */
	public void stopThread()
	{
		if (!connected) return;
		
		runit = false;
		timerThread.interrupt();
		System.out.println("SCRAMNetSource: Stopped thread.");
	}

	/** Perform the thread work */
	public void runWork() {
				
		boolean done = false;
		while (!done) {
			done = execute();					
			try {Thread.sleep(RETRY_INTERVAL); } catch (Exception e) {}
		}
		stop();		
	}
	
	/** Main data push from SCRAMNet */
	private boolean execute()
	{
		if (!connected) return false;
		
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Connect to SCRAMNet
        ScramNetIO scr = new ScramNetIO();
        scr.initScramnet();
        
        // Create a new Channel map
		ChannelMap cmap = new ChannelMap();
		
		// Gather all the channels
		int channelId[] = new int[rbnbChannelNames.length];
		for (int i = 0; i < rbnbChannelNames.length; i++) {
			try {
				// add the channels
				channelId[i] = cmap.Add(rbnbChannelNames[i]);
			} catch (SAPIException e) {
				System.err.println("Failed to add SCRAMNet channel to channel map; name = "
					+ rbnbChannelNames[i]);
				disconnect();
				return false;
			}
	
			try {
				cmap.PutUserInfo(channelId[i], 
					"Channel_Name=" + rbnbChannelNames[i]);				
			} catch (SAPIException e) {
				System.err.println("Failed to register SCRAMnet channel metadata.");
			}	
		}
		
		try {			
			source.Register(cmap);
		} catch (SAPIException e) {
			System.err.println("Failed to register SCRAMnet channel metadata.");
		}
	       										
		

		while(true) {			
			for (int k = 0; k < pushes; k++) {
				// Read SCRAMNet channel depending on if its DAQ or other
				for (int i = 0; i < rbnbChannelNames.length; i++) {			
					float scrdata[] = new float[1];
					
					if (xml.getisDAQ(rbnbChannelIndex[i]).equals("True")) {
						scrdata[0] = (float)scr.readDAQ(
												xml.getLocation(rbnbChannelIndex[i]),
												Double.parseDouble(xml.getGain(rbnbChannelIndex[i])),
												Double.parseDouble(xml.getVoltageOffset(rbnbChannelIndex[i])),
												Double.parseDouble(xml.getVoltageSlope(rbnbChannelIndex[i])),
												Double.parseDouble(xml.getEUOffset(rbnbChannelIndex[i])),
												Double.parseDouble(xml.getEUSlope(rbnbChannelIndex[i])));								
					}
					else {
						scrdata[0] = scr.readFloat(
											Integer.parseInt(xml.getLocation(rbnbChannelIndex[i])))*
											Float.parseFloat(xml.getGain(rbnbChannelIndex[i]));				
					}
				
					// Push data to turbine
					try {										
						cmap.PutTimeAuto("timeofday");		
						cmap.PutDataAsFloat32(channelId[i], scrdata);					
					} catch (SAPIException e) {					
						System.err.println("Failed to put SCRAMNet data into channel map.");
						scr.unmapScramnet();				
						return false;
					}
				}
				
				try {
					// Delay between each data load
					Thread.sleep(delay/pushes);
				} catch (InterruptedException e) {	}
			}
			
			// Flush output after every 5 loads
			try {				
				source.Flush(cmap, true);					
			} catch (SAPIException e) {			  
				e.printStackTrace();
				System.err.println("Failed to flush channel output data to server.");
				scr.unmapScramnet();				
				return false;
			}
		
					
			//try {
			//	// Delay between
			//	Thread.sleep(delay);
			//} catch (InterruptedException e) {	
			//}
		}							
	}
	
	/** Check if the thread is running */
	public boolean isRunning()
	{
		return (connected && runit);
	}
	
	/** Start the thread */
	public boolean start() {
		if (isRunning()) return false;
		if (connected) disconnect();
		connect();
		if (!connected) return false;
		startThread();
		return true;
	}
	
	/** Stop the thread */
	public boolean stop() {
		if (!isRunning()) return false;
		stopThread();
		disconnect();
		return true;
	}
	 	
}