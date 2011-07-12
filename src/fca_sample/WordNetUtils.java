package fca_sample;

import java.util.ArrayList;
import java.util.List;

import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public final class WordNetUtils {
  
  /**
   * Retorna true si las dos palabras son sinonimos o antonimos
   * @param a
   * @param b
   * @return
   */
  public static boolean isRelated(String a, String b) {
    if (a.equals(b))
      return true;
    
    List<String> synonymsA;
    List<String> synonymsB;
    List<String> antonymsA;
    List<String> antonymsB;
    
    // Si a y b son sustantivos
    synonymsA = getNounSynonyms(a);
    synonymsB = getNounSynonyms(b);
    if (synonymsA.contains(b) || synonymsB.contains(a))
      return true;
    antonymsA = getNounAntonyms(a);
    antonymsB = getNounAntonyms(b);
    if (antonymsA.contains(b) || antonymsB.contains(a))
      return true;
    
    // si a y b son verbos
    synonymsA = getVerbSynonyms(a);
    synonymsB = getVerbSynonyms(b);
    if (synonymsA.contains(b) || synonymsB.contains(a))
      return true;
    antonymsA = getVerbAntonyms(a);
    antonymsB = getVerbAntonyms(b);
    if (antonymsA.contains(b) || antonymsB.contains(a))
      return true;
    
    // si a y b son adjetivos
    synonymsA = getAdjectiveSynonyms(a);
    synonymsB = getAdjectiveSynonyms(b);
    if (synonymsA.contains(b) || synonymsB.contains(a))
      return true;
    antonymsA = getAdjectiveAntonyms(a);
    antonymsB = getAdjectiveAntonyms(b);
    if (antonymsA.contains(b) || antonymsB.contains(a))
      return true;
    
    return false;
  }

  
  public static List<String> getVerbSynonyms(String word) {
    List<String> synonyms = new ArrayList<String>();
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets(word, SynsetType.VERB);
    for (int i = 0; i < synsets.length; i++) {
      String wordForms[] = synsets[i].getWordForms();
      for (int j = 0; j < wordForms.length; j++) {
        if (!word.equals(wordForms[j]))
          synonyms.add(wordForms[j]);
      }
    }
    return synonyms;
  }
  
  public static List<String> getVerbAntonyms(String word) {
    List<String> antonyms = new ArrayList<String>();
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets(word, SynsetType.VERB);
    for (int i = 0; i < synsets.length; i++) {
      WordSense wordSenses[] = synsets[i].getAntonyms(word);
      for (int j = 0; j < wordSenses.length; j++) {
        String wordForm = wordSenses[j].getWordForm();
        if (!antonyms.contains(wordForm))
          antonyms.add(wordForm);
      }
    }
    //parches (casos no provistos por wordnet)
    if (word.equals("start"))
      antonyms.add("end");
    if (word.equals("end"))
      antonyms.add("start");
    if (word.equals("add"))
      antonyms.add("remove");
    if (word.equals("remove"))
      antonyms.add("add");
    if (word.equals("redo"))
      antonyms.add("undo");
    if (word.equals("undo"))
      antonyms.add("redo");
    
    return antonyms;
  }
  
  public static List<String> getNounSynonyms(String word) {
    List<String> synonyms = new ArrayList<String>();
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets(word, SynsetType.NOUN);
    for (int i = 0; i < synsets.length; i++) {
      String wordForms[] = synsets[i].getWordForms();
      for (int j = 0; j < wordForms.length; j++) {
        if (!word.equals(wordForms[j]))
          synonyms.add(wordForms[j]);
      }
    }
    return synonyms;
  }
  
  public static List<String> getNounAntonyms(String word) {
    List<String> antonyms = new ArrayList<String>();
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets(word, SynsetType.NOUN);
    for (int i = 0; i < synsets.length; i++) {
      WordSense wordSenses[] = synsets[i].getAntonyms(word);
      for (int j = 0; j < wordSenses.length; j++) {
        String wordForm = wordSenses[j].getWordForm();
        if (!antonyms.contains(wordForm))
          antonyms.add(wordForm);
      }
    }
    return antonyms;
  }
  
  public static List<String> getAdjectiveSynonyms(String word) {
    List<String> synonyms = new ArrayList<String>();
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets(word, SynsetType.ADJECTIVE);
    for (int i = 0; i < synsets.length; i++) {
      String wordForms[] = synsets[i].getWordForms();
      for (int j = 0; j < wordForms.length; j++) {
        if (!word.equals(wordForms[j]))
          synonyms.add(wordForms[j]);
      }
    }
    return synonyms;
  }
  
  public static List<String> getAdjectiveAntonyms(String word) {
    List<String> antonyms = new ArrayList<String>();
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets(word, SynsetType.ADJECTIVE);
    for (int i = 0; i < synsets.length; i++) {
      WordSense wordSenses[] = synsets[i].getAntonyms(word);
      for (int j = 0; j < wordSenses.length; j++) {
        String wordForm = wordSenses[j].getWordForm();
        if (!antonyms.contains(wordForm))
          antonyms.add(wordForm);
      }
    }
    
    //parches (casos no provistos por wordnet)
    if (word.equals("usable"))
      antonyms.add("unusable");
    if (word.equals("unusable"))
      antonyms.add("usable");
    
    return antonyms;
  }
}
