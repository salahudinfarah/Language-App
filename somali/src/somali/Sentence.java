package somali;
import java.util.*;

class Sentence {
    private List<Word> word_list;
    private Integer index;
    private Integer length = 0;
    
    public Sentence(Integer index, List<Word> word_list) {
    	this.word_list = word_list;
    	length = this.word_list.size();
    	this.index = index;
    }
    
    public void add(Word word) {
    	word_list.add(word);
    	length++;
    }
    
    public Integer size() {
    	return length;
    }
    
    public Integer getIndex() {
    	return index;
    }
    
    public List<Word> getWords() {
    	return word_list;
    }
}