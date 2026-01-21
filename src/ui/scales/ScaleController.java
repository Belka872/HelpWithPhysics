package ui.scales;

import model.scales.MassBlock;
import model.scales.ScaleConfig;
import model.scales.ScaleModel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ScaleController extends MouseAdapter {
    private final ScaleModel model;
    private final ScaleConfig config;
    private final ScaleCanvas canvas;

    public ScaleController(ScaleModel model, ScaleConfig config, ScaleCanvas canvas) {
        this.model = model;
        this.config = config;
        this.canvas = canvas;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();
        int width = config.getCubeSize();
        int height = (int) (config.getCubeSize() * 0.75);
        int handleHeight = (int) (height * 0.3);

        for (MassBlock block : model.getBlocks()) {
            int x = block.getPosition().x;
            int y = block.getPosition().y;
            int left = x - width / 2;
            int right = x + width / 2;
            int top = y - height / 2 - handleHeight / 2;
            int bottom = y + height / 2;
            if (mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= bottom) {
                canvas.setDraggingBlockId(block.getId());
                return;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canvas.setDraggingBlockId(-1);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int draggingId = canvas.getDraggingBlockId();
        if (draggingId == -1) {
            return;
        }
        MassBlock block = model.getBlockById(draggingId);
        if (block != null) {
            block.setPosition(e.getX(), e.getY());
            canvas.repaint();
        }
    }
}
