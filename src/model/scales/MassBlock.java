package model.scales;

import java.awt.Point;

public class MassBlock {
    private final int id;
    private final int mass;
    private final boolean hiddenLabel;
    private final Point initialPosition;
    private final Point position;

    public MassBlock(int id, int mass, boolean hiddenLabel, Point initialPosition) {
        this.id = id;
        this.mass = mass;
        this.hiddenLabel = hiddenLabel;
        this.initialPosition = new Point(initialPosition);
        this.position = new Point(initialPosition);
    }

    public int getId() {
        return id;
    }

    public int getMass() {
        return mass;
    }

    public boolean isHiddenLabel() {
        return hiddenLabel;
    }

    public Point getInitialPosition() {
        return new Point(initialPosition);
    }

    public Point getPosition() {
        return new Point(position);
    }

    public void setPosition(int x, int y) {
        position.setLocation(x, y);
    }

    public String getDisplayLabel() {
        return hiddenLabel ? "?" : String.valueOf(mass);
    }
}
