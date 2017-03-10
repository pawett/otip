package com.cognitionis.tipsem;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.cognitionis.external_tools.IMachineLearningMethod;
import com.cognitionis.feature_builder.BaseTokenFeatures;
import com.cognitionis.feature_builder.Classification;
import com.cognitionis.feature_builder.PlainTokenFeatures;
import com.cognitionis.feature_builder.TimexNormalization;
import com.cognitionis.nlp_files.NLPFile;
import com.cognitionis.nlp_files.PipesFile;
import com.cognitionis.nlp_files.PlainFile;
import com.cognitionis.nlp_files.TempEvalFiles;
import com.cognitionis.timeml_basickit.Event;
import com.cognitionis.timeml_basickit.Link;
import com.cognitionis.timeml_basickit.Timex;
import com.cognitionis.tipsem.helpers.ElementFiller;
import com.cognitionis.tipsem.helpers.FileConverter;
import com.cognitionis.tipsem.helpers.FilesHelper;
import com.cognitionis.tipsem.helpers.Logger;
import com.cognitionis.utils_basickit.FileUtils;
import com.cognitionis.wiki_basickit.Wiki_bk;

/**
 * @author Hector Llorens
 * @since 2011
 */

public class OptionHandler {

public static String localDatasetPath = FileUtils.getApplicationPath() + "program-data" + File.separator + "TIMEE-training" + File.separator;
private String[] input_files;
private ArrayList<NLPFile> getInputFiles() throws FileNotFoundException
{
	ArrayList<NLPFile> nlp_files = new ArrayList<NLPFile>();
	if (input_files != null) {
	    //System.err.println("File/s to annotate: " + input_files.length);

	    for (int i = 0; i < input_files.length; i++) {
	        // Check files - exist/encoding
	        File f = new File(input_files[i]);
	        if (!f.exists()) {
	        	
	            Logger.Write("Searching and downloading from Wikipedia...");
	            String wikifile = Wiki_bk.wiki2txt(input_files[i], lang);
	            if (wikifile != null) {
	                f = null;
	                f = new File(wikifile);
	            } else {
	                throw new FileNotFoundException("File does not exist neither locally nor in Wikipedia: " + f);
	            }
	        }

	        if (!f.isFile()) {
	            throw new IllegalArgumentException("Must be a regular file: " + f);
	        }

	        NLPFile nlpfile;
	        nlpfile = (NLPFile) new PlainFile(f.getAbsolutePath());
	        nlpfile.setLanguage(lang);
	        nlp_files.add(nlpfile);
	    }
	}
	return nlp_files;
	
}
private String action_parameters;
private String lang="en";
private TipSemAction action;
private IMachineLearningMethod mlmethod;
private String approach;
private String task;
private String element;
private String strategy;
private boolean rebuild_dataset;
private File test_dir;
private File train_dir;

    
   public OptionHandler(String[] input_files, String action_parameters, String lang) throws Exception
   {
	   this.input_files = input_files;
	   this.action_parameters = action_parameters;
	   if(lang == null || lang.equals(""))
	   {
		   lang = "en";
	   }else{
	   
	   this.lang = lang;
	   }
	   
	   this.mlmethod = null;
		String paramMethod = getParameter("ML");
		if (paramMethod == null || paramMethod.equalsIgnoreCase("crf")) {
			this.mlmethod  =  new com.cognitionis.external_tools.CRF();
		}else
		{
			this.mlmethod  = new com.cognitionis.external_tools.SVM();
		}
		
		String approach = getParameter("approach");
		if (approach == null) {
		    this.approach = "TIPSemB";
		}
		String task = getParameter("task");
		if (task == null) {
		    this.task = "recognition";
		}
		String element = getParameter("element");
		if (element == null) {
		    this.element = "timex";
		}
		String strategy = getParameter("strategy");
		if (strategy == null) {
		    this.strategy = "normal";
		}
		String rebuild_dataset =  getParameter("rebuild_dataset");
		if (rebuild_dataset == null) {
		    this.rebuild_dataset = false;
		}else
		{
			this.rebuild_dataset = Boolean.parseBoolean(rebuild_dataset);
		}
		
		
		String traind = getParameter("train_dir");
		train_dir = null;
		if (traind == null) {
		    train_dir = FilesHelper.GetFileAndCreateDir(localDatasetPath + lang.toUpperCase() + "/train_tml");
		} else {
		    train_dir = FilesHelper.GetFileAndCreateDir(traind);
		    if (!train_dir.exists() || !train_dir.isDirectory()) {
		        throw new Exception("Input " + traind + " does not exist or is not a directory.");
		    }
		}
		
		String testd = getParameter("test_dir");
		test_dir = null;
		
		if (testd == null) {
		    test_dir = FilesHelper.GetFileAndCreateDir(localDatasetPath + lang.toUpperCase() + "/test_tml");
		} else {
		    test_dir = FilesHelper.GetFileAndCreateDir(testd);
		    if (!test_dir.exists() || !test_dir.isDirectory()) {
		        throw new Exception("Input " + testd + " does not exist or is not a directory.");
		    }
		}
			   
   }
    /* IF A COMPANY WANTS TIPSEM:
     * TRY TO FIND STANDARD SENTENCE SEGMENTER JAVA (LIKE TREETAGER ALGORITHM WITH CERTAIN ABREVIATION MANAGEMENT...
     * TRY TO FIND JAVA STANDARD TOKENIZER (LIKE TREETAGER ALGORITHM WITH GOOD AND PREDICTABLE-STABLE BEHAVIOUR)
     * TRY TO FIND JAVA NATIVE CRF, SVM, ETC.., AND POS, AND SYTN-DEP, LEMMA...
     * Replace SRL AND Treetagger otherwise IT HAS TO ACCEPT AND PROBABLY PAY Treetagger and SRL
     */
    public String getParameter(String param) {
        String paramValue = null;
        if (action_parameters != null && action_parameters.contains(param)) {
            if (action_parameters.matches(".*" + param + "=[^,]*,.*")) {
                paramValue = action_parameters.substring(action_parameters.lastIndexOf(param + "=") + param.length() + 1, action_parameters.indexOf(',', action_parameters.lastIndexOf(param + "=")));
            } else {
                if (action_parameters.matches(".*" + param + "=[^,]*")) {
                    paramValue = action_parameters.substring(action_parameters.lastIndexOf(param + "=") + param.length() + 1);
                }
            }
        }
        return paramValue;
    }

