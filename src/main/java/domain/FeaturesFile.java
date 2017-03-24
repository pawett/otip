package domain;

import java.util.ArrayList;
import java.util.List;

public class FeaturesFile {
	
	private ArrayList<Features> lines;
	private FilesType type;
	
	
	public FeaturesFile(FilesType type)
	{
		lines = new ArrayList<Features>();
		this.type = type;
	}
	
	public void addLine(Features f)
	{
		lines.add(f);
	}
	//TODO:Add correct implementation
	public String getLanguage()
	{
		return "en";
	}
	
	public ArrayList<Features> getLines()
	{
		return lines;
	}
	
	private String printLine(Features f)
	{
		String line = null;
		switch(type)
		{
		case e_dct_link_features:
			line = f.e_dct_link_features();
			break;
		case e_main_link_features:
			line = f.e_main_link_features();
			break;
		case e_t_link_features:
			line = f.e_t_link_features();
			break;
		case freeling:
			line = f.freeling();
			break;
		case freeling_POS2:
			line = f.freeling_POS2();
			break;
		case freeling_WNHyps:
			line = f.freeling_WNHyps();
			break;
		case freeling_WNHyps_POS2:
			line = f.freeling_WNHyps_POS2();
			break;
		case journal2010_stats:
			line = f.journal2010_stats();
			break;
		case journal2010_stats_annotatedWith:
			line = f.journal2010_stats_annotatedWith();
			break;
		case journal2010_stats_annotationKey:
			line = f.journal2010_stats_annotationKey();
			break;
		case paired:
			line = f.paired();
			break;
		case pipes:
			line = f.pipes();
			break;
		case pipes_annotatedWith:
			line = f.pipes_annotatedWith();
			break;
		case pre:
			line = f.pre();
			break;
		case pre_treetag:
			line = f.pre_treetag();
			break;
		case pre_treetag_annotatedWith:
			line = f.pre_treetag_annotatedWith();
			break;
		case pre_treetag_roleconfig:
			line = f.pre_treetag_roleconfig();
			break;
		case pre_treetag_roleconfig_simpleroles:
			line = f.pre_treetag_roleconfig_simpleroles();
			break;
		case pre_treetag_roleconfig_simpleroles_mainphrases:
			line = f.pre_treetag_roleconfig_simpleroles_mainphrases();
			break;
		case pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps:
			line = f.pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps();
			break;
		case pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps_annotationKey:
			line = f.pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps_annotationKey();
			break;
		case roth:
			line = f.roth();
			break;
		case roth_treetag:
			line = f.roth_treetag();
			break;
		case roth_treetag_roleconfig:
			line = f.roth_treetag_roleconfig();
			break;
		case roth_treetag_roleconfig_simpleroles:
			line = f.roth_treetag_roleconfig_simpleroles();
			break;
		case roth_treetag_roleconfig_simpleroles_mainphrases:
			line = f.roth_treetag_roleconfig_simpleroles_mainphrases();
			break;
		case roth_treetag_roleconfig_simpleroles_mainphrases_WNHyps:
			line = f.roth_treetag_roleconfig_simpleroles_mainphrases_WNHyps();
			break;
		case roth_treetag_WNHyps:
			line = f.roth_treetag_WNHyps();
			break;
		case roth_treetag_WNHyps_roleconfig:
			line = f.roth_treetag_WNHyps_roleconfig();
			break;
		case roth_treetag_WNHyps_roleconfig_simpleroles:
			line = f.roth_treetag_WNHyps_roleconfig_simpleroles();
			break;
		case roth_treetag_WNHyps_roleconfig_simpleroles_mainphrases:
			line = f.roth_treetag_WNHyps_roleconfig_simpleroles_mainphrases();
			break;
		case TempEval2_features:
			line = f.TempEval2_features();
			break;
		case TempEval2_features_annotatedWith:
			line = f.TempEval2_features_annotatedWith();
			break;
		case TempEval2_features_annotationKey:
			line = f.TempEval2_features_annotationKey();
			break;
		case TempEval_attributes:
			line = f.TempEval_attributes();
			break;
		case TempEval_bs:
			line = f.TempEval_bs();
			break;
		case TempEval_bs_annotationKey:
			line = f.TempEval_bs_annotationKey();
			break;
		case TempEval_classik_features:
			line = f.TempEval_classik_features();
			break;
		case TempEval_classik_features_annotatedWith:
			line = f.TempEval_classik_features_annotatedWith();
			break;
		case TempEval_extents:
			line = f.TempEval_extents();
			break;
		case TempEval_features_ANT:
			line = f.TempEval_features_ANT();
			break;
		case TempEval_features_annotatedWith:
			line = f.TempEval_features_annotatedWith();
			break;
		case TempEval_features_annotationKey:
			line = f.TempEval_features_annotationKey();
			break;
		case TempEval_normalization:
			line = f.TempEval_normalization();
			break;
		case TempEval_normalization_annotatedWith:
			line = f.TempEval_normalization_annotatedWith();
			break;
		case tok:
			line = f.tok();
			break;
		case treetag:
			line = f.treetag();
			break;
		case treetag_POS2:
			line = f.treetag_POS2();
			break;
		case word:
			line = f.word();
			break;
		}
		return line;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for(Features f : lines)
		{
			sb.append(printLine(f));
			sb.append(System.lineSeparator());
		}
		
		return sb.toString();
	}
	

}
