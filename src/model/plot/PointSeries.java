package model.plot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PointSeries {
    private final List<Point2D> points = new ArrayList<>();

    public void add(Point2D point) {
        points.add(point);
    }

    public void clear() {
        points.clear();
    }

    public int size() {
        return points.size();
    }

    public List<Point2D> asList() {
        return Collections.unmodifiableList(points);
    }
}