    public void doAction(TipSemAction action) {

        try {
            //System.err.println("\n\nDoing action: " + action_parameters.toString() + "\n------------");
        	switch (action) {

        	case UTF_2_ISO_8859_1_SAFE_UTF:
        		FileConverter.ConvertUtfToIso8859(input_files);
        		break;
        	case CONVERT_TML:
        		String convert_to = getParameter("convert_to");
        		FileConverter.ConvertToTml(input_files, convert_to);
        		break;
        	case PLAIN2TE3:
        		FileConverter.ConvertPlainToTe3(input_files);
        		break;
        	case ANNOTATE: 
        		Annotate(new com.cognitionis.external_tools.SVM());
        		break;
        	case ANNOTATECRF:
        		Annotate(new com.cognitionis.external_tools.CRF());
        		break;
        	case TAN:
        		Tan();
        		break;
        	case TML_NFOLD: 
        		TmnFold();
        		break;         
        	case TRAIN_AND_TEST:
        		TrainTest();               
        		break;
        	case TEST_NORMALIZATION_TML: 
        		train_model trainModel = new train_model(null, test_dir, approach,lang, rebuild_dataset, new TemporalInformationProcessingStrategy());
        		trainModel.Normalization_tml("timex");
        		break;
        	case DATASET2TML:
        		FileConverter.ConvertDataSetToTML(input_files, lang);
        		break;
        	case RECOGNITION2TML:
        		FileConverter.ConvertRecognitionToTML(input_files, lang);
        		break;

        	}
        } catch (Exception e) {
            Logger.WriteError("\nErrors found (OptionHandler):\n\t" + e.toString() + "\n", e);
        }

    }

	

	private void TrainTest() throws Exception {
				
		train_model trainModel = new train_model(train_dir, test_dir, approach, lang, rebuild_dataset, mlmethod);
		
		switch(task.toLowerCase())
		{
		case "recognition":
			trainModel.Recognition_tml(element.toLowerCase());
			break;
		case "classification":
			 trainModel.Classification_tml(element.toLowerCase());
			break;
		case "normalization":
			trainModel.NormalizationType_tml("timex");
			break;
		case "categorization":
			trainModel.Categorization_tml(element.toLowerCase());
			break;
		case "idcat":
			trainModel.idcat_tml(strategy);
			break;
		case "all":
		default:
			trainModel.full_tml();
			break;
		}		
	}

