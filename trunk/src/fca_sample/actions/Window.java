package fca_sample.actions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.text.Collator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.swing.JList;

import org.apache.commons.collections15.functors.ConstantTransformer;
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
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import Grafo.BasicRendererColor;
import Grafo.DelegateForestColor;
import colibri.lib.Concept;
import colibri.lib.Lattice;

import com.swtdesigner.SWTResourceManager;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingGraphMousePlugin;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import fca_sample.ClassifiedTableItem;
import fca_sample.Classifier;
import fca_sample.Criteria;
import fca_sample.FCAImplementation;
import fca_sample.TreeUtils;
import fca_sample.WordNetUtils;
import org.eclipse.swt.widgets.Link;

/**
 * 
 * @author Cristian Vitale & Ivan Dietz
 *
 */
public class Window {

  protected Shell shlFcaSample;
  private TabFolder tabFolder;
  private Combo projectsCombo;
  private Tree tree;
  private HashMap<String, TreeItem> packagesMap;
  private Button btnClasses;
  private Button btnMethods;
  private Button btnParameters;
  private Label lblRunning;
  private Map<String, IProject> projectsMap;
  private Table resultsTable;
  private boolean resultsTableOrderAscendant = true;
  private Table detailsTable;
  private Text attributesText;
  private Label elements1;
  private Label conceptsNumber;
  private Table classifiedConceptsTable;
  private boolean classifiedConceptsTableOrderAscendant = true;
  private Table classifiedDetailsTable;
  private Text classifiedAttributeText;
  private Label elements2;
  private Label classificationNumber;
//  private Combo criteriaCombo;
  private List<ClassifiedTableItem> classifiedItems = new ArrayList<ClassifiedTableItem>();
  private FCAImplementation fca;
  private Table groupedConceptsTable;
  private boolean groupedConceptsOrderAscendant = true;
  private Button btnGroupCrosscuttingsConcepts;
  private Table groupedDetailsTable;
  private Text groupedAttributesText;  
  private Label elements3;
  private Map<String, String> groupedConceptsMap;
  private Label groupedNumber;
  private Map<String, String> crosscutingItems;
  
  //Elements for GraphViewer
  public JList attributesList;
  public JList elementsList;
  private Text percentage;
  private Text elementsMin;
  private Text attributesMin;
  private Text percentageCrosscutting;

