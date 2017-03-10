package com.cognitionis.feature_builder;

import java.io.File;

import com.cognitionis.external_tools.FreeLing;
import com.cognitionis.external_tools.SRL_Roth;
import com.cognitionis.external_tools.TreeTagger;
import com.cognitionis.nlp_files.NLPFile;
import com.cognitionis.nlp_files.PipesFile;
import com.cognitionis.nlp_files.TabFile;
import com.cognitionis.tipsem.helpers.Logger;
import com.cognitionis.utils_basickit.FileUtils;

public class TabTokenFeatures extends BaseTokenFeatures {
	 /**
     * Returns the input TabFile, annotated with the featurevector for the approach and language
     *
     * @param lang  language code (en for English, es for Spanish)
     * @param file  input TabFile (base-segmentation.tab format)
     * @param feature_vector features to be obtained (default: TempEval2-features)
     * @param approach approach required features (TIPSem or TIPSemB)
     *
     * @return outputfilename
     */
    public static void getFeatures4Tab(String lang, String file, String feature_vector, String approach) {
        NLPFile nlpfile = null;
        String output = null;
        try {
            if (approach.matches("(?i)(TIPSem|TIPSemB)")) {
                if (feature_vector.matches("(TempEval2-features|(Dynamic|Static)Win-features)")) {

                    nlpfile = new TabFile(file);
                    nlpfile.setLanguage(lang);
                    System.err.println("Executing GET_PIPES");
                    output = ((TabFile) nlpfile).getPipesFile();
                    output = FileUtils.renameTo(output, "\\.tab\\.pipes", "\\.TempEval-bs");

                    if (approach.equalsIgnoreCase("TIPSem")) {
                        ProcessTabTipSem(lang, file, feature_vector, output);

                    } else { //TIPSem-B

                        ProcessTabTipSemB(lang, output);
                    }

                } else {
                    throw new Exception("Unknown feature vector: " + feature_vector);
                }
            } else {
                throw new Exception("Unknown approach: " + approach);
            }
        } catch (Exception e) {
            Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
        }
    }

	private static void ProcessTabTipSemB(String lang, String output) {
		if (lang.equalsIgnoreCase("EN")) {
		    output = ProcessTabTimSemB_EN(lang, output);
		}


		if (lang.equalsIgnoreCase("ES")) {
		    ProcessTabTipSemB_ES(lang, output);
		}
	}