	private void TmnFold() throws Exception, IOException {
		double percentage_size_margin = 0.2;
		String nfolds = getParameter("n");
		if (nfolds == null) {
		    nfolds = "5";
		}
		int n = Integer.parseInt(nfolds);
		if (n < 1) {
		    throw new Exception("The number of folds (n) must be greater than 0.");
		}
		if (input_files.length != 1) {
		    throw new Exception("One and only one directory containing tml files is expected.");
		}
		File dir = new File(input_files[0]);
		if (!dir.isDirectory()) {
		    throw new Exception("The input must be a directory.");
		}

		File[] tmlfiles = dir.listFiles(FileUtils.onlyFilesFilter);
		Arrays.sort(tmlfiles, FileUtils.fileSizeDesc);
		// estaria bien que si ya estan procesadas (.d) copie tb los directorios donde corresponda

		if (n > tmlfiles.length) {
		    throw new Exception("The number of folds (n) must be lower or equal than the number of files (" + tmlfiles.length + ").");
		}
		int total_size = 0;
		ArrayList<File> farr = new ArrayList<File>();
		for (int i = 0; i < tmlfiles.length; i++) {
		    total_size += tmlfiles[i].length();
		    farr.add(tmlfiles[i]);
		}
		int fold_size = total_size / n;
		int fold_min_size = (int) ((int) fold_size * percentage_size_margin);
		ArrayList<ArrayList<String>> folds = new ArrayList<ArrayList<String>>();
		ArrayList<String> fold = new ArrayList<String>();
		int current_fold_size = 0;
		//int last_size=farr.size();
		while (farr.size() > 0) {
		    for (int i = 0; i < farr.size(); i++) {
		        if (current_fold_size < fold_size) {
		            if (fold.size() == 0 || current_fold_size + farr.get(i).length() < fold_size) {
		                fold.add(farr.get(i).getCanonicalPath());
		                current_fold_size += farr.get(i).length();
		                farr.remove(i);
		            }
		        }
		    }
		    if (current_fold_size < fold_min_size) {
		        throw new Exception("Impossible folding... with this percentage margin: " + percentage_size_margin);
		    } else {
		        folds.add(fold);
		        fold = null;
		        fold = new ArrayList<String>();
		        current_fold_size = 0;
		    }
		}

		System.out.println(folds);


		/*input must be the dir with the tmlfiles to nfold

		output must be just in the same path below one dir... nameof the dir_numfold

		This will contain the combinations of folds*/

		// GENERATE THE train1, ... trainN
		// test1, ... testN

		/*make array file/size (count total size)(sort by size)
		split size into n parts.
		then start creating groups by the bigger files until they reach the fileseze
		if a group reaches the size in more than the size of the smallest file
		then use the smallest file before

		when the groups are created then with a for bucle generate the Nx2 folders
		with the proper tml_files inside

		then use tml_training tml_testing 10 fold...*/
	}

