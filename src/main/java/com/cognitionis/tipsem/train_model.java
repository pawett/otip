package com.cognitionis.tipsem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.cognitionis.external_tools.CRF;
import com.cognitionis.external_tools.IMachineLearningMethod;
import com.cognitionis.external_tools.SVM;
import com.cognitionis.feature_builder.Classification;
import com.cognitionis.feature_builder.TimexNormalization;
import com.cognitionis.nlp_files.PipesFile;
import com.cognitionis.nlp_files.TempEvalFiles;
import com.cognitionis.nlp_files.XMLFile;
import com.cognitionis.nlp_files.annotation_scorers.Score;
import com.cognitionis.nlp_files.annotation_scorers.Scorer;
import com.cognitionis.tasks.EventProcessing;
import com.cognitionis.tasks.TemporalRelationProcessing;
import com.cognitionis.tasks.TimexProcessing;
import com.cognitionis.timeml_basickit.TML_file_utils;
import com.cognitionis.tipsem.helpers.FileConverter;
import com.cognitionis.tipsem.helpers.FilesHelper;
import com.cognitionis.tipsem.helpers.Logger;
import com.cognitionis.utils_basickit.FileUtils;

public class train_model {

	private File train_dir;
	private File test_dir;
	private String approach;
	private String lang;
	private boolean rebuild_dataset;
	private TemporalInformationProcessingStrategy strategy;
	public train_model(File train_dir, File test_dir, String approach,
			String lang, boolean rebuild_dataset, 
			TemporalInformationProcessingStrategy strategy)
	{
		this.train_dir = train_dir;
		this.test_dir = test_dir;
		this.approach = approach;
		this.lang = lang;
		this.rebuild_dataset = rebuild_dataset;
		this.strategy = strategy;

	}
	
	public train_model(File train_dir, File test_dir, String approach,
			String lang, boolean rebuild_dataset, 
			IMachineLearningMethod method)
	{
		this.train_dir = train_dir;
		this.test_dir = test_dir;
		this.approach = approach;
		this.lang = lang;
		this.rebuild_dataset = rebuild_dataset;
		this.strategy = new TemporalInformationProcessingStrategy();
		TimexProcessing timexP = new TimexProcessing();
		timexP.setClassification(method);
		timexP.setNormalization(method);
		timexP.setRecognition(method);
		strategy.setTimexProcessing(timexP);
		
		EventProcessing eventP = new EventProcessing();
		eventP.setClassification(method);
		eventP.setRecognition(method);
		strategy.setEventProcessing(eventP);
		
		TemporalRelationProcessing temporalRelationP = new TemporalRelationProcessing();
		temporalRelationP.setEvent_DCT(method);
		temporalRelationP.setEvent_timex(method);
		temporalRelationP.setMain_events(method);
		temporalRelationP.setSubordinate_events(method);
		

	}

	public  String localDatasetPath = FileUtils.getApplicationPath() + "program-data/TIMEE-training/";
	public  HashMap<String, String> category_files = new HashMap<String, String>() {

		{
			put("e-t", "base-segmentation.e-t-link-features");
			put("e-dct", "base-segmentation.e-dct-link-features");
			put("e-main", "base-segmentation.e-main-link-features");
			put("e-sub", "base-segmentation.e-sub-link-features");
		}
	};


