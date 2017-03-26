package com.cognitionis.tipsem;

import com.cognitionis.external_tools.IMachineLearningMethod;
import com.cognitionis.tasks.EventProcessing;
import com.cognitionis.tasks.TemporalRelationProcessing;
import com.cognitionis.tasks.TimexProcessing;

public class TemporalInformationProcessingStrategy {
	
	private TimexProcessing timexProcessing;
	private EventProcessing eventProcessing;
	private TemporalRelationProcessing temporalRelationProcessing;
	
	public TimexProcessing getTimexProcessing()
	{
		return timexProcessing;
	}
	
	public void setTimexProcessing(TimexProcessing timexP)
	{
		timexProcessing = timexP;
	}
	

	public EventProcessing getEventProcessing()
	{
		return eventProcessing;
	}
	
	public void setEventProcessing(EventProcessing eventP)
	{
		eventProcessing = eventP;
	}
	

	public TemporalRelationProcessing getTemporalRelationProcessing()
	{
		return temporalRelationProcessing;
	}
	
	public void setTemporalRelationProcessing(TemporalRelationProcessing temporalRelationP)
	{
		temporalRelationProcessing = temporalRelationP;
	}
	
	public TemporalInformationProcessingStrategy(String lang)
	{
		if (lang.contains("es"))
		{
		timexProcessing = new TimexProcessing();
		timexProcessing.setRecognition(new com.cognitionis.external_tools.CRF());
		timexProcessing.setClassification(new com.cognitionis.external_tools.SVM());
		timexProcessing.setNormalization(new com.cognitionis.external_tools.SVM());
		
		eventProcessing = new EventProcessing();
		eventProcessing.setRecognition(new com.cognitionis.external_tools.CRF());
		eventProcessing.setClassification(new com.cognitionis.external_tools.SVM());
		
		temporalRelationProcessing = new TemporalRelationProcessing();
		temporalRelationProcessing.setEvent_timex(new com.cognitionis.external_tools.SVM());
		temporalRelationProcessing.setEvent_DCT(new com.cognitionis.external_tools.SVM());
		temporalRelationProcessing.setMain_events(new com.cognitionis.external_tools.CRF());
		temporalRelationProcessing.setSubordinate_events(new com.cognitionis.external_tools.CRF());
		}
		
		else //for english
		{
			timexProcessing = new TimexProcessing();
			timexProcessing.setRecognition(new com.cognitionis.external_tools.CRF());
			timexProcessing.setClassification(new com.cognitionis.external_tools.SVM());
			timexProcessing.setNormalization(new com.cognitionis.external_tools.SVM());
			
			eventProcessing = new EventProcessing();
			eventProcessing.setRecognition(new com.cognitionis.external_tools.CRF());
			eventProcessing.setClassification(new com.cognitionis.external_tools.SVM());
			
			temporalRelationProcessing = new TemporalRelationProcessing();
			temporalRelationProcessing.setEvent_timex(new com.cognitionis.external_tools.CRF());
			temporalRelationProcessing.setEvent_DCT(new com.cognitionis.external_tools.CRF());
			temporalRelationProcessing.setMain_events(new com.cognitionis.external_tools.CRF());
			temporalRelationProcessing.setSubordinate_events(new com.cognitionis.external_tools.CRF());
		}
	}
	
	
	public TemporalInformationProcessingStrategy()
	{
		//Default strategy
		timexProcessing = new TimexProcessing();
		timexProcessing.setRecognition(new com.cognitionis.external_tools.CRF());
		timexProcessing.setClassification(new com.cognitionis.external_tools.SVM());
		timexProcessing.setNormalization(new com.cognitionis.external_tools.SVM());
		
		eventProcessing = new EventProcessing();
		eventProcessing.setRecognition(new com.cognitionis.external_tools.CRF());
		eventProcessing.setClassification(new com.cognitionis.external_tools.SVM());
		
		temporalRelationProcessing = new TemporalRelationProcessing();
		temporalRelationProcessing.setEvent_timex(new com.cognitionis.external_tools.CRF());
		temporalRelationProcessing.setEvent_DCT(new com.cognitionis.external_tools.CRF());
		temporalRelationProcessing.setMain_events(new com.cognitionis.external_tools.CRF());
		temporalRelationProcessing.setSubordinate_events(new com.cognitionis.external_tools.CRF());
	}

}
