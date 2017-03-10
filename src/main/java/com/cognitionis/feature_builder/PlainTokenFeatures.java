package com.cognitionis.feature_builder;

import java.io.File;

import com.cognitionis.external_tools.FreeLing;
import com.cognitionis.external_tools.SRL_Roth;
import com.cognitionis.external_tools.TreeTagger;
import com.cognitionis.nlp_files.NLPFile;
import com.cognitionis.nlp_files.PipesFile;
import com.cognitionis.tipsem.helpers.Logger;

public class PlainTokenFeatures extends BaseTokenFeatures {

	   
    /**
     * Returns the input PlainFile, annotated with the featurevector for the approach and language
     *
     * @param lang  language code (en for English, es for Spanish)
     * @param file  input PlainFile (text.txt format)
     * @param tokenize 0 false 1 true
     * @param clean false|true delete temporal files
     * @param feature_vector features to be obtained (default: TempEval2-features)
     * @param approach approach required features (TIPSem or TIPSemB)
     *
     * @return outputfilename
     */
    public static String getFeatures(String lang, String file, int tokenize, boolean clean, String feature_vector, String approach) {
        NLPFile nlpfile = null;
        String output = null;
        try {
            output = file;
            if (approach.matches("(?i)(TIPSem|TIPSemB)")) 
            {
                if (feature_vector.matches("(TempEval2-features|(Dynamic|Static)Win-features)")) 
                {
                    if (approach.equalsIgnoreCase("TIPSem"))
                    {
                        output = ProcessTipSem(lang, file, tokenize, output);
                    } else {    // TIPSem-B
                        output = ProcessTipSemB(lang, file, tokenize, output);
                    }

                } else 
                {
                    throw new Exception("Unknown feature vector: " + feature_vector);
                }
            } else 
            {
                throw new Exception("Unknown approach: " + approach);
            }

            if (clean) {
                Clean(file);
            }


        } catch (Exception e) {
            Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
            return null;
        }
        return output;
    }

	private static void Clean(String file) {
		try {
		    String[] command = {"/bin/sh", "-c", "rm -rf " + file + ".treetag* " + file + ".freeling* " + file + ".roth*"};
		    Process p = Runtime.getRuntime().exec(command);

		    if (p != null) {
		        p.getInputStream().close();
		        p.getOutputStream().close();
		        p.getErrorStream().close();
		        p.destroy();
		    }
		} catch (Exception e) {
		    System.err.println("\nErrors found (TIMEE):\n\t" + e.toString() + "\n");
		    e.printStackTrace(System.err);
		}
	}

	private static String ProcessTipSemB(String lang, String file, int tokenize, String output) {
		if (lang.equalsIgnoreCase("EN")) {
		    
			Logger.WriteDebug("Executing TREETAG");
			
			File f = new File(output + ".treetag");
			if (!f.exists()) {
			    output = TreeTagger.run_tok(output);
			} else 
			{
				Logger.WriteDebug(" OMITING");
			    
			    output = output + ".treetag";
			}
		}

		if (lang.equalsIgnoreCase("ES")) {

			Logger.WriteDebug("Executing Freeling");
			
			File f = new File(output + ".freeling");
			if (!f.exists()) {
			    output = FreeLing.run(output, lang, tokenize);
			} else {
				Logger.WriteDebug(" OMITING");
			    output = output + ".freeling";
			}
		}
		
		output = GetLemmaPOS2TempEval2Features(lang, output);

		output = GetPairSpecialTemEval2FeaturesFromString(lang, file, output);
		return output;
	}

	
	private static String GetPairSpecialTemEval2FeaturesFromString(String lang, String file, String output) {
		NLPFile nlpfile;
		nlpfile=new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		output = GetPairSpecialTempEval2_features(((PipesFile) nlpfile), file);
		return output;
	}

	private static String ProcessTipSem(String lang, String file, int tokenize, String output) {
		NLPFile nlpfile;
		if (lang.equalsIgnoreCase("EN")) {
		    output = ProcessTipSemEN(lang, tokenize, output);
		}

		if (lang.equalsIgnoreCase("ES")) {
		    output = ProcessTipSemES(lang, tokenize, output);
		}

		output = GetPairSpecialTemEval2FeaturesFromString(lang, file, output);
		return output;
	}

	private static String ProcessTipSemES(String lang, int tokenize, String output) {
		NLPFile nlpfile;
		Logger.WriteDebug("Executing Freeling");
		
		File f = new File(output + ".freeling");
		if (!f.exists()) {
		    output = FreeLing.run(output, lang, tokenize);
		} else 
		{
			Logger.WriteDebug(" OMITING");
		    
		    output = output + ".freeling";
		}

		output = WN_features(output, lang);

		output = GetLemmaPOS2TempEval2Features(lang, output);
		return output;
	}

	private static String GetLemmaPOS2TempEval2Features(String lang, String output) {
		NLPFile nlpfile;
		nlpfile = new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		Logger.WriteDebug("Executing lemmaPOS2TempEval2_features");
		
		output = lemmaPOS2TempEval2_features((PipesFile) nlpfile, lang);
		return output;
	}

	private static String ProcessTipSemEN(String lang, int tokenize, String output) {
		Logger.WriteDebug("Executing SRL_Roth");
		
		File f = new File(output + ".roth");
		if (!f.exists()) {
		    output = SRL_Roth.run(output, tokenize);
		} else {
			Logger.WriteDebug(" OMITING");
		    output = output + ".roth";
		}

		Logger.WriteDebug("Executing TREETAG");
		
		f = new File(output + "-treetag");
		if (!f.exists()) {
		    output = TreeTagger.run_and_merge(output, 0, 2, 6);
		} else {
			Logger.WriteDebug(" OMITING");
		    
		    output = output + "-treetag";
		}

		output = WN_features(output, lang);
		output = roles_features(output, lang);
		return output;
	}

}
