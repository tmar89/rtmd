package edu.lehigh.nees.util.filefilter;

import java.io.File;

/********************************* 
 * SDAPFileFilter
 * <p>
 * SDAP FileFilter extension class
 * <p>
 * @author Thomas Marullo
 * <p>
 * <b><u>Revisions</u></b><br>
 * 28 Feb 11  T. Marullo  Initial
 * 
 *********************************/
public class SDAPFileFilter extends FileFilterExtended {
	public boolean accept(File f)
	{
		if (f.isDirectory()) return true;
		String exten = getExtension(f);
		if (exten != null && exten.equalsIgnoreCase("sdap"))
				return true;
		return false;
	}

	public String getDescription()
	{ return "sDAP files (*.sdap)"; }
	
	public String getExtensionType() {
		return ".sdap";
	}
}