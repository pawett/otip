package com.cognitionis.tipsem;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.cognitionis.tipsem.helpers.FileConverter;
import com.cognitionis.utils_basickit.StringUtils;

/** @author Hector Llorens 
 *  @since 2009
 */
public class Main {

	/** @param args the command line arguments */
	public static void main(String[] args) {
		try {
			String lang = null;
			//String action = "annotatecrf"; //default action
			TipSemAction action = TipSemAction.ANNOTATECRF;
			String action_parameters = null;
			String input_files[];
			String input_text = null;
			long startTime = System.currentTimeMillis();

			Options opt = new Options();
			opt.addOption("h", "help", false, "Print this help");
			opt.addOption("l", "lang", true, "Language code (default \"EN\" [English])");
			opt.addOption("a", "action", true, "Action/s to be done (annotate,TAn)");
			opt.addOption("ap", "action_parameters", true, "Optionally actions can have parameters (	,dct=1999-09-01,entities=event)");
			opt.addOption("t", "text", true, "To use text instead of a file (for short texts)");
			opt.addOption("d", "debug", false, "Debug mode: Output errors stack trace (default: disabled)");

			PosixParser parser = new PosixParser();
			CommandLine cl_options = parser.parse(opt, args);

			input_files = cl_options.getArgs();
			HelpFormatter hf = new HelpFormatter();
			for (Option option : cl_options.getOptions()) {
				switch(option.getLongOpt())
				{
				case "help":
					hf.printHelp("TIPSem", opt);
					System.exit(0);
					break;
				case "debug":
					System.setProperty("DEBUG", "true");
					break;
				case "lang":
					lang = option.getValue().toLowerCase();
					if (lang.length() != 2) {
						hf.printHelp("TIPSem", opt);
						throw new Exception("Error: incorrect language " + lang + " -- must be 2 chars");
					}
					break;
				case "action":					
					try {
						action = TipSemAction.valueOf(option.getValue().toUpperCase());
					} catch (Exception e) {
						String errortext = "\nValid actions are:\n";
						
						for (TipSemAction ac : TipSemAction.values()) {
							errortext += "\t" + ac.name() + "\n";
						}
						throw new RuntimeException("\tIlegal action: " + option.getValue().toUpperCase() + "\n" + errortext);
					}
					break;
				case "action_parameters":
					action_parameters = option.getValue();
					break;
				case "text":
					input_text = option.getValue();
					input_files = FileConverter.ConvertTextToFile(input_files, input_text);
					break;
				}
			}	

			OptionHandler actionExecuter = new OptionHandler(input_files, action_parameters, lang);
			actionExecuter.doAction(action);
			
			long endTime = System.currentTimeMillis();
			long sec=(endTime-startTime)/1000;
			if(sec<60){
				System.err.println("Done in "+StringUtils.twoDecPosS(sec)+" sec!\n");
			}else{
				System.err.println("Done in "+StringUtils.twoDecPosS(sec/60)+" min!\n");
			}
			PrintResultFiles(input_files, input_text);
		} catch (Exception e) {
			System.err.println("Errors found:\n\t" + e.getMessage() + "\n");
			if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
				e.printStackTrace(System.err);
			}
			System.exit(1);
		}
    }

	private static void PrintResultFiles(String[] input_files, String input_text)
			throws FileNotFoundException, IOException {
		if (input_text != null) {
			System.err.println("Result:\n");
			BufferedReader reader = new BufferedReader(new FileReader(input_files[0] + ".tml"));
			try{
				String text = null;
				while ((text = reader.readLine()) != null) {
					System.out.println(text + "\n");
				}
			}finally{
				if(reader!=null) reader.close();
			}
		}
	}

	
}
