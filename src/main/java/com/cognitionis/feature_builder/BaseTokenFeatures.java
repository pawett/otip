package com.cognitionis.feature_builder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import com.cognitionis.external_tools.FreeLing;
import com.cognitionis.external_tools.SRL_Roth;
import com.cognitionis.external_tools.TreeTagger;
import com.cognitionis.external_tools.WNInterface;
import com.cognitionis.knowledgek.NUMEK.NUMEK;
import com.cognitionis.nlp_files.NLPFile;
import com.cognitionis.nlp_files.PipesFile;
import com.cognitionis.nlp_files.TabFile;
import com.cognitionis.nlp_files.parentical_parsers.SRLColParser;
import com.cognitionis.nlp_files.parentical_parsers.SyntColParser;
import com.cognitionis.tipsem.helpers.Logger;
import com.cognitionis.utils_basickit.FileUtils;
import com.cognitionis.utils_basickit.StringUtils;

import domain.*;


public class BaseTokenFeatures {


    public static String WN_features(String output, String lang) {
        NLPFile nlpfile = null;
        try {
            nlpfile = new PipesFile(output);
            nlpfile.setLanguage(lang);
            ((PipesFile) nlpfile).isWellFormedOptimist();
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                System.err.println("Executing WN_HYPS");
            }
            File f = new File((nlpfile.getFile()).getCanonicalPath() + "-WNHyps");
            if (!f.exists()) {
                output = BaseTokenFeatures.getWNHyps((PipesFile) nlpfile, lang);
            } else {
                if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                    System.err.println("OMITING");
                }
                output = (nlpfile.getFile()).getCanonicalPath() + "-WNHyps";
            }
        } catch (Exception e) {
            System.err.println("Errors found (Experimenter):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return output;
    }

    public static String roles_features(String output, String lang) {
        NLPFile nlpfile = null;
        try {
            nlpfile = new PipesFile(output);
            nlpfile.setLanguage(lang);
            ((PipesFile) nlpfile).isWellFormedOptimist();
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                System.err.println("Executing ROLECONF");
            }
            output = getVerbRoleconfig((PipesFile) nlpfile);

            nlpfile=new PipesFile(output);
            nlpfile.setLanguage(lang);
            ((PipesFile) nlpfile).isWellFormedOptimist();
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                System.err.println("Executing SIMPLEROLES");
            }
            output = getSimpleRoles((PipesFile) nlpfile);

            nlpfile=new PipesFile(output);
            nlpfile.setLanguage(lang);
            ((PipesFile) nlpfile).isWellFormedOptimist();
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                System.err.println("Executing MAINPHRASES");
            }
            //output = ((PipesFile) nlpfile).getMainPhrases();
            output = getMainPhrasesPPdetail((PipesFile) nlpfile);
        } catch (Exception e) {
            System.err.println("Errors found (Experimenter):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return output;
    }

    public static String addFileSentTokenColumns(PipesFile pipesfile) {
        String outputfile = null;
        try {
            outputfile = pipesfile.getFile().getCanonicalPath() + ".TempEval2-features";
            String filename = pipesfile.getFile().getCanonicalPath().substring(pipesfile.getFile().getCanonicalPath().lastIndexOf('/') + 1, pipesfile.getFile().getCanonicalPath().lastIndexOf(".tml"));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            try {
                int sentence = 0, token = 0;
                String pipesline;
                String[] pipesarr = null;
                int blanks_col = pipesfile.getColumn("leading(-|_)blanks");
                while ((pipesline = pipesreader.readLine()) != null) {
                    if (pipesline.trim().length() > 1) {
                        pipesarr = pipesline.split("\\|");
                        //outfile.write(pipesfile.getFile().getName()+"|"+sentence+"|"+token);
                        outfile.write(filename + "|" + sentence + "|" + token);
                        for (int i = 0; i < pipesarr.length; i++) {
                            if (i != blanks_col) {
                                outfile.write("|" + pipesarr[i]);
                            }
                        }
                        outfile.write("\n");
                        token++;
                    } else {
                        outfile.write(pipesline + "\n");
                        token = 1;
                        sentence++;
                    }
                }
            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TempEval):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns the pipesfile splited in sentences by empty |
     *
     * @return outputfilename
     */
    public String sentSplit(PipesFile pf) {
        String outputfile = pf.getFile().toString() + ".pipes";
        int numline = 0;
        try {
            BufferedReader pipesreader = new BufferedReader(new FileReader(pf.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));

            int sentcolumn = 1;
            int filecolumn = 0;
            try {
                String line;
                String numsent = "-1";
                String filename = "-1";
                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    String[] linearr = line.split("\\|");
                    if ((!filename.equals(linearr[filecolumn]) || !numsent.equals(linearr[sentcolumn])) && !numsent.equals("-1") && !filename.equals("-1")) {
                        outfile.write("|\n");
                    }
                    for (int i = 3; i < linearr.length - 1; i++) {
                        outfile.write(linearr[i] + "|");
                    }
                    outfile.write(linearr[linearr.length - 1] + "\n");

                    numsent = linearr[sentcolumn];
                    filename = linearr[filecolumn];
                }
                outfile.write("|\n");

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (" + this.getClass().getSimpleName() + "):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns the wnhyps as the perl does
     *
     * @return outputfilename
     */
    public static String getWNHyps(PipesFile pipesfile, String lang) {
        String outputfile = pipesfile.getFile().toString() + "-WNHyps";
        int numline = 0;
        try {
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));

            int poscolumn = pipesfile.getColumn("pos");
            int lemmacolumn = pipesfile.getColumn("lemma");
            int last_desc_column = pipesfile.getLastDescColumn();
            ArrayList<String> sentence = null;
            try {
                String line;
                int numsent = 0;
                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    String[] linearr = line.split("\\|");
                    //System.err.println(line);
                    if (linearr.length >= pipesfile.getPipesDescArrCount()) {
                        if (sentence == null) {
                            sentence = new ArrayList();
                        }
                        sentence.add(line);
                    } else {
                        int numtok = 0;
                        // Write file plus WNHyps
                        String WNHyps = "-";
                        WNInterface wn = new WNInterface();

                        for (String sline : sentence) {
                            linearr = sline.split("\\|");
                            numtok++;
                            if (linearr[poscolumn].matches("(V|N).*")) {
                                if (lang.equalsIgnoreCase("EN")) {
                                    WNHyps = linearr[poscolumn].substring(0, 1).toLowerCase() + "-" + wn.getHypersHACK(linearr[lemmacolumn].toLowerCase(), linearr[poscolumn]);
                                }
                                if (lang.equalsIgnoreCase("ES")) {
                                    WNHyps = linearr[poscolumn].substring(0, 1).toLowerCase() + "-" + wn.getHypersHACKES2(linearr[lemmacolumn].toLowerCase(), linearr[poscolumn]);
                                }
                            } else {
                                WNHyps = "-";
                            }
                            for (int i = 0; i < linearr.length - 1; i++) {
                                // There are roles columns in the sentence
                                if (i == last_desc_column) {
                                    outfile.write(linearr[i] + "|" + WNHyps + "|");
                                } else {
                                    outfile.write(linearr[i] + "|");
                                }
                            }
                            // There arent roles columns in the sentences
                            if (linearr.length - 1 == last_desc_column) {
                                outfile.write(linearr[linearr.length - 1] + "|" + WNHyps);
                            } else {
                                outfile.write(linearr[linearr.length - 1]);
                            }
                            outfile.write("\n");
                            WNHyps = "-";
                        }
                        outfile.write("|\n");
                        numsent++;
                        sentence = null;
                    }
                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns the TempEval-2 feature set for TreeTagger(en) and FreeLing (es)
     * Tense and polarity are calculated
     *
     * @return outputfilename
     */
    public static TokenizedFile lemmaPOS2TempEval2_features(TokenizedFile file, String lang) {
    	// String outputfile = pipesfile.getFile().toString() + "-POS2";
    	int numline = 0;
    	file.setType(FilesType.treetag_POS2);
    	try {
    		// BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
    		// BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));

    		// int wordcolumn = pipesfile.getColumn("word");
    		// int poscolumn = pipesfile.getColumn("pos");
    		// int lemmacolumn = pipesfile.getColumn("lemma");
    		// int wncolumn = pipesfile.getColumn("wn");
    		int wordposition = 0;

    		ArrayList<Word> sentence = null;
    		ArrayList<String[]> verbs = null;
    		ArrayList<String[]> ppdetail = null;

     		

    		String line;
    		int numsent = 0;
    		//  while ((line = pipesreader.readLine()) != null) 
    		for(TokenizedSentence sentenceInFile : file)
    		{
    			for(Word token : sentenceInFile)             
    			{
	    			numline++;
	    			wordposition++; 				

    				if (sentence == null) {
    					sentence = new ArrayList();
    					verbs = new ArrayList<String[]>(); // position, lemma, tense, assertype
    					ppdetail = new ArrayList<String[]>(); // start, end, text
    					wordposition = 1;
    				}

    			//	sentence.add(token);

    				if (token.pos.startsWith("V"))
    				{ 	
    					token = ProcessVerbs(lang, token, wordposition, verbs);
    				}
    		  }
    			
    			int numtok = 0;
				int nextverbpositioncover = -1;
				int nextverb = 1;
				wordposition = -1;
				String tense = "-";
				String assertype ="-";
				String verb[];
				String depverb = "-";
				if (verbs.size() != 0) 
				{
					verb = verbs.get(0);
					wordposition = Integer.parseInt(verb[0]);
					tense = verb[2];
					assertype = verb[3];
					depverb = verb[1];
					if (verbs.size() > 1) {
						nextverbpositioncover = Integer.parseInt(verbs.get(1)[0]);
						nextverbpositioncover = wordposition + (int) Math.ceil((nextverbpositioncover - wordposition) / 2.0);
					}
				}
				
				String pp = "-";
				Word previousToken = null;
				int pos = 0;
				for(Word token : sentenceInFile)             
    			{
					wordposition = ProcessSentence(lang, token, previousToken, wordposition, sentence, verbs, numtok, nextverbpositioncover, nextverb, tense,assertype, depverb, pp);
    			}
				/*for (int i = 0; i < sentence.size(); i++) 
				{
					Word tok = sentence.get(i);
					wordposition = ProcessSentence(lang, tok, previousToken, wordposition, sentence, verbs, numtok, nextverbpositioncover, nextverb, tense,
							assertype, depverb, pp);
					returnFile.addLine(tok);
					previousToken = tok;
				}*/
				// outfile.write("\n");
				numsent++;
				sentence = null;
    		}

    		// IF LAST SENTENCE DOES NOT ENDED CORRECTLY
    		/* if (sentence != null) {
                    ProcessNotEndedCorrectlySentence(lang, outfile, wordcolumn, poscolumn, lemmacolumn, wncolumn,
							sentence, verbs, numsent);
                }*/


    	} catch (Exception e) {
    		Logger.WriteError("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n", e);
    		System.exit(1);
    		return null;
    	}
    	return file;
    }

	private static void ProcessNotEndedCorrectlySentence(String lang, BufferedWriter outfile, int wordcolumn,
			int poscolumn, int lemmacolumn, int wncolumn, ArrayList<String> sentence, ArrayList<String[]> verbs,
			int numsent) throws IOException {
		int wordposition;
		String[] linearr;
		String tense = "-";
		String assertype = "-";
		int numtok = 0;
		int nextverbpositioncover = -1;
		int nextverb = 1;
		wordposition = -1;
		String verb[];
		String depverb = "-";
		if (verbs.size() != 0) {
		    verb = verbs.get(0);
		    wordposition = Integer.parseInt(verb[0]);
		    tense = verb[2];
		    assertype = verb[3];
		    depverb = verb[1];
		    if (verbs.size() > 1) {
		        nextverbpositioncover = Integer.parseInt(verbs.get(1)[0]);
		        nextverbpositioncover = wordposition + ((nextverbpositioncover - wordposition) / 2);
		    }
		}
		for (int i = 0; i < sentence.size(); i++) {
		    String sline = sentence.get(i);
		    linearr = sline.split("\\|");
		    numtok++;
		    String simplepos = linearr[poscolumn];
		    String simplelemma = linearr[lemmacolumn];
		    String pp = "-";
		    if (linearr[poscolumn].matches("(IN|TO)")) {
		        // if pp is just after end of last one is a multi-pp (2word)
		        if (!pp.equals("-") && i > 0 && sentence.get(i - 1).split("\\|")[poscolumn].matches("(IN|TO)")) {
		            pp += "_" + linearr[wordcolumn];
		        } else {
		            pp += linearr[wordcolumn];
		        }
		    }
		    if (linearr[poscolumn].startsWith("V")) {
		        pp = "-";
		    }
		    if (lang.equalsIgnoreCase("es")) {
		        if (simplepos.length() > 2) {
		            if (simplepos.startsWith("V")) {
		                simplepos = simplepos.substring(0, 4);
		            } else {
		                if (simplepos.startsWith("N")) {
		                    simplepos = simplepos.substring(0, 2) + simplepos.substring(3, 4); // N (Common or Proper) (S or Plural)
		                } else {
		                    simplepos = simplepos.substring(0, 2);
		                }
		            }
		        }
		        if (linearr[wordcolumn].matches("([0-9]+[0-9./:,-]*|" + NUMEK.numbers_re_ES + ")")) {
		            simplepos = "CD";
		        }
		    }

		    if (lang.equalsIgnoreCase("en")) {
		        if (!simplepos.matches("(?i)(NP|NPS|NNP|NNPS)")) {
		            simplelemma = simplelemma.toLowerCase(); // lemma to lower case
		        }
		    }

		    if (numtok == nextverbpositioncover) {
		        verb = verbs.get(nextverb);
		        nextverb++;
		        wordposition = Integer.parseInt(verb[0]);
		        tense = verb[2];
		        assertype = verb[3];
		        depverb = verb[1];
		        if (verbs.size() > (nextverb)) {
		            nextverbpositioncover = Integer.parseInt(verbs.get(nextverb)[0]);
		            nextverbpositioncover = wordposition + ((nextverbpositioncover - wordposition) / 2);
		        }
		    }

		    if (wncolumn == -1) {
		        outfile.write(linearr[wordcolumn] + "|" + simplepos + "|siob|st|sy|-|" + simplelemma + "|wn|rc|sriob|sriobv|sr|" + depverb + "|" + tense + "|" + assertype + "|iobph|php|phi|" + pp + "\n");
		    } else {
		        outfile.write(linearr[wordcolumn] + "|" + simplepos + "|siob|st|sy|-|" + simplelemma + "|" + linearr[wncolumn] + "|rc|sriob|sriobv|sr|" + depverb + "|" + tense + "|" + assertype + "|iobph|php|phi|" + pp + "\n");
		    }
		}
		outfile.write("\n");
		numsent++;
		sentence = null;
	}

	private static int ProcessSentence(String lang, Word token, Word previousToken, int wordposition, ArrayList<Word> sentence, ArrayList<String[]> verbs,
			int numtok, int nextverbpositioncover, int nextverb, String tense, String assertype, String depverb,
			String pp) throws IOException {
		String[] linearr;
		String[] verb;
			
		numtok++;	

		if (token.pos.matches("(IN|TO)")) {
		    // if pp is just after end of last one is a multi-pp (2word)
		    if (!pp.equals("-") && previousToken != null && previousToken.pos.matches("(IN|TO)")) {
		        pp += "_" + token.word;
		    } else {
		        pp += token.word;
		    }
		}
		if (token.pos.startsWith("V")) {
		    pp = "-";
		}


		if (lang.equalsIgnoreCase("es")) 
		{
		    ProcessSentenceSpanish(token);
		}

		if (lang.equalsIgnoreCase("en")) {
		    ProcessSentenceEnglish(token);
		}

		if (numtok == nextverbpositioncover) {
		    verb = verbs.get(nextverb);
		    nextverb++;
		    wordposition = Integer.parseInt(verb[0]);
		    tense = verb[2];
		    assertype = verb[3];
		    depverb = verb[1];
		    if (verbs.size() > (nextverb)) {
		        nextverbpositioncover = Integer.parseInt(verbs.get(nextverb)[0]);
		        nextverbpositioncover = wordposition + (int) Math.ceil((nextverbpositioncover - wordposition) / 2);
		    }
		}
		

		 token.syntbio = "siob";
			token.sentence = "st";
			token.synt = "sy";
			token.verb = "-";
			token.roleconf = "rc";
			token.simplerolesIOB2 = "sriob";
			token.simplerolesIOB2_verb = "sriobv";
			token.simpleroles = "sr";
			token.depverb = depverb;
			token.tense = tense;
			token.assertype = assertype;
			token.iobmainphrase ="iobph";
			token.mainp_position = "php";
			token.phra_id = "phi";
			token.PPdetail = pp;
		
			if (token.wn == null || token.wn == "-") 
			token.wn ="wn";

		
		return wordposition;
	}

	private static void ProcessSentenceEnglish(Word token) {
		if (!token.pos.matches("(?i)(NP|NPS|NNP|NNPS)")) {
			token.lemma = token.lemma.toLowerCase(); // lemma to lower case
		}
	}

	private static void ProcessSentenceSpanish(Word token) {
		if (token.pos.length() > 2) {
		    if (token.pos.startsWith("V")) {
		    	token.pos = token.pos.substring(0, 4);
		    } else {
		        if (token.pos.startsWith("N")) {
		        	token.pos = token.pos.substring(0, 2) + token.pos.substring(3, 4); // N (Common or Proper) (S or Plural)
		        } else {
		        	token.pos = token.pos.substring(0, 2);
		        }
		    }
		}
		if (token.word.matches("([0-9]+[0-9./:,-]*|" + NUMEK.numbers_re_ES + ")")) {
			token.pos = "CD";
		}
	}
    
	private static Word ProcessVerbs(String lang, Word token, int wordposition,
			ArrayList<String[]> verbs) 
	{
		String tense = "-";
		String assertype = "-";
		String auxiliary = "-";
		if (lang.equalsIgnoreCase("es")) 
		{ // Freeling tenses
		    String FreelingTense = token.pos.substring(1, 4); // 0 type, 1 mode, 2 time
		    //if (FreelingTense.charAt(0) == 'M') { // only main verbs
		    if (FreelingTense.charAt(0) == 'A' || FreelingTense.charAt(0) == 'S') {
		        auxiliary = "1";
		    }
		    if (FreelingTense.charAt(1) == 'G') { // gerundio
		        if (token.prev.word.matches("(?:est(?:oy|ás|á|amos|áis|án|é|és|emos|éis|én))")) {
		            tense = "present-continuous";
		        } else {
		            if (token.prev.word.matches("(estaba(?:s|n|is)?|estábamos|estuvie(?:ra|se)(?:s|n|is)?|estuvié(?:ra|se)mos)")) {
		                tense = "past-continuous";
		            } else {
		                if (token.prev.word.equals("estado") && token.prev.prev.word.matches("(?:he|has|ha|hemos|habéis|han)")) {
		                    tense = "present-perfect-compound-continuous";
		                } else {
		                    if (token.prev.word.equals("estado") && token.prev.prev.word.matches("había(?:s|n|mos|is)?")) {
		                        tense = "past-perfect-compound-continuous";
		                    }
		                }
		            }
		        }
		    } else {
		        if (FreelingTense.charAt(1) == 'P') { // participio
		            if (token.prev.word.matches("(?:he|has|ha|hemos|habéis|han)") || (token.prev.word.equals("sido") && token.prev.prev.word.matches("(?:he|has|ha|hemos|habéis|han)"))) {
		                tense = "present-perfect-compound";
		            } else {
		                if (token.prev.word.matches("había(?:s|n|mos|is)?") || (token.prev.word.equals("sido") && token.prev.prev.word.matches("había(?:s|n|mos|is)?"))) {
		                    tense = "past-perfect-compound";
		                }
		                // there's another rare case (han estado siendo transportados...) but is lingusitically obscure...
		            }
		        } else {
		            if (FreelingTense.charAt(1) == 'I' || FreelingTense.charAt(1) == 'S' || FreelingTense.charAt(1) == 'M') { // INDICATIVE, SUBJUNCTIVE, IMPERATIVE ...DISCARD INFINITVE...
		                if (FreelingTense.charAt(2) == 'P') {
		                    tense = "present";
		                } else {
		                    if (FreelingTense.charAt(2) == 'I') {
		                        tense = "past-imperfect";
		                    } else {
		                        if (FreelingTense.charAt(2) == 'S') {
		                            tense = "past-perfect-simple";
		                        } else {
		                            if (FreelingTense.charAt(2) == 'F') {
		                                tense = "future";
		                            } else {
		                                if (FreelingTense.charAt(2) == 'C') {
		                                    tense = "conditional";
		                                }
		                            }
		                        }

		                    }
		                }
		            }
		            // hack for Spanish infinitives: NOT useful since we loose the tense of the sentence
		            /*else{
		                if (FreelingTense.charAt(1) == 'N'){
		                    tense = "present"; // generic infinitive verbs... (we can decide what to do with them)
		                }
		            }*/
		        }
		    }
		    //}

		    if (!tense.equals("-")) {

		        if (token.prev.word.matches("(no|nunca|jamas)") || (token.prev.word.matches("(se|me|nos|os|fu.+|he|has|ha|hemos|habéis|han|había(?:s|n|mos|is)?)") &&
		        		(token.prev.prev.word.matches("(no|nunca|jamás)") || token.prev.prev.prev.word.matches("(no|nunca|jamás)")))) {
		            assertype = "negative";
		        } else {
		            assertype = "positive";
		        }
		    }

		} else { // en -- Treetager tenses

		    // TODO falta todo lo de being (is being constructed)


		    if (token.lemma.matches("(?:have|be|go|do)")) {
		        auxiliary = "1";
		    }

		    if (tense.equals("-") && (token.prev.word.matches("(?:.*ed|(?:was|were|did)(?:n't)?|been)") ||
		    		(token.prev.prev.word.matches("(?:.*ed|was|were|did|been)") &&
		    				(token.prev.word.equals("to") || token.prev.word.matches("(not|n't)"))))) {
		        tense = "past";
		    } // TODO might be continuous
		    if (token.prev.word.matches("had(n't)?") || 
		    		(token.prev.prev.word.equals("had") && 
		    				token.prev.word.matches("(not|n't|to)"))) {
		        tense = "past-perfect";
		    }
		    if (token.prev.word.matches("(have|has|'ve)(n't)?") ||
		    		(token.prev.prev.word.matches("(have|has|'ve)") &&
		    				token.prev.word.matches("(not|n't|to)"))) {
		        tense = "present-perfect";
		    }
		    if (tense.equals("-") && token.prev.prev.prev.word.matches("(will|wo)") && 
		    		token.prev.prev.word.matches("(not|n't|have)") &&
		    		token.prev.word.equals("be|to")) {
		        tense = "future";
		    }
		    if (tense.equals("-") && token.prev.prev.prev.word.equals("will") &&
		    		token.prev.prev.word.equals("have") && token.prev.word.equals("to")) {
		        tense = "future";
		    }
		    // generic hack for futures like will start crying, will start to cry, will|won't have to/be
		    if (tense.equals("-") && (token.prev.word.equals("will") ||
		    		token.prev.word.equals("won't") || 
		    		token.prev.prev.word.equals("will") ||
		    		token.prev.prev.prev.word.equals("will") || 
		    		(token.prev.prev.prev.word.equals("wo") && 
		    				token.prev.prev.word.equals("n't")) || 
		    		(token.prev.prev.word.equals("wo") && token.prev.word.equals("n't")))) {
		        tense = "future";
		    }
		    if (tense.equals("-") && 
		    		token.prev.prev.word.equals("going") && token.prev.word.matches("to")) {
		        tense = "future";
		    }
		    if (tense.equals("-") && 
		    		(token.prev.word.matches("(?:would|may|might|should|must)") || 
		    				((token.prev.prev.word.matches("(?:would|may|might|should|must)") ||
		    						token.prev.prev.word.matches("(?:would|should)n't")) && 
		    						token.prev.word.matches("(?:not|n't|be|would(?:n't)?)")) ||
		    				token.prev.prev.prev.word.matches("(?:would|may|might|should|must)") &&
		    				token.prev.prev.word.matches("(?:not|n't)") && token.prev.word.equals("be"))) {
		        tense = "conditional";
		    }

		    // super-hack for not geting past tenses from suposed that to be finished...
		    if (token.prev.word.matches("be")) {
		        tense = "present";
		    }

		    if (tense.equals("-") && (token.prev.word.matches("(?:is|are|do)(?:n't)?") ||
		    		(token.prev.prev.word.matches("(?:is|are|do)") && 
		    				token.prev.word.matches("(?:not|n't)")))) {
		        tense = "present";
		    } // TODO might be continuous


		    if (tense.equals("-") && (token.pos.matches("VB(?:D|N)") || (token.pos.equals("AUX") &&
		    		token.word.matches("(?i)(?:was|were)")))) {
		        tense = "past";
		    }
		    if (tense.equals("-") && (token.pos.matches("VB(?:P|Z)") || (token.pos.equals("AUX") &&
		    		token.word.matches("(?i)(?:was|were)")))) {
		        tense = "present";
		    }
		    if (tense.equals("-") && (token.pos.equals("VBG"))) { // can be improved...
		        tense = "present";
		    }

		    // ignoring possessive 's
		    if(token.word.equalsIgnoreCase("'s")
		            && !token.prev.pos.equalsIgnoreCase("PP")){
		        tense="-";
		    }


		    if (!tense.equals("-")) {
		        if (token.prev.word.matches("(?:.*n't|not|never)") || 
		        		(token.prev.prev.word.matches("(?:.*n't|not|never)")) || 
		        		(token.prev.prev.prev.word.equals("not") && 
		        				token.prev.prev.word.equals("going") && token.prev.word.equals("to"))) {
		            assertype = "negative";
		        } else {
		            assertype = "positive";
		        }
		    }

		}

		if (!tense.equals("-")) {
		    if (!tense.endsWith("-continuous") && (lang.equalsIgnoreCase("en") && 
		    		token.word.endsWith("ing") || lang.equalsIgnoreCase("es") &&
		    		token.word.endsWith("ndo"))) {
		        tense += "-continuous";
		    }
		    //System.out.println(linearr[lemmacolumn] + "/"+wordposition);
		    String[] verb = {"" + wordposition, token.lemma, tense, assertype, auxiliary};
		    if (verbs.size() > 0 && (verbs.get(verbs.size() - 1)[4].equals("1") && (wordposition - Integer.parseInt((verbs.get(verbs.size() - 1)[0]))) < 5)) {
		        //System.out.println("yes");
		        verbs.remove(verbs.size() - 1);
		    }
		    verbs.add(verb);
		}
		token.tense = tense;
		return token;
	}

    /*public static String lemmaPOS2TempEval2_featuresOld(PipesFile pipesfile, String lang) {
        String outputfile = pipesfile.getFile().toString() + "-POS2";
        int numline = 0;
        try {
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));

            int wordcolumn = pipesfile.getColumn("word");
            int poscolumn = pipesfile.getColumn("pos");
            int lemmacolumn = pipesfile.getColumn("lemma");
            int wncolumn = pipesfile.getColumn("wn");
            int wordposition = 0;
            ArrayList<String> sentence = null;
            ArrayList<String[]> verbs = null;
            ArrayList<String[]> ppdetail = null;
            try {
                String[] wordmem = new String[4];
                String[] posmem = new String[2];
                wordmem[2] = "-";
                wordmem[1] = "-";
                wordmem[0] = "-";
                posmem[1] = "-";
                posmem[0] = "-";
                String line;
                int numsent = 0;

                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    wordposition++;
                    String[] linearr = line.split("\\|");
                    String tense = "-";
                    String assertype = "-";
                    String auxiliary = "0";

                    if (linearr.length >= pipesfile.getPipesDescArrCount()) {
                        wordmem[3] = wordmem[2];
                        wordmem[2] = wordmem[1];
                        wordmem[1] = wordmem[0];
                        wordmem[0] = linearr[wordcolumn].toLowerCase();
                        posmem[1] = posmem[0];
                        posmem[0] = linearr[poscolumn];

                        if (sentence == null) {
                            sentence = new ArrayList();
                            verbs = new ArrayList<String[]>(); // position, lemma, tense, assertype
                            ppdetail = new ArrayList<String[]>(); // start, end, text
                            wordposition = 1;
                        }
                        //System.out.println(wordposition);

                        sentence.add(line);

                        if (linearr[poscolumn].startsWith("V"))
                        {
                        	
                            ProcessVerbs(pipesfile, lang, wordcolumn, poscolumn, lemmacolumn, wordposition, verbs,
									wordmem, posmem, linearr, tense, assertype, auxiliary);
                        }

//The info of the token is fewer than the parameters expected (End of sentence??)
                    } else {
                        int numtok = 0;
                        int nextverbpositioncover = -1;
                        int nextverb = 1;
                        wordposition = -1;
                        String verb[];
                        String depverb = "-";
                        if (verbs.size() != 0) {
                            verb = verbs.get(0);
                            wordposition = Integer.parseInt(verb[0]);
                            tense = verb[2];
                            assertype = verb[3];
                            depverb = verb[1];
                            if (verbs.size() > 1) {
                                nextverbpositioncover = Integer.parseInt(verbs.get(1)[0]);
                                nextverbpositioncover = wordposition + (int) Math.ceil((nextverbpositioncover - wordposition) / 2.0);
                            }
                        }
                        String pp = "-";
                        for (int i = 0; i < sentence.size(); i++) {
                            String sline = sentence.get(i);
                            linearr = sline.split("\\|");
                            numtok++;
                            String simplepos = linearr[poscolumn];
                            String simplelemma = linearr[lemmacolumn];

                            if (linearr[poscolumn].matches("(IN|TO)")) {
                                // if pp is just after end of last one is a multi-pp (2word)
                                if (!pp.equals("-") && i > 0 && sentence.get(i - 1).split("\\|")[poscolumn].matches("(IN|TO)")) {
                                    pp += "_" + linearr[wordcolumn];
                                } else {
                                    pp += linearr[wordcolumn];
                                }
                            }
                            if (linearr[poscolumn].startsWith("V")) {
                                pp = "-";
                            }


                            if (lang.equalsIgnoreCase("es")) {
                                if (simplepos.length() > 2) {
                                    if (simplepos.startsWith("V")) {
                                        simplepos = simplepos.substring(0, 4);
                                    } else {
                                        if (simplepos.startsWith("N")) {
                                            simplepos = simplepos.substring(0, 2) + simplepos.substring(3, 4); // N (Common or Proper) (S or Plural)
                                        } else {
                                            simplepos = simplepos.substring(0, 2);
                                        }
                                    }
                                }
                                if (linearr[wordcolumn].matches("([0-9]+[0-9./:,-]*|" + NUMEK.numbers_re_ES + ")")) {
                                    simplepos = "CD";
                                }
                            }

                            if (lang.equalsIgnoreCase("en")) {
                                if (!simplepos.matches("(?i)(NP|NPS|NNP|NNPS)")) {
                                    simplelemma = simplelemma.toLowerCase(); // lemma to lower case
                                }
                            }

                            if (numtok == nextverbpositioncover) {
                                verb = verbs.get(nextverb);
                                nextverb++;
                                wordposition = Integer.parseInt(verb[0]);
                                tense = verb[2];
                                assertype = verb[3];
                                depverb = verb[1];
                                if (verbs.size() > (nextverb)) {
                                    nextverbpositioncover = Integer.parseInt(verbs.get(nextverb)[0]);
                                    nextverbpositioncover = wordposition + (int) Math.ceil((nextverbpositioncover - wordposition) / 2);
                                }
                            }



                            if (wncolumn == -1) {
                                outfile.write(linearr[wordcolumn] + "|" + simplepos + "|siob|st|sy|-|" + simplelemma + "|wn|rc|sriob|sriobv|sr|" + depverb + "|" + tense + "|" + assertype + "|iobph|php|phi|" + pp + "\n");
                            } else {
                                outfile.write(linearr[wordcolumn] + "|" + simplepos + "|siob|st|sy|-|" + simplelemma + "|" + linearr[wncolumn] + "|rc|sriob|sriobv|sr|" + depverb + "|" + tense + "|" + assertype + "|iobph|php|phi|" + pp + "\n");
                            }
                        }
                        outfile.write("\n");
                        numsent++;
                        sentence = null;
                    }
                }

                // IF LAST SENTENCE DOES NOT ENDED CORRECTLY
                if (sentence != null) {
                    String[] linearr;
                    String tense = "-";
                    String assertype = "-";
                    int numtok = 0;
                    int nextverbpositioncover = -1;
                    int nextverb = 1;
                    wordposition = -1;
                    String verb[];
                    String depverb = "-";
                    if (verbs.size() != 0) {
                        verb = verbs.get(0);
                        wordposition = Integer.parseInt(verb[0]);
                        tense = verb[2];
                        assertype = verb[3];
                        depverb = verb[1];
                        if (verbs.size() > 1) {
                            nextverbpositioncover = Integer.parseInt(verbs.get(1)[0]);
                            nextverbpositioncover = wordposition + ((nextverbpositioncover - wordposition) / 2);
                        }
                    }
                    for (int i = 0; i < sentence.size(); i++) {
                        String sline = sentence.get(i);
                        linearr = sline.split("\\|");
                        numtok++;
                        String simplepos = linearr[poscolumn];
                        String simplelemma = linearr[lemmacolumn];
                        String pp = "-";
                        if (linearr[poscolumn].matches("(IN|TO)")) {
                            // if pp is just after end of last one is a multi-pp (2word)
                            if (!pp.equals("-") && i > 0 && sentence.get(i - 1).split("\\|")[poscolumn].matches("(IN|TO)")) {
                                pp += "_" + linearr[wordcolumn];
                            } else {
                                pp += linearr[wordcolumn];
                            }
                        }
                        if (linearr[poscolumn].startsWith("V")) {
                            pp = "-";
                        }
                        if (lang.equalsIgnoreCase("es")) {
                            if (simplepos.length() > 2) {
                                if (simplepos.startsWith("V")) {
                                    simplepos = simplepos.substring(0, 4);
                                } else {
                                    if (simplepos.startsWith("N")) {
                                        simplepos = simplepos.substring(0, 2) + simplepos.substring(3, 4); // N (Common or Proper) (S or Plural)
                                    } else {
                                        simplepos = simplepos.substring(0, 2);
                                    }
                                }
                            }
                            if (linearr[wordcolumn].matches("([0-9]+[0-9./:,-]*|" + NUMEK.numbers_re_ES + ")")) {
                                simplepos = "CD";
                            }
                        }

                        if (lang.equalsIgnoreCase("en")) {
                            if (!simplepos.matches("(?i)(NP|NPS|NNP|NNPS)")) {
                                simplelemma = simplelemma.toLowerCase(); // lemma to lower case
                            }
                        }

                        if (numtok == nextverbpositioncover) {
                            verb = verbs.get(nextverb);
                            nextverb++;
                            wordposition = Integer.parseInt(verb[0]);
                            tense = verb[2];
                            assertype = verb[3];
                            depverb = verb[1];
                            if (verbs.size() > (nextverb)) {
                                nextverbpositioncover = Integer.parseInt(verbs.get(nextverb)[0]);
                                nextverbpositioncover = wordposition + ((nextverbpositioncover - wordposition) / 2);
                            }
                        }

                        if (wncolumn == -1) {
                            outfile.write(linearr[wordcolumn] + "|" + simplepos + "|siob|st|sy|-|" + simplelemma + "|wn|rc|sriob|sriobv|sr|" + depverb + "|" + tense + "|" + assertype + "|iobph|php|phi|" + pp + "\n");
                        } else {
                            outfile.write(linearr[wordcolumn] + "|" + simplepos + "|siob|st|sy|-|" + simplelemma + "|" + linearr[wncolumn] + "|rc|sriob|sriobv|sr|" + depverb + "|" + tense + "|" + assertype + "|iobph|php|phi|" + pp + "\n");
                        }
                    }
                    outfile.write("\n");
                    numsent++;
                    sentence = null;
                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }*/


    /**
     * Returns the input file, plus one column only filled for each verb
     *
     *
     * example:
     *
     * (A0*)    *
     * (V*)     *
     * (A1*     (A0*)
     * *        (V*)
     * *
     * *)       (AM-TMP*)
     * (A2*)    *
     * (A3*)    (A2*)
     *
     * Output:  verb1->A0,V,A1,A2,A3
     *          verb2->A0,V,AM-TMP,A2
     *
     * ** PULS HACK FOR NUM-LENGTH IN THE 3rd column
     *
     * @return outputfilename
     */
    public static String getVerbRoleconfig(PipesFile pipesfile) {
        String outputfile = pipesfile.getFile().toString() + "-roleconfig";
        int numline = 0;
        try {
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));

            int verbcolumn = pipesfile.getColumn("verb"); // each verb will have a roles column
            int last_desc_column = pipesfile.getLastDescColumn();
            ArrayList<String> sentence = null;

            try {
                String line;
                int numsent = 0;
                ArrayList<SRLColParser> rolesverbs = null;
                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    String[] linearr = line.split("\\|");
                    if (linearr.length >= pipesfile.getPipesDescArrCount()) {
                        if (sentence == null) {
                            sentence = new ArrayList();
                            if (linearr.length - pipesfile.getPipesDescArrCount() > 0) {
                                rolesverbs = new ArrayList<SRLColParser>();
                            }
                        }
                        if (!linearr[verbcolumn].equals("-")) {
                            rolesverbs.add(new SRLColParser(linearr[verbcolumn], "*", 0));
                        }
                        sentence.add(line);
                    } else {
                        int numtok = 0;
                        // Parse roles
                        for (String sline : sentence) {
                            linearr = sline.split("\\|");
                            if (rolesverbs != null) {
                                //System.out.println(sline+" "+rolesverbs.size()+"\n");
                                for (int srlcol = 0; srlcol < rolesverbs.size(); srlcol++) {
                                    SRLColParser tempsrl = rolesverbs.get(srlcol);
                                    tempsrl.parse(linearr[pipesfile.getPipesDescArrCount() + srlcol]);
                                    rolesverbs.set(srlcol, tempsrl);
                                }
                            }
                        }
                        // Write file plus rolesconf
                        String rolesconf = "-";
                        int verbact = 0;
                        for (String sline : sentence) {
                            linearr = sline.split("\\|");
                            numtok++;

                            if (rolesverbs != null && !linearr[verbcolumn].equals("-")) {
                                rolesconf = rolesverbs.get(verbact).getRoleconf();
                                verbact++;
                            } else {
                                rolesconf = "-";
                            }

                            for (int i = 0; i < linearr.length - 1; i++) {
                                // There are roles columns in the sentence
                                if (i == last_desc_column) {
                                    outfile.write(linearr[i] + "|" + rolesconf + "|");
                                } else {
                                    // HACK FOR NUM-LENGHT
                                    if (i == 3) {
                                        if (linearr[0].matches("[0-9]+")) {
                                            linearr[i] = "" + linearr[0].length();
                                        } else {
                                            linearr[i] = "-";
                                        }
                                    }
                                    outfile.write(linearr[i] + "|");
                                }
                            }
                            // There arent roles columns in the sentences
                            if (linearr.length - 1 == last_desc_column) {
                                outfile.write(linearr[linearr.length - 1] + "|" + rolesconf);
                            } else {
                                outfile.write(linearr[linearr.length - 1]);
                            }
                            outfile.write("\n");
                            rolesconf = "-";
                        }


                        outfile.write("|\n");
                        numsent++;
                        sentence = null;
                        rolesverbs = null;
                    }
                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns the input file, simplifying all the roles columns in just one
     *
     * The simplification process consists in getting always the smallest available role
     * taking into account the roles columns order. That is to say, if we beggin using roles
     * of the first column and the we switch to the second we only can go back to the first
     * if the role starts in this line (X*. THAT PREVENTS US TO MIX ROLES.
     *
     * example:
     *                      SIMPLIFICATION
     * (A0*)    *           (A0*)
     * (V*)     *           (V*)
     * (A1*     (A0*)       (A0*)
     * *        (V*)        (V*)
     * *                    *           ---> PREVENTED TO BE A1
     * *)       (AM-TMP*)   (AM-TMP*)
     * (A2*)    *           (A2*)       ----> NOW WE CAN GO BACK
     * (A3*)    (A2*)       (A2*)       ----> THE PREFERENCE IF EQ LENGHT IS FOR THE LAST ONE
     *
     * @param pipesfile the pipesfile over which the simple roles must be obtained
     * 
     * @return outputfilename
     */
    public static String getSimpleRoles(PipesFile pipesfile) {
        String outputfile = pipesfile.getFile().toString() + "-simpleroles";
        int numline = 0;
        try {
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));

            int verbcolumn = pipesfile.getColumn("verb"); // each verb will have a roles column
            int poscolumn = pipesfile.getColumn("pos");
            int wordcolumn = pipesfile.getColumn("(word|token)");
            int rccolumn = pipesfile.getColumn("roleconf");
            ArrayList<String> sentence = null;

            try {
                String[] wordmem = new String[4];
                wordmem[2] = "-";
                wordmem[1] = "-";
                wordmem[0] = "-";
                String line;
                int numsent = 0;
                ArrayList<SRLColParser> rolesverbs = null;
                ArrayList<String> roleconf = null;
                ArrayList<String> tenses = null; // tense(pseudo-aspect) //present-perfect == past
                ArrayList<String> assertype = null; // afirmative - negative

                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    String[] linearr = line.split("\\|");
                    if (linearr.length >= pipesfile.getPipesDescArrCount()) {
                        wordmem[3] = wordmem[2];
                        wordmem[2] = wordmem[1];
                        wordmem[1] = wordmem[0];
                        wordmem[0] = linearr[wordcolumn];

                        if (sentence == null) {
                            sentence = new ArrayList();
                            if (linearr.length - pipesfile.getPipesDescArrCount() > 0) {
                                rolesverbs = new ArrayList<SRLColParser>();
                                tenses = new ArrayList<String>();
                                assertype = new ArrayList<String>();
                                roleconf = new ArrayList<String>();
                            }
                        }

                        if (!linearr[verbcolumn].equals("-")) {
                            String tense = "-";
                            String assetype = "positive";
                            rolesverbs.add(new SRLColParser(linearr[verbcolumn], "*", 0));
                            roleconf.add(linearr[rccolumn]);


                            if (pipesfile.getLanguage().equalsIgnoreCase("es")) {
                                if (wordmem[1].matches("(no|nunca|jamas)") || (wordmem[1].matches("(se|fu.+|he|has|ha|hemos|habéis|han|había)") && wordmem[2].matches("(no|nunca|jamás)")) || (wordmem[3].matches("(no|nunca|jamás)") && wordmem[2].equals("se") && wordmem[1].matches("(he|has|ha|hemos|habéis|han|había)"))) {
                                    assetype = "negative";
                                }
                                tense = linearr[poscolumn].substring(1, 4);

                                if (tense.equals("PAS") || tense.equals("IMP")) {
                                    tense = "past";
                                } else {
                                    if (tense.equals("PRE")) {
                                        tense = "present";
                                    } else {
                                        if (tense.equals("FUT")) {
                                            tense = "future";
                                        } else {
                                            if (tense.equals("CON")) {
                                                tense = "conditional";
                                            } else {
                                            }
                                        }
                                    }
                                }

                                if (linearr[poscolumn].length() >= 7 && linearr[poscolumn].substring(4, 7).equals("past")) {
                                    tense = "present-perfect";
                                }


                            } else {

                                if (tense.equals("-") && (wordmem[1].matches("(?:.*ed|(?:was|were|did)(?:n't)?|been)") || (wordmem[2].matches("(?:.*ed|was|were|did|been)") && (wordmem[1].equals("to") || wordmem[1].matches("(not|n't)"))))) {
                                    tense = "past";
                                } // TODO might be continuous
                                if (wordmem[1].matches("had(n't)?") || (wordmem[2].equals("had") && wordmem[1].matches("(not|n't|to)"))) {
                                    tense = "past-perfect";
                                }
                                if (wordmem[1].matches("(have|has|'ve)(n't)?") || (wordmem[2].matches("(have|has|'ve)") && wordmem[1].matches("(not|n't|to)"))) {
                                    tense = "present-perfect";
                                }
                                if (tense.equals("-") && wordmem[3].matches("(will|wo)") && wordmem[2].matches("(not|n't|have)") && wordmem[1].equals("be|to")) {
                                    tense = "future";
                                }
                                if (tense.equals("-") && wordmem[3].equals("will") && wordmem[2].equals("have") && wordmem[1].equals("to")) {
                                    tense = "future";
                                }
                                // generic hack for futures like will start crying, will start to cry, will|won't have to/be
                                if (tense.equals("-") && (wordmem[1].equals("will") || wordmem[1].equals("won't") || wordmem[2].equals("will") || wordmem[3].equals("will") || (wordmem[3].equals("wo") && wordmem[2].equals("n't")) || (wordmem[2].equals("wo") && wordmem[1].equals("n't")))) {
                                    tense = "future";
                                }
                                if (tense.equals("-") && wordmem[2].equals("going") && wordmem[1].matches("to")) {
                                    tense = "future";
                                }
                                if (tense.equals("-") && (wordmem[1].equals("would") || ((wordmem[2].equals("would") || wordmem[2].equals("wouldn't")) && wordmem[1].matches("(?:not|n't|be|would(?:n't)?)")) || wordmem[3].equals("would") && wordmem[2].matches("(?:not|n't)") && wordmem[1].equals("be"))) {
                                    tense = "conditional";
                                }

                                if (tense.equals("-") && (wordmem[1].matches("(?:is|are|do)(?:n't)?") || (wordmem[2].matches("(?:is|are|do)") && wordmem[1].matches("(?:not|n't)")))) {
                                    tense = "present";
                                } // TODO might be continuous


                                if (tense.equals("-") && (linearr[poscolumn].matches("VB(?:D|N)") || (linearr[poscolumn].equals("AUX") && wordmem[0].matches("(?i)(?:was|were)")))) {
                                    tense = "past";
                                }
                                if (tense.equals("-") && (linearr[poscolumn].matches("VB(?:P|Z)") || (linearr[poscolumn].equals("AUX") && wordmem[0].matches("(?i)(?:was|were)")))) {
                                    tense = "present";
                                }
                                if (tense.equals("-") && (linearr[poscolumn].equals("VBG"))) { // can be improved...
                                    tense = "present";
                                }

                                if (!tense.equals("-")) {
                                    if (wordmem[1].matches("(?:.*n't|not|never)") || (wordmem[2].matches("(?:.*n't|not|never)")) || (wordmem[3].equals("not") && wordmem[2].equals("going") && wordmem[1].equals("to"))) {
                                        assetype = "negative";
                                    }
                                }
                            }

                            // TODO HANDLE INFINITIVES...
                            if (!tense.equals("-")) {
                                if (!tense.endsWith("-continuous") && (pipesfile.getLanguage().equalsIgnoreCase("en") && linearr[wordcolumn].endsWith("ing") || pipesfile.getLanguage().equalsIgnoreCase("es") && linearr[wordcolumn].endsWith("ndo"))) {
                                    tense += "-continuous";
                                }
                            }

                            if (tense.equals("-")) {
                                tense = "present";
                            }

                            tenses.add(tense);
                            assertype.add(assetype);
                        }
                        sentence.add(line);
                    } else {
                        int numtok = 0;
                        int active_roles_col = 0;
                        for (String sline : sentence) {
                            linearr = sline.split("\\|");
                            numtok++;
                            // Roles
                            String currentrole = "O";
                            String currentverb = "-";
                            String currentrc = "-";
                            String currentIOB2 = "";
                            String currentTense = "present";
                            String currentAssertype = "positive";
                            if (rolesverbs != null) {

                                int currentrolesize = 100;
                                int currentcol = 0;
                                for (int srlcol = 0; srlcol < rolesverbs.size(); srlcol++) {
                                    // Parse roles
                                    SRLColParser tempsrl = rolesverbs.get(srlcol);
                                    tempsrl.parse(linearr[pipesfile.getPipesDescArrCount() + srlcol]);
                                    rolesverbs.set(srlcol, tempsrl);
                                    // Choose roles
                                    if (rolesverbs.get(srlcol).getSize() > 0 && rolesverbs.get(srlcol).getSize() <= currentrolesize) {
                                        if (srlcol >= active_roles_col || linearr[pipesfile.getPipesDescArrCount() + srlcol].startsWith("(")) {
                                            if (linearr[pipesfile.getPipesDescArrCount() + srlcol].startsWith("(")) {
                                                currentIOB2 = "B-";
                                            } else {
                                                currentIOB2 = "I-";
                                            }
                                            currentrole = rolesverbs.get(srlcol).getRole();
                                            currentverb = rolesverbs.get(srlcol).getVerb();
                                            currentrc = roleconf.get(srlcol);
                                            currentrolesize = rolesverbs.get(srlcol).getSize();
                                            currentcol = srlcol;
                                            currentTense = tenses.get(srlcol);
                                            currentAssertype = assertype.get(srlcol);
                                        }
                                    }
                                }
                                if (rolesverbs.size() == 1) {
                                    currentverb = rolesverbs.get(0).getVerb();
                                    currentTense = tenses.get(0);
                                }
                                active_roles_col = currentcol;
                            }
                            // "currentrole" in the new column simplerole
                            for (int i = 0; i < pipesfile.getPipesDescArrCount() - 1; i++) {
                                outfile.write(linearr[i] + "|");
                            }
                            //System.err.println("tense="+currentTense);
                            if (pipesfile.getLanguage().equalsIgnoreCase("es") && !linearr[verbcolumn].equals("-")) {
                                currentrole = "V";
                            }
                            outfile.write(currentrc + "|" + currentIOB2 + currentrole + "|" + currentIOB2 + currentrole + "+" + currentverb + "|" + currentrole + "|" + currentverb + "|" + currentTense + "|" + currentAssertype + "\n");
                        }
                        outfile.write("|\n");
                        numsent++;
                        sentence = null;
                        rolesverbs = null;
                    }
                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns the input file plus 4 columns
     *  1 a column indicating the BIO of the main phrases (arguments)
     *  2 a column indicating the position of the current token in this phrase
     *  3 a column indicating if the token is the header of the phrase
     *  4 a column (only for PP) indicating if the token is the secondary header of the phrase (NN, ADJ, ADV)
     *
     * @return outputfilename
     */
    public static String getMainPhrasesPPdetail(PipesFile pipesfile) {
        String outputfile = pipesfile.getFile().toString() + "-mainphrases";
        int numline = 0;
        try {
            int syntcolumn = pipesfile.getColumn("synt");
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            ArrayList<String> sentence = null;
            int POScol = pipesfile.getColumn("pos");
            int wordcol = pipesfile.getColumn("(word|token)");
            try {
                String line;
                int numsent = 0;
                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    String[] linearr = line.split("\\|");
                    if (linearr.length >= pipesfile.getPipesDescArrCount()) {
                        if (sentence == null) {
                            sentence = new ArrayList();
                        }
                        sentence.add(line);
                    } else {
                        //System.out.println("\n");
                        int numtok = 0;
                        int phra_id = 0;
                        SyntColParser syntparser = new SyntColParser();
                        String currentmainphrase = "O";
                        ArrayList<String[]> completemainphrase = new ArrayList<String[]>();
                        for (String sline : sentence) {
                            linearr = sline.split("\\|");
                            numtok++;
                            // Synt
                            boolean hasClosingBrakets = false;
                            if (linearr[syntcolumn].indexOf(')') != -1) {
                                hasClosingBrakets = true;
                            }
                            if (hasClosingBrakets) {
                                syntparser.parse(linearr[syntcolumn].substring(0, linearr[syntcolumn].indexOf(')')));
                            } else {
                                syntparser.parse(linearr[syntcolumn]);
                            }

                            currentmainphrase = syntparser.getCurrentMainPhraseBIO();
                            if (currentmainphrase.equals("O") || (currentmainphrase.startsWith("B-") && completemainphrase.size() > 0)) {
                                if (completemainphrase.size() > 0) {
                                    phra_id++;
                                    String PPdetail = "-";
                                    if (completemainphrase.get(0)[pipesfile.getPipesDescArrCount()].equals("B-PP")) {
                                        for (String[] tmp_linearr : completemainphrase) {
                                            if (tmp_linearr[POScol].matches("(IN|TO)")) {
                                                if (PPdetail.equals("-")) {
                                                    PPdetail = tmp_linearr[wordcol];
                                                } else {
                                                    PPdetail += "_" + tmp_linearr[wordcol];
                                                }
                                            } else {
                                                break;
                                            }
                                        }
                                    }
                                    for (String[] tmp_linearr : completemainphrase) {
                                        for (int i = 0; i < tmp_linearr.length; i++) {
                                            outfile.write(tmp_linearr[i] + "|");
                                        }
                                        outfile.write("phra" + phra_id + "|" + PPdetail + "\n");
                                    }
                                    completemainphrase.clear();
                                }
                                if (currentmainphrase.equals("O")) {
                                    for (int i = 0; i < pipesfile.getPipesDescArrCount(); i++) {
                                        outfile.write(linearr[i] + "|");
                                    }
                                    outfile.write(syntparser.getCurrentMainPhraseBIO() + "|" + syntparser.getCurrentPositionInMainPhrase() + "|-|-\n");
                                } else {
                                    String[] tmp_linearr = new String[pipesfile.getPipesDescArrCount() + 2];
                                    for (int i = 0; i < pipesfile.getPipesDescArrCount(); i++) {
                                        tmp_linearr[i] = linearr[i];
                                    }
                                    tmp_linearr[pipesfile.getPipesDescArrCount()] = currentmainphrase;
                                    tmp_linearr[pipesfile.getPipesDescArrCount() + 1] = ((Integer) syntparser.getCurrentPositionInMainPhrase()).toString();
                                    completemainphrase.add(tmp_linearr);
                                }
                            } else {
                                String[] tmp_linearr = new String[pipesfile.getPipesDescArrCount() + 2];
                                for (int i = 0; i < pipesfile.getPipesDescArrCount(); i++) {
                                    tmp_linearr[i] = linearr[i];
                                }
                                tmp_linearr[pipesfile.getPipesDescArrCount()] = currentmainphrase;
                                tmp_linearr[pipesfile.getPipesDescArrCount() + 1] = ((Integer) syntparser.getCurrentPositionInMainPhrase()).toString();
                                completemainphrase.add(tmp_linearr);
                            }

                            //System.out.println(sline + " - " + syntparser.getParlevel());
                            if (hasClosingBrakets) {
                                //System.out.println(syntparser.getFull() + " \n---> " + syntparser.getCurrent());
                                syntparser.parse(linearr[syntcolumn].substring(linearr[syntcolumn].indexOf(')')));
                            }
                            //System.out.println(sline + " - " + syntparser.getParlevel());

                        }

                        if (completemainphrase.size() > 0) {
                            phra_id++;
                            String PPdetail = "-";
                            if (completemainphrase.get(0)[pipesfile.getPipesDescArrCount()].equals("B-PP")) {
                                for (String[] tmp_linearr : completemainphrase) {
                                    if (tmp_linearr[POScol].matches("(IN|TO)")) {
                                        if (PPdetail.equals("-")) {
                                            PPdetail = tmp_linearr[wordcol];
                                        } else {
                                            PPdetail += "_" + tmp_linearr[wordcol];
                                        }
                                    } else {
                                        break;
                                    }
                                }
                            }
                            for (String[] tmp_linearr : completemainphrase) {
                                for (int i = 0; i < tmp_linearr.length; i++) {
                                    outfile.write(tmp_linearr[i] + "|");
                                }
                                outfile.write("phra" + phra_id + "|" + PPdetail + "\n");
                            }
                            completemainphrase.clear();
                        }

                        if (syntparser.getParlevel() != 0) {
                            throw new Exception("Syntactic Parser ended with no 0 parlevel (" + syntparser.getParlevel() + ").");
                        }
                        //System.out.println(numline);

                        outfile.write("|\n");
                        numsent++;
                        sentence = null;
                        syntparser = null;
                    }
                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns
     * A static n window context (default 9) whenever it is possible...
     *
     * @return outputfilename
     */
    public static String getStaticWin(PipesFile pf) {
        String outputfile = pf.getFile().toString() + ".StaticWin-features";
        int numline = 0;


        try {
            // Window configuration
            int window_size = 9; // must be a odd number
            if (window_size % 2 != 1) {
                throw new Exception("Window size must be a positive odd number (1,3,5,7,etc.)");
            }
            int half_win_size = (window_size - 1) / 2;

            BufferedReader pipesreader = new BufferedReader(new FileReader(pf.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            ArrayList<String> sentence = null;
            int POScol = pf.getColumn("pos");
            int wordcol = pf.getColumn("(word|token)");
            int lemmacol = pf.getColumn("lemma");
            try {
                String curr_fileid = "";
                String curr_sentN = "";
                String line;
                String[] linearr;
                int numsent = 0;
                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    linearr = line.split("\\|");
                    if (curr_fileid.equals("")) {
                        curr_fileid = linearr[0];
                    }
                    if (curr_sentN.equals("")) {
                        curr_sentN = linearr[1];
                    }
                    //System.out.println(curr_fileid+" "+curr_sentN+" "+linearr[0]+" "+linearr[1]+"\n");
                    if (curr_fileid.equals(linearr[0]) && curr_sentN.equals(linearr[1])) {
                        //System.out.println(curr_fileid+" adding "+curr_sentN+"\n");
                        if (sentence == null) {
                            sentence = new ArrayList();
                        }
                        sentence.add(line);
                    } else {
                        // update curr_markers
                        curr_fileid = linearr[0];
                        curr_sentN = linearr[1];

                        //System.out.println("Processing "+curr_fileid+" "+curr_sentN+" "+linearr[0]+" "+linearr[1]+"\n");
                        String[] lemma_win = new String[window_size];
                        String[] pos_win = new String[window_size];
                        String lepo = "-";
                        for (int numtok = 0; numtok < sentence.size(); numtok++) {
                            //System.out.println("processing token "+numtok+" size="+sentence.size());
                            for (int i = 0; i < window_size; i++) {
                                int position = numtok + i - half_win_size;
                                if (position < 0 || position >= sentence.size()) {
                                    lemma_win[i] = "-";
                                    pos_win[i] = "-";
                                } else {
                                    linearr = sentence.get(position).split("\\|");
                                    lemma_win[i] = linearr[lemmacol];
                                    pos_win[i] = linearr[POScol];
                                    if (position == numtok) {
                                        lepo = linearr[lemmacol] + "+" + linearr[POScol];
                                    }
                                }
                            }
                            outfile.write(sentence.get(numtok));
                            // write lemma window
                            for (int i = 0; i < window_size; i++) {
                                outfile.write("|" + lemma_win[i]);
                            }
                            // write lemma bigrams
                            for (int i = 0; i < window_size - 1; i++) {
                                outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1]);
                            }
                            // write lemma trigrams
                            for (int i = 0; i < window_size - 2; i++) {
                                outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1] + "+" + lemma_win[i + 2]);
                            }

                            // write POS window
                            for (int i = 0; i < window_size; i++) {
                                outfile.write("|" + pos_win[i]);
                            }
                            // write POS bigrams
                            for (int i = 0; i < window_size - 1; i++) {
                                outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1]);
                            }
                            // wirte POS trigrams
                            for (int i = 0; i < window_size - 2; i++) {
                                outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1] + "+" + pos_win[i + 2]);
                            }

                            outfile.write("|" + lepo + "\n");

                        }

                        numsent++;
                        sentence = null;
                        sentence = new ArrayList();
                        sentence.add(line);

                    }


                }

                if (sentence != null) {
                    String[] lemma_win = new String[window_size];
                    String[] pos_win = new String[window_size];
                    String lepo = "-";
                    for (int numtok = 0; numtok < sentence.size(); numtok++) {
                        for (int i = 0; i < window_size; i++) {
                            int position = numtok + i - half_win_size;
                            if (position < 0 || position >= sentence.size()) {
                                lemma_win[i] = "-";
                                pos_win[i] = "-";
                            } else {
                                linearr = sentence.get(position).split("\\|");
                                lemma_win[i] = linearr[lemmacol];
                                pos_win[i] = linearr[POScol];
                                if (position == numtok) {
                                    lepo = linearr[lemmacol] + "+" + linearr[POScol];
                                }
                            }
                        }
                        outfile.write(sentence.get(numtok));
                        // write lemma window
                        for (int i = 0; i < window_size; i++) {
                            outfile.write("|" + lemma_win[i]);
                        }
                        // write lemma bigrams
                        for (int i = 0; i < window_size - 1; i++) {
                            outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1]);
                        }
                        // write lemma trigrams
                        for (int i = 0; i < window_size - 2; i++) {
                            outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1] + "+" + lemma_win[i + 2]);
                        }

                        // write POS window
                        for (int i = 0; i < window_size; i++) {
                            outfile.write("|" + pos_win[i]);
                        }
                        // write POS bigrams
                        for (int i = 0; i < window_size - 1; i++) {
                            outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1]);
                        }
                        // wirte POS trigrams
                        for (int i = 0; i < window_size - 2; i++) {
                            outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1] + "+" + pos_win[i + 2]);
                        }

                        outfile.write("|" + lepo + "\n");
                    }

                }



            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * Returns
     * A Dynamic n window context (default 9) syntactically motivated whenever it is possible...
     *
     * @return outputfilename
     */
    public static String getDynamicWin(PipesFile pf) {
        String outputfile = pf.getFile().toString() + ".DynamicWin-features";
        int numline = 0;

        try {
            // Window configuration
            int window_size = 9; // must be a odd number
            if (window_size % 2 != 1) {
                throw new Exception("Window size must be a positive odd number (1,3,5,7,etc.)");
            }
            int half_win_size = (window_size - 1) / 2;

            BufferedReader pipesreader = new BufferedReader(new FileReader(pf.getFile()));
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            ArrayList<String> sentence = null;
            int POScol = pf.getColumn("pos");
            int wordcol = pf.getColumn("(word|token)");
            int lemmacol = pf.getColumn("lemma");
            int syntcolumn = pf.getColumn("synt");
            ArrayList<Integer[]> mainPhrasesSpan = null;

            try {
                String curr_fileid = "";
                String curr_sentN = "";
                String line;
                String[] linearr;
                int numsent = 0;
                while ((line = pipesreader.readLine()) != null) {
                    numline++;
                    linearr = line.split("\\|");
                    if (curr_fileid.equals("")) {
                        curr_fileid = linearr[0];
                    }
                    if (curr_sentN.equals("")) {
                        curr_sentN = linearr[1];
                    }

                    if (curr_fileid.equals(linearr[0]) && curr_sentN.equals(linearr[1])) {
                        if (sentence == null) {
                            sentence = new ArrayList();
                        }
                        sentence.add(line);
                    } else {
                        // update curr_markers
                        curr_fileid = linearr[0];
                        curr_sentN = linearr[1];

                        //System.out.println("\n");
                        SyntColParser syntparser = new SyntColParser();
                        for (String sline : sentence) {
                            linearr = sline.split("\\|");
                            // Synt
                            boolean hasClosingBrakets = false;
                            if (linearr[syntcolumn].indexOf(')') != -1) {
                                hasClosingBrakets = true;
                            }
                            if (hasClosingBrakets) {
                                syntparser.parse(linearr[syntcolumn].substring(0, linearr[syntcolumn].indexOf(')')));
                            } else {
                                syntparser.parse(linearr[syntcolumn]);
                            }
                            if (hasClosingBrakets) {
                                syntparser.parse(linearr[syntcolumn].substring(linearr[syntcolumn].indexOf(')')));
                            }
                        }
                        mainPhrasesSpan = syntparser.getMainPhrasesSpan();


                        //System.out.println("Processing "+curr_fileid+" "+curr_sentN+" "+linearr[0]+" "+linearr[1]+"\n");
                        String[] lemma_win = new String[window_size];
                        String[] pos_win = new String[window_size];
                        String lepo = "-";
                        int current_main_phrase = 0;
                        Integer[] current_mainPhraseSpan = null;

                        if (mainPhrasesSpan != null && mainPhrasesSpan.size() > 0) {
                            current_mainPhraseSpan = new Integer[2];
                            current_mainPhraseSpan = mainPhrasesSpan.get(current_main_phrase);
                        }

                        for (int numtok = 0; numtok < sentence.size(); numtok++) {
                            //System.out.println("processing token "+numtok+" size="+sentence.size());
                            if (mainPhrasesSpan != null && current_mainPhraseSpan != null && numtok > current_mainPhraseSpan[1]) {
                                if (mainPhrasesSpan.size() > (current_main_phrase + 1)) {
                                    current_main_phrase++;
                                    current_mainPhraseSpan = mainPhrasesSpan.get(current_main_phrase);
                                }
                            }
                            String[] numtok_linearr = sentence.get(numtok).split("\\|");
                            for (int i = 0; i < window_size; i++) {
                                int position = numtok + i - half_win_size;
                                if (position < 0 || position >= sentence.size()) {
                                    lemma_win[i] = "-";
                                    pos_win[i] = "-";
                                } else {
                                    linearr = sentence.get(position).split("\\|");
                                    if (((current_mainPhraseSpan != null
                                            && !numtok_linearr[POScol].matches("(V.*|AUX|MD)")
                                            && (position < current_mainPhraseSpan[0] || position > current_mainPhraseSpan[1] || numtok < current_mainPhraseSpan[0] || numtok > current_mainPhraseSpan[1]))
                                            || (numtok_linearr[POScol].matches("(V.*|AUX|MD)") && (position < numtok - 2 || position > numtok + 2 || !linearr[POScol].matches("(V.*|AUX|MD)"))))
                                            && position != numtok) {
                                        lemma_win[i] = "-";
                                        pos_win[i] = "-";
                                    } else {
                                        lemma_win[i] = linearr[lemmacol];
                                        pos_win[i] = linearr[POScol];
                                        if (position == numtok) {
                                            lepo = linearr[lemmacol] + "+" + linearr[POScol];
                                        }
                                    }
                                }
                            }
                            outfile.write(sentence.get(numtok));
                            // write lemma window
                            for (int i = 0; i < window_size; i++) {
                                outfile.write("|" + lemma_win[i]);
                            }
                            // write lemma bigrams
                            for (int i = 0; i < window_size - 1; i++) {
                                outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1]);
                            }
                            // write lemma trigrams
                            for (int i = 0; i < window_size - 2; i++) {
                                outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1] + "+" + lemma_win[i + 2]);
                            }

                            // write POS window
                            for (int i = 0; i < window_size; i++) {
                                outfile.write("|" + pos_win[i]);
                            }
                            // write POS bigrams
                            for (int i = 0; i < window_size - 1; i++) {
                                outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1]);
                            }
                            // wirte POS trigrams
                            for (int i = 0; i < window_size - 2; i++) {
                                outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1] + "+" + pos_win[i + 2]);
                            }

                            outfile.write("|" + lepo + "\n");
                        }

                        numsent++;
                        syntparser = null;
                        sentence = null;
                        sentence = new ArrayList();
                        sentence.add(line);

                    }


                }

                if (sentence != null) {

                    SyntColParser syntparser = new SyntColParser();
                    for (String sline : sentence) {
                        linearr = sline.split("\\|");
                        // Synt
                        boolean hasClosingBrakets = false;
                        if (linearr[syntcolumn].indexOf(')') != -1) {
                            hasClosingBrakets = true;
                        }
                        if (hasClosingBrakets) {
                            syntparser.parse(linearr[syntcolumn].substring(0, linearr[syntcolumn].indexOf(')')));
                        } else {
                            syntparser.parse(linearr[syntcolumn]);
                        }
                        if (hasClosingBrakets) {
                            syntparser.parse(linearr[syntcolumn].substring(linearr[syntcolumn].indexOf(')')));
                        }
                    }
                    mainPhrasesSpan = syntparser.getMainPhrasesSpan();


                    String[] lemma_win = new String[window_size];
                    String[] pos_win = new String[window_size];
                    String lepo = "-";
                    int current_main_phrase = 0;
                    Integer[] current_mainPhraseSpan = null;
                    if (mainPhrasesSpan != null && mainPhrasesSpan.size() > 0) {
                        current_mainPhraseSpan = new Integer[2];
                        current_mainPhraseSpan = mainPhrasesSpan.get(current_main_phrase);
                    }

                    for (int numtok = 0; numtok < sentence.size(); numtok++) {
                        //System.out.println("processing token "+numtok+" size="+sentence.size());
                        if (current_mainPhraseSpan != null && current_mainPhraseSpan != null && numtok > current_mainPhraseSpan[1]) {
                            if (mainPhrasesSpan.size() > (current_main_phrase + 1)) {
                                current_main_phrase++;
                                current_mainPhraseSpan = mainPhrasesSpan.get(current_main_phrase);
                            }
                        }
                        String[] numtok_linearr = sentence.get(numtok).split("\\|");
                        for (int i = 0; i < window_size; i++) {
                            int position = numtok + i - half_win_size;
                            if (position < 0 || position >= sentence.size()) {
                                lemma_win[i] = "-";
                                pos_win[i] = "-";
                            } else {
                                linearr = sentence.get(position).split("\\|");
                                if (((current_mainPhraseSpan != null
                                        && !numtok_linearr[POScol].matches("(V.*|AUX|MD)")
                                        && (position < current_mainPhraseSpan[0] || position > current_mainPhraseSpan[1] || numtok < current_mainPhraseSpan[0] || numtok > current_mainPhraseSpan[1]))
                                        || (numtok_linearr[POScol].matches("(V.*|AUX|MD)") && (position < numtok - 2 || position > numtok + 2 || !linearr[POScol].matches("(V.*|AUX|MD)"))))
                                        && position != numtok) {
                                    lemma_win[i] = "-";
                                    pos_win[i] = "-";
                                } else {
                                    lemma_win[i] = linearr[lemmacol];
                                    pos_win[i] = linearr[POScol];
                                    if (position == numtok) {
                                        lepo = linearr[lemmacol] + "+" + linearr[POScol];
                                    }
                                }
                            }
                        }
                        outfile.write(sentence.get(numtok));
                        // write lemma window
                        for (int i = 0; i < window_size; i++) {
                            outfile.write("|" + lemma_win[i]);
                        }
                        // write lemma bigrams
                        for (int i = 0; i < window_size - 1; i++) {
                            outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1]);
                        }
                        // write lemma trigrams
                        for (int i = 0; i < window_size - 2; i++) {
                            outfile.write("|" + lemma_win[i] + "+" + lemma_win[i + 1] + "+" + lemma_win[i + 2]);
                        }

                        // write POS window
                        for (int i = 0; i < window_size; i++) {
                            outfile.write("|" + pos_win[i]);
                        }
                        // write POS bigrams
                        for (int i = 0; i < window_size - 1; i++) {
                            outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1]);
                        }
                        // wirte POS trigrams
                        for (int i = 0; i < window_size - 2; i++) {
                            outfile.write("|" + pos_win[i] + "+" + pos_win[i + 1] + "+" + pos_win[i + 2]);
                        }

                        outfile.write("|" + lepo + "\n");

                    }
                }






            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    @Deprecated
    public static String GetPairSpecialTempEval2_features(PipesFile pipesfile, String plainmodel) {
        String outputfile = null;
        int linen = 0;
        try {
            boolean usingNotUTF8tool = false;
            outputfile = plainmodel + ".TempEval2-features";
            String filename = "";
            if (pipesfile.getLanguage().equalsIgnoreCase("EN")) {
                if (pipesfile.getFile().getAbsolutePath().contains(".roth")) {
                    filename = pipesfile.getFile().getAbsolutePath().substring(pipesfile.getFile().getCanonicalPath().lastIndexOf('/') + 1, pipesfile.getFile().getAbsolutePath().lastIndexOf(".roth"));
                    usingNotUTF8tool = true;
                } else {
                    filename = pipesfile.getFile().getAbsolutePath().substring(pipesfile.getFile().getCanonicalPath().lastIndexOf('/') + 1, pipesfile.getFile().getAbsolutePath().lastIndexOf(".freeling"));
                }
            }
            if (pipesfile.getLanguage().equalsIgnoreCase("ES")) {
                filename = pipesfile.getFile().getAbsolutePath().substring(pipesfile.getFile().getCanonicalPath().lastIndexOf('/') + 1, pipesfile.getFile().getAbsolutePath().lastIndexOf(".freeling"));
                usingNotUTF8tool = true;
            }
            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
            BufferedReader modelreader = new BufferedReader(new FileReader(plainmodel));

            try {
                int sentence = 0, tokn = 0;
                int tokcolumn = pipesfile.getColumn("(tok|word).*");
                String pipesline;
                String[] pipesarr = null;
                char cmodel = '\0';
                char cmodel_prev = '\0';
                int offset = -1;
                boolean readmodel = true;

                /*int token_leading_blanks = 0;int token_leading_tabs = 0;int token_leading_newlines = 0;*/
                String leadingBlanksString = "";

                while ((pipesline = pipesreader.readLine()) != null) {
                    linen++;
                    //System.err.println(pipesline);
                    if (pipesline.trim().length() > 1) {


                        pipesarr = pipesline.split("\\|");
                        String token = pipesarr[tokcolumn];
                        int token_offset = -1;
                        String paired_token = "";

                        int cn = 0;
                        while (true) {
                            if (readmodel) {
                                cmodel_prev = cmodel;
                                if ((cmodel = (char) modelreader.read()) == -1) { // NOTE: does not work if no EOF char \0A
                                    if (cn == token.length()) {
                                        break; // save last token end of file
                                    } else {
                                        throw new Exception("Premature end of model file");
                                    }
                                }
                                if(cmodel=='\uffff'){
                                        break; // save last token end of file                                    
                                }
                                offset++;
                            } else {
                                readmodel = true;
                            }

                            char cpipes = '\0';
                            if (cn >= token.length()) {
                                if (usingNotUTF8tool) {
                                    if (StringUtils.isISO_8859_1(cmodel)) {
                                        readmodel = false;
                                        break;
                                    } else { // delayed token mode for non-ISO desperate cases
                                        cpipes = 'a';
                                    }
                                } else {
                                    readmodel = false;
                                    break;
                                }
                            } else {
                                cpipes = token.charAt(cn);
                            }


                            //System.out.println("offset=" + offset + " cmodel(" + cmodel + ") cpipes(" + cpipes + ")");
                            if (Character.toLowerCase(cpipes) == Character.toLowerCase(cmodel) || (cmodel == '|' && cpipes == '-')) {
                                if (cmodel == '|') {
                                    paired_token += "&#124;"; //scape char for features
                                } else {
                                    paired_token += cmodel;
                                }
                                if (token_offset == -1) {
                                    token_offset = offset;
                                }

                                // multi-dashes problem ('---' is translated by e.g. Roth to '-')
                                if (usingNotUTF8tool && cmodel == '-' && cn == token.length() - 1) {
                                    // read a new char (cmodel) if not end of file to check multi-dash
                                    if (!((cmodel = (char) modelreader.read()) == -1)) {
                                        readmodel = false;
                                        offset++;
                                        if (cmodel == '-') {
                                            cn--;
                                        }
                                        //if (cmodel == ' ' || cmodel == '\n' || cmodel == '\r' || cmodel == '\t') {
                                        //cn++;
                                        //readmodel = true;
                                        //}
                                    }
                                } else {
                                    readmodel = true;
                                }
                                //readmodel = true;

                            } else {
                                if (cmodel == ' ' || cmodel == '\t' || cmodel == '\n' || cmodel == '\r') {
                                    cn--;
                                    /* DEPRECATED: if ((cmodel == ' ' || cmodel == '\t') && token_offset == -1) {token_leading_blanks++;}if (cmodel == '\n' && token_offset == -1) {token_leading_newlines++;                                        }*/
                                    if (token_offset == -1 && paired_token.equals("")) {
                                        if (cmodel == ' ') {
                                            leadingBlanksString += "s";
                                        } else {
                                            if (cmodel == '\t') {
                                                leadingBlanksString += "t";
                                            } else {
                                                if (cmodel == '\n') {
                                                    leadingBlanksString += "n";
                                                } else {
                                                    if (Character.toLowerCase(cmodel) == '\r') {
                                                        if ((cmodel = (char) modelreader.read()) != (char) -1) {
                                                            offset++;
                                                            if (Character.toLowerCase(cmodel) != '\n') {
                                                                throw new Exception("End of pipesline not found (rn) " + "offset=" + offset + ". cmodel(" + cmodel + ") found instead.");
                                                            } else {
                                                                //DEPRECATED: token_leading_newlines++;
                                                                leadingBlanksString += "n";
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        // if (cmodel == ' ') {paired_token += "&nbsp;";} // No please
                                        throw new Exception("A space, tab, or newline in the middle of the token cannot be paired, use UTF-8 NLP tools.");
                                    }
                                } else {
                                    // Special for quotes (Roth translates " to `` or '')
                                    if (usingNotUTF8tool && (cmodel == '"' && ((cpipes == '`') || (cpipes == '\'')))) {
                                        if (cn + 1 < token.length() && cpipes == token.charAt(cn + 1)) {
                                            cn += 2;
                                            paired_token += cmodel;
                                        }
                                    } else {
                                        // Special for quotes2 (Roth sudenly changes '' by ``)
                                        if (usingNotUTF8tool && ((cmodel == '\'' || cmodel == '`') && (cpipes == '`' || cpipes == '\''))) {
                                            paired_token += cmodel;
                                        } else {
                                            // multi-dashes problem ('---' is translated by e.g. Roth to '-')
                                            if (usingNotUTF8tool && cmodel == '-' && cmodel_prev == '-') {
                                                paired_token += cmodel;
                                                readmodel = true;
                                                cn--;
                                            } else {
                                                // special for ISO NLP tools
                                                if (usingNotUTF8tool && !StringUtils.isISO_8859_1(cmodel)) {
                                                    paired_token += cmodel;
                                                    readmodel = true;
                                                    cn--;
                                                } else {
                                                    throw new Exception("Distinct chars " + paired_token + " offset=" + offset + " cmodel(" + cmodel + ") cpipes(" + cpipes + ")");
                                                }
                                            }
                                        }

                                    }
                                }
                            }
                            cn++;
                        }
                        // DEPRECATED: outfile.write(filename + "|" + sentence + "|" + tokn + "-" + token_leading_blanks + "-" + token_leading_newlines + "|" + paired_token);
                        outfile.write(filename + "|" + sentence + "|" + tokn + "-" + leadingBlanksString + "|" + paired_token);
                        for (int i = 1; i < pipesarr.length; i++) {
                            outfile.write("|" + pipesarr[i]);
                        }
                        outfile.write("\n");
                        //DEPRECATED: token_leading_blanks = 0;  token_leading_newlines = 0;
                        leadingBlanksString = "";
                        tokn++;
                    } else { // newline new sentence
                        // DEPRECATED: outfile.write(pipesline + "\n"); // ommit this because of sentences
                        tokn = 0;
                        sentence++;
                    }
                }
            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (modelreader != null) {
                    modelreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + "- line:" + linen + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    public static String merge_classik(String extentsfile, String attribsfile, String attrib) {
        String outputfile = null;
        try {
            outputfile = extentsfile + ".TempEval2-features-annotatedWith-attribs";
            //String extentsfile = pipesfile.getFile().getCanonicalPath().substring(0, pipesfile.getFile().getCanonicalPath().indexOf(".")) + "." + elemext + ".TempEval-extents";

            PipesFile keypipes = new PipesFile(extentsfile);
            keypipes.isWellFormedOptimist();

            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader extentsreader = new BufferedReader(new FileReader(extentsfile));
            BufferedReader pipesreader = new BufferedReader(new FileReader(new File(attribsfile)));

            try {
                String extentline;
                String[] extentarr = null;
                String pipesline;
                String[] pipesarr = null;

                while ((extentline = extentsreader.readLine()) != null) {
                    extentarr = extentline.split("\\|");
                    if (pipesarr == null && (pipesline = pipesreader.readLine()) != null) {
                        pipesarr = pipesline.split("\\|");
                    }

                    if (pipesarr != null) {
                        if (pipesarr[0].equals(extentarr[0]) && pipesarr[1].equals(extentarr[1]) && pipesarr[2].equals(extentarr[2])) {
                            outfile.write(extentline + "|" + attrib + "=" + pipesarr[pipesarr.length - 1] + "\n");
                            pipesarr = null;
                        } else {
                            outfile.write(extentline + "|-\n");
                        }
                    } else {
                        outfile.write(extentline + "|-\n");
                    }

                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (extentsreader != null) {
                    extentsreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }



        } catch (Exception e) {
            System.err.println("Errors found (TempEval):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    public static String merge_classik_append(String appendfile, String attribsfile, String attrib) {
        String outputfile = null;
        try {
            outputfile = appendfile + ".TempEval2-features-annotatedWith-attribs-append";
            //String extentsfile = pipesfile.getFile().getCanonicalPath().substring(0, pipesfile.getFile().getCanonicalPath().indexOf(".")) + "." + elemext + ".TempEval-extents";
            String extentsfile = appendfile;

            PipesFile keypipes = new PipesFile(extentsfile);
            keypipes.isWellFormedOptimist();

            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader extentsreader = new BufferedReader(new FileReader(extentsfile));
            BufferedReader pipesreader = new BufferedReader(new FileReader(new File(attribsfile)));

            try {
                String extentline;
                String[] extentarr = null;
                String pipesline;
                String[] pipesarr = null;

                while ((extentline = extentsreader.readLine()) != null) {
                    extentarr = extentline.split("\\|");
                    if (pipesarr == null && (pipesline = pipesreader.readLine()) != null) {
                        pipesarr = pipesline.split("\\|");
                    }

                    if (pipesarr != null) {
                        if (pipesarr[0].equals(extentarr[0]) && pipesarr[1].equals(extentarr[1]) && pipesarr[2].equals(extentarr[2])) {
                            outfile.write(extentline + ";" + attrib + "=" + pipesarr[pipesarr.length - 1] + "\n");
                            pipesarr = null;
                        } else {
                            outfile.write(extentline + "\n");
                        }
                    } else {
                        outfile.write(extentline + "\n");
                    }

                }

            } finally {
                if (pipesreader != null) {
                    pipesreader.close();
                }
                if (extentsreader != null) {
                    extentsreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }



        } catch (Exception e) {
            System.err.println("Errors found (TempEval):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    /**
     * The priority is on the file of the first paramenter
     *
     */
    static String merge_pipes(String primary, String secondary) {
        String outputfile = null;
        try {
            outputfile = primary + "-merged";

            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader extentsreader = new BufferedReader(new FileReader(primary));
            BufferedReader extentsreader2 = new BufferedReader(new FileReader(secondary));


            try {
                String extentline;
                String[] extentarr = null;
                String extentline2;
                String[] extentarr2 = null;
                PipesFile keypipes = new PipesFile(primary);
                keypipes.isWellFormedOptimist();
                int iob2col1 = keypipes.getColumn("element\\(IOB2\\)");
                keypipes = new PipesFile(secondary);
                keypipes.isWellFormedOptimist();
                int iob2col2 = keypipes.getColumn("element\\(IOB2\\)");

                boolean firstO = true;

                while ((extentline = extentsreader.readLine()) != null) {
                    extentarr = extentline.split("\\|");

                    if ((extentline2 = extentsreader2.readLine()) == null) {
                        throw new Exception("Secondary file ended prematurely.");
                    }
                    extentarr2 = extentline2.split("\\|");

                    if (!extentarr[iob2col1].equals("O")) {
                        if (iob2col1 == (extentarr.length - 1)) {
                            outfile.write(extentline + "|-\n");
                        } else {
                            outfile.write(extentline + "\n");
                        }
                        firstO = true;
                    } else {
                        if (firstO && extentarr2[iob2col2].startsWith("I-")) {
                            String tmpelem = extentarr2[iob2col2].substring(2);
                            extentline2 = extentline2.replaceAll("\\|I-" + tmpelem, "\\|B-" + tmpelem);
                        }
                        if (iob2col2 == (extentarr2.length - 1)) {
                            outfile.write(extentline2 + "|-\n");
                        } else {
                            outfile.write(extentline2 + "\n");
                        }
                        firstO = false;
                    }

                }

            } finally {
                if (extentsreader != null) {
                    extentsreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }



        } catch (Exception e) {
            System.err.println("Errors found (TempEval):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    public static String putids(String pipes) {
        String outputfile = null;
        try {
            outputfile = pipes + "-ids";
            HashMap<String, Integer> ids = new HashMap<String, Integer>();

            PipesFile keypipes = new PipesFile(pipes);
            keypipes.isWellFormedOptimist();

            BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader extentsreader = new BufferedReader(new FileReader(pipes));

            try {
                String extentline;
                String[] extentarr = null;
                int iob2col = keypipes.getColumn("element\\(IOB2\\)");

                while ((extentline = extentsreader.readLine()) != null) {
                    extentarr = extentline.split("\\|");
                    if (extentarr[iob2col].startsWith("B-")) {
                        String element = extentarr[iob2col].substring(extentarr[iob2col].lastIndexOf("-") + 1).toLowerCase();
                        Integer id = 1;
                        if (ids.containsKey(element)) {
                            ids.put(element, ids.get(element) + 1);
                            id = ids.get(element);
                        } else {
                            ids.put(element, 1);
                        }
                        if (iob2col == (extentarr.length - 1)) {
                            outfile.write(extentline + "|" + element.substring(0, 1) + "id=" + element.substring(0, 1) + id + "\n");
                        } else {
                            if (extentarr[iob2col + 1].length() > 1) {
                                outfile.write(extentline + ";" + element.substring(0, 1) + "id=" + element.substring(0, 1) + id + "\n");
                            } else {
                                outfile.write(extentline.substring(0, extentline.lastIndexOf("|") + 1) + element.substring(0, 1) + "id=" + element.substring(0, 1) + id + "\n");
                            }
                        }
                    } else {
                        if (iob2col == (extentarr.length - 1)) {
                            outfile.write(extentline + "|-\n");
                        } else {
                            outfile.write(extentline + "\n");
                        }
                    }

                }

            } finally {
                if (extentsreader != null) {
                    extentsreader.close();
                }
                if (outfile != null) {
                    outfile.close();
                }
            }



        } catch (Exception e) {
            System.err.println("Errors found (TempEval):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return outputfile;
    }

    public static void clean(String dir) {
        Process p;
        try {
            String[] command3 = {"/bin/sh", "-c", "rm -rf " + dir + "*TempEval2* " + dir + "*roleconf*"};
            p = Runtime.getRuntime().exec(command3);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                String line;
                while ((line = stdInput.readLine()) != null) {
                    System.err.println(line);
                }
            } finally {
                if (stdInput != null) {
                    stdInput.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Errors found (TIMEE):\n\t" + e.toString() + "\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }
    }
}
// Remove if there are no problems
/*    public static String GetPairSpecialTempEval2_features_treetagger(PipesFile pipesfile, String plainmodel) {
String outputfile = null;
try {
outputfile = plainmodel + ".TempEval2-features";
String filename = "";
if (pipesfile.getLanguage().equalsIgnoreCase("EN")) {
filename = pipesfile.getFile().getAbsolutePath().substring(pipesfile.getFile().getCanonicalPath().lastIndexOf('/') + 1, pipesfile.getFile().getAbsolutePath().lastIndexOf(".treetag"));
}
BufferedWriter outfile = new BufferedWriter(new FileWriter(outputfile));
BufferedReader pipesreader = new BufferedReader(new FileReader(pipesfile.getFile()));
BufferedReader modelreader = new BufferedReader(new FileReader(plainmodel));

try {
int sentence = 0, tokn = 0;
int tokcolumn = pipesfile.getColumn("(tok|word).*");
String pipesline;
String[] pipesarr = null;
char cmodel = '\0';
int offset = -1;
boolean readmodel = true;

int token_leading_blanks = 0;
int token_leading_newlines = 0;

while ((pipesline = pipesreader.readLine()) != null) {

//System.out.println(pipesline);
if (pipesline.trim().length() > 1) {
pipesarr = pipesline.split("\\|");

String token = pipesarr[tokcolumn];
int token_offset = -1;
String paired_token = "";


for (int cn = 0; cn < token.length(); cn++) {
char cpipes = token.charAt(cn);
if (readmodel) {
if ((cmodel = (char) modelreader.read()) == -1) {
throw new Exception("Premature end of model file");
}
offset++;
} else {
readmodel = true;
}

//System.out.println("offset=" + offset + " cmodel(" + cmodel + ") cpipes(" + cpipes + ")");
if (Character.toLowerCase(cpipes) == Character.toLowerCase(cmodel)) {
paired_token += cmodel;
if (token_offset == -1) {
token_offset = offset;
}
// multi-dashes problem
if (cmodel == '-' && cn == token.length() - 1) {
// read a new char (cmodel) if not end of file to check multi-dash
if (!((cmodel = (char) modelreader.read()) == -1)) {
readmodel = false;
offset++;
if (cmodel == '-') {
cn--;
}
if (cmodel == ' ' || cmodel == '\n' || cmodel == '\r' || cmodel == '\t') {
cn++;
readmodel = true;
}
}
}

} else {
if (cmodel == ' ' || cmodel == '\t' || cmodel == '\n' || cmodel == '\r') {
cn--;
if ((cmodel == ' ' || cmodel == '\t') && token_offset == -1) {
token_leading_blanks++;
}
if (cmodel == '\n' && token_offset == -1) {
token_leading_newlines++;
}
} else {
// Special for quotes
if (cmodel == '"' && ((cpipes == '`') || (cpipes == '\''))) {
if (cn + 1 < token.length() && cpipes == token.charAt(cn + 1)) {
cn += 2;
paired_token += cmodel;
}
} else {
if (((cmodel == '\'' || cmodel == '`') && (cpipes == '`' || cpipes == '\'')) || (cmodel == '—')) {
paired_token += cmodel;
} else {

throw new Exception("Distinct chars " + paired_token + " offset=" + offset + " cmodel(" + cmodel + ") cpipes(" + cpipes + ")");

}
}
}
}
}

///////////////////////////////////////////

//outfile.write(pipesfile.getFile().getName()+"|"+sentence+"|"+token);
if (!pipesarr[1].equalsIgnoreCase("NP")) {
pipesarr[2] = pipesarr[2].toLowerCase(); // lemma to lower case
}
outfile.write(filename + "|" + sentence + "|" + tokn + "-" + token_leading_blanks + "-" + token_leading_newlines + "|" + pipesarr[0] + "|" + pipesarr[1] + "|-|-|-|-|" + pipesarr[2] + "|-|-|-|-|-|-|-|-|-|-|-|-\n");
token_leading_blanks = 0;
token_leading_newlines = 0;
tokn++;
} else { // newline new sentence
//System.out.println("cmodel(" + cmodel + ")");
if (Character.toLowerCase(cmodel) != '\n' && Character.toLowerCase(cmodel) != '\r' && Character.toLowerCase(cmodel) != ' ' && Character.toLowerCase(cmodel) != '\t') {
if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
System.err.println("Ignore Treetager newline");
}
} else {
if (Character.toLowerCase(cmodel) == ' ' ||  Character.toLowerCase(cmodel) != '\t') {
token_leading_blanks++;
}
if (Character.toLowerCase(cmodel) == '\n') {
token_leading_newlines++;
}
if (Character.toLowerCase(cmodel) == '\r') {
if ((cmodel = (char) modelreader.read()) != (char) -1) {
offset++;
if (Character.toLowerCase(cmodel) != '\n') {
throw new Exception("End of pipesline not found (rn) " + "offset=" + offset + ". cmodel(" + cmodel + ") found instead.");
} else {
token_leading_newlines++;
}
}
}
}
//outfile.write(pipesline + "\n"); // ommit this because of sentences
tokn = 0;
sentence++;
}
}
} finally {
if (pipesreader != null) {
pipesreader.close();
}
if (modelreader != null) {
modelreader.close();
}
if (outfile != null) {
outfile.close();
}
}

} catch (Exception e) {
System.err.println("Errors found (TIMEE):\n\t" + e.toString() + "\n");
if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
e.printStackTrace(System.err);
System.exit(1);
}
return null;
}
return outputfile;
}
 */
