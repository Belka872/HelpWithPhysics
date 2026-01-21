package ui.plot;

import model.plot.Point2D;
import model.plot.PointSeries;
import service.plot.AxisTickGenerator;
import service.plot.ExpressionParser;
import service.plot.LagrangeInterpolator;
import service.plot.PolynomialService;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Locale;

public class PointPlotterPanel extends JPanel {
    private final PointSeries points = new PointSeries();
    private final PointSeries curvePoints = new PointSeries();
    private final PointSeries formulaPoints = new PointSeries();
    private final LagrangeInterpolator interpolator = new LagrangeInterpolator();
    private final PolynomialService polynomialService = new PolynomialService();
    private final AxisTickGenerator tickGenerator = new AxisTickGenerator();

    private JTextField xField;
    private JTextField yField;
    private JTextField formulaField;
    private JCheckBox lagrangeToggle;
    private PlotCanvas canvas;
    private PointsTableModel pointsTableModel;

    private List<ExpressionParser.Token> formulaRpn;
    private boolean showLagrange = true;

    private int maxX = 10;
    private int maxY = 10;
    private final int padding = 60;
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    public PointPlotterPanel() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new GridLayout(2, 1));
        topPanel.add(buildPointPanel());
        topPanel.add(buildFormulaPanel());
        add(topPanel, BorderLayout.NORTH);

        pointsTableModel = new PointsTableModel();
        JTable pointsTable = new JTable(pointsTableModel);
        pointsTable.setFillsViewportHeight(true);
        JScrollPane tableScroll = new JScrollPane(pointsTable);
        tableScroll.setPreferredSize(new Dimension(180, 0));
        add(tableScroll, BorderLayout.EAST);

        canvas = new PlotCanvas();
        canvas.setBackground(Color.WHITE);
        add(canvas, BorderLayout.CENTER);

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                int selectedWidth = Math.abs(endX - startX);
                int selectedHeight = Math.abs(endY - startY);
                int usableWidth = Math.max(1, canvas.getWidth() - 2 * padding);
                int usableHeight = Math.max(1, canvas.getHeight() - 2 * padding);
                double widthRatio = selectedWidth / (double) usableWidth;
                double heightRatio = selectedHeight / (double) usableHeight;
                if (widthRatio > 0) {
                    maxX = Math.max(maxX, (int) Math.ceil(maxX * widthRatio));
                }
                if (heightRatio > 0) {
                    maxY = Math.max(maxY, (int) Math.ceil(maxY * heightRatio));
                }
                canvas.repaint();
            }
        });
    }

    private JPanel buildPointPanel() {
        JPanel pointPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        xField = new JTextField(5);
        yField = new JTextField(5);
        JButton addButton = new JButton("Add Point");
        addButton.addActionListener(e -> handleAddPoint());

        lagrangeToggle = new JCheckBox("Lagrange", true);
        lagrangeToggle.addActionListener(e -> {
            showLagrange = lagrangeToggle.isSelected();
            canvas.repaint();
        });

        pointPanel.add(new JLabel("X:"));
        pointPanel.add(xField);
        pointPanel.add(new JLabel("Y:"));
        pointPanel.add(yField);
        pointPanel.add(addButton);
        pointPanel.add(lagrangeToggle);
        return pointPanel;
    }

    private JPanel buildFormulaPanel() {
        JPanel formulaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formulaField = new JTextField(20);
        JButton plotButton = new JButton("Plot Formula");
        plotButton.addActionListener(e -> plotFormula());

        formulaPanel.add(new JLabel("Formula:"));
        formulaPanel.add(formulaField);
        formulaPanel.add(plotButton);
        return formulaPanel;
    }

    private void handleAddPoint() {
        try {
            int x = Integer.parseInt(xField.getText().trim());
            int y = Integer.parseInt(yField.getText().trim());
            if (x >= 1 && y >= 1) {
                if (x > maxX || y > maxY) {
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                }
                points.add(new Point2D(x, y));
                pointsTableModel.fireTableDataChanged();
                rebuildCurve();
                rebuildFormulaCurve();
                canvas.repaint();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Coordinates must be positive integers.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter valid integer values for X and Y.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rebuildCurve() {
        curvePoints.clear();
        int safeMaxX = Math.max(1, maxX);
        for (int i = 0; i <= 1000; i++) {
            double x = i * safeMaxX / 1000.0;
            double y = interpolator.interpolate(points.asList(), x);
            if (Double.isFinite(y)) {
                curvePoints.add(new Point2D(x, y));
                if (y > maxY) {
                    maxY = Math.max(maxY, (int) Math.ceil(y));
                }
            }
        }
    }

    private void plotFormula() {
        String expression = formulaField.getText().trim();
        if (expression.isEmpty()) {
            formulaRpn = null;
            formulaPoints.clear();
            canvas.repaint();
            return;
        }
        List<ExpressionParser.Token> rpn = ExpressionParser.toRpn(expression);
        if (rpn == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid formula. Check syntax and try again.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        formulaRpn = rpn;
        rebuildFormulaCurve();
        canvas.repaint();
    }

    private void rebuildFormulaCurve() {
        formulaPoints.clear();
        if (formulaRpn == null) {
            return;
        }
        int safeMaxX = Math.max(1, maxX);
        for (int i = 0; i <= 1000; i++) {
            double x = i * safeMaxX / 1000.0;
            double y;
            try {
                y = ExpressionParser.evalRpn(formulaRpn, x);
            } catch (RuntimeException ex) {
                continue;
            }
            if (Double.isFinite(y) && y >= 0) {
                formulaPoints.add(new Point2D(x, y));
                if (y > maxY) {
                    maxY = Math.max(maxY, (int) Math.ceil(y));
                }
            }
        }
    }

    private class PlotCanvas extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            drawAxes(g2d);

            if (showLagrange) {
                g2d.setColor(new Color(38, 116, 201));
                for (Point2D point : curvePoints.asList()) {
                    drawCurvePoint(g2d, point.x(), point.y());
                }
            }

            g2d.setColor(new Color(46, 153, 91));
            for (Point2D point : formulaPoints.asList()) {
                drawCurvePoint(g2d, point.x(), point.y());
            }

            g2d.setColor(new Color(202, 59, 59));
            for (Point2D point : points.asList()) {
                drawPoint(g2d, point.x(), point.y());
            }

            if (showLagrange && points.size() > 1) {
                float[] xs = new float[points.size()];
                float[] ys = new float[points.size()];
                for (int i = 0; i < points.size(); i++) {
                    Point2D point = points.asList().get(i);
                    xs[i] = (float) point.x();
                    ys[i] = (float) point.y();
                }
                float[] coeffs = polynomialService.getPolynomial(xs, ys);
                String equation = polynomialService.formatPolynomial(coeffs);
                g2d.setColor(Color.BLACK);
                g2d.drawString(equation, getWidth() - padding - 220, padding + 20);
            }
        }
    }

    private void drawAxes(Graphics g) {
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int usableWidth = Math.max(1, width - 2 * padding);
        int usableHeight = Math.max(1, height - 2 * padding);
        int safeMaxX = Math.max(1, maxX);
        int safeMaxY = Math.max(1, maxY);

        g.setColor(Color.BLACK);
        g.drawLine(padding, height - padding, width - padding, height - padding);
        g.drawString("X", width - padding + 10, height - padding + 10);

        for (int tick : tickGenerator.generateTicks(safeMaxX, usableWidth)) {
            int x = padding + (int) Math.round(tick * (usableWidth / (double) safeMaxX));
            g.drawLine(x, height - padding, x, height - padding + 6);
            g.drawString(Integer.toString(tick), x - 6, height - padding + 20);
        }

        g.drawLine(padding, height - padding, padding, padding);
        g.drawString("Y", padding - 15, padding - 10);

        for (int tick : tickGenerator.generateTicks(safeMaxY, usableHeight)) {
            int y = height - padding - (int) Math.round(tick * (usableHeight / (double) safeMaxY));
            g.drawLine(padding - 6, y, padding, y);
            g.drawString(Integer.toString(tick), padding - 35, y + 5);
        }
    }

    private void drawPoint(Graphics g, double x, double y) {
        int pixelX = toPixelX(x);
        int pixelY = toPixelY(y);
        g.fillOval(pixelX - 4, pixelY - 4, 8, 8);
    }

    private void drawCurvePoint(Graphics g, double x, double y) {
        int pixelX = toPixelX(x);
        int pixelY = toPixelY(y);
        g.fillOval(pixelX - 2, pixelY - 2, 4, 4);
    }

    private int toPixelX(double x) {
        int usableWidth = Math.max(1, canvas.getWidth() - 2 * padding);
        int safeMaxX = Math.max(1, maxX);
        return (int) Math.round(padding + x * (usableWidth / (double) safeMaxX));
    }

    private int toPixelY(double y) {
        int usableHeight = Math.max(1, canvas.getHeight() - 2 * padding);
        int safeMaxY = Math.max(1, maxY);
        return (int) Math.round(canvas.getHeight() - padding - y * (usableHeight / (double) safeMaxY));
    }

    private class PointsTableModel extends AbstractTableModel {
        private final String[] columns = {"X", "Y"};

        @Override
        public int getRowCount() {
            return points.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Point2D point = points.asList().get(rowIndex);
            double value = columnIndex == 0 ? point.x() : point.y();
            if (Math.rint(value) == value) {
                return Integer.toString((int) value);
            }
            return String.format(Locale.US, "%.2f", value);
        }
    }
}
