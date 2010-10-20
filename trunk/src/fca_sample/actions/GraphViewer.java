package fca_sample.actions;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import edu.uci.ics.jung.visualization.VisualizationViewer;

public class GraphViewer extends JFrame {

  private JPanel contentPane;

//  /**
//   * Launch the application.
//   */
//  public static void main(String[] args) {
//    EventQueue.invokeLater(new Runnable() {
//      public void run() {
//        try {
//          GraphViewer frame = new GraphViewer(null);
//          frame.setVisible(true);
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
//      }
//    });
//  }

  /**
   * Create the frame.
   */
  public GraphViewer(VisualizationViewer<String,String> graphView, JList attributesList, JList elementsList) {
    setResizable(false);
    setTitle("Concepts Lattice");
//    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setBounds(100, 100, 1200, 700);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    setContentPane(contentPane);
    contentPane.setLayout(null);
    {
      JLabel lblReferences = new JLabel("References");
      lblReferences.setBounds(10, 11, 264, 14);
      contentPane.add(lblReferences);
    }
    {
      JLabel lblP = new JLabel("T - Transform Mode (Move Lattice)");
      lblP.setBounds(20, 61, 254, 14);
      contentPane.add(lblP);
    }
    {
      JLabel lblTSelect = new JLabel("P - Pick Mode (Select Concepts)");
      lblTSelect.setBounds(20, 36, 255, 14);
      contentPane.add(lblTSelect);
    }
    {
      JLabel lblMouseScrollUp = new JLabel("Mouse Scroll Up - Zoom Out");
      lblMouseScrollUp.setBounds(20, 86, 254, 14);
      contentPane.add(lblMouseScrollUp);
    }
    {
      JLabel lblMouseScrollDown = new JLabel("Mouse Scroll Down - Zoom In");
      lblMouseScrollDown.setBounds(20, 111, 254, 14);
      contentPane.add(lblMouseScrollDown);
    }
    {
      JLabel lblConceptDetail = new JLabel("Concept Detail");
      lblConceptDetail.setBounds(10, 136, 254, 14);
      contentPane.add(lblConceptDetail);
    }
    {
      JLabel lblAttributes = new JLabel("Attributes");
      lblAttributes.setBounds(20, 161, 244, 14);
      contentPane.add(lblAttributes);
    }
    {
      JLabel lblElements = new JLabel("Elements (Class | Method | Param)");
      lblElements.setBounds(19, 348, 244, 14);
      contentPane.add(lblElements);
    }
    {
      JScrollPane scrollAttributes = new JScrollPane(attributesList);
      scrollAttributes.setBounds(10, 186, 264, 151);
      scrollAttributes.getViewport().add(attributesList); 
      contentPane.add(scrollAttributes);
    }
    {
      JScrollPane scrollElements = new JScrollPane(elementsList);
      scrollElements.setBounds(10, 373, 264, 278);
      scrollElements.getViewport().add(elementsList);
      contentPane.add(scrollElements);
    }
    {
      JPanel panel = new JPanel();
      panel.setBackground(Color.BLACK);
      panel.setBounds(284, 11, 890, 640);
      panel.add(graphView);
      contentPane.add(panel);
    }
  }
}
