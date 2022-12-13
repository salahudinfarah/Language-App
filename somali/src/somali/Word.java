package somali;

class Word {
    private Integer index = null;
    private String characters = null;
    
    /* Constructor for Word object
     * @param index -> the index/row_number of the word
     * @param characters -> the string representation of the word
     */
    public Word(Integer index, String characters) {
        this.index = index;
        this.characters = characters;
    }
 
    public Integer getIndex() {
        return index;
    }
    
    // Overriding toString() method in Java
    public String toString() {
    	return characters;
    }
    
    /* Function to check if two words are equivalent
     * @param other -> The other word to be compared to
     */
    public boolean isEquivalent(Word other) {
    	if (other == null) {
    		return false;
    	}
    	Integer other_index = other.getIndex();
    	return this.index == other_index;
    }
}