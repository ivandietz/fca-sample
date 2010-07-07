package fca_sample.actions;

import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import colibri.lib.Concept;

import com.swtdesigner.SWTResourceManager;

import fca_sample.ClassifiedTableItem;
import fca_sample.Classifier;
import fca_sample.Criteria;
import fca_sample.FCAImplementation;
import fca_sample.TreeUtils;
import fca_sample.WordNetUtils;

/**
 * TODO En general:
 * - sacar columna de detalles en las tablas de la izquierda
 * - sacar el confirm de la ultima ventana
 * - implementar resto del algoritmo de agrupar
 * - mantener los check cuando ordena la primer tabla
 * - el agrupamiento mejor hacerlo con un boton en la ultima pestaña, tarda mucho hacerlo en el confirm de la 2da
 * 
 */
public class Window {

  protected Shell shlFcaSample;
  private Combo projectsCombo;
  private Tree tree;
  private HashMap<String, TreeItem> packagesMap;
  private Button btnClasses;
  private Button btnMethods;
  private Button btnParameters;
  private Label lblRunning;
  private Map<String, IProject> projectsMap;
  private Table resultsTable;
  boolean resultsTableOrderAscendant = true;
  private Table detailsTable;
  private Text attributesText;
  private Table classifiedConceptsTable;
  boolean classifiedConceptsTableOrderAscendant = true;
  private Table classifiedDetailsTable;
  private Text classifiedAttributeText;
  private Combo criteriaCombo;
  private List<ClassifiedTableItem> classifiedItems = new ArrayList<ClassifiedTableItem>();
  FCAImplementation fca;
  private Table grouped_concepts;
  boolean groupedConceptsOrderAscendant = true;
  private Table groupedDetailsTable;
  private Text groupedAttributesText;

