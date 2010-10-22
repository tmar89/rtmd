package org.nees.rbnb;

import java.io.File;
import java.lang.InterruptedException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import edu.lehigh.nees.xml.ReadxPCXMLConfig;
import edu.lehigh.nees.xml.XMLxPCConfig;
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
 * 
 ********************************/
public class SCRAMNetSourceOld extends RBNBBase {
	
	private final static String DEFAULT_XML_PATH = "turbine.xml";
	private String xmlPath = DEFAULT_XML_PATH;
	private final static String DEFAULT_RBNB_SOURCE = "RTMD";
	private String rbnbSourceName = DEFAULT_RBNB_SOURCE;
	private String[] rbnbChannelNames;
	
	private static final int DEFAULT_CACHE_SIZE = 900;
	private int cacheSize = DEFAULT_CACHE_SIZE;
	private static final int DEFAULT_ARCHIVE_SIZE = 0;
	private int archiveSize = DEFAULT_ARCHIVE_SIZE;
	private static final int DEFAULT_DELAY = 100; // Default to 100 ms between flushes 
	private int delay = DEFAULT_DELAY; 
	
	Source source = null;
	boolean connected = false;
	Thread timerThread;
	boolean runit = false;
	private final static long RETRY_INTERVAL = 1000;
	
	XMLxPCConfig xml = null;
		
	/** Constructor that starts the thread **/
	public SCRAMNetSourceOld () {
       Runtime.getRuntime ().addShutdownHook (new Thread () {
          public void run () {
             try {
                disconnect ();
                System.out.println ("Shutdown hook for " + SCRAMNetSourceOld.class.getName ());
             } catch (Exception e) {
                System.out.println ("Unexpected error closing " + SCRAMNetSourceOld.class.getName ());
             }
          } // run ()
       }); //?addHook
    } // constructor
	
	public static void main(String[] args) {
		// start from command line
		SCRAMNetSourceOld s = new SCRAMNetSourceOld();
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
				+ "\n RBNB Cache Size = " + cacheSize
				+ "\n RBNB Archive Size = " + archiveSize
				+ "\n RBNB Source name = " + rbnbSourceName				
				+ "\n Number of channels = " + rbnbChannelNames.length
				+ "\n Delay between frames = " + ((float)delay/1000F) + "s"
				+ "\n RBNB Frame Size = " + (rbnbChannelNames.length * 4 * 1000/delay) + " bytes");
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
		opt.addOption("p",true,"delay *" + DEFAULT_DELAY);
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
		if (cmd.hasOption('p')) {
			String a=cmd.getOptionValue('p');
			try
			{
				Integer i =  new Integer(a);
				int value = i.intValue();
				delay = value;
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
		ReadxPCXMLConfig xmlfile = new ReadxPCXMLConfig(new File(xmlPath));
		xml = xmlfile.getxPCConfig();
		
		// Get the number of Channels
		rbnbChannelNames = new String[xml.getnumxPCReadBlocks()];
		for (int i = 0; i < rbnbChannelNames.length; i++)
			rbnbChannelNames[i] = xml.getxPCReadName(i) + " " + xml.getxPCReadUnits(i);			
				
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
			// Read SCRAMNet channel depending on if its DAQ or other
			for (int i = 0; i < rbnbChannelNames.length; i++) {			
				float scrdata[] = new float[1];
				
				if (xml.getxPCReadisDAQ(i).equals("true")) {
					scrdata[0] = (float)scr.readDAQ(
										xml.getxPCReadLocation(i),
										xml.getxPCReadGain(i),
										xml.getxPCReadVoffset(i),
										xml.getxPCReadVslope(i),
										xml.getxPCReadEUoffset(i),
										xml.getxPCReadEUslope(i));								
				}
				else {
					scrdata[0] = scr.readFloat(
										Integer.parseInt(xml.getxPCReadLocation(i)))*
										(float)xml.getxPCReadGain(i);				
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
			
			// Flush output
			try {				
				source.Flush(cmap, true);					
			} catch (SAPIException e) {			  
				e.printStackTrace();
				System.err.println("Failed to flush channel output data to server.");
				scr.unmapScramnet();				
				return false;
			}
		
					
			try {
				// Delay between
				Thread.sleep(delay);
			} catch (InterruptedException e) {	
			}
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