  /**
   * Launch the application.
   * 
   * @param args
   * @wbp.parser.entryPoint
   */
  public static void main() {
    try {
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
    shlFcaSample.setSize(1000, 568);
    shlFcaSample.setText("FCA Tool for Aspect Mining");
    {
      tabFolder = new TabFolder(shlFcaSample, SWT.NONE);
      tabFolder.setBounds(0, 10, 992, 530);
      {
        TabItem tbtmSelectProjects = new TabItem(tabFolder, SWT.NONE);
        tbtmSelectProjects.setText("Select Projects");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmSelectProjects.setControl(composite);
          {
            projectsCombo = new Combo(composite, SWT.READ_ONLY);
            projectsCombo.setBounds(10, 42, 316, 23);
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
                    tabFolder.setSelection(1);
                    conceptsNumber.setText("Total concepts: " + String.valueOf(fca.getConcepts().size()));
                  } catch (CoreException e1) {
                    e1.printStackTrace();
                  }
                }
              }
            });
            btnRunFcaAlgorithm.setBounds(10, 140, 244, 23);
            btnRunFcaAlgorithm.setText("Run FCA Algorithm");
          }
          {
            Group grpCodeElements = new Group(composite, SWT.NONE);
            grpCodeElements.setText("Code Elements");
            grpCodeElements.setBounds(10, 82, 316, 52);
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
            lblSelectProject.setBounds(10, 23, 78, 13);
            lblSelectProject.setText("Select Project");
          }
          {
            lblRunning = new Label(composite, SWT.NONE);
            lblRunning.setFont(SWTResourceManager.getFont("Tahoma", 8, SWT.BOLD));
            lblRunning.setBounds(260, 145, 66, 13);
          }
          {
            {
              Group grpProjectTree = new Group(composite, SWT.NONE);
              grpProjectTree.setText("Project tree");
              grpProjectTree.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
              grpProjectTree.setBounds(332, 23, 642, 469);
              tree = new Tree(grpProjectTree, SWT.CHECK);
              tree.setBackground(SWTResourceManager.getColor(SWT.COLOR_INFO_BACKGROUND));
              tree.setBounds(10, 21, 622, 438);
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
            }
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
                classifiedConceptsTable.removeAll();
                classifiedDetailsTable.removeAll();
                classifiedAttributeText.setText("");
//                criteriaCombo.clearSelection();
                groupedConceptsTable.removeAll();
                groupedDetailsTable.removeAll();
                classificationNumber.setText("Total concepts: ");
                updateClassifiedConceptsTable(Criteria.CROSSCUTTING_METHOD.getName());
                elements1.setText("# Elements: ");
                elements2.setText("# Elements: ");
                elements3.setText("# Elements: ");
                tabFolder.setSelection(2);
              }
            });
            btnConfirm.setBounds(10, 463, 103, 25);
            btnConfirm.setText("Confirm Selected");
          }
          {
            Group grpLatticeConcepts = new Group(composite, SWT.NONE);
            grpLatticeConcepts.setText("Lattice Concepts");
            grpLatticeConcepts.setBounds(10, 10, 332, 340);
            {
              resultsTable = new Table(grpLatticeConcepts, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
              resultsTable.setBounds(10, 21, 316, 288);
              resultsTable.setLinesVisible(true);
              resultsTable.setHeaderVisible(true);
              {
                TableColumn tblclmnAttributes_1 = new TableColumn(resultsTable, SWT.NONE);
                tblclmnAttributes_1.setResizable(false);
                tblclmnAttributes_1.setWidth(312);
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
                          boolean wasChecked = items[i].getChecked();
                          items[i].dispose();
                          TableItem item = new TableItem(resultsTable, SWT.NONE, j);
                          item.setText(values);
                          item.setChecked(wasChecked);
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
                tblclmnElements_1.setResizable(false);
                tblclmnElements_1.setText("Elements");
              }
              {
                Link selectAll1 = new Link(grpLatticeConcepts, SWT.NONE);
                selectAll1.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = resultsTable.getItems();
                    for (int i = 0; i < items.length; i++) {
                      items[i].setChecked(true);
                    }
                  }
                });
                selectAll1.setBounds(10, 315, 49, 15);
                selectAll1.setText("<a>Select All</a>");
              }
              {
                Link unselectAll1 = new Link(grpLatticeConcepts, SWT.NONE);
                unselectAll1.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = resultsTable.getItems();
                    for (int i = 0; i < items.length; i++) {
                      items[i].setChecked(false);
                    }
                  }
                });
                unselectAll1.setBounds(65, 315, 67, 15);
                unselectAll1.setText("<a>Unselect All</a>");
              }
              resultsTable.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  attributesText.setText(resultsTable.getSelection()[0].getText(0));
                  updateDetails(detailsTable, resultsTable.getSelection()[0]);
                  elements1.setText("# Elements: " + String.valueOf(detailsTable.getItems().length));
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  if (resultsTable.getSelection().length != 0) {
                    attributesText.setText(resultsTable.getSelection()[0].getText(0));
                    updateDetails(detailsTable, resultsTable.getSelection()[0]);
                    elements1.setText("# Elements: " + String.valueOf(detailsTable.getItems().length));
                  }
                }
              });
            }
          }
          {
            Group grpConceptDetails = new Group(composite, SWT.NONE);
            grpConceptDetails.setText("Concept Details (elements)");
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
              attributesText.setBounds(74, 21, 400, 19);
            }
            {
              Label lblAttributes = new Label(grpConceptDetails, SWT.NONE);
              lblAttributes.setBounds(10, 24, 59, 13);
              lblAttributes.setText("Attributes:");
            }
            {
              elements1 = new Label(grpConceptDetails, SWT.NONE);
              elements1.setBounds(480, 24, 136, 15);
              elements1.setText("# Elements: ");
            }
          }
          {
            Button button = new Button(composite, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                drawGraph(false,"Lattice");
              }
            });
            button.setBounds(119, 463, 75, 25);
            button.setText("Show Graph");
          }
          {
            conceptsNumber = new Label(composite, SWT.NONE);
            conceptsNumber.setText("Total concepts: ");
            conceptsNumber.setBounds(200, 468, 167, 15);
          }
          {
            Group grpProperties = new Group(composite, SWT.NONE);
            grpProperties.setBounds(10, 356, 332, 101);
            grpProperties.setText("Filter configuration");
            {
              Label label = new Label(grpProperties, SWT.NONE);
              label.setText("Mininum number of elements per concept");
              label.setBounds(10, 44, 239, 15);
            }
            {
              elementsMin = new Text(grpProperties, SWT.BORDER);
              elementsMin.setText("0");
              elementsMin.setBounds(255, 41, 32, 21);
            }
            {
              Label label = new Label(grpProperties, SWT.NONE);
              label.setText("Mininum number of attributes per concept");
              label.setBounds(10, 23, 239, 15);
            }
            {
              attributesMin = new Text(grpProperties, SWT.BORDER);
              attributesMin.setText("0");
              attributesMin.setBounds(255, 20, 32, 21);
            }
            {
              Label lblOfMethods = new Label(grpProperties, SWT.NONE);
              lblOfMethods.setBounds(10, 65, 239, 15);
              lblOfMethods.setText("% of methods in different hierarchies");
            }
            {
              percentageCrosscutting = new Text(grpProperties, SWT.BORDER);
              percentageCrosscutting.setBounds(255, 62, 32, 21);
              percentageCrosscutting.setText("1");
            }
          }
        }
      }
      {
        TabItem tbtmClassification = new TabItem(tabFolder, SWT.NONE);
        tbtmClassification.setText("Crosscutting methods");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmClassification.setControl(composite);
          {
            Group grpConcepts = new Group(composite, SWT.NONE);
            grpConcepts.setBounds(10, 10, 332, 447);
            grpConcepts.setText("Concepts");
//            {
//              criteriaCombo = new Combo(composite, SWT.READ_ONLY);
//              criteriaCombo.setVisibleItemCount(10);
//              criteriaCombo.setBounds(65, 10, 266, 21);
//              Criteria[] criterias = Criteria.values();
//              for (int i = 0; i < criterias.length; i++) {
//                criteriaCombo.add(criterias[i].getName());
//              }
//              criteriaCombo.addSelectionListener(new SelectionListener() {
//                @Override
//                public void widgetDefaultSelected(SelectionEvent e) {
//                  updateClassifiedConceptsTable(criteriaCombo.getText());
//                }
//
//                @Override
//                public void widgetSelected(SelectionEvent e) {
//                  updateClassifiedConceptsTable(criteriaCombo.getText());
//                }
//              });
//            }
            {
              classifiedConceptsTable = new Table(grpConcepts, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
              classifiedConceptsTable.setLinesVisible(true);
              classifiedConceptsTable.setHeaderVisible(true);
              classifiedConceptsTable.setBounds(10, 21, 316, 395);
              {
                TableColumn tblclmnAttributes = new TableColumn(classifiedConceptsTable, SWT.NONE);
                tblclmnAttributes.setResizable(false);
                tblclmnAttributes.setWidth(280);
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
                          boolean wasChecked = items[i].getChecked();
                          org.eclipse.swt.graphics.Color color = items[i].getBackground(2);
                          items[i].dispose();
                          TableItem item = new TableItem(classifiedConceptsTable, SWT.NONE, j);
                          item.setText(values);
                          item.setChecked(wasChecked);
                          item.setBackground(2, color);
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
                tblclmnElements.setResizable(false);
                tblclmnElements.setText("Elements");
              }
              {
                TableColumn tblclmnColor = new TableColumn(classifiedConceptsTable, SWT.NONE);
                tblclmnColor.setResizable(false);
                tblclmnColor.setWidth(32);
              }
              {
                Link selectAll2 = new Link(grpConcepts, 0);
                selectAll2.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = classifiedConceptsTable.getItems();
                    for (int i = 0; i < items.length; i++) {
                      items[i].setChecked(true);
                    }
                  }
                });
                selectAll2.setBounds(10, 422, 49, 15);
                selectAll2.setText("<a>Select All</a>");
              }
              {
                Link unselectAll2 = new Link(grpConcepts, 0);
                unselectAll2.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = classifiedConceptsTable.getItems();
                    for (int i = 0; i < items.length; i++) {
                      items[i].setChecked(false);
                    }
                  }
                });
                unselectAll2.setBounds(65, 422, 67, 15);
                unselectAll2.setText("<a>Unselect All</a>");
              }
              classifiedConceptsTable.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  classifiedAttributeText.setText(classifiedConceptsTable.getSelection()[0].getText(0));
                  updateDetails(classifiedDetailsTable, classifiedConceptsTable.getSelection()[0]);
                  elements2.setText("# Elements: " + String.valueOf(classifiedDetailsTable.getItems().length));
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  if (classifiedConceptsTable.getSelection().length != 0) {
                    classifiedAttributeText.setText(classifiedConceptsTable.getSelection()[0].getText(0));
                    updateDetails(classifiedDetailsTable, classifiedConceptsTable.getSelection()[0]);
                    elements2.setText("# Elements: " + String.valueOf(classifiedDetailsTable.getItems().length));
                  }
                }
              });
            }
          }
          {
            Group grpConceptDetailselements_1 = new Group(composite, SWT.NONE);
            grpConceptDetailselements_1.setBounds(348, 10, 626, 447);
            grpConceptDetailselements_1.setText("Concept Details (elements)");
            {
              classifiedDetailsTable = new Table(grpConceptDetailselements_1, SWT.BORDER | SWT.FULL_SELECTION);
              classifiedDetailsTable.setLinesVisible(true);
              classifiedDetailsTable.setHeaderVisible(true);
              classifiedDetailsTable.setBounds(10, 46, 606, 391);
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
              classifiedAttributeText = new Text(grpConceptDetailselements_1, SWT.BORDER);
              classifiedAttributeText.setEnabled(false);
              classifiedAttributeText.setEditable(false);
              classifiedAttributeText.setBounds(74, 21, 400, 19);
            }
            {
              Label label = new Label(grpConceptDetailselements_1, SWT.NONE);
              label.setText("Attributes:");
              label.setBounds(10, 24, 58, 13);
            }
            {
              elements2 = new Label(grpConceptDetailselements_1, SWT.NONE);
              elements2.setBounds(480, 24, 136, 15);
              elements2.setText("# Elements: ");
            }
          }
//          {
//            Label lblCriteria = new Label(composite, SWT.NONE);
//            lblCriteria.setBounds(10, 13, 49, 13);
//            lblCriteria.setText("Criteria");
//          }
          {
            Button button = new Button(composite, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                drawGraph(true, Criteria.CROSSCUTTING_METHOD.getName());                
              }
            });
            button.setBounds(100, 463, 99, 25);
            button.setText("Show Graph");
          }
          {
            Button btnChooseColor = new Button(composite, SWT.NONE);
            btnChooseColor.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                ColorDialog cd = new ColorDialog(shlFcaSample);
                cd.setText("ColorDialog Demo");
                cd.setRGB(new RGB(255, 255, 255));
                RGB newColor = cd.open();
                if (newColor == null) {
                  return;
                }
                TableItem[] items = classifiedConceptsTable.getItems(); 
                for (int i = 0; i < items.length; i++) {
                  if (items[i].getChecked()) {
                    items[i].setBackground(2, new org.eclipse.swt.graphics.Color(Display.getCurrent(), newColor.red, newColor.green, newColor.blue));
                  }
                }
              }
            });
            btnChooseColor.setBounds(10, 463, 84, 25);
            btnChooseColor.setText("Choose Color");
          }
          {
            classificationNumber = new Label(composite, SWT.NONE);
            classificationNumber.setBounds(205, 468, 167, 15);
            classificationNumber.setText("Total concepts: ");
          }

        }
      }
      {
        TabItem tbtmGroupCrosscutings = new TabItem(tabFolder, SWT.NONE);
        tbtmGroupCrosscutings.setText("Grouped Crosscuttings");
        {
          Composite composite = new Composite(tabFolder, SWT.NONE);
          tbtmGroupCrosscutings.setControl(composite);
          {
            Group grpConcepts_1 = new Group(composite, SWT.NONE);
            grpConcepts_1.setText("Concepts");
            grpConcepts_1.setBounds(10, 72, 332, 385);
            {
              groupedConceptsTable = new Table(grpConcepts_1, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
              groupedConceptsTable.setLinesVisible(true);
              groupedConceptsTable.setHeaderVisible(true);
              groupedConceptsTable.setBounds(10, 19, 316, 335);
              {
                TableColumn tableColumn = new TableColumn(groupedConceptsTable, SWT.NONE);
                tableColumn.setResizable(false);
                tableColumn.setWidth(280);
                tableColumn.setText("Attributes");
                tableColumn.addListener(SWT.Selection, new Listener() {
                  public void handleEvent(Event e) {
                    // sort column 1
                    TableItem[] items = groupedConceptsTable.getItems();
                    Collator collator = Collator.getInstance(Locale.getDefault());
                    for (int i = 1; i < items.length; i++) {
                      String value1 = items[i].getText(0);
                      for (int j = 0; j < i; j++) {
                        String value2 = items[j].getText(0);
                        if (collator.compare(value1, value2) < 0 && groupedConceptsOrderAscendant
                            || collator.compare(value1, value2) > 0 && !groupedConceptsOrderAscendant) {
                          String[] values = { items[i].getText(0),
                              items[i].getText(1) };
                          boolean wasChecked = items[i].getChecked();
                          org.eclipse.swt.graphics.Color color = items[i].getBackground(2);
                          items[i].dispose();
                          TableItem item = new TableItem(groupedConceptsTable, SWT.NONE, j);
                          item.setText(values);
                          item.setChecked(wasChecked);
                          item.setBackground(2, color);
                          items = groupedConceptsTable.getItems();
                          break;
                        }
                      }
                    }
                    groupedConceptsOrderAscendant = !groupedConceptsOrderAscendant;
                  }
                });
              }
              {
                TableColumn tableColumn = new TableColumn(groupedConceptsTable, SWT.NONE);
                tableColumn.setResizable(false);
                tableColumn.setText("Elements");
              }
              {
                TableColumn tableColumn = new TableColumn(groupedConceptsTable, SWT.NONE);
                tableColumn.setResizable(false);
                tableColumn.setWidth(32);
              }
              {
                Link selectAll3 = new Link(grpConcepts_1, 0);
                selectAll3.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = groupedConceptsTable.getItems();
                    for (int i = 0; i < items.length; i++) {
                      items[i].setChecked(true);
                    }
                  }
                });
                selectAll3.setBounds(10, 360, 49, 15);
                selectAll3.setText("<a>Select All</a>");
              }
              {
                Link unselectAll3 = new Link(grpConcepts_1, 0);
                unselectAll3.addSelectionListener(new SelectionAdapter() {
                  @Override
                  public void widgetSelected(SelectionEvent e) {
                    TableItem[] items = groupedConceptsTable.getItems();
                    for (int i = 0; i < items.length; i++) {
                      items[i].setChecked(false);
                    }
                  }
                });
                unselectAll3.setBounds(65, 360, 67, 15);
                unselectAll3.setText("<a>Unselect All</a>");
              }
              groupedConceptsTable.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                  groupedAttributesText.setText(groupedConceptsTable.getSelection()[0].getText(0));
                  updateDetails(groupedDetailsTable, groupedConceptsTable.getSelection()[0]);
                  elements3.setText("# Elements: " + String.valueOf(groupedDetailsTable.getItems().length));
                }

                @Override
                public void widgetSelected(SelectionEvent e) {
                  if (groupedConceptsTable.getSelection().length != 0) {
                    groupedAttributesText.setText(groupedConceptsTable.getSelection()[0].getText(0));
                    updateDetails(groupedDetailsTable, groupedConceptsTable.getSelection()[0]);
                    elements3.setText("# Elements: " + String.valueOf(groupedDetailsTable.getItems().length));
                  }
                }
              });
            }
          }
          {
            Group grpConceptDetailselements = new Group(composite, SWT.NONE);
            grpConceptDetailselements.setText("Concept Details (elements)");
            grpConceptDetailselements.setBounds(348, 10, 626, 447);
            {
              groupedDetailsTable = new Table(grpConceptDetailselements, SWT.BORDER | SWT.FULL_SELECTION);
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
              groupedAttributesText = new Text(grpConceptDetailselements, SWT.BORDER);
              groupedAttributesText.setEnabled(false);
              groupedAttributesText.setEditable(false);
              groupedAttributesText.setBounds(74, 21, 400, 19);
            }
            {
              Label label = new Label(grpConceptDetailselements, SWT.NONE);
              label.setText("Attributes:");
              label.setBounds(10, 24, 55, 13);
            }
            {
              elements3 = new Label(grpConceptDetailselements, SWT.NONE);
              elements3.setBounds(480, 24, 136, 15);
              elements3.setText("# Elements: ");
            }
          }
          {
            Button groupButton = new Button(composite, SWT.NONE);
            groupButton.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                DirectoryDialog directoryDialog = new DirectoryDialog(shlFcaSample);
                directoryDialog.setMessage("Please select WordNet DICTIONARY folder (\"dict\").");
                String dir = directoryDialog.open();
                if (dir != null) {
                  System.setProperty("wordnet.database.dir", dir);
                  btnGroupCrosscuttingsConcepts.setEnabled(true);
                }
              }
            });
            groupButton.setBounds(10, 10, 332, 25);
            groupButton.setText("Select Wordnet Database Directory");
          }
          {
            Button button = new Button(composite, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                drawGroupedGraph();
              }
            });
            button.setBounds(100, 463, 99, 25);
            button.setText("Show Graph");
          }
          {
            Button button = new Button(composite, SWT.NONE);
            button.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                ColorDialog cd = new ColorDialog(shlFcaSample);
                cd.setText("ColorDialog Demo");
                cd.setRGB(new RGB(255, 255, 255));
                RGB newColor = cd.open();
                if (newColor == null) {
                  return;
                }
                TableItem[] items = groupedConceptsTable.getItems(); 
                for (int i = 0; i < items.length; i++) {
                  if (items[i].getChecked()) {
                    items[i].setBackground(2, new org.eclipse.swt.graphics.Color(Display.getCurrent(), newColor.red, newColor.green, newColor.blue));
                  }
                }
              }
            });
            button.setBounds(10, 463, 84, 25);
            button.setText("Choose Color");
          }
          {
            btnGroupCrosscuttingsConcepts = new Button(composite, SWT.NONE);
            btnGroupCrosscuttingsConcepts.addSelectionListener(new SelectionAdapter() {
              @Override
              public void widgetSelected(SelectionEvent e) {
                try {
                  if (Float.valueOf(percentage.getText()) > 0 && Float.valueOf(percentage.getText()) <= 100)
                    groupCrosscuttingMethods();
                  else {
                    MessageBox box = new MessageBox(shlFcaSample, SWT.ICON_ERROR);
                    box.setMessage("Please enter a percentage value between 1 and 100.");
                    box.open();
                  }
                }
                catch (Exception ex) {
                  MessageBox box = new MessageBox(shlFcaSample, SWT.ICON_ERROR);
                  box.setMessage("Please enter a valid percentage value.");
                  box.open();
                }
              }
            });
            btnGroupCrosscuttingsConcepts.setBounds(10, 41, 189, 25);
            btnGroupCrosscuttingsConcepts.setText("Group Crosscuttings Concepts");
            btnGroupCrosscuttingsConcepts.setEnabled(false);
          }
          {
            percentage = new Text(composite, SWT.BORDER);
            percentage.setText("100");
            percentage.setBounds(205, 43, 30, 21);
          }
          {
            Label lblMatching = new Label(composite, SWT.NONE);
            lblMatching.setBounds(241, 46, 101, 15);
            lblMatching.setText("% matching");
          }
          {
            groupedNumber = new Label(composite, SWT.NONE);
            groupedNumber.setBounds(205, 468, 167, 15);
            groupedNumber.setText("Total concepts: ");
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
//    criteriaCombo.clearSelection();
    classifiedItems.clear();
    groupedConceptsTable.removeAll();
    groupedDetailsTable.removeAll();
    elements1.setText("# Elements: ");
    elements2.setText("# Elements: ");
    elements3.setText("# Elements: ");
    
    // Fill results table
    TableItem item = null;
    for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
      Concept concept = (Concept) iterator.next();
      if (concept.getAttributes().size() > 0 && concept.getObjects().size() > 0) {
        item = new TableItem(resultsTable, SWT.NONE);
        item.setText(0, concept.getAttributes().toString().replace("[", "").replace("]", ""));
        item.setText(1, concept.getObjects().toString().replace("[", "").replace("]", ""));
        item.setChecked(true);
      }
    }
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
        if (j == components.length - 1)
          item.setFont(j, new org.eclipse.swt.graphics.Font(Display.getCurrent(), "", 10, SWT.BOLD));
      }
    }
    for (int i = 0; i < table.getColumnCount(); i++)
      table.getColumn(i).pack();
    shlFcaSample.update();
  }

  private void classifyConcepts(TableItem[] items) {
    int minAttr = 0;
    int minElems = 0;
    try {
      minAttr = Integer.valueOf(attributesMin.getText());
      minElems = Integer.valueOf(elementsMin.getText());
    } catch (NumberFormatException e){
    }
    classifiedItems.clear();
    for (int i = 0; i < items.length; i++) {
      if (items[i].getChecked() && items[i].getText(0).split(", ").length >= minAttr && items[i].getText(1).split(", ").length >= minElems) {
        classifiedItems.add(new ClassifiedTableItem(Classifier.classify(items[i], fca.getHierarchy(), Float.valueOf(percentageCrosscutting.getText())).getName(), items[i]));
      }
    }
  }

  private void updateClassifiedConceptsTable(String criteria) {
    classifiedConceptsTable.removeAll();
    classifiedDetailsTable.removeAll();
    classifiedAttributeText.setText("");
    elements1.setText("# Elements: ");
    elements2.setText("# Elements: ");
    elements3.setText("# Elements: ");

    TableItem item = null;
    for (Iterator iterator = classifiedItems.iterator(); iterator.hasNext();) {
      ClassifiedTableItem classifiedItem = (ClassifiedTableItem) iterator.next();
      if (classifiedItem.getCriteria().equals(criteria)) {
        item = new TableItem(classifiedConceptsTable, SWT.NONE);
        item.setText(0, (classifiedItem.getItem().getText(0)));
        item.setText(1, (classifiedItem.getItem().getText(1)));
        item.setBackground(2, new org.eclipse.swt.graphics.Color(Display.getCurrent(), 0, 255, 0));
      }
    }    
    classificationNumber.setText("Total concepts: " + String.valueOf(classifiedConceptsTable.getItemCount()));
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
    crosscutingItems = getCrosscuttingItems();
    
    // Filtramos elementos incluidos en otro concepto
    filterItems(crosscutingItems);
    
    // Agrupamos los conceptos semanticamente relacionados
    Map<String, String> groupedConcepts = groupConcepts(crosscutingItems);
    
    // Clear tables
    groupedConceptsTable.removeAll();
    groupedDetailsTable.removeAll();
    groupedAttributesText.setText("");
    elements1.setText("# Elements: ");
    elements2.setText("# Elements: ");
    elements3.setText("# Elements: ");
    
    // Fill results table
    Set<String> keySet = groupedConcepts.keySet();
    TableItem item = null;
    for (Iterator iterator = keySet.iterator(); iterator.hasNext();) {
      String key = (String) iterator.next();
      item = new TableItem(groupedConceptsTable, SWT.NONE);
      item.setText(0, key);
      item.setText(1, groupedConcepts.get(key));
      if(groupedConceptsMap.containsKey(item.getText(0))){
        item.setBackground(2, new org.eclipse.swt.graphics.Color(Display.getCurrent(), 0, 255, 255));
      } else {
        item.setBackground(2, new org.eclipse.swt.graphics.Color(Display.getCurrent(), 0, 255, 0));
      }
    }
    groupedNumber.setText("Total concepts: " + String.valueOf(groupedConceptsTable.getItemCount()));
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
    groupedConceptsMap = new HashMap<String, String>();
    for (int i = 0; i < attributesArray.length; i++) {
      //si esta en blanco ya fue usado...
      boolean used = attributesArray[i].equals("");
      if(!used) {
        String newKey = attributesArray[i];
        String newValue = items.get(attributesArray[i]);
        String children = attributesArray[i];
        boolean grouped = false;
        for (int j = i + 1; j < attributesArray.length; j++) {
          if (groupRelated(children, attributesArray[j])){
            newKey = newKey + ", " + attributesArray[j];
            newValue = newValue + ", " + items.get(attributesArray[j]);
            children = children + ":" + attributesArray[j];
            grouped = true;
            // "sacar" de la lista el concepto que ya agregamos y pasar al siguiente
            attributesArray[j] = "";
          }
        }
        returnMap.put(eliminateDuplicates(newKey), newValue);
        if(grouped)
          groupedConceptsMap.put(eliminateDuplicates(newKey), children);
      }
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
//    if (a.length == b.length) {
      int matchCount = 0;
      for (int i = 0; i < a.length; i++) {
//        boolean matched = false;
        for (int j = 0; j < b.length /**&& !matched**/; j++) {
          if (WordNetUtils.isRelated(a[i], b[j])) {
            matchCount++;
//            matched = true;
            a[i] = "";
            b[j] = "";
          }
        }
      }
//      if (matchCount >= a.length * (Float.valueOf(percentage.getText()) / 100))
      if(matchCount * 2 >= (a.length + b.length)* (Float.valueOf(percentage.getText()) / 100))
        return true;
//    }
    return false;
  }
  
  public boolean groupRelated(String group, String concept){
    String[] concepts = group.split(":");
    boolean related = true;
    for (int i = 0; i < concepts.length && related; i++) {
      if(!isRelated(concepts[i],concept)) {
        related = false;
      }      
    }
    return related;
  }
  
  public void drawGraph(boolean paintClassification, String frameName){
    DelegateForestColor<String, String> f = new DelegateForestColor<String, String>();
    Lattice lattice = fca.getLattice();
    Map<String, String> vertexMessages = new HashMap<String, String>();
    
    // add vertex
    List<Concept> concepts = fca.getConcepts();
    for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
      Concept concept = (Concept) iterator.next();
        f.addVertex(concept.getAttributes().toString(), new Color(255, 255, 140));
        vertexMessages.put(concept.getAttributes().toString(), concept.getObjects().toString());
    }
    
    //add edges
    int edgeCount = 0;
    for (Iterator iterator = concepts.iterator(); iterator.hasNext();) {
      Concept concept = (Concept) iterator.next();
      Iterator<Concept> childs = lattice.lowerNeighbors(concept);
      while (childs.hasNext()) {
        f.addEdge(String.valueOf(edgeCount), concept.getAttributes().toString(), childs.next().getAttributes().toString());
        edgeCount++;
      }
    }
    
    draw(f, paintClassification, frameName, vertexMessages);
  }
  
  public void drawGroupedGraph(){
    DelegateForestColor<String, String> groupedGraph = new DelegateForestColor<String, String>();
    groupedGraph.addVertex("[]", new Color(255, 255, 140));
    TableItem[] items = groupedConceptsTable.getItems(); 
    Map<String, String> vertexMessages = new HashMap<String, String>();
    int edgeCount = 0;
    for (int i = 0; i < items.length; i++) {
      if(groupedConceptsMap.containsKey(items[i].getText(0))){
        groupedGraph.addVertex("[" + items[i].getText(0) + "]", new Color(items[i].getBackground(2).getRed(), items[i].getBackground(2).getGreen(), items[i].getBackground(2).getBlue()));
        vertexMessages.put("[" + items[i].getText(0) + "]", "[" + items[i].getText(1) + "]");
        groupedGraph.addEdge(String.valueOf(edgeCount), "[]", "[" + items[i].getText(0) + "]");
        edgeCount++;
        String[] childs = groupedConceptsMap.get(items[i].getText(0)).split(":");
        for (int j = 0; j < childs.length; j++) {
          groupedGraph.addVertex("[" + childs[j] + "]", Color.GREEN);                    
          vertexMessages.put("[" + childs[j] + "]", "[" + crosscutingItems.get(childs[j]) + "]");
          groupedGraph.addEdge(String.valueOf(edgeCount), "[" + items[i].getText(0) + "]", "[" + childs[j] + "]");
          edgeCount++; 
        }
      } else {
        groupedGraph.addVertex("[" + items[i].getText(0) + "]", new Color(items[i].getBackground(2).getRed(), items[i].getBackground(2).getGreen(), items[i].getBackground(2).getBlue()));
        vertexMessages.put("[" + items[i].getText(0) + "]", "[" + items[i].getText(1) + "]");
        groupedGraph.addEdge(String.valueOf(edgeCount), "[]", "[" + items[i].getText(0) + "]");
        edgeCount++;
      }
    }
    
    draw(groupedGraph, false, "Grouped Crosscuting Concepts", vertexMessages);
  }
  
  public void draw(DelegateForestColor<String, String> f, boolean paintClassification, String frameName, Map<String, String> vertexMessages){
    Layout<String, String> layout = new TreeLayout<String,String>(f, 100, 300);
    
    VisualizationViewer<String,String> vv = new VisualizationViewer<String,String>(layout);
    vv.setPreferredSize(new Dimension(880, 630));
    vv.setRenderer(new BasicRendererColor<String,String>(f.getVertexColors()));
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());    
    vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
    
    //FORMA y TAMAÑO
    vv.getRenderContext().setVertexShapeTransformer(new ConstantTransformer(new Ellipse2D.Float(-30 ,-30, 80, 80)));

    //LETRA
    vv.getRenderContext().setVertexFontTransformer(new ConstantTransformer(new Font("Arial", Font.PLAIN, 12)));       

    Lattice lattice = fca.getLattice();
    
    BasicRendererColor<String,String> renderer = (BasicRendererColor) vv.getRenderer();
    renderer.setTopVertex(lattice.top().getAttributes().toString());
    renderer.setBottomVertex(lattice.bottom().getAttributes().toString());        
    
    attributesList = new JList();
    elementsList = new JList();
    DefaultModalGraphMouse gm = new DefaultModalGraphMouse(vertexMessages, attributesList, elementsList);
    gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    gm.add(new ScalingGraphMousePlugin(new CrossoverScalingControl(), 2, 1.1f, 0.9f));
    vv.setGraphMouse(gm);
    
    vv.addKeyListener(gm.getModeKeyListener());    
    
    if (paintClassification){
      TableItem[] items = classifiedConceptsTable.getItems();
      for (TableItem tableItem : items) {
        renderer.setVertexColor("[" + tableItem.getText(0) + "]", new Color(tableItem.getBackground(2).getRed(), tableItem.getBackground(2).getGreen(), tableItem.getBackground(2).getBlue()));      
      }
    }
    
    GraphViewer graph = new GraphViewer(vv, attributesList, elementsList);
    graph.setVisible(true);
    
  }
}
