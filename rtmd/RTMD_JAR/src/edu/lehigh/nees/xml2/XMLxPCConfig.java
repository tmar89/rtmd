package edu.lehigh.nees.xml2;

/********************************* 
 * XMLRTMDConfig
 * <p>
 * SCRAMNet RTMD XML configuration utility
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 19 Oct 10  T. Marullo  Initial 
 * 
 ********************************/
public class XMLxPCConfig {    
    private int numChannels;      
    private xPCConfig[] config;      
    
    /** Creates a new instance of XMLRTMDConfig with the number of channels */
    public XMLxPCConfig(int _numChannels) {
    	// Sets number of channels
    	numChannels = _numChannels;
    	config = new xPCConfig[numChannels];
        for (int i = 0; i < numChannels; i++)
        	config[i] = new xPCConfig();
    }            
    
    // Getters and Setters for each Channel attribute        
    /** Get number of channels */
    public int getNumChannels() {
        return numChannels;
    }   
           
    /** Set Location */
    public void setLocation(int channelID, String s) {
    	config[channelID].Location = s;
    }
    /** Get Location */
    public String getLocation(int channelID) {
        return config[channelID].Location;
    }       
    
    /** Set Name */
    public void setName(int channelID, String s) {
    	config[channelID].Name = s;
    }
    /** Get Name */
    public String getName(int channelID) {
        return config[channelID].Name;
    }
    
    /** Set Units */
    public void setUnits(int channelID, String s) {
    	config[channelID].Units = s;
    }
    /** Get Units */
    public String getUnits(int channelID) {
        return config[channelID].Units;
    }        
    
    /** Set Gain */
    public void setGain(int channelID, String s) {
    	config[channelID].Gain = s;
    }
    /** Get Gain */
    public String getGain(int channelID) {
        return config[channelID].Gain;
    }
    
    /** Set isDAQ */
    public void setisDAQ(int channelID, String s) {
    	config[channelID].isDAQ = s;
    }
    /** Get isDAQ */
    public String getisDAQ(int channelID) {
        return config[channelID].isDAQ;
    }
    
    /** Set VoltageSlope */
    public void setVoltageSlope(int channelID, String s) {
    	config[channelID].VoltageSlope = s;
    }
    /** Get VoltageSlope */
    public String getVoltageSlope(int channelID) {
        return config[channelID].VoltageSlope;
    }
    
    /** Set VoltageOffset */
    public void setVoltageOffset(int channelID, String s) {
    	config[channelID].VoltageOffset = s;
    }
    /** Get VoltageOffset */
    public String getVoltageOffset(int channelID) {
        return config[channelID].VoltageOffset;
    }
    
    /** Set EUSlope */
    public void setEUSlope(int channelID, String s) {
    	config[channelID].EUSlope = s;
    }
    /** Get EUSlope */
    public String getEUSlope(int channelID) {
        return config[channelID].EUSlope;
    }
    
    /** Set EUOffset */
    public void setEUOffset(int channelID, String s) {
    	config[channelID].EUOffset = s;
    }
    /** Get EUOffset */
    public String getEUOffset(int channelID) {
        return config[channelID].EUOffset;
    }                     
    
    
    /** Print config */
    public void print() {        
        for (int i = 0; i < numChannels; i++) {
        	if (config[i].isDAQ.equals("True")) {
	        	System.out.println(	"Location:" + config[i].Location + " " +	        						
	        						"Name:" + config[i].Name + " " +
	        						"Units:" + config[i].Units + " " +	        						
	        						"Gain:" + config[i].Gain + " " +
	        						"isDAQ:" + config[i].isDAQ + " " +
	        						"VoltageSlope:" + config[i].VoltageSlope + " " +
	        						"VoltageOffset:" + config[i].VoltageOffset + " " +
	        						"EUSlope:" + config[i].EUSlope + " " +
	        						"EUOffset:" + config[i].EUOffset);
        	}
        	else {
        		System.out.println(	"Location:" + config[i].Location + " " +						
						"Name:" + config[i].Name + " " +
						"Units:" + config[i].Units + " " +						
						"Gain:" + config[i].Gain + " " +
						"isDAQ:" + config[i].isDAQ);
        	}
        }
    }
    
}

/** SCRAMNet configuration inner class  */
class xPCConfig {
    public String Location;        
    public String Name;
    public String Units;
    public String Gain;
    public String isDAQ;
    public String VoltageSlope;
    public String VoltageOffset;
    public String EUSlope;
    public String EUOffset;    
    
    public xPCConfig() {
    }
}