	private void Tan()
			throws FileNotFoundException, Exception, ParseException, IOException {
		
		ArrayList<NLPFile> nlp_files = getInputFiles();
		TemporalInformationProcessingStrategy tipStrategy = new TemporalInformationProcessingStrategy();
		if (nlp_files.size() <= 0) {
		    throw new Exception("No input files found");
		}

		for (NLPFile nlpfile : nlp_files) {
		    if (!nlpfile.getClass().getSimpleName().equals("PlainFile")) {
		        throw new Exception("TIPSem requires PlainFile files as input. Found: " + nlpfile.getClass().getSimpleName());
		    }

		    String dctvalue = getParameter("dct");
		    Timex dct = null;
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		    if (dctvalue == null) {
		        dctvalue = sdf.format(new Date());
		        dct = new Timex("t0", dctvalue, "DATE", dctvalue, nlpfile.getFile().getName(), -1, -1, true);
		    } else {
		        sdf.setLenient(false);
		        Date tmpdct = null;
		        if ((tmpdct = sdf.parse(dctvalue)) != null) {
		            dctvalue = sdf.format(tmpdct);
		            dct = new Timex("t0", dctvalue, "DATE", dctvalue, nlpfile.getFile().getName(), -1, -1, true);
		        } else {
		            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		            if ((tmpdct = sdf2.parse(dctvalue)) != null) {
		                dctvalue = sdf2.format(tmpdct);
		                dct = new Timex("t0", dctvalue, "DATE", dctvalue, nlpfile.getFile().getName(), -1, -1, true);
		            }
		        }
		    }

		    String ommit_current_implicit = getParameter("ommitcurrent");
		    String ommit_re = getParameter("ommit_re");

		    Logger.Write("File: " + nlpfile.getFile() + " Language:" + nlpfile.getLanguage() + " omitcurrent: " + ommit_current_implicit);


		    File dir = FilesHelper.GetFileAndCreateDir(nlpfile.getFile().getCanonicalPath()  + "_" + approach + "_features" + File.separator);
		   
		    String output = dir + "/" + nlpfile.getFile().getName();
		    FileUtils.copyFileUtil(nlpfile.getFile(), new File(output));

		    // Write dct.tab
		    BufferedWriter dct_writer = new BufferedWriter(new FileWriter(new File(dir + "/" + "dct.tab")));
		    try 
		    {
		        dct_writer.write(nlpfile.getFile().getName() + "\t" + dct.get_value());
		    } 
		    finally 
		    {
		        if (dct_writer != null) {
		            dct_writer.close();
		        }
		    }

		    String features = null;
		    features = PlainTokenFeatures.getFeatures(lang, output, 1, false, "TempEval2-features", approach);

		    Logger.WriteDebug("Recognizing TIMEX3s");
		    
		    String timex = tipStrategy.getTimexProcessing().getRecognition().Test(features, approach + "_rec_timex_" + nlpfile.getLanguage().toUpperCase() + ".CRFmodel");
		    PipesFile nlpfile_temp = new PipesFile(timex);
		    ((PipesFile) nlpfile_temp).isWellFormedOptimist();
		    timex = PipesFile.IOB2check(nlpfile_temp);


		    Logger.WriteDebug("Classifying TIMEX3s");
		    
		    output = Classification.get_classik(timex, lang);
		    String timex_class = tipStrategy.getTimexProcessing().getClassification().Test(output, approach + "_class_timex_" + nlpfile.getLanguage().toUpperCase() + ".CRFmodel");

		    Logger.WriteDebug("Normalizing TIMEX3s (DCT=" + dct.get_value() + ")");
		    
		    output = TimexNormalization.getTIMEN(timex, timex_class, lang);
		    output = tipStrategy.getTimexProcessing().getNormalization().Test(output, approach + "_timen_timex_" + nlpfile.getLanguage().toUpperCase() + ".CRFmodel");
		    String timex_norm = TimexNormalization.get_normalized_values(output, lang);

		    output = TempEvalFiles.merge_classik(timex, timex_class, "type");
		    String timex_merged = BaseTokenFeatures.merge_classik_append(output, timex_norm, "value");

		    Logger.WriteDebug("Recognizing EVENTs");
		    
		    String event = tipStrategy.getEventProcessing().getRecognition().Test(features, approach + "_rec_event_" + nlpfile.getLanguage().toUpperCase() + ".CRFmodel");
		    nlpfile_temp = new PipesFile(event);
		    ((PipesFile) nlpfile_temp).isWellFormedOptimist();
		    event = PipesFile.IOB2check(nlpfile_temp);

		    Logger.WriteDebug("Classifying EVENTs");
		    
		    output = Classification.get_classik(event, lang);
		    String event_class = tipStrategy.getEventProcessing().getClassification().Test(output, approach + "_class_event_" + nlpfile.getLanguage().toUpperCase() + ".CRFmodel");

		    String event_merged = TempEvalFiles.merge_classik(event, event_class, "class");

		    String all_merged = PipesFile.merge_pipes(timex_merged, event_merged);

		    all_merged = BaseTokenFeatures.putids(all_merged);

		    PipesFile pf = new PipesFile(all_merged);
		    pf.isWellFormedOptimist();

		    HashMap<String, Timex> DCTs = new HashMap<String, Timex>();
		    
		    if (dct != null)
		    {
		        DCTs.put(nlpfile.getFile().getName(), dct);
		    }
		    HashMap<String, Timex> timexes = new HashMap<String, Timex>();
		    HashMap<String, Event> events = new HashMap<String, Event>();
		    HashMap<String,Link> links = new HashMap<String,Link>();

		    timexes.put("t0", new Timex("t0", dct.get_value(), "DATE", dct.get_value(), "-", -1, -1));
		    ElementFiller.get_elements_old(pf, timexes, events, links, ommit_re);

		    // generate possible links / features

		    // categorize them

		    Logger.WriteDebug("Generating TML");
		    
		    // convert links into file-links Hash
		    HashMap<String, HashMap<String,Link>> linksHash = new HashMap<String, HashMap<String,Link>>();
		    linksHash.put(nlpfile.getFile().getName(), links);
		    // TML output: add links to the output
		    output = FileConverter.pipes2tml(pf, DCTs, null, linksHash);

		    // TAn output (XML): conteo, ordenation,...
		    // FAKE TAN


		    // Timexes: x
		    // References
		    // Date
		    // Time
		    // Durations
		    // Sets
		    // Events: y
		    // Occurrences
		    //...

		    // Distribution
		    // Reference a: n events
		    // Reference b: m events
		    // ...


		    // TAn a representación pictonica, html+jgraphs, o gd...

		    // Navegació frases:
		    // Events + importants x referencies + importants (+ separades, amb + events)
		    // Click -> events de la referència + referencies colindants

		    // OUTPUT FILES
		    (new File(output)).renameTo(new File(output.substring(0, output.lastIndexOf("_" + approach + "_features" + File.separator)) + ".tml"));
		    (new File(dir + "/" + nlpfile.getFile().getName())).renameTo(nlpfile.getFile());

		    Logger.WriteDebug("Getting TAn");
		    
		    TAn.fake_TAn(timexes, events, links, nlpfile.getFile().getAbsolutePath(), dct.get_value(), ommit_current_implicit);

		    output = TAn.pipes2navphp(pf);
		    (new File(output)).renameTo(new File(output.substring(0, output.lastIndexOf("_" + approach + "_features" + File.separator)) + ".nav.php"));

		    BaseTokenFeatures.clean(dir + File.separator);

		    /*System.err.print("Executing for " + output);
		    output = CRF.test(output, eventmodel);
		    System.err.println(" saving output in " + output);*/


		    // falta abrir output i convertir a xml (fusionando las columnas...)
		    // si es con classificación normalización, etc.. -> varios ficheros...
		}
	}


