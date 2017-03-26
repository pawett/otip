package domain;

import java.io.StreamCorruptedException;

import com.cognitionis.tipsem.helpers.Logger;

public class Word 
{
	public Word prev = null;
	public Word next = null;

	public String file = "-";
	public String lid = "-";
	public String eiid = "-";
	public String tid = "-";
	public String e_class = "-";
	public String e_pos = "-";
	public String e_token = "-";
	public String e_tense = "-";
	public String e_tense_aspect = "-";
	public String e_govPP = "-";
	public String e_govTMPSub = "-";
	public String gov_e_class = "-";
	public String gov_e_pos = "-";
	public String gov_e_token = "-";
	public String gov_e_tense_aspect = "-";
	public String eiid1 = "-";
	public String eiid2 = "-";
	public String e1_class = "-";
	public String e1_pos = "-";
	public String e1_token = "-";
	public String e1_tense = "-";
	public String e1_tense_aspect = "-";
	public String e1_govPP = "-";
	public String e1_govTMPSub = "-";
	public String e2_class = "-";
	public String e2_pos = "-";
	public String e2_token = "-";
	public String e2_tense = "-";
	public String e2_tense_aspect = "-";
	public String e2_govPP = "-";
	public String e2_govTMPSub = "-";
	public String syntrel = "-";
	public String t_type = "-";
	public String t_ref = "-";
	public String t_govPP = "-";
	public String t_govTMPSub = "-";
	public String synt_relation = "-";
	public String syntbio = "-";
	public String sentence = "-";
	public String synt = "-";
	public String verb = "-";
	public String roleconf = "roleconf";
	public String simplerolesIOB2 = "-";
	public String simplerolesIOB2_verb = "-";
	public String simpleroles = "simpleroles";
	public String depverb = "depverb";
	public String tense = "tense";
	public String assertype = "-";
	public String iobmainphrase = "-";
	public String mainp_position = "-";
	public String phra_id = "-";
	public String PPdetail = "PPdetatil";
	
	public String phrase_IOB2 = "-";
	public String role_IOB2 = "-";
	public String depv = "-";
	public String role_depv_IOB2 = "-";
	
	public String element_IOB2 = "-";
	public String attribs = "-";
	
	public String token = "-";
	public String pipe = "-";
	
	public String leading_blanks = "-";
	public String elementBIO = "-";
	public String element_attribs = "-";
		
	public String mainphra = "-";
	public String main_position = "-";
	public String sent_num = "sentN";
	public String tok_num = "tokN";
	public String tag = "-";
	public String id = "-";
	public String iid = "-";
	public String attrname = "-";
	public String attrvalue = "-";
	
	public String polarity = "polarity";
	public String mainphrase = "mainphrase"; 
	public String extra1 = "-";
	public String extra2 = "-";
	public String extra3 = "-";
	public String extra4 = "-";
	public String extra5 = "-";
	public String extra6 = "-";
	public String element2 = "-";
	public String rolesconf = "-";
	public String element = "element";
	public String te_type = "-";
	public String DCT = "-";
	public String ref_val = "-";
	public String value = "-";
	public String norm_type2 = "-";
	
	public String classik = "-";
	public String word = "word";
	public String pos = "pos";
	public String lemma = "lemma";
	public String wn = "wn";
	
	public Word()
	{
	
	}
	public Word(String pipeWord, FilesType type)
	{
		try{
			String[] pipesArray = pipeWord.split("\\|");
			if(pipesArray.length <= 0)
				return;
			switch(type)
			{
			case treetag:
				setTreeTag(pipesArray);
				break;
			case TempEval2_features:
				SetTempEval2_features(pipesArray);
				break;
			case TempEval2_features_annotatedWith:
				setTempEval2_features_annotatedWith(pipesArray);
				break;
			case e_dct_link_features:
				set_e_dct_link_features(pipesArray);
				break;
			case e_main_link_features:
				set_e_main_link_features(pipesArray);
				break;
			case e_t_link_features:
				set_e_t_link_features(pipesArray);
				break;
			}
		}catch (Exception e) {
			Logger.WriteError("Errors tying to get Wrod from pipes", e);
			System.exit(1);
		}
		
	}

	

	
	private void set_e_t_link_features(String[] pipesArray) {
		// TODO Auto-generated method stub
		
	}