	/*public void Recognition_tml(String elem) {
		String output = "";
		PipesFile nlpfile;
		Scorer scorer = new Scorer();
		try {
			File dir = new File(train_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {  // mkdir only creates one, mkdirs creates many parent dirs if needed
					throw new Exception("Directory not created...");
				}
			}
			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}

			String model = dir + File.separator + approach + "_rec_" + elem + "_" + lang + "." + strategy.getTimexProcessing().getRecognition() + "model";
			Logger.Write("model: " + model);
			output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);

			output = strategy.getTimexProcessing().getRecognition().Train(output, approach + "_rec_" + elem + ".template");

			(new File(output)).renameTo(new File(model));
			//(new File(output)).renameTo(new File((new File(output)).getCanonicalPath().substring((new File(output)).getName().indexOf(approach))));
			//test Model
			// Hacer opcional por parametro...
			//getFeatures(lang,"test/entities");
			Logger.Write("Test..." + model);

			output = strategy.getTimexProcessing().getRecognition().Test(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", model);

			nlpfile = new PipesFile(output);
			((PipesFile) nlpfile).isWellFormedOptimist();
			String temp = PipesFile.IOB2check(nlpfile);
			(new File(temp)).renameTo(new File(output));

			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));
			nlpfile = new PipesFile(annot);
			((PipesFile) nlpfile).isWellFormedOptimist();

			output = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			(new File(output)).renameTo(new File(annot + "-key"));


			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);

			// AnnotScore results
			Score score = scorer.score(nlpfile, annot + "-key", nlpfile.getColumn("element\\(IOB2\\)"), -1);
			//score.print("attribs");
			score.print("detail");
			//score.print(printopts);
			//score.print("");


		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);  
		}
	}*/

	/*public void Classification_tml(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			File dir = FilesHelper.GetFileAndCreateDir(train_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}


			String model = dir + File.separator + approach + "_class_" + elem + "_" + lang + "." + strategy.getTimexProcessing().getClassification() + "model";

			output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			output = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			output = Classification.get_classik(output, lang);

			output = strategy.getTimexProcessing().getClassification().Train(output, approach + "_class_" + elem + ".template");

			(new File(output)).renameTo(new File(model));
			//(new File(output)).renameTo(new File((new File(output)).getCanonicalPath().substring((new File(output)).getName().indexOf(approach))));
			//test Model
			// Hacer opcional por parametro...
			//getFeatures(lang,"test/entities");
			//System.out.println("Test...");
			output = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			output = Classification.get_classik(output, lang);

			output = strategy.getTimexProcessing().getClassification().Test(output, model);

			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));
			//PipesFile nlpannot = new PipesFile();
            //nlpannot.loadFile(new File(annot));
            //((PipesFile) nlpannot).isWellFormedOptimist();


			key = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			key = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			key = Classification.get_classik(key, lang);


			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);

			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			//score.print("attribs");
			//score.print("detail");
			//score.print(printopts);
			score.print("");

		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e); 
		}
	}*/

	/*public void NormalizationType_tml(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			File dir = FilesHelper.GetFileAndCreateDir(train_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}

			String model = dir + File.separator + approach + "_timen_" + elem + "_" + lang + "." + strategy.getTimexProcessing().getNormalization() + "model";
			output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String features = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			output = Classification.get_classik(features, lang);
			output = TimexNormalization.getTIMEN(features, output, lang);

			output = strategy.getTimexProcessing().getNormalization().Train(output, approach + "_timen_" + elem + ".template");

			(new File(output)).renameTo(new File(model));

			features = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			output = Classification.get_classik(features, lang);
			output = TimexNormalization.getTIMEN(features, output, lang);

			output = strategy.getTimexProcessing().getNormalization().Test(output, model);


			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));

			key = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String keyfeatures = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			key = Classification.get_classik(keyfeatures, lang);
			key = TimexNormalization.getTIMEN(keyfeatures, key, lang);


			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);
			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			score.print("");


		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
		}
	}*/

