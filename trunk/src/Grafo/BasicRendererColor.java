package Grafo;


import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ConcurrentModificationException;
import java.util.Map;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicEdgeRenderer;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;


/**
 * The default implementation of the Renderer used by the
 * VisualizationViewer. Default Vertex and Edge Renderers
 * are supplied, or the user may set custom values. The
 * Vertex and Edge renderers are used in the renderVertex
 * and renderEdge methods, which are called in the render
 * loop of the VisualizationViewer.
 * 
 * @author Tom Nelson
 */
public class BasicRendererColor<V,E> implements Renderer<V, E> {
  
    BasicVertexRendererColor<V,E> vertexRenderer = new BasicVertexRendererColor<V,E>();
    Renderer.VertexLabel<V,E> vertexLabelRenderer = new BasicVertexLabelRenderer<V,E>();
    Renderer.Edge<V,E> edgeRenderer = new BasicEdgeRenderer<V,E>();
    Renderer.EdgeLabel<V,E> edgeLabelRenderer = new BasicEdgeLabelRenderer<V,E>();
    private V topVertex;
    private V bottomVertex;
    int counter;
    
    public V getBottomVertex() {
      return bottomVertex;
    }

    public void setBottomVertex(V bottomVertex) {
      this.bottomVertex = bottomVertex;
    }

    public V getTopVertex() {
      return topVertex;
    }

    public void setTopVertex(V topVertex) {
      this.topVertex = topVertex;
    }            
    
    public BasicRendererColor(Map<V, Paint> colores){
        vertexRenderer.setVertexColors(colores);      
        counter = 0;
    }
    
    public void setVertexColor(V v,Paint p){
       if (vertexRenderer.getVertexColors().containsKey(v)){
         vertexRenderer.getVertexColors().remove(v);
         vertexRenderer.getVertexColors().put(v, p);
       }
       else vertexRenderer.getVertexColors().put(v, p);            
    }
    
  public void render(RenderContext<V, E> renderContext, Layout<V, E> layout) {
    
    // paint all the edges
        try {
          for(E e : layout.getGraph().getEdges()) {

            renderEdge(
                    renderContext,
                    layout,
                    e);
            renderEdgeLabel(
                    renderContext,
                    layout,
                    e);
          }
        } catch(ConcurrentModificationException cme) {
          renderContext.getScreenDevice().repaint();
        }
    
    // paint all the vertices
        try {
          for(V v : layout.getGraph().getVertices()) {

          if (v.equals(bottomVertex) && counter == 0)          
            locateBottomVertex(renderContext,layout);
          
          renderVertex(
                    renderContext,
                        layout,
                    v);
          renderVertexLabel(
                    renderContext,
                        layout,
                    v);                    
          }
        } catch(ConcurrentModificationException cme) {
            renderContext.getScreenDevice().repaint();
        }
  }
  
    public void renderVertex(RenderContext<V,E> rc, Layout<V,E> layout, V v) {
        vertexRenderer.paintVertex(rc, layout, v);
    }
    
    public void renderVertexLabel(RenderContext<V,E> rc, Layout<V,E> layout, V v) {
        vertexLabelRenderer.labelVertex(rc, layout, v, rc.getVertexLabelTransformer().transform(v));
    }
    
    public void renderEdge(RenderContext<V,E> rc, Layout<V,E> layout, E e) {
      edgeRenderer.paintEdge(rc, layout, e);
    }
    
    public void renderEdgeLabel(RenderContext<V,E> rc, Layout<V,E> layout, E e) {
      edgeLabelRenderer.labelEdge(rc, layout, e, rc.getEdgeLabelTransformer().transform(e));
    }
    
    public void setVertexRenderer(BasicVertexRendererColor<V,E> r) {
      this.vertexRenderer = r;
    }

    public void setEdgeRenderer(Renderer.Edge<V,E> r) {
      this.edgeRenderer = r;
    }

  /**
   * @return the edgeLabelRenderer
   */
  public Renderer.EdgeLabel<V, E> getEdgeLabelRenderer() {
    return edgeLabelRenderer;
  }

  /**
   * @param edgeLabelRenderer the edgeLabelRenderer to set
   */
  public void setEdgeLabelRenderer(Renderer.EdgeLabel<V, E> edgeLabelRenderer) {
    this.edgeLabelRenderer = edgeLabelRenderer;
  }

  /**
   * @return the vertexLabelRenderer
   */
  public Renderer.VertexLabel<V, E> getVertexLabelRenderer() {
    return vertexLabelRenderer;
  }

  /**
   * @param vertexLabelRenderer the vertexLabelRenderer to set
   */
  public void setVertexLabelRenderer(
      Renderer.VertexLabel<V, E> vertexLabelRenderer) {
    this.vertexLabelRenderer = vertexLabelRenderer;
  }

  /**
   * @return the edgeRenderer
   */
  public Renderer.Edge<V, E> getEdgeRenderer() {
    return edgeRenderer;
  }

  /**
   * @return the vertexRenderer
   */
  public Renderer.Vertex<V, E> getVertexRenderer() {
    return vertexRenderer;
  }

  @Override
  public void setVertexRenderer(edu.uci.ics.jung.visualization.renderers.Renderer.Vertex<V, E> r) {
    // TODO Auto-generated method stub
    
  }
  
  public void locateBottomVertex(RenderContext<V, E> renderContext, Layout<V, E> layout){
    counter++;
    Point2D p = layout.transform(topVertex);
    p = renderContext.getMultiLayerTransformer().transform(Layer.LAYOUT, p);
    float topX = (float)p.getX();
//    float topY = (float)p.getY();
    
    Point2D p2 = layout.transform(bottomVertex);
    p2 = renderContext.getMultiLayerTransformer().transform(Layer.LAYOUT, p2);
//    float bottomX = (float)p.getX();
    float bottomY = (float)p2.getY();

    Point2D pos = new Point2D.Float (topX,bottomY + 200);
    layout.setLocation(bottomVertex, pos);    
  }
}