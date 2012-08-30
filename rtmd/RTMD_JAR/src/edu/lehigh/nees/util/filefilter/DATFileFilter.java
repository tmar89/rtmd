package edu.lehigh.nees.util.filefilter;

import java.io.File;

/********************************* 
 * DATFileFilter
 * <p>
 * CSDATV FileFilter extension class
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 *  23 Aug 11  T. Marullo  Initial  
 *********************************/
public class DATFileFilter extends FileFilterExtended {
	public boolean accept(File f)
	{
		if (f.isDirectory()) return true;
		String exten = getExtension(f);
		if (exten != null && exten.equalsIgnoreCase("dat"))
				return true;
		return false;
	}

	public String getDescription()
	{ return "DAT files (*.dat)"; }
	
	public String getExtensionType() {
		return ".dat";
	}
}
    
    