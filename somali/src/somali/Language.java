package somali;
import java.util.*;
import java.io.File;  
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Cell;  
import org.apache.poi.ss.usermodel.Row;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook; 

class Language {
    private List<Word> words;
    private String language;
    
    /*
     * @param language -> language of object
     */
    public Language(String language) {
        this.language = language.toLowerCase();
        words = new ArrayList<>();
    }
    
    public void setLanguage(String language) {
        this.language = language.toLowerCase();
    }
    
    /* Function to read language excel file and populate word array*/
    public void setWords() {
        /* If language is not set, unable to search file for list of words */
        if (language == null) return;
        /* Attempting to open of filename, return false if error */
        String workingDir = System.getProperty("user.dir") + "\\word_list.xlsx";
        File file = new File(workingDir);   //creating a new file instance  
		FileInputStream fis = null;
		XSSFWorkbook wb = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			System.out.println("Failed to open file.");
			e.printStackTrace();
		}  
		try {
			wb = new XSSFWorkbook(fis);
		} catch (IOException e) {
			System.out.println("Failed to open workbook");
			e.printStackTrace();
		}
        /* Return error if word list is already set */
        if (!words.isEmpty()) {
            System.out.println("_list_ words is non empty, clear() must be called");
            return;
        }
        /* Look for column that begins with language */
        XSSFSheet sheet = wb.getSheetAt(0);
        Row header = sheet.getRow(0);
        Integer language_row = -1;
        for (Cell cell : header) {
        	String cell_value = cell.getStringCellValue();
        	if (cell_value.toLowerCase().equals(language)) {
        		language_row = cell.getColumnIndex();
        	}
        }
        
        /* Return if language column not found */
        if (language_row == -1) {
        	return;
        }
        
        /* Read column of words, create Word object for each cell and add Word object to ArrayList words */
		for (Row row : sheet) {
			Cell cell = row.getCell(language_row);
			String value = cell.getStringCellValue().toLowerCase();
			if (!value.equals(language)) {
				Word word = new Word(words.size(), value);
				words.add(word);
			}
		}  
    }

    public String getLanguage() {
        return language;
    }

    public List<Word> getWords() {
        return words;
    }
    
    /* Return a random word in the language */
    public Word getRandomWord() {
    	Random random = new Random();
    	int random_index = random.nextInt(words.size());
    	return words.get(random_index);
    }
    
    public boolean isEmpty() {
    	return words.isEmpty();
    }
    
    public void clear() {
        words.clear();
    }
}
 