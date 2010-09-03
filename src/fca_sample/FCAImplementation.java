package fca_sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.widgets.TreeItem;

import colibri.lib.Concept;
import colibri.lib.HybridLattice;
import colibri.lib.Lattice;
import colibri.lib.Relation;
import colibri.lib.Traversal;
import colibri.lib.TreeRelation;

public class FCAImplementation {

  private Lattice lattice = null;
  private HashMap<String, String> hierarchy = null;  

  public FCAImplementation(IProject project, boolean useClasses,
      boolean useMethods, boolean useParams, HashMap packagesMap) throws CoreException {
    hierarchy = new HashMap<String,String>();
    if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
      IJavaProject javaProject = JavaCore.create(project);
      createLattice(javaProject, useClasses, useMethods, useParams, packagesMap);
    }
  }

  private void createLattice(IJavaProject javaProject, boolean useClasses,
      boolean useMethods, boolean useParams, HashMap<String, TreeItem> packagesMap) throws JavaModelException {
    Relation rel = new TreeRelation();
    Pattern p = Pattern.compile("[a-zA-Z]{1}[a-z]*");
    Matcher matcher;

    IPackageFragment[] packages = javaProject.getPackageFragments();
    for (IPackageFragment mypackage : packages) {
      // Tomamos todos los paquetes de la carpeta source, que hayan sido seleccionados en el tree.
      if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE && packagesMap.get(mypackage.getElementName()).getChecked()) {
        for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
          if (packagesMap.get(unit.getPath().toString()).getChecked()) {
            Document doc = new Document(unit.getSource());
            IType[] allTypes = unit.getAllTypes();
            for (IType type : allTypes) {
              hierarchy.put(type.getElementName(), type.getSuperclassName());
              matcher = p.matcher(type.getElementName());
              while (matcher.find()) {
                if (useClasses && !Filter.isStopWord(matcher.group().toLowerCase()))
                  rel.add(type.getElementName(), matcher.group().toLowerCase());
              }
              IMethod[] methods = type.getMethods();
              for (IMethod method : methods) {
                matcher = p.matcher(method.getElementName());
                while (matcher.find()) {
                  if (useMethods && !Filter.isStopWord(matcher.group().toLowerCase()))
                    if (!type.getElementName().equals(method.getElementName()))
                      rel.add(type.getElementName() + ":" + method.getElementName(), matcher.group().toLowerCase());
                }
                String[] parameterNames = method.getParameterNames();
                for (int i = 0; i < parameterNames.length; i++) {
                  matcher = p.matcher(parameterNames[i]);
                  while (matcher.find()) {
                    if (useParams && !Filter.isStopWord(matcher.group().toLowerCase()))
                      rel.add(type.getElementName() + ":" + method.getElementName() + ":" + parameterNames[i], matcher.group().toLowerCase());
                  }
                }
              }
            }
          }
        }
      }
    }
    lattice = new HybridLattice(rel);
  }

  public List<Concept> getConcepts() {
    List<Concept> concepts = new ArrayList<Concept>();
    if (lattice != null) {
      // get the iterator
      Iterator<Concept> it = lattice.conceptIterator(Traversal.TOP_OBJSIZE);

      while (it.hasNext()) {
        Concept c = it.next();
        // filter by size
//        if (c.getAttributes().size() > 0 && c.getObjects().size() > 1)
          concepts.add(c);
      }
    }
    return concepts;
  }

  public HashMap<String, String> getHierarchy() {
    return hierarchy;
  }
  
  public Lattice getLattice() {
    return lattice;
  }

}
