package com.cognitionis.external_tools;

public interface IMachineLearningMethod {
	String Test(String featuresfile, String modelfile);
	String Train(String featuresfile, String templatefile);
}
