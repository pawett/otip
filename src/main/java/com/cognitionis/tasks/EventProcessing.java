package com.cognitionis.tasks;

import com.cognitionis.external_tools.IMachineLearningMethod;

public class EventProcessing {

	private IMachineLearningMethod recognition;
	private IMachineLearningMethod classification;

	public IMachineLearningMethod getRecognition() {
		return recognition;
	}
	public void setRecognition(IMachineLearningMethod recognition) {
		this.recognition = recognition;
	}

	public IMachineLearningMethod getClassification() {
		return classification;
	}
	public void setClassification(IMachineLearningMethod classification) {
		this.classification = classification;
	}

}
