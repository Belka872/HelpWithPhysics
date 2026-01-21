package ui.scales;

import model.scales.MassBlock;
import model.scales.ScaleConfig;
import model.scales.ScaleModel;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ScaleRenderer {
    public void render(Graphics g, ScaleModel model, ScaleConfig config) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(216, 216, 216));
        g2d.fillRect(0, config.getFloorStartY(), config.getSceneWidth(), config.getSceneHeight());

        g2d.setColor(new Color(40, 40, 40));
        g2d.fillRect(config.getBaseX(), config.getBaseY(), config.getBaseWidth(), config.getBaseHeight());
        g2d.fillRect(config.getBasePlatformX(), config.getBasePlatformY(),
                config.getBasePlatformWidth(), config.getBasePlatformHeight());

        drawBeamAndPlates(g2d, config, model.getLimitedAngle());

        for (MassBlock block : model.getBlocks()) {
            drawWeight(g2d, block, config.getCubeSize());
        }

        g2d.dispose();
    }

    private void drawBeamAndPlates(Graphics2D g2d, ScaleConfig config, double angle) {
        double halfWidth = config.getRectWidth() / 2.0;
        double halfHeight = config.getRectHeight() / 2.0;
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double cx = config.getCenterX();
        double cy = config.getCenterY();

        double[] dx = {-halfWidth, halfWidth, halfWidth, -halfWidth};
        double[] dy = {-halfHeight, -halfHeight, halfHeight, halfHeight};
        int[] xs = new int[4];
        int[] ys = new int[4];
        for (int i = 0; i < 4; i++) {
            double x = cx + dx[i] * cos - dy[i] * sin;
            double y = cy + dx[i] * sin + dy[i] * cos;
            xs[i] = (int) Math.round(x);
            ys[i] = (int) Math.round(y);
        }
        g2d.fillPolygon(xs, ys, 4);

        double rightX = cx + halfWidth * cos;
        double rightY = cy + halfWidth * sin;
        double leftX = cx - halfWidth * cos;
        double leftY = cy - halfWidth * sin;

        drawPlate(g2d, config, rightX, rightY);
        drawPlate(g2d, config, leftX, leftY);
    }

    private void drawPlate(Graphics2D g2d, ScaleConfig config, double anchorX, double anchorY) {
        int handleX = (int) Math.round(anchorX - config.getHandleSizeX() / 2.0);
        int handleY = (int) Math.round(anchorY - config.getHandleSizeY());
        g2d.fillRect(handleX, handleY, config.getHandleSizeX(), config.getHandleSizeY());

        int plateX = (int) Math.round(anchorX - config.getScaleWidth());
        int plateY = (int) Math.round(anchorY - config.getHandleSizeY() - config.getScaleHeight());
        g2d.fillRect(plateX, plateY, config.getScaleWidth() * 2, config.getScaleHeight());
    }

    private void drawWeight(Graphics2D g2d, MassBlock block, int size) {
        int width = size;
        int height = (int) (size * 0.75);
        int x = block.getPosition().x - width / 2;
        int y = block.getPosition().y - height / 2;

        GradientPaint bodyPaint = new GradientPaint(x, y, new Color(96, 96, 96),
                x + width, y + height, new Color(48, 48, 48));
        g2d.setPaint(bodyPaint);
        g2d.fillRoundRect(x, y, width, height, 14, 14);
        g2d.setColor(new Color(30, 30, 30));
        g2d.drawRoundRect(x, y, width, height, 14, 14);

        int handleWidth = (int) (width * 0.5);
        int handleHeight = (int) (height * 0.3);
        int handleX = block.getPosition().x - handleWidth / 2;
        int handleY = y - handleHeight / 2;
        g2d.setColor(new Color(120, 120, 120));
        g2d.fillRoundRect(handleX, handleY, handleWidth, handleHeight, 12, 12);
        g2d.setColor(new Color(70, 70, 70));
        g2d.drawRoundRect(handleX, handleY, handleWidth, handleHeight, 12, 12);

        g2d.setColor(new Color(150, 150, 150, 140));
        g2d.fillRoundRect(x + 4, y + 4, width - 8, height / 3, 12, 12);

        Font font = new Font("Trebuchet MS", Font.BOLD, 18);
        g2d.setFont(font);
        String text = block.getDisplayLabel();
        FontMetrics fm = g2d.getFontMetrics();
        int textX = block.getPosition().x - fm.stringWidth(text) / 2;
        int textY = y + height / 2 + fm.getAscent() / 2 - 2;
        g2d.setColor(block.isHiddenLabel() ? new Color(246, 218, 130) : new Color(238, 238, 238));
        g2d.drawString(text, textX, textY);
    }
}
