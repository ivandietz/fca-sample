package fca_sample;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class TestWordNet {

  /**
   * @param args
   */
  public static void main(String[] args) {
    System.setProperty("wordnet.database.dir", "C:\\Archivos de programa\\WordNet\\2.1\\dict");
    NounSynset nounSynset;
    NounSynset[] hyponyms;
    VerbSynset verbSynset;
    VerbSynset[] troponyms;
    

    //VERBS
    WordNetDatabase database = WordNetDatabase.getFileInstance();
    Synset[] synsets = database.getSynsets("stop", SynsetType.VERB);
    for (int i = 0; i < synsets.length; i++) {
        verbSynset = (VerbSynset)(synsets[i]);
        System.out.println("------ Definicion: " + verbSynset.getDefinition());
        String[] wordForms = verbSynset.getWordForms();
        System.out.println("- WordForms:");
        for (int j = 0; j < wordForms.length; j++) {
          System.out.println(wordForms[j]);
        }
        System.out.println("---- Troponimos:");
        troponyms = verbSynset.getTroponyms();
        for (int j = 0; j < troponyms.length; j++) {
          String[] wordForms2 = troponyms[j].getWordForms();
          System.out.println("- WordForms:");
          for (int k = 0; k < wordForms2.length; k++) {
            System.out.println(wordForms2[k]);
          }
        }
    
    
    
    //NOUNS
//    WordNetDatabase database = WordNetDatabase.getFileInstance();
//    Synset[] synsets = database.getSynsets("car", SynsetType.NOUN);
//    for (int i = 0; i < synsets.length; i++) {
//        nounSynset = (NounSynset)(synsets[i]);
//        System.out.println("------ Definicion: " + nounSynset.getDefinition());
//        String[] wordForms = nounSynset.getWordForms();
//        System.out.println("- WordForms:");
//        for (int j = 0; j < wordForms.length; j++) {
//          System.out.println(wordForms[j]);
//        }
//        System.out.println("---- Hiponimos:");
//        hyponyms = nounSynset.getHyponyms();
//        for (int j = 0; j < hyponyms.length; j++) {
//          String[] wordForms2 = hyponyms[j].getWordForms();
//          System.out.println("- WordForms:");
//          for (int k = 0; k < wordForms2.length; k++) {
//            System.out.println(wordForms2[k]);
//          }
//        }
        
        
//        hyponyms = nounSynset.getHyponyms();
////        System.out.println(nounSynset.getWordForms()[0] +
////                ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
//        for (int j = 0; j < hyponyms.length; j++) {
//          System.out.println(hyponyms[j]);
//          
//        }
    } 
  }
}