	private void Annotate(IMachineLearningMethod method)
			throws FileNotFoundException, Exception {
		ArrayList<NLPFile> nlp_files = getInputFiles();

		if (nlp_files.size() <= 0) 
		{
			throw new Exception("No input files found");
		}

	
		String entities = getParameter("entities");
		if (entities != null)
		{
			entities=entities.replaceAll("\\s+", "");
			if(!entities.matches("(timex|event|tlink|tlink-rel-only|timex;event|event;timex|timex;event;tlink|event;timex;tlink)[;]?")) 
			{
				throw new Exception("entities must follow (timex|event|tlink|tlink-rel-only|timex;event|event;timex|timex;event;tlink|event;timex;tlink)[;]? pattern. Found: " + entities + ".");
			}
		}else
		{
			entities="timex;event;tlink";
		}

		String inputf = getParameter("inputf");
		
		if (inputf == null) 
		{
			if(!entities.matches("(tlink|tlink-rel-only)"))
				inputf = "plain";
			else
				inputf = "te3input-with-entities"; // just for informative purposes
		}
		
		if (!entities.matches("(tlink|tlink-rel-only)"))
		{
			inputf=inputf.replaceAll("\\s+", "").toLowerCase();
			if(!(inputf.equals("te3input") || inputf.equals("plain")))
				throw new Exception("inputf must be plain (default) or te3input. Found: " + inputf + ".");
		}
		

		String dctvalue = getParameter("dct");

		// consider null DCT for history domain in the future... not now.

		for (NLPFile nlpfile : nlp_files) 
		{
			Logger.Write("\n\nFile: " + nlpfile.getFile() + " Language:" + nlpfile.getLanguage());
			String output=null;
			TIP tip = new TIP(nlpfile, inputf, approach, lang, entities, dctvalue, null, new TemporalInformationProcessingStrategy(method));
			if(!entities.matches("(tlink|tlink-rel-only)"))
				output= tip.Annotate();
			else
				output=tip.annotate_links(null);

			// OUTPUT FILES
			(new File(output)).renameTo(new File(output.substring(0, output.lastIndexOf("_" + approach + "_features" + File.separator)) + ".tml"));
			//(new File(dir + "/" + nlpfile.getFile().getName())).renameTo(nlpfile.getFile());
		}
	}
	
	
	
}
