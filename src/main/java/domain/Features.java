package domain;

import java.io.StreamCorruptedException;

public class Features {

	public String file;
	public String lid;
	public String eiid;
	public String tid;
	public String e_class;
	public String e_pos;
	public String e_token;
	public String e_tense;
	public String e_tense_aspect;
	public String e_govPP;
	public String e_govTMPSub;
	public String gov_e_class;
	public String gov_e_pos;
	public String gov_e_token;
	public String gov_e_tense_aspect;
	public String eiid1;
	public String eiid2;
	public String e1_class;
	public String e1_pos;
	public String e1_token;
	public String e1_tense;
	public String e1_tense_aspect;
	public String e1_govPP;
	public String e1_govTMPSub;
	public String e2_class;
	public String e2_pos;
	public String e2_token;
	public String e2_tense;
	public String e2_tense_aspect;
	public String e2_govPP;
	public String e2_govTMPSub;
	public String syntrel;
	public String t_type;
	public String t_ref;
	public String t_govPP;
	public String t_govTMPSub;
	public String synt_relation;
	public String syntbio;
	public String sentence;
	public String synt;
	public String verb;
	public String roleconf;
	public String simplerolesIOB2;
	public String simplerolesIOB2_verb;
	public String simpleroles;
	public String depverb;
	public String tense;
	public String assertype;
	public String iobmainphrase;
	public String mainp_position;
	public String phra_id;
	public String PPdetail;
	
	public String phrase_IOB2;
	public String role_IOB2;
	public String depv;
	public String role_depv_IOB2;
	
	public String element_IOB2;
	public String attribs;
	
	public String token;
	public String pipe;
	
	public String leading_blanks;
	public String elementBIO;
	public String element_attribs;
	
	public String mainphra;
	public String main_position;
	public String sent_num;
	public String tok_num;
	public String tag;
	public String id;
	public String iid;
	public String attrname;
	public String attrvalue;
	
	public String polarity;
	public String mainphrase;
	public String extra1;
	public String extra2;
	public String extra3;
	public String extra4;
	public String extra5;
	public String extra6;
	public String element2;
	public String rolesconf;
	public String element;
	public String te_type;
	public String DCT;
	public String ref_val;
	public String value;
	public String norm_type2;
	
	
	public String word;
	public String pos;
	public String lemma;
	public String wn;

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