	private void set_e_main_link_features(String[] pipesArray) {
		// TODO Auto-generated method stub
		
	}

	private void set_e_dct_link_features(String[] pipesArray) {
		// TODO Auto-generated method stub
		
	}

	private void set_e_dct_link_features(String pipeWord) {
		// TODO Auto-generated method stub
		
	}

	public String toString(FilesType type, String fileName, int numSentence, int numWord)
	{
		this.file = fileName;
		this.sent_num = String.valueOf(numSentence);
		this.tok_num = String.valueOf(numWord);
		if(this.prev != null)
		this.tok_num += "-s";
		String line = null;
		switch(type)
		{
		case e_dct_link_features:
			line = e_dct_link_features();
			break;
		case e_main_link_features:
			line = e_main_link_features();
			break;
		case e_t_link_features:
			line = e_t_link_features();
			break;
		case freeling:
			line = freeling();
			break;
		case freeling_POS2:
			line = freeling_POS2();
			break;
		case freeling_WNHyps:
			line = freeling_WNHyps();
			break;
		case freeling_WNHyps_POS2:
			line = freeling_WNHyps_POS2();
			break;
		case journal2010_stats:
			line = journal2010_stats();
			break;
		case journal2010_stats_annotatedWith:
			line = journal2010_stats_annotatedWith();
			break;
		case journal2010_stats_annotationKey:
			line = journal2010_stats_annotationKey();
			break;
		case paired:
			line = paired();
			break;
		case pipes:
			line = pipes();
			break;
		case pipes_annotatedWith:
			line = pipes_annotatedWith();
			break;
		case pre:
			line = pre();
			break;
		case pre_treetag:
			line = pre_treetag();
			break;
		case pre_treetag_annotatedWith:
			line = pre_treetag_annotatedWith();
			break;
		case pre_treetag_roleconfig:
			line = pre_treetag_roleconfig();
			break;
		case pre_treetag_roleconfig_simpleroles:
			line = pre_treetag_roleconfig_simpleroles();
			break;
		case pre_treetag_roleconfig_simpleroles_mainphrases:
			line = pre_treetag_roleconfig_simpleroles_mainphrases();
			break;
		case pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps:
			line = pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps();
			break;
		case pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps_annotationKey:
			line = pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps_annotationKey();
			break;
		case roth:
			line = roth();
			break;
		case roth_treetag:
			line = roth_treetag();
			break;
		case roth_treetag_roleconfig:
			line = roth_treetag_roleconfig();
			break;
		case roth_treetag_roleconfig_simpleroles:
			line = roth_treetag_roleconfig_simpleroles();
			break;
		case roth_treetag_roleconfig_simpleroles_mainphrases:
			line = roth_treetag_roleconfig_simpleroles_mainphrases();
			break;
		case roth_treetag_roleconfig_simpleroles_mainphrases_WNHyps:
			line = roth_treetag_roleconfig_simpleroles_mainphrases_WNHyps();
			break;
		case roth_treetag_WNHyps:
			line = roth_treetag_WNHyps();
			break;
		case roth_treetag_WNHyps_roleconfig:
			line = roth_treetag_WNHyps_roleconfig();
			break;
		case roth_treetag_WNHyps_roleconfig_simpleroles:
			line = roth_treetag_WNHyps_roleconfig_simpleroles();
			break;
		case roth_treetag_WNHyps_roleconfig_simpleroles_mainphrases:
			line = roth_treetag_WNHyps_roleconfig_simpleroles_mainphrases();
			break;
		case TempEval2_features:
			line = TempEval2_features();
			break;
		case TempEval2_features_annotatedWith:
			line = TempEval2_features_annotatedWith();
			break;
		case TempEval2_features_annotationKey:
			line = TempEval2_features_annotationKey();
			break;
		case TempEval_attributes:
			line = TempEval_attributes();
			break;
		case TempEval_bs:
			line = TempEval_bs();
			break;
		case TempEval_bs_annotationKey:
			line = TempEval_bs_annotationKey();
			break;
		case TempEval_classik_features:
			line = TempEval_classik_features();
			break;
		case TempEval_classik_features_annotatedWith:
			line = TempEval_classik_features_annotatedWith();
			break;
		case TempEval_extents:
			line = TempEval_extents();
			break;
		case TempEval_features_ANT:
			line = TempEval_features_ANT();
			break;
		case TempEval_features_annotatedWith:
			line = TempEval_features_annotatedWith();
			break;
		case TempEval_features_annotationKey:
			line = TempEval_features_annotationKey();
			break;
		case TempEval_normalization:
			line = TempEval_normalization();
			break;
		case TempEval_normalization_annotatedWith:
			line = TempEval_normalization_annotatedWith();
			break;
		case tok:
			line = tok();
			break;
		case treetag:
			line = treetag();
			break;
		case treetag_POS2:
			line = treetag_POS2();
			break;
		case word:
			line = word();
			break;
		}
		return line;
	}
	
