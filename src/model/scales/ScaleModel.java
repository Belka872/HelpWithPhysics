package model.scales;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScaleModel {
    private final List<MassBlock> blocks;
    private double angle;
    private double limitedAngle;

    public ScaleModel(List<MassBlock> blocks) {
        this.blocks = new ArrayList<>(blocks);
    }

    public List<MassBlock> getBlocks() {
        return Collections.unmodifiableList(blocks);
    }

    public MassBlock getBlockById(int id) {
        for (MassBlock block : blocks) {
            if (block.getId() == id) {
                return block;
            }
        }
        return null;
    }

    public List<MassBlock> getHiddenBlocks() {
        List<MassBlock> hidden = new ArrayList<>();
        for (MassBlock block : blocks) {
            if (block.isHiddenLabel()) {
                hidden.add(block);
            }
        }
        return hidden;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getLimitedAngle() {
        return limitedAngle;
    }

    public void setLimitedAngle(double limitedAngle) {
        this.limitedAngle = limitedAngle;
    }
}