  /**
   * Launch the application.
   * 
   * @param args
   * @wbp.parser.entryPoint
   */
  public static void main() {
    try {
      // TODO Cambiar esto..
      System.setProperty("wordnet.database.dir", "C:\\Archivos de programa\\WordNet\\2.1\\dict");
      
      Window window = new Window();
      window.open();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Open the window.
   */
  public void open() {
    Display display = Display.getDefault();
    createContents();
    shlFcaSample.open();
    shlFcaSample.layout();
    while (!shlFcaSample.isDisposed()) {
      if (!display.readAndDispatch()) {
        display.sleep();
      }
    }
  }

  /**
   * Create contents of the window.
   */
  protected void createContents() {
    shlFcaSample = new Shell(SWT.MIN);
    shlFcaSample.setSize(1000, 580);
    shlFcaSample.setText("FCA Sample");
    {
      TabFolder tabFolder = new TabFolder(shlFcaSample, SWT.NONE);
      tabFolder.setBounds(0, 10, 992, 522);
      {
        TabItem tbtmSelectProjects = new TabItem(tabFolder, SWT.NONE);
        tbtmSelectProjects.setText("Select Projects");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmSelectProjects.setControl(composite);
          {
            projectsCombo = new Combo(composite, SWT.READ_ONLY);
            projectsCombo.setBounds(10, 42, 283, 21);
            fillProjectsMap();
            Iterator it = projectsMap.entrySet().iterator();
            while (it.hasNext()) {
              Map.Entry project = (Map.Entry) it.next();
              projectsCombo.add(project.getKey().toString());
            }
            projectsCombo.addSelectionListener(new SelectionListener() {
              @Override
              public void widgetDefaultSelected(SelectionEvent e) {
              }

              @Override
              public void widgetSelected(SelectionEvent e) {
                fillTree(projectsMap.get(projectsCombo.getText()));
              }
            });
          }
          {
            Button btnRunFcaAlgorithm = new Button(composite, SWT.NONE);
            btnRunFcaAlgorithm.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                if (!projectsCombo.getText().equals("")) {
                  try {
                    lblRunning.setText("Running...");
                    fca = new FCAImplementation(projectsMap.get(projectsCombo.getText()), btnClasses.getSelection(), btnMethods.getSelection(), btnParameters.getSelection(), packagesMap);
                    showResults(fca.getConcepts());
                    lblRunning.setText("");
                  } catch (CoreException e1) {
                    e1.printStackTrace();
                  }
                }
              }
            });
            btnRunFcaAlgorithm.setBounds(10, 159, 146, 23);
            btnRunFcaAlgorithm.setText("Run FCA Algorithm");
          }
          {
            Group grpCodeElements = new Group(composite, SWT.NONE);
            grpCodeElements.setText("Code Elements");
            grpCodeElements.setBounds(10, 82, 283, 52);
            {
              btnClasses = new Button(grpCodeElements, SWT.CHECK);
              btnClasses.setBounds(10, 20, 85, 16);
              btnClasses.setSelection(true);
              btnClasses.setText("Classes");
            }
            {
              btnMethods = new Button(grpCodeElements, SWT.CHECK);
              btnMethods.setBounds(101, 20, 85, 16);
              btnMethods.setSelection(true);
              btnMethods.setText("Methods");
            }
            {
              btnParameters = new Button(grpCodeElements, SWT.CHECK);
              btnParameters.setBounds(192, 20, 85, 16);
              btnParameters.setSelection(true);
              btnParameters.setText("Parameters");
            }
          }
          {
            Label lblSelectProject = new Label(composite, SWT.NONE);
            lblSelectProject.setBounds(10, 23, 66, 13);
            lblSelectProject.setText("Select Project");
          }
          {
            lblRunning = new Label(composite, SWT.NONE);
            lblRunning.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
            lblRunning.setBounds(193, 164, 66, 13);
          }
          {
            tree = new Tree(composite, SWT.CHECK);
            tree.setBounds(332, 42, 300, 444);
            tree.addListener(SWT.Selection, new Listener() {
              public void handleEvent(Event event) {
                  if (event.detail == SWT.CHECK) {
                      TreeItem item = (TreeItem) event.item;
                      boolean checked = item.getChecked();
                      TreeUtils.checkItems(item, checked);
                      TreeUtils.checkPath(item.getParentItem(), checked, false);
                  }
              }
          });
            packagesMap = new HashMap<String, TreeItem>();
          }
        }
      }
      {
        TabItem tbtmFca = new TabItem(tabFolder, SWT.NONE);
        tbtmFca.setText("FCA Results");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmFca.setControl(composite);
          {
            Button btnConfirm = new Button(composite, SWT.NONE);
            btnConfirm.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                TableItem[] items = resultsTable.getItems();
                classifyConcepts(items);
                groupCrosscuttingMethods();
                classifiedConceptsTable.removeAll();
                classifiedDetailsTable.removeAll();
                classifiedAttributeText.setText("");
                criteriaCombo.clearSelection();
              }
            });
            btnConfirm.setBounds(653, 463, 68, 23);
            btnConfirm.setText("Confirm");
          }
          {
            Group grpLatticeConcepts = new Group(composite, SWT.NONE);
            grpLatticeConcepts.setText("Lattice Concepts");
            grpLatticeConcepts.setBounds(10, 10, 332, 447);
            {
              resultsTable = new Table(grpLatticeConcepts, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
              resultsTable.setBounds(10, 21, 316, 416);
              resultsTable.setLinesVisible(true);
              resultsTable.setHeaderVisible(true);
              {
                TableColumn tblclmnAttributes_1 = new TableColumn(resultsTable, SWT.NONE);
                tblclmnAttributes_1.setWidth(182);
                tblclmnAttributes_1.setText("Attributes");
                tblclmnAttributes_1.addListener(SWT.Selection, new Listener() {
                  public void handleEvent(Event e) {
                    // sort column 1
                    TableItem[] items = resultsTable.getItems();
                    Collator collator = Collator.getInstance(Locale.getDefault());
                    for (int i = 1; i < items.length; i++) {
                      String value1 = items[i].getText(0);
                      for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(0);
                        if (collator.compare(value1, value2) < 0 && resultsTableOrderAscendant
                            || collator.compare(value1, value2) > 0 && !resultsTableOrderAscendant) {
                          String[] values = { items[i].getText(0),
                              items[i].getText(1) };
                          items[i].dispose();
                          TableItem item = new TableItem(resultsTable, SWT.NONE, j);
                          item.setText(values);
                          items = resultsTable.getItems();
                          break;
                        }
                      }
                    }
                    resultsTableOrderAscendant = !resultsTableOrderAscendant;
                  }
                });
              }
              {
                TableColumn tblclmnElements_1 = new TableColumn(resultsTable, SWT.NONE);
                tblclmnElements_1.setWidth(242);
                tblclmnElements_1.setText("Elements");
              }
              resultsTable.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  attributesText.setText(resultsTable.getSelection()[0].getText(0));
                  updateDetails(detailsTable, resultsTable.getSelection()[0]);
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  attributesText.setText(resultsTable.getSelection()[0].getText(0));
                  updateDetails(detailsTable, resultsTable.getSelection()[0]);
                }
              });
            }
          }
          {
            Group grpConceptDetails = new Group(composite, SWT.NONE);
            grpConceptDetails.setText("Concept Details");
            grpConceptDetails.setBounds(348, 10, 626, 447);
            {
              detailsTable = new Table(grpConceptDetails, SWT.BORDER | SWT.FULL_SELECTION);
              detailsTable.setBounds(10, 46, 606, 391);
              detailsTable.setHeaderVisible(true);
              detailsTable.setLinesVisible(true);
              {
                TableColumn tblclmnClassName = new TableColumn(detailsTable, SWT.NONE);
                tblclmnClassName.setWidth(286);
                tblclmnClassName.setText("Class Name");
              }
              {
                TableColumn tblclmnMethodName = new TableColumn(detailsTable, SWT.NONE);
                tblclmnMethodName.setWidth(172);
                tblclmnMethodName.setText("Method Name");
              }
              {
                TableColumn tblclmnParameterName = new TableColumn(detailsTable, SWT.NONE);
                tblclmnParameterName.setWidth(142);
                tblclmnParameterName.setText("Parameter Name");
              }
            }
            {
              attributesText = new Text(grpConceptDetails, SWT.BORDER);
              attributesText.setEditable(false);
              attributesText.setEnabled(false);
              attributesText.setBounds(65, 21, 423, 19);
            }
            {
              Label lblAttributes = new Label(grpConceptDetails, SWT.NONE);
              lblAttributes.setBounds(10, 27, 49, 13);
              lblAttributes.setText("Attributes:");
            }
          }
        }
      }
      {
        TabItem tbtmClassification = new TabItem(tabFolder, SWT.NONE);
        tbtmClassification.setText("Classification");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmClassification.setControl(composite);
          {
            Group grpConcepts = new Group(composite, SWT.NONE);
            grpConcepts.setBounds(10, 37, 332, 420);
            grpConcepts.setText("Concepts");
            {
              criteriaCombo = new Combo(composite, SWT.READ_ONLY);
              criteriaCombo.setVisibleItemCount(10);
              criteriaCombo.setBounds(65, 10, 266, 21);
              Criteria[] criterias = Criteria.values();
              for (int i = 0; i < criterias.length; i++) {
                criteriaCombo.add(criterias[i].getName());
              }
              criteriaCombo.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  updateClassifiedConceptsTable(criteriaCombo.getText());
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  updateClassifiedConceptsTable(criteriaCombo.getText());
                }
              });
            }
            {
              classifiedConceptsTable = new Table(grpConcepts, SWT.BORDER | SWT.FULL_SELECTION);
              classifiedConceptsTable.setLinesVisible(true);
              classifiedConceptsTable.setHeaderVisible(true);
              classifiedConceptsTable.setBounds(10, 21, 316, 389);
              {
                TableColumn tblclmnAttributes = new TableColumn(classifiedConceptsTable, SWT.NONE);
                tblclmnAttributes.setWidth(155);
                tblclmnAttributes.setText("Attributes");
                tblclmnAttributes.addListener(SWT.Selection, new Listener() {
                  public void handleEvent(Event e) {
                    // sort column 1
                    TableItem[] items = classifiedConceptsTable.getItems();
                    Collator collator = Collator.getInstance(Locale.getDefault());
                    for (int i = 1; i < items.length; i++) {
                      String value1 = items[i].getText(0);
                      for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(0);
                        if (collator.compare(value1, value2) < 0 && classifiedConceptsTableOrderAscendant
                            || collator.compare(value1, value2) > 0 && !classifiedConceptsTableOrderAscendant) {
                          String[] values = { items[i].getText(0),
                              items[i].getText(1) };
                          items[i].dispose();
                          TableItem item = new TableItem(classifiedConceptsTable, SWT.NONE, j);
                          item.setText(values);
                          items = classifiedConceptsTable.getItems();
                          break;
                        }
                      }
                    }
                    classifiedConceptsTableOrderAscendant = !classifiedConceptsTableOrderAscendant;
                  }
                });
              }
              {
                TableColumn tblclmnElements = new TableColumn(classifiedConceptsTable, SWT.NONE);
                tblclmnElements.setWidth(143);
                tblclmnElements.setText("Elements");
              }
              classifiedConceptsTable.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  classifiedAttributeText.setText(classifiedConceptsTable.getSelection()[0].getText(0));
                  updateDetails(classifiedDetailsTable, classifiedConceptsTable.getSelection()[0]);
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  classifiedAttributeText.setText(classifiedConceptsTable.getSelection()[0].getText(0));
                  updateDetails(classifiedDetailsTable, classifiedConceptsTable.getSelection()[0]);
                }
              });
            }
          }
          {
            Group group = new Group(composite, SWT.NONE);
            group.setBounds(348, 37, 626, 420);
            group.setText("Concept Details");
            {
              classifiedDetailsTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
              classifiedDetailsTable.setLinesVisible(true);
              classifiedDetailsTable.setHeaderVisible(true);
              classifiedDetailsTable.setBounds(10, 46, 606, 364);
              {
                TableColumn tableColumn = new TableColumn(classifiedDetailsTable, SWT.NONE);
                tableColumn.setWidth(286);
                tableColumn.setText("Class Name");
              }
              {
                TableColumn tableColumn = new TableColumn(classifiedDetailsTable, SWT.NONE);
                tableColumn.setWidth(172);
                tableColumn.setText("Method Name");
              }
              {
                TableColumn tableColumn = new TableColumn(classifiedDetailsTable, SWT.NONE);
                tableColumn.setWidth(142);
                tableColumn.setText("Parameter Name");
              }
            }
            {
              classifiedAttributeText = new Text(group, SWT.BORDER);
              classifiedAttributeText.setEnabled(false);
              classifiedAttributeText.setEditable(false);
              classifiedAttributeText.setBounds(65, 21, 551, 19);
            }
            {
              Label label = new Label(group, SWT.NONE);
              label.setText("Attributes:");
              label.setBounds(10, 27, 49, 13);
            }
          }
          {
            Label lblCriteria = new Label(composite, SWT.NONE);
            lblCriteria.setBounds(10, 13, 49, 13);
            lblCriteria.setText("Criteria");
          }

        }
      }
      {
        TabItem tbtmQueLePonemo = new TabItem(tabFolder, SWT.NONE);
        tbtmQueLePonemo.setText("Grouped Crosscuttings");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmQueLePonemo.setControl(composite);
          {
            Button button = new Button(composite, SWT.NONE);
            button.setText("Confirm");
            button.setBounds(653, 463, 68, 23);
          }
          {
            Group group = new Group(composite, SWT.NONE);
            group.setText("Lattice Concepts");
            group.setBounds(10, 10, 332, 447);
            {
              grouped_concepts = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
              grouped_concepts.setLinesVisible(true);
              grouped_concepts.setHeaderVisible(true);
              grouped_concepts.setBounds(10, 21, 316, 416);
              {
                TableColumn tableColumn = new TableColumn(grouped_concepts, SWT.NONE);
                tableColumn.setWidth(182);
                tableColumn.setText("Attributes");
                tableColumn.addListener(SWT.Selection, new Listener() {
                  public void handleEvent(Event e) {
                    // sort column 1
                    TableItem[] items = grouped_concepts.getItems();
                    Collator collator = Collator.getInstance(Locale.getDefault());
                    for (int i = 1; i < items.length; i++) {
                      String value1 = items[i].getText(0);
                      for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(0);
                        if (collator.compare(value1, value2) < 0 && groupedConceptsOrderAscendant
                            || collator.compare(value1, value2) > 0 && !groupedConceptsOrderAscendant) {
                          String[] values = { items[i].getText(0),
                              items[i].getText(1) };
                          items[i].dispose();
                          TableItem item = new TableItem(grouped_concepts, SWT.NONE, j);
                          item.setText(values);
                          items = grouped_concepts.getItems();
                          break;
                        }
                      }
                    }
                    groupedConceptsOrderAscendant = !groupedConceptsOrderAscendant;
                  }
                });
              }
              {
                TableColumn tableColumn = new TableColumn(grouped_concepts, SWT.NONE);
                tableColumn.setWidth(242);
                tableColumn.setText("Elements");
              }
              grouped_concepts.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  groupedAttributesText.setText(grouped_concepts.getSelection()[0].getText(0));
                  updateDetails(groupedDetailsTable, grouped_concepts.getSelection()[0]);
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  groupedAttributesText.setText(grouped_concepts.getSelection()[0].getText(0));
                  updateDetails(groupedDetailsTable, grouped_concepts.getSelection()[0]);
                }
              });
            }
          }
          {
            Group group = new Group(composite, SWT.NONE);
            group.setText("Concept Details");
            group.setBounds(348, 10, 626, 447);
            {
              groupedDetailsTable = new Table(group, SWT.BORDER | SWT.FULL_SELECTION);
              groupedDetailsTable.setLinesVisible(true);
              groupedDetailsTable.setHeaderVisible(true);
              groupedDetailsTable.setBounds(10, 46, 606, 391);
              {
                TableColumn tableColumn = new TableColumn(groupedDetailsTable, SWT.NONE);
                tableColumn.setWidth(286);
                tableColumn.setText("Class Name");
              }
              {
                TableColumn tableColumn = new TableColumn(groupedDetailsTable, SWT.NONE);
                tableColumn.setWidth(172);
                tableColumn.setText("Method Name");
              }
              {
                TableColumn tableColumn = new TableColumn(groupedDetailsTable, SWT.NONE);
                tableColumn.setWidth(142);
                tableColumn.setText("Parameter Name");
              }
            }
            {
              groupedAttributesText = new Text(group, SWT.BORDER);
              groupedAttributesText.setEnabled(false);
              groupedAttributesText.setEditable(false);
              groupedAttributesText.setBounds(65, 21, 423, 19);
            }
            {
              Label label = new Label(group, SWT.NONE);
              label.setText("Attributes:");
              label.setBounds(10, 27, 49, 13);
            }
          }
        }
      }
    }

  }

  private void fillProjectsMap() {
    projectsMap = new HashMap<String, IProject>();
    // Get the root of the workspace
    IWorkspace workspace = ResourcesPlugin.getWorkspace();
    IWorkspaceRoot root = workspace.getRoot();
    // Get all projects in the workspace
    IProject[] projects = root.getProjects();
    // Loop over all projects
    for (int i = 0; i < projects.length; i++) {
      IProject project = projects[i];
      projectsMap.put(project.getName(), project);
    }
  }

  private void showResults(java.util.List<Concept> concepts) {
    // Clear tables
    resultsTable.removeAll();
    detailsTable.removeAll();
    attributesText.setText("");
    classifiedConceptsTable.removeAll();
    classifiedDetailsTable.removeAll();
    classifiedAttributeText.setText("");
    criteriaCombo.clearSelection();
    classifiedItems.clear();
    
    // Fill results table
    TableItem item = null;
    for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
      Concept concept = (Concept) iterator.next();
      item = new TableItem(resultsTable, SWT.NONE);
      item.setText(0, concept.getAttributes().toString().replace("[", "").replace("]", ""));
      item.setText(1, concept.getObjects().toString().replace("[", "").replace("]", ""));
      item.setChecked(true);
    }
    for (int i = 0; i < resultsTable.getColumnCount(); i++)
      resultsTable.getColumn(i).pack();
    shlFcaSample.update();
  }

  private void updateDetails(Table table, TableItem tableItem) {
    table.removeAll();
    TableItem item = null;
    String elementList = tableItem.getText(1);
    String[] elements = elementList.split(", ");
    for (int i = 0; i < elements.length; i++) {
      item = new TableItem(table, SWT.NONE);
      String element = elements[i];
      String components[] = element.split(":");
      for (int j = 0; j < components.length; j++) {
        item.setText(j, components[j]);
      }
    }
    for (int i = 0; i < table.getColumnCount(); i++)
      table.getColumn(i).pack();
    shlFcaSample.update();
  }

  private void classifyConcepts(TableItem[] items) {
    classifiedItems.clear();
    for (int i = 0; i < items.length; i++) {
      if (items[i].getChecked()) {
        classifiedItems.add(new ClassifiedTableItem(Classifier.classify(items[i], fca.getHierarchy()).getName(), items[i]));
      }
    }
  }

  private void updateClassifiedConceptsTable(String criteria) {
    classifiedConceptsTable.removeAll();
    classifiedDetailsTable.removeAll();
    classifiedAttributeText.setText("");

    TableItem item = null;
    for (Iterator iterator = classifiedItems.iterator(); iterator.hasNext();) {
      ClassifiedTableItem classifiedItem = (ClassifiedTableItem) iterator.next();
      if (classifiedItem.getCriteria().equals(criteria)) {
        item = new TableItem(classifiedConceptsTable, SWT.NONE);
        item.setText(0, (classifiedItem.getItem().getText(0)));
        item.setText(1, (classifiedItem.getItem().getText(1)));
      }
    }
    for (int i = 0; i < classifiedConceptsTable.getColumnCount(); i++)
      classifiedConceptsTable.getColumn(i).pack();
    shlFcaSample.update();
  }
  
  private void fillTree(IProject project) {
    tree.removeAll();
    packagesMap.clear();
    try {
      if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
        IJavaProject javaProject = JavaCore.create(project);
        IPackageFragment[] packages = javaProject.getPackageFragments();
        for (IPackageFragment mypackage : packages) {
          if (mypackage.getKind() == IPackageFragmentRoot.K_SOURCE) {
            if (!mypackage.getElementName().contains(".")) {
              TreeItem itemTree = new TreeItem(tree, SWT.NONE);
              String packageName;
              if (mypackage.getElementName().equals(""))
                packageName = "(default package)";
              else
                packageName = mypackage.getElementName();
              itemTree.setText(packageName);
              itemTree.setImage(new Image(itemTree.getDisplay(), this.getClass().getResourceAsStream("/icons/package_obj.gif")));
              packagesMap.put(mypackage.getElementName(), itemTree);
              for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
                TreeItem itemTreeClass = new TreeItem(itemTree, SWT.NONE);
                String className = unit.getPath().toString().replace(".java", "");
                itemTreeClass.setText(className.substring(className.lastIndexOf("/") + 1, className.length()));
                itemTreeClass.setImage(new Image(itemTree.getDisplay(), this.getClass().getResourceAsStream("/icons/class_obj.gif")));
                packagesMap.put(unit.getPath().toString(), itemTreeClass);
              }
            } else {
              String elementName = mypackage.getElementName();
              TreeItem parent = packagesMap.get(elementName.substring(0, elementName.lastIndexOf(".")));
              TreeItem itemTree = new TreeItem(parent, SWT.NONE);
              itemTree.setText(elementName.substring(elementName.lastIndexOf(".") + 1, elementName.length()));
              itemTree.setImage(new Image(itemTree.getDisplay(), this.getClass().getResourceAsStream("/icons/package_obj.gif")));
              packagesMap.put(elementName, itemTree);
              for (ICompilationUnit unit : mypackage.getCompilationUnits()) {
                TreeItem itemTreeClass = new TreeItem(itemTree, SWT.NONE);
                String className = unit.getPath().toString().replace(".java", "");
                itemTreeClass.setText(className.substring(className.lastIndexOf("/") + 1, className.length()));
                itemTreeClass.setImage(new Image(itemTree.getDisplay(), this.getClass().getResourceAsStream("/icons/class_obj.gif")));
                packagesMap.put(unit.getPath().toString(), itemTreeClass);
              }
            }
          }
        }
      }
    } catch (JavaModelException e) {
      e.printStackTrace();
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * Agrupa conceptos semanticamente relacionados (sinonimos, antonimos..)
   */
  void groupCrosscuttingMethods(){
    Map<String, String> items = getCrosscuttingItems();
    
    // Filtramos elementos incluidos en otro concepto
    filterItems(items);
    
    // Agrupamos los conceptos semanticamente relacionados
    Map<String, String> groupedConcepts = groupConcepts(items);
    
    // Clear tables
    grouped_concepts.removeAll();
    groupedDetailsTable.removeAll();
    groupedAttributesText.setText("");
    
    // Fill results table
    Set<String> keySet = groupedConcepts.keySet();
    TableItem item = null;
    for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      item = new TableItem(grouped_concepts, SWT.NONE);
      item.setText(0, key);
      item.setText(1, groupedConcepts.get(key));
    }
    
  }
  
  /**
   * Obtiene los items cuyo Criteria sea Crosscutting Method.
   * @return
   */
  private Map<String,String> getCrosscuttingItems(){
    Map<String,String> map = new HashMap<String,String>();
    for (Iterator iterator = classifiedItems.iterator(); iterator.hasNext();) {
      ClassifiedTableItem classifiedItem = (ClassifiedTableItem) iterator.next();
      if (classifiedItem.getCriteria().equals(Criteria.CROSSCUTTING_METHOD.getName())) {
        map.put(classifiedItem.getItem().getText(0),classifiedItem.getItem().getText(1));        
      }
    }
    return map;
  }
  
  /**
   * Quita los conceptos cuyos elementos sean subconjuntos de los elementos de otro concepto.
   * @param items
   * @return
   */
  private void filterItems(Map<String,String> items) {
    Set<String> attributesSet = items.keySet();
    String[] attributesArray = attributesSet.toArray(new String[0]);
    for (int i = 0; i < attributesArray.length; i++) {
      for (int j = i + 1; j < attributesArray.length; j++) {
        if (isIncluded(attributesArray[i], attributesArray[j]))
          items.remove(attributesArray[j]);
        if (isIncluded(attributesArray[j], attributesArray[i]))
          items.remove(attributesArray[i]);
      }
    }
  }
  
  /**
   * Devuelve true si los atrubutos en A estan incluidos en B.
   * @param attributesA
   * @param attributesB
   * @return
   */
  public boolean isIncluded(String attributesA, String attributesB) {
    String[] a = attributesA.split(", ");
    String[] b = attributesB.split(", ");
    boolean cont = true;
    for (int i = 0; i < a.length && cont; i++) {
      cont = false;
      for (int j = 0; j < b.length && !cont; j++) {
        if (a[i].equals(b[j]))
          cont = true;
      }
    }
    return cont;
  }
  
  /**
   * Agrupa conceptos relacionados utilizando WordNet
   * @param items
   */
  public Map<String, String> groupConcepts(Map<String, String> items) {
    Map<String, String> returnMap = new HashMap<String, String>();
    Set<String> attributesSet = items.keySet();
    String[] attributesArray = attributesSet.toArray(new String[0]);
    for (int i = 0; i < attributesArray.length; i++) {
      //si esta en blanco ya fue usado...
      boolean used = attributesArray[i].equals("");
      for (int j = i + 1; !used && j < attributesArray.length; j++) {
        if (isRelated(attributesArray[i], attributesArray[j])){
          String newKey = attributesArray[i] + ", " + attributesArray[j];
          String newValue = items.get(attributesArray[i]) + ", " + items.get(attributesArray[j]);
          returnMap.put(eliminateDuplicates(newKey), newValue);
          // "sacar" de la lista el concepto que ya agregamos y pasar al siguiente
          attributesArray[j] = "";
          used = true;
        }
      }
      if (!used) 
        returnMap.put(attributesArray[i], items.get(attributesArray[i]));
    }
    return returnMap;
  }
  
  /**
   * De una lista de atributos (en un string), elimina duplicados
   * @param attribues
   * @return
   */
  private String eliminateDuplicates(String attributes) {
    String[] array = attributes.split(", ");
    //en un set no puede haber duplicados...
    Set<String> set = new HashSet<String>();
    for (int i = 0; i < array.length; i++)
      set.add(array[i]);
    String ret = "";
    Iterator iterator = set.iterator();
    if (iterator.hasNext()) 
      ret = ret + (String) iterator.next();
    while (iterator.hasNext())
      ret = ret + ", " + (String) iterator.next();
    return ret;
  }
  
  /**
   * Devuelve true si algun atrubuto en A esta relacionado con alguno de B.
   * @param attributesA
   * @param attributesB
   * @return
   */
  public boolean isRelated(String attributesA, String attributesB) {
    String[] a = attributesA.split(", ");
    String[] b = attributesB.split(", ");
    if (a.length == b.length) {
      int matchCount = 0;
      for (int i = 0; i < a.length; i++) {
        boolean matched = false;
        for (int j = 0; j < b.length && !matched; j++) {
          if (WordNetUtils.isRelated(a[i], b[j])) {
            matchCount++;
            matched = true;
          }
        }
      }
      if (matchCount == a.length) 
        return true;
    }
    return false;
  }
}
