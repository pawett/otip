package com.cognitionis.external_tools;

public interface IMachineLearningMethod {
	String Test(String featuresfile, String models_path, String approach, String type, String language);
	String Train(String featuresfile, String templatefile);
}
