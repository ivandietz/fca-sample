package semantic_fca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.widgets.TableItem;

public class Classifier {

  public static Criteria classify(TableItem item, HashMap classHierarchy, float percentage) {
//    if (classNameInKeyboard(item))
//      return Criteria.CLASS_NAME_IN_KEYBOARD;
//    if (classNameInParameter(item))
//      return Criteria.CLASS_NAME_IN_PARAMETER;
//    if (classesOnly(item))
//      return Criteria.CLASSES_ONLY;
//    if (hierarchyMethod(item, classHierarchy))
//      return Criteria.HIERARCHY_METHOD;
    if (crosscuttingMethod(item, classHierarchy, percentage))
      return Criteria.CROSSCUTTING_METHOD;
    return Criteria.NONE;
  }

  private static boolean classNameInKeyboard(TableItem item) {
    boolean hasMethod = false;
    String elements[] = item.getText(1).split(", ");
    for (int i = 0; i < elements.length; i++) {
      String components[] = elements[i].split(":");
      if (components.length <= 2) {
        if (components.length == 2) {
          hasMethod = true;
          if (!components[1].toLowerCase().contains(components[0].toLowerCase()))
            return false;
        }
      } else
        return false;
    }
    return hasMethod;
  }

  private static boolean classNameInParameter(TableItem item) {
    boolean hasParameter = false;
    String elements[] = item.getText(1).split(", ");
    for (int i = 0; i < elements.length; i++) {
      String components[] = elements[i].split(":");
      if (components.length == 1 || components.length == 3) {
        if (components.length == 3) {
          hasParameter = true;
          if (!components[2].toLowerCase().contains(components[0].toLowerCase()))
            return false;
        }
      } else
        return false;
    }
    return hasParameter;
  }

  private static boolean classesOnly(TableItem item) {
    String elements[] = item.getText(1).split(", ");
    for (int i = 0; i < elements.length; i++) {
      String components[] = elements[i].split(":");
      if (!(components.length == 1))
        return false;
    }
    return true;
  }

  private static boolean hierarchyMethod(TableItem item, HashMap hierarchy) {
    HashMap methodHierarchy = new HashMap<String, List<String>>();
    Set<String> classNames = new HashSet<String>();
    String elements[] = item.getText(1).split(", ");
    for (int i = 0; i < elements.length; i++) {
      String components[] = elements[i].split(":");
      if (components.length == 2) {
        if (!classNames.contains(components[0])) {
          classNames.add(components[0]);
          methodHierarchy.put(elements[i], getHierarchy(components[0], hierarchy));
        } else
          return false; // methods must be from different classes
      } else
        return false; // we only want methods
    }
    if (methodHierarchy.size() < 2)
      return false; // we want at least 2 different methods
    // search for shared parents
    Iterator<String> i = methodHierarchy.keySet().iterator();
    String element = i.next();
    List<String> elementParents = (List<String>) methodHierarchy.get(element);
    boolean shareParents = true;
    while (i.hasNext() && shareParents) {
      shareParents = false;
      String nextElement = i.next();
      List<String> parents = (List<String>) methodHierarchy.get(nextElement);
      for (Iterator iterator = parents.iterator(); iterator.hasNext() && !shareParents;) {
        String superclass = (String) iterator.next();
        if (elementParents.contains(superclass)) {
          shareParents = true;
        }
      }
    }
    return shareParents;
  }

  private static boolean crosscuttingMethod(TableItem item, HashMap hierarchy, float percentage) {
    HashMap methodHierarchy = new HashMap<String, List<String>>();
    Set<String> classNames = new HashSet<String>();
    String elements[] = item.getText(1).split(", ");
    for (int i = 0; i < elements.length; i++) {
      String components[] = elements[i].split(":");
      if (components.length == 2) {
//        if (!classNames.contains(components[0])) {
          classNames.add(components[0]);
          methodHierarchy.put(elements[i], getHierarchy(components[0], hierarchy));
//        } else
//          return false; // methods must be from different classes
      } else
        return false; // we only want methods
    }
    if (methodHierarchy.size() < 2)
      return false; // we want at least 2 different methods
    
    // search for shared parents
    
    // IVAN
    int crosscutingsFound = 0;
    int crosscutingsNotFound = 0;
    int total = 0;
    
    Object methods[] = methodHierarchy.keySet().toArray();
    for (int i = 0; i < methods.length; i++) {
      String element = (String) methods[i];
      List<String> elementParents = (List<String>) methodHierarchy.get(element);
      for (int j = i+1; j < methods.length; j++) {
        total++;
        String nextElement = (String) methods[j];
        List<String> nextElementParents = (List<String>) methodHierarchy.get(nextElement);
        boolean isParent = false;
        for (Iterator iterator = nextElementParents.iterator(); iterator.hasNext() && !isParent;) {
          String parent = (String) iterator.next();
          if (elementParents.contains(parent))
            isParent = true;          
        }
        if(!isParent)  
          crosscutingsFound++;     
      }
    }
    if (crosscutingsFound >= total * percentage / 100 ) {
      return true;
    }
    
//    // CRISTIAN
//    int crosscutingsFound = 0;
//
//    Object methods[] = methodHierarchy.keySet().toArray();
//    for (int i = 0; i < methods.length; i++) {
//      String element = (String) methods[i];
//      List<String> elementParents = (List<String>) methodHierarchy.get(element);
//      boolean shareParents = false;
//      for (int j = i+1; j < methods.length && !shareParents; j++) {
//        String nextElement = (String) methods[j];
//        List<String> nextElementParents = (List<String>) methodHierarchy.get(nextElement);
//        for (Iterator iterator = nextElementParents.iterator(); iterator.hasNext() && !shareParents;) {
//          String parent = (String) iterator.next();
//          if (elementParents.contains(parent)) {
//            shareParents = true;
//          }
//        }
//      }
//      if (!shareParents) {
//        crosscutingsFound++;
//      }
//    }
//    if (crosscutingsFound >= elements.length * percentage / 100 ) {
//      return true;
//    }
    
    
    return false;
  }
  
  
  private static List<String> getHierarchy(String className, HashMap hierarchyMap) {
    List<String> list = new ArrayList<String>();
    list.add(className);
    String father = (String) hierarchyMap.get(className);
    while (father != null) {
      list.add(father);
      father = (String) hierarchyMap.get(father);
    }

    return list;
  }
}