	/*public void Categorization_tml(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			if (!elem.matches("e-(t|dct|main|sub)")) {
				throw new Exception("elem must match:e-(t|dct|main|sub). Found: " + elem);
			}

			File dir = FilesHelper.GetFileAndCreateDir(train_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}

			String model = dir + File.separator + approach + "_categ_" + elem + "_" + lang + "." + strategy.getTemporalRelationProcessing().getEvent_timex() + "model";
			output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get(elem) + "-annotationKey";

			output = strategy.getTemporalRelationProcessing().getEvent_timex().Train(output, approach + "_categ_" + elem + ".template");

			(new File(output)).renameTo(new File(model));


			output = test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get(elem);

			output = strategy.getTemporalRelationProcessing().getEvent_timex().Test(output, model);


			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));
			//PipesFile nlpannot = new PipesFile();
            //nlpannot.loadFile(new File(annot));
            //((PipesFile) nlpannot).isWellFormedOptimist();
			 
			key = test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get(elem) + "-annotationKey";


			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);

			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			//score.print("attribs");
			//score.print("detail");
			//score.print(printopts);
			score.print("");

		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
		}

	}*/

	// test normalization...
	public void Normalization_tml(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			File dir = FilesHelper.GetFileAndCreateDir(test_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}
			output = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String features = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			output = Classification.get_classik(features, lang);
			output = TimexNormalization.getTIMEN(features, output, lang);
			output = TimexNormalization.get_normalized_values(output, lang);
			Logger.Write(output);
			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));
			key = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String keyfeatures = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			key = Classification.get_classik(keyfeatures, lang);
			key = TimexNormalization.getTIMEN(keyfeatures, key, lang);
			key = TimexNormalization.get_key_normalized_values(key);
			// TempEvalFiles-2 results
			Logger.Write("Testset Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);
			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			score.print("detail");
		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
			System.exit(1);
		}
	}

	public void full_tml() {
		String output = "";
		try 
		{
			File dir_trainmodels = FilesHelper.GetFileAndCreateDir(train_dir.getCanonicalPath() + "-models-" + approach + File.separator);
			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			/*if (re_build_dataset.equalsIgnoreCase("true") || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
            FileConverter.tmldir2features(test_dir, approach, lang);
            }*/

			String model = dir_trainmodels + File.separator + approach + "_rec_timex_" + lang + ".CRFmodel";
			// check if already trained
			if (!(new File(model)).exists()) 
			{
				TrainTimex(dir_trainmodels, model);
				// event
				TrainEvents(dir_trainmodels);
				// links
				TrainLinks(dir_trainmodels);
			}

			// test

			File dir_test_annotation = FilesHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-" + approach + File.separator);
			File dir_test_te3input = FilesHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-input-" + approach + File.separator);

			File[] tmlfiles = test_dir.listFiles(FileUtils.onlyFilesFilter);
			for (int i = 0; i < tmlfiles.length; i++) 
			{
				XMLFile nlpfile =  FilesHelper.GetNLPFile(tmlfiles[i].getAbsolutePath());

				String te3input = TML_file_utils.TML2TE3(nlpfile.getFile().getCanonicalPath());
				String basefile = dir_test_te3input + File.separator + new File(te3input).getName();
				(new File(te3input)).renameTo(new File(basefile));
				nlpfile= FilesHelper.GetNLPFile(basefile);
				nlpfile.setLanguage(lang);
				TIP tip = new TIP(nlpfile, "te3input", approach, lang, null, null, dir_trainmodels.getCanonicalPath(), new TemporalInformationProcessingStrategy());
				output = tip.Annotate();
				(new File(output)).renameTo(new File(dir_test_annotation + File.separator + tmlfiles[i].getName()));
			}

		} catch (Exception e) 
		{
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
			System.exit(1);
		}
	}

	private void TrainLinks(File dir_trainmodels) throws IOException {
		String output;
		String model;
		model = dir_trainmodels + File.separator + approach + "_categ_e-t_" + lang + ".SVMmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-t") + "-annotationKey";
		output = strategy.getTemporalRelationProcessing().getEvent_timex().Train(output, approach + "_categ_e-t.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_categ_e-dct_" + lang + ".SVMmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-dct") + "-annotationKey";
		output = strategy.getTemporalRelationProcessing().getEvent_DCT().Train(output, approach + "_categ_e-dct.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_categ_e-main_" + lang + ".CRFmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-main") + "-annotationKey";
		output = strategy.getTemporalRelationProcessing().getMain_events().Train(output, approach + "_categ_e-main.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_categ_e-sub_" + lang + ".CRFmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-sub") + "-annotationKey";
		output = strategy.getTemporalRelationProcessing().getSubordinate_events().Train(output, approach + "_categ_e-sub.template");
		(new File(output)).renameTo(new File(model));
	}

	private void TrainEvents(File dir_trainmodels) throws IOException {
		String output;
		String model;
		model = dir_trainmodels + File.separator + approach + "_rec_event_" + lang + ".CRFmodel";
		System.out.println("model: " + model);
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "event-extents.tab", "event");
		output = strategy.getEventProcessing().getRecognition().Train(output, approach + "_rec_event.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_class_event_" + lang + ".SVMmodel";
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "event-extents.tab", "event");
		output = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-event", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "event-attributes.tab", "event");
		output = Classification.get_classik(output, lang);
		output = strategy.getEventProcessing().getClassification().Train(output, approach + "_class_event.template");
		(new File(output)).renameTo(new File(model));
	}

	private void TrainTimex(File dir_trainmodels, String model) throws IOException {
		String output;
		//timex
		Logger.Write("model: " + model);
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-extents.tab", "timex");
		output = strategy.getTimexProcessing().getRecognition().Train(output, approach + "_rec_timex.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_class_timex_" + lang + ".SVMmodel";
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-extents.tab", "timex");
		output = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-timex", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-attributes.tab", "timex");
		output = Classification.get_classik(output, lang);
		output = strategy.getTimexProcessing().getClassification().Train(output, approach + "_class_timex.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_timen_timex_" + lang + ".SVMmodel";
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-extents.tab", "timex");
		String features = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-timex", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-attributes.tab", "timex");
		output = Classification.get_classik(features, lang);
		output = TimexNormalization.getTIMEN(features, output, lang);
		output = strategy.getTimexProcessing().getNormalization().Train(output, approach + "_timen_timex.template");
		(new File(output)).renameTo(new File(model));
	}

	public void idcat_tml(String strategy) {
		String output = "";
		try {
			File dir_trainmodels = FilesHelper.GetFileAndCreateDir(train_dir.getCanonicalPath() + "-models-" + approach + File.separator);
			File dir_test_annotation = FilesHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-" + approach + "-links-"+ strategy + File.separator);
			File dir_test_te3input = FilesHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-input4links-" + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) 
			{
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			/*if (re_build_dataset.equalsIgnoreCase("true") || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
            FileConverter.tmldir2features(test_dir, approach, lang);
            }*/

			String model = dir_trainmodels + File.separator + approach + "_categ_e-t_" + lang + ".SVMmodel";
			// check if already trained
			if (!(new File(model)).exists() && !strategy.equalsIgnoreCase("super-baseline")) 
			{
				TrainLinks(dir_trainmodels);
			}

			// test
			File[] tmlfiles = test_dir.listFiles(FileUtils.onlyFilesFilter);
			for (int i = 0; i < tmlfiles.length; i++) 
			{
				XMLFile nlpfile = FilesHelper.GetNLPFile(tmlfiles[i].getAbsolutePath());

				String onlyEntitiesInput = TML_file_utils.TML2onlyEntities(nlpfile.getFile().getCanonicalPath());
				String basefile = dir_test_te3input + File.separator + new File(onlyEntitiesInput).getName();
				(new File(onlyEntitiesInput)).renameTo(new File(basefile));
				nlpfile= FilesHelper.GetNLPFile(basefile);
				nlpfile.setLanguage(lang);
				TIP tip = new TIP(nlpfile,null, approach, lang, null,null, dir_trainmodels.getCanonicalPath(),null);
				output = tip.annotate_links(strategy);
				(new File(output)).renameTo(new File(dir_test_annotation + File.separator + tmlfiles[i].getName()));
			}

		} catch (Exception e) 
		{
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
			System.exit(1);
		}
	}

}
