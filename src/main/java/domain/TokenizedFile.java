package domain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.cognitionis.tipsem.helpers.Logger;

public class TokenizedFile extends LinkedList<TokenizedSentence> {
	
	private FilesType type;
	private String language = "en";
	private String name = null;
	
	
	public TokenizedFile(FilesType type, String language, String name)
	{
		this.type = type;
		this.language = language;
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setType(FilesType type)
	{
		this.type = type;
	}
	
	public FilesType getType()
	{
		return type;
	}
		
	//TODO:Add correct implementation
	public String getLanguage()
	{
		return language;
	}
	
	public void addFromPipes(String line)
	{
		if(this.isEmpty() || this.getLast().getLast().lemma.equalsIgnoreCase("fp"))
		{
			TokenizedSentence s = new TokenizedSentence();
			this.addLast(s);
		}
		this.getLast().addFromPipes(line, type);
	}
	
	public boolean add(TokenizedSentence sentence)
	{
		if(!this.isEmpty())
		{
			this.getLast().next = sentence;
			sentence.prev = this.getLast();
		}
		this.addLast(sentence);
		return true;
	}
		

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		int numSentence = 0;
		for(TokenizedSentence sentence : this)
		{
			sb.append(sentence.toString(type, name, numSentence));
			numSentence++;
		}		
		return sb.toString();
	}
	
	public File toFile()
	{
		File f = new File(name+"."+type);
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(f.getName()));

			try{
				output.write(this.toString());
			} catch (IOException e) {
				Logger.WriteError("Cannot write in the file" + name, e);
			} finally {

				if (output != null)
				{
					output.close();
				}
			}
		} catch (IOException e1) {
			Logger.WriteError("Cannot create the file in the path" + name, e1);
		}
		return f;
		
	}
	

}
