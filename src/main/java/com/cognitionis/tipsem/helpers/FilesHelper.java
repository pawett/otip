package com.cognitionis.tipsem.helpers;

import java.io.File;

import com.cognitionis.nlp_files.XMLFile;

public class FilesHelper {
	
	public static File GetFileAndCreateDir(String path) throws Exception
	{
		 File dir = new File(path);
         if (!dir.exists()) {
             if (!dir.mkdirs()) {  // mkdir only creates one, mkdirs creates many parent dirs if needed
                 throw new Exception("Directory not created...");
             }
         }
         return dir;
	}
	
	public static XMLFile GetNLPFile(String path) throws Exception
	{
		XMLFile nlpfile = new XMLFile(path, null);
        if (!nlpfile.getExtension().equalsIgnoreCase("tml")) 
        {
            nlpfile.overrideExtension("tml");
        }
        if (!nlpfile.isWellFormatted()) 
        {
            throw new Exception("File: " + nlpfile.getFile() + " is not a valid TimeML (.tml) XML file.");
        }
        
        return nlpfile;
	}

}
