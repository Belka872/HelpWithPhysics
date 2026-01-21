package service.plot;

import model.plot.Point2D;

import java.util.List;

public class LagrangeInterpolator {
    public double interpolate(List<Point2D> points, double x) {
        int n = points.size();
        if (n == 0) {
            return Double.NaN;
        }
        double result = 0;
        for (int i = 0; i < n; i++) {
            double term = points.get(i).y();
            for (int j = 0; j < n; j++) {
                if (j != i) {
                    term = term * (x - points.get(j).x()) / (points.get(i).x() - points.get(j).x());
                }
            }
            result += term;
        }
        return result;
    }
}
