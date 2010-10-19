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
public class XMLRTMDConfig {    
    private int numChannels;      
    private SCRAMNetRTMDConfig[] config;      
    
    /** Creates a new instance of XMLRTMDConfig with the number of channels */
    public XMLRTMDConfig(int _numChannels) {
    	// Sets number of channels
    	numChannels = _numChannels;
    	config = new SCRAMNetRTMDConfig[numChannels];
        for (int i = 0; i < numChannels; i++)
        	config[i] = new SCRAMNetRTMDConfig();
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
    
    /** Set Type */
    public void setType(int channelID, String s) {
    	config[channelID].Type = s;
    }
    /** Get Type */
    public String getType(int channelID) {
        return config[channelID].Type;
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
    
    /** Set Stream */
    public void setStream(int channelID, String s) {
    	config[channelID].Stream = s;
    }
    /** Get Stream */
    public String getStream(int channelID) {
        return config[channelID].Stream;
    }
    
    /** Set Record */
    public void setRecord(int channelID, String s) {
    	config[channelID].Record = s;
    }
    /** Get Record */
    public String getRecord(int channelID) {
        return config[channelID].Record;
    }
    
    /** Set ReadWrite */
    public void setReadWrite(int channelID, String s) {
    	config[channelID].ReadWrite = s;
    }
    /** Get ReadWrite */
    public String getReadWrite(int channelID) {
        return config[channelID].ReadWrite;
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
    
    /** Set GaugeType */
    public void setGaugeType(int channelID, String s) {
    	config[channelID].GaugeType = s;
    }
    /** Get GaugeType */
    public String getGaugeType(int channelID) {
        return config[channelID].GaugeType;
    }                 
    
    
    /** Print config */
    public void print() {        
        for (int i = 0; i < numChannels; i++) {
        	if (config[i].isDAQ.equals("True")) {
	        	System.out.println(	"Location:" + config[i].Location + " " +
	        						"Type:" + config[i].Type + " " +
	        						"Name:" + config[i].Name + " " +
	        						"Units:" + config[i].Units + " " +
	        						"Stream:" + config[i].Stream + " " +
	        						"Record:" + config[i].Record + " " +
	        						"ReadWrite:" + config[i].ReadWrite + " " +
	        						"Gain:" + config[i].Gain + " " +
	        						"isDAQ:" + config[i].isDAQ + " " +
	        						"VoltageSlope:" + config[i].VoltageSlope + " " +
	        						"VoltageOffset:" + config[i].VoltageOffset + " " +
	        						"EUSlope:" + config[i].EUSlope + " " +
	        						"EUOffset:" + config[i].EUOffset + " " +
	        						"GaugeType:" + config[i].GaugeType);
        	}
        	else {
        		System.out.println(	"Location:" + config[i].Location + " " +
						"Type:" + config[i].Type + " " +
						"Name:" + config[i].Name + " " +
						"Units:" + config[i].Units + " " +
						"Stream:" + config[i].Stream + " " +
						"Record:" + config[i].Record + " " +
						"ReadWrite:" + config[i].ReadWrite + " " +
						"Gain:" + config[i].Gain + " " +
						"isDAQ:" + config[i].isDAQ);
        	}
        }
    }
    
}

/** SCRAMNet configuration inner class  */
class SCRAMNetRTMDConfig {
    public String Location;    
    public String Type;
    public String Name;
    public String Units;
    public String Stream;
    public String Record;
    public String ReadWrite;
    public String Gain;
    public String isDAQ;
    public String VoltageSlope;
    public String VoltageOffset;
    public String EUSlope;
    public String EUOffset;
    public String GaugeType;
    
    public SCRAMNetRTMDConfig() {
    }
}