	private static void ProcessTabTipSemB_ES(String lang, String output) {
		NLPFile nlpfile;
		nlpfile = new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Executing TO_TOKENS");
		}
		output = ((PipesFile) nlpfile).saveColumnFile("word");
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Executing FreeLing");
		}
		File f = new File(output + ".freeling");
		if (!f.exists()) {
		    output = FreeLing.run(output, lang, 0);
		} else {
		    System.err.println(" OMITING");
		    output = output + ".freeling";
		}

		nlpfile = new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Executing lemmaPOS2TempEval2_features");
		}
		output = lemmaPOS2TempEval2_features((PipesFile) nlpfile, lang);

		nlpfile=new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		String model = nlpfile.getFile().toString().substring(0, nlpfile.getFile().toString().lastIndexOf(".word"));
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Pairing PIPES");
		}
		output = ((PipesFile) nlpfile).pair_pipes_by_column_JOIN(0, model, 3);
		output = FileUtils.renameTo(output, "\\.TempEval-bs\\.word\\.freeling-POS2\\.paired", "\\.TempEval2-features");
	}

	private static String ProcessTabTimSemB_EN(String lang, String output) {
		NLPFile nlpfile;
		nlpfile = new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Executing TO_TOKENS");
		}
		output = ((PipesFile) nlpfile).saveColumnFile("word");

		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Executing treetag");
		}
		File f = new File(output + ".treetag");
		if (!f.exists()) {
		    output = TreeTagger.run_tok(output);
		} else {
		    System.err.println(" OMITING");
		    output = output + ".treetag";
		}

		nlpfile = new PipesFile(output);
		((PipesFile) nlpfile).isWellFormedOptimist();
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Executing lemmaPOS2TempEval2_features");
		}
		output = lemmaPOS2TempEval2_features((PipesFile) nlpfile, lang);

		nlpfile=new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		String model = nlpfile.getFile().toString().substring(0, nlpfile.getFile().toString().lastIndexOf(".word"));
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("Pairing PIPES");
		}
		output = ((PipesFile) nlpfile).pair_pipes_by_column_JOIN(0, model, 3);
		output = FileUtils.renameTo(output, "\\.TempEval-bs\\.word\\.treetag-POS2\\.paired", "\\.TempEval2-features");
		return output;
	}

	private static void ProcessTabTipSem(String lang, String file, String feature_vector, String output) {
		NLPFile nlpfile;
		if (lang.equalsIgnoreCase("EN")) {
		    output = ProcessTabTipSemEN(lang, output);
		}

		// already processed with AnCora
		if (lang.equalsIgnoreCase("ES")) {
		    output = ProcessTabTipSemES(lang, file, output);


		}

		if (feature_vector.equalsIgnoreCase("DynamicWin-features")) {
		    nlpfile=new PipesFile(output);
		    nlpfile.setLanguage(lang);
		    ((PipesFile) nlpfile).isWellFormedOptimist();
		    System.err.println("Executing DynamicWin features");
		    output = BaseTokenFeatures.getDynamicWin((PipesFile) nlpfile);
		    output = FileUtils.renameTo(output, "\\.TempEval2-features\\.DynamicWin-features", "\\.DynamicWin-features");
		}

		if (feature_vector.equalsIgnoreCase("StaticWin-features")) {
		    nlpfile=new PipesFile(output);
		    nlpfile.setLanguage(lang);
		    ((PipesFile) nlpfile).isWellFormedOptimist();
		    System.err.println("Executing StaticcWin features");
		    output = BaseTokenFeatures.getStaticWin((PipesFile) nlpfile);
		    output = FileUtils.renameTo(output, "\\.TempEval2-features\\.StaticWin-features", "\\.StaticWin-features");
		}
	}

	private static String ProcessTabTipSemES(String lang, String file, String output) {
		NLPFile nlpfile;
		nlpfile = new PipesFile(output);
		nlpfile.setLanguage(lang);
		File ancora = new File(file.substring(0, file.lastIndexOf(".")) + ".TempEval-bs.plain.roth-treetag");
		if (ancora.exists() && ancora.isFile()) {
		    output = WN_features(ancora.getAbsolutePath(), lang);
		    output = roles_features(output, lang);
		    ((PipesFile) nlpfile).isWellFormedOptimist();
		    String model = nlpfile.getFile().toString().substring(0, nlpfile.getFile().toString().lastIndexOf(".plain"));
		    if (model.endsWith("\\.tml")) {
		        output = addFileSentTokenColumns(((PipesFile) nlpfile));
		    } else {
		        System.err.println("Pairing PIPES");
		        output = ((PipesFile) nlpfile).pair_pipes_by_column_JOIN(0, model, 3);
		        output = FileUtils.renameTo(output, "\\.TempEval-bs\\.plain\\.roth-treetag-WNHyps-roleconfig-simpleroles-mainphrases\\.paired", "\\.TempEval2-features");
		    }

		} else {

		    nlpfile = new PipesFile(output);
		    nlpfile.setLanguage(lang);
		    ((PipesFile) nlpfile).isWellFormedOptimist();
		    System.err.println("Executing TO_TOKENS");
		    output = ((PipesFile) nlpfile).saveColumnFile("word");

		    System.err.println("Executing FreeLing");
		    File f = new File(output + ".freeling");
		    if (!f.exists()) {
		        output = FreeLing.run(output, lang, 0);
		    } else {
		        System.err.println(" OMITING");
		        output = output + ".freeling";
		    }

		    output = WN_features(output, lang);

		    nlpfile = new PipesFile(output);
		    nlpfile.setLanguage(lang);

		    ((PipesFile) nlpfile).isWellFormedOptimist();
		    System.err.println("Executing lemmaPOS2TempEval2_features");
		    output = lemmaPOS2TempEval2_features((PipesFile) nlpfile, lang);

		    nlpfile=new PipesFile(output);
		    nlpfile.setLanguage(lang);
		    ((PipesFile) nlpfile).isWellFormedOptimist();
		    String model = nlpfile.getFile().toString().substring(0, nlpfile.getFile().toString().lastIndexOf(".word"));
		    System.err.println("Pairing PIPES");
		    output = ((PipesFile) nlpfile).pair_pipes_by_column_JOIN(0, model, 3);
		    output = FileUtils.renameTo(output, "\\.TempEval-bs\\.word\\.freeling-WNHyps-POS2\\.paired", "\\.TempEval2-features");

		}
		return output;
	}

	private static String ProcessTabTipSemEN(String lang, String output) {
		NLPFile nlpfile;
		nlpfile = new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		System.err.println("Executing TO_PLAIN");
		output = ((PipesFile) nlpfile).toPlain();
		System.err.println("Executing SRL_Roth");
		File f = new File(output + ".roth");
		if (!f.exists()) {
		    output = SRL_Roth.run(output, 0);
		} else {
		    System.err.println(" OMITING");
		    output = output + ".roth";
		}

		System.err.println("Executing TREETAG");
		f = new File(output + "-treetag");
		if (!f.exists()) {
		    output = TreeTagger.run_and_merge(output, 0, 2, 6);
		} else {
		    System.err.println(" OMITING");
		    output = output + "-treetag";
		}

		output = WN_features(output, lang);
		output = roles_features(output, lang);

		nlpfile=new PipesFile(output);
		nlpfile.setLanguage(lang);
		((PipesFile) nlpfile).isWellFormedOptimist();
		String model = nlpfile.getFile().toString().substring(0, nlpfile.getFile().toString().lastIndexOf(".plain"));
		if (model.endsWith("\\.tml")) {
		    output = addFileSentTokenColumns(((PipesFile) nlpfile));
		} else {
		    System.err.println("Pairing PIPES");
		    output = ((PipesFile) nlpfile).pair_pipes_by_column_JOIN(0, model, 3);
		    output = FileUtils.renameTo(output, "\\.TempEval-bs\\.plain\\.roth-treetag-WNHyps-roleconfig-simpleroles-mainphrases\\.paired", "\\.TempEval2-features");
		}
		return output;
	}

}