	 private String AppendPipes(String s)
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append("|");
		 sb.append(s);
		 return sb.toString();
	 }
	 
	 
	 public String e_dct_link_features()
	 { 
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(lid));
		 sb.append(AppendPipes(eiid));
		 sb.append(AppendPipes(tid));
		 sb.append(AppendPipes(e_class));
		 sb.append(AppendPipes(e_pos));
		 sb.append(AppendPipes(e_token));
		 sb.append(AppendPipes(e_tense));
		 sb.append( AppendPipes(e_tense_aspect));
		 sb.append(AppendPipes(e_govPP));
		 sb.append(AppendPipes(e_govTMPSub));
		 sb.append(AppendPipes(gov_e_class));
		 sb.append(AppendPipes(gov_e_pos));
		 sb.append(AppendPipes(gov_e_token));
		 sb.append(AppendPipes(gov_e_tense_aspect));
		 return sb.toString();
	 }
	 public String e_main_link_features()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(lid));
		 sb.append(AppendPipes(eiid1));
		 sb.append(AppendPipes(eiid2));
		 sb.append(AppendPipes(e1_class));
		 sb.append(AppendPipes(e1_pos));
		 sb.append(AppendPipes(e1_token));
		 sb.append(AppendPipes(e1_tense));
		 sb.append(AppendPipes(e1_tense_aspect));
		 sb.append(AppendPipes(e2_class));
		 sb.append(AppendPipes(e2_pos));
		 sb.append(AppendPipes(e2_token));
		 sb.append(AppendPipes(e2_tense));
		 sb.append(AppendPipes(e2_tense_aspect));
		 return sb.toString();
	 }
	 
	 public String e_sub_link_features()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(id));
		 sb.append(AppendPipes(eiid1));
		 sb.append(AppendPipes(eiid2));
		 sb.append(AppendPipes(e1_class));
		 sb.append(AppendPipes(e1_pos));
		 sb.append(AppendPipes(e1_token));
		 sb.append(AppendPipes(e1_tense));
		 sb.append(AppendPipes(e1_tense_aspect));
		 sb.append(AppendPipes(e1_govPP));
		 sb.append(AppendPipes(e1_govTMPSub));
		 sb.append(AppendPipes(e2_class));
		 sb.append(AppendPipes(e2_pos));
		 sb.append(AppendPipes(e2_token));
		 sb.append(AppendPipes(e2_tense));
		 sb.append(AppendPipes(e2_tense_aspect));
		 sb.append(AppendPipes(e2_govPP));
		 sb.append(AppendPipes(e2_govTMPSub));
		 sb.append(AppendPipes(syntrel));
		 return sb.toString();
	 }
	 
	 public String e_t_link_features()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(lid));
		 sb.append(AppendPipes(eiid));
		 sb.append(AppendPipes(tid));
		 sb.append(AppendPipes(e_class));
		 sb.append(AppendPipes(e_pos));
		 sb.append(AppendPipes(e_token));
		 sb.append(AppendPipes(e_tense));
		 sb.append(AppendPipes(e_tense_aspect));
		 sb.append(AppendPipes(e_govPP));
		 sb.append(AppendPipes(e_govTMPSub));
		 sb.append(AppendPipes(t_type));
		 sb.append(AppendPipes(t_ref));
		 sb.append(AppendPipes(t_govPP));
		 sb.append(AppendPipes(t_govTMPSub));
		 sb.append(AppendPipes(synt_relation));
		 return sb.toString();
	 }
	 
	 public String freeling()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(pos));
		 return sb.toString();
	 }
	 
	 public String freeling_POS2()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	 
	 public String freeling_WNHyps()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(wn));
		 return sb.toString();
	 }
	 
	 public String freeling_WNHyps_POS2()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	 
	 public String journal2010_stats()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(phrase_IOB2));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(role_IOB2));
		 sb.append(AppendPipes(depv));
		 sb.append(AppendPipes(role_depv_IOB2));
		 return sb.toString();
	 }
	 
	 public String journal2010_stats_annotatedWith()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(journal2010_stats());
		 sb.append(AppendPipes(element_IOB2));
		 sb.append(AppendPipes(attribs));
		 return sb.toString();
	 }
	 
	 public String journal2010_stats_annotationKey()
	 {
		 return journal2010_stats_annotatedWith();
	 }
	 
	 public String paired()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(token);
		 sb.append(AppendPipes(pipe));
		 return sb.toString();
	 }
	 
	 public String pipes()
	 {
		 return paired();
	 }
	 
	 public String pipes_annotatedWith()
	 {
		 return paired();
	 }
	 
	 public String pre()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(leading_blanks));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 return sb.toString();
	 }
	 
	 public String pre_treetag()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(leading_blanks));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(verb));
		 return sb.toString();
	 }
	 
	 public String pre_treetag_annotatedWith()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(pre_treetag());
		 sb.append(AppendPipes(elementBIO));
		 sb.append(AppendPipes(element_attribs));
		 return sb.toString();
	 }
	 
	 public String pre_treetag_roleconfig()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(leading_blanks));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(mainphra));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(verb));
		 sb.append(AppendPipes(roleconf));
		 return sb.toString();
	 }
	 
	 public String pre_treetag_roleconfig_simpleroles()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(pre_treetag());
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 return sb.toString();
	 }
	 
	 public String pre_treetag_roleconfig_simpleroles_mainphrases()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(pre_treetag_roleconfig_simpleroles());
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	 
	 public String pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(pre_treetag_roleconfig_simpleroles_mainphrases());
		 sb.append(AppendPipes(wn));
		 return sb.toString();
	 }
	 public String pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps_annotationKey()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(pre_treetag_roleconfig_simpleroles_mainphrases_WNHyps());
		 sb.append(AppendPipes(element_IOB2));
		 sb.append(AppendPipes(attribs));
		 return sb.toString();
	 }
	 
	 public String roth()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 return sb.toString();
	 }
	 
	 public String roth_treetag()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth());
		 sb.append(AppendPipes(lemma));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_roleconfig()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag());
		 sb.append(AppendPipes(roleconf));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_roleconfig_simpleroles()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag_roleconfig());
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_roleconfig_simpleroles_mainphrases()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag_roleconfig_simpleroles());
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_roleconfig_simpleroles_mainphrases_WNHyps()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag_roleconfig_simpleroles_mainphrases());
		 sb.append(AppendPipes(wn));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_WNHyps()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag());
		 sb.append(AppendPipes(wn));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_WNHyps_roleconfig()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag_WNHyps());
		 sb.append(AppendPipes(roleconf));
		 return sb.toString();
	 }
	 
	 public String roth_treetag_WNHyps_roleconfig_simpleroles()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag_WNHyps_roleconfig());
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 return sb.toString();
	 }
	 public String roth_treetag_WNHyps_roleconfig_simpleroles_mainphrases()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(roth_treetag_WNHyps_roleconfig_simpleroles());
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	 
	 public void SetTempEval2_features(String[] values)
	 {
		file = values[0];
		sent_num = values[1];
		tok_num= values[2];
		word = values[3];
		pos = values[4];
		syntbio = values[5];
		sentence= values[6];
		synt= values[7];
		verb= values[8];
		lemma= values[9];
		wn= values[10];
		roleconf= values[11];
		simplerolesIOB2= values[12];
		simplerolesIOB2_verb= values[13];
		simpleroles= values[14];
		depverb= values[15];
		tense= values[16];
		assertype= values[17];
		iobmainphrase= values[18];
		mainp_position= values[19];
		phra_id= values[20];
		PPdetail= values[21];
	 }
	 
	 public String TempEval2_features()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(word));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	
	 public void setTempEval2_features_annotatedWith(String[] values)
	 {
		SetTempEval2_features(values);
		element_IOB2 = values[22];
		
	 }
	 public String TempEval2_features_annotatedWith()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(TempEval2_features());
		 sb.append(AppendPipes(element_IOB2));
		 return sb.toString();
	 }
	 
	 public String TempEval2_features_annotationKey()
	 {
		 return TempEval2_features_annotatedWith();
	 }
	 
	 public String TempEval_attributes()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(tag));
		 sb.append(AppendPipes(id));
		 sb.append(AppendPipes(iid));
		 sb.append(AppendPipes(attrname));
		 sb.append(AppendPipes(attrvalue));
		 return sb.toString();
	 }

	 public String TempEval_bs()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(word));
		 return sb.toString();
	 }
	 
	 public String TempEval_bs_annotationKey()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(TempEval_bs());
		 sb.append(AppendPipes(element_IOB2));
		 return sb.toString();
	 }
	 
	 public String TempEval_classik_features()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(word));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(rolesconf));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(polarity));
		 sb.append(AppendPipes(mainphrase));
		 sb.append(AppendPipes(PPdetail));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(extra1));
		 sb.append(AppendPipes(extra2));
		 sb.append(AppendPipes(extra3));
		 sb.append(AppendPipes(extra4));
		 sb.append(AppendPipes(extra5));
		 sb.append(AppendPipes(extra6));
		 sb.append(AppendPipes(element));
		 return sb.toString();
	 }
	 
	 public String TempEval_classik_features_annotatedWith()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(TempEval_classik_features());
		 sb.append(AppendPipes(element2));
		 return sb.toString();
	 }
	 
	 public String TempEval_extents()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(tag));
		 sb.append(AppendPipes(id));
		 sb.append(AppendPipes(iid));
		 return sb.toString();
	 }
	 
	 public String TempEval_features_ANT()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(word));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 sb.append(AppendPipes(wn));
		 return sb.toString();
	 }
	 
	 public String TempEval_features_annotatedWith()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(TempEval_features_ANT());
		 sb.append(AppendPipes(element_IOB2));
		 return sb.toString();
	 }
	 
	 public String TempEval_features_annotationKey()
	 {
		 return TempEval_features_annotatedWith();
	 }
	 
	 public String TempEval_normalization()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(file);
		 sb.append(AppendPipes(sent_num));
		 sb.append(AppendPipes(tok_num));
		 sb.append(AppendPipes(word));
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(rolesconf));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(polarity));
		 sb.append(AppendPipes(mainphrase));
		 sb.append(AppendPipes(PPdetail));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(extra1));
		 sb.append(AppendPipes(extra2));
		 sb.append(AppendPipes(extra3));
		 sb.append(AppendPipes(extra4));
		 sb.append(AppendPipes(extra5));
		 sb.append(AppendPipes(extra6));
		 sb.append(AppendPipes(element));
		 sb.append(AppendPipes(te_type));
		 sb.append(AppendPipes(DCT));
		 sb.append(AppendPipes(ref_val));
		 sb.append(AppendPipes(value));
		 return sb.toString();
	 }
	 
	 public String TempEval_normalization_annotatedWith()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(TempEval_normalization());
		 sb.append(AppendPipes(norm_type2));
		 return sb.toString();
	 }
	 
	 public String tok()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 return sb.toString();
	 }
	 
	 
	 private void setTreeTag(String[] pipesArray)
	 {
		word = pipesArray[0];
		pos = pipesArray[1];
		lemma = pipesArray[2];	
	}
	 public String treetag()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(lemma));
		 return sb.toString();
	 }
	 
	 public String treetag_POS2()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 sb.append(AppendPipes(pos));
		 sb.append(AppendPipes(syntbio));
		 sb.append(AppendPipes(sentence));
		 sb.append(AppendPipes(synt));
		 sb.append(AppendPipes(verb));
		 sb.append(AppendPipes(lemma));
		 sb.append(AppendPipes(wn));
		 sb.append(AppendPipes(roleconf));
		 sb.append(AppendPipes(simplerolesIOB2));
		 sb.append(AppendPipes(simplerolesIOB2_verb));
		 sb.append(AppendPipes(simpleroles));
		 sb.append(AppendPipes(depverb));
		 sb.append(AppendPipes(tense));
		 sb.append(AppendPipes(assertype));
		 sb.append(AppendPipes(iobmainphrase));
		 sb.append(AppendPipes(mainp_position));
		 sb.append(AppendPipes(phra_id));
		 sb.append(AppendPipes(PPdetail));
		 return sb.toString();
	 }
	 public String word()
	 {
		 StringBuilder sb = new StringBuilder();
		 sb.append(word);
		 return sb.toString();
	 }
}
