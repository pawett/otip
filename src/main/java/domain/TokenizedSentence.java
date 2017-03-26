package domain;

import java.util.ArrayList;
import java.util.LinkedList;

public class TokenizedSentence  extends LinkedList<Word>{
	
	public TokenizedSentence prev;
	public TokenizedSentence next;
	
	public void addFromPipes(String pipesWord, FilesType type)
	{
		Word w = new Word(pipesWord, type);
		this.addLast(w);		
	}
	
	public boolean add(Word word)
	{
		if(!this.isEmpty())
		{
			Word previousLast = this.getLast();
			previousLast.next = word;
			word.prev = previousLast;
		}
		//word.tok_num = this.size() + "-s";
		this.addLast(word);
		return true;
	}
	
	

	public String toString(FilesType type, String fileName, int numSentence)
	{
		StringBuilder sb = new StringBuilder();
		int numWord = 0;
		for(Word word : this){
			sb.append(word.toString(type, fileName, numSentence, numWord));
			sb.append(System.lineSeparator());
			numWord++;
		}
		//sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}
	
	
}
