package ui.scales;

import model.scales.ScaleConfig;
import model.scales.ScaleModel;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;

public class ScaleCanvas extends JPanel {
    private final ScaleConfig config;
    private final ScaleRenderer renderer;
    private ScaleModel model;
    private int draggingBlockId = -1;

    public ScaleCanvas(ScaleModel model, ScaleConfig config, ScaleRenderer renderer) {
        this.model = model;
        this.config = config;
        this.renderer = renderer;
        setPreferredSize(new Dimension(config.getSceneWidth(), config.getSceneHeight()));
    }

    public void setModel(ScaleModel model) {
        this.model = model;
        repaint();
    }

    public ScaleModel getModel() {
        return model;
    }

    public void setDraggingBlockId(int id) {
        this.draggingBlockId = id;
    }

    public int getDraggingBlockId() {
        return draggingBlockId;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        renderer.render(g, model, config);
    }
}
