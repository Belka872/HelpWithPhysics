package service.scales;

import model.scales.MassBlock;
import model.scales.ScaleConfig;
import model.scales.ScaleModel;

public class ScalePhysics {
    public void update(ScaleModel model, ScaleConfig config, int draggingBlockId) {
        int yRealRight = config.getCenterY()
                + (int) (Math.sin(model.getLimitedAngle()) * (config.getRectWidth() / 2.0))
                + config.getPlateYOffset();
        int yRealLeft = config.getCenterY()
                - (int) (Math.sin(model.getLimitedAngle()) * (config.getRectWidth() / 2.0))
                + config.getPlateYOffset();

        int yRangeMinRight = yRealRight - config.getCubeSize() / 2;
        int yRangeMaxRight = yRealRight + config.getCubeSize() * 2;
        int yRangeMinLeft = yRealLeft - config.getCubeSize() * 2;
        int yRangeMaxLeft = yRealLeft + config.getCubeSize() * 2;

        int leftSideMass = 0;
        int rightSideMass = 0;

        for (MassBlock block : model.getBlocks()) {
            int x = block.getPosition().x;
            int y = block.getPosition().y;
            boolean withinRight = x >= config.getXRangeMinRight() && x <= config.getXRangeMaxRight()
                    && y >= yRangeMinRight && y <= yRangeMaxRight;
            boolean withinLeft = x >= config.getXRangeMinLeft() && x <= config.getXRangeMaxLeft()
                    && y >= yRangeMinLeft && y <= yRangeMaxLeft;

            if (withinLeft) {
                leftSideMass += block.getMass();
            }
            if (withinRight) {
                rightSideMass += block.getMass();
            }

            if (withinLeft && block.getId() != draggingBlockId) {
                block.setPosition(x, yRealLeft);
            }
            if (withinRight && block.getId() != draggingBlockId) {
                block.setPosition(x, yRealRight);
            }
        }

        double targetAngle = (rightSideMass - leftSideMass) * config.getMaxAngleScale();
        targetAngle = Math.max(-config.getMaxTiltAngle(), Math.min(config.getMaxTiltAngle(), targetAngle));
        double angle = model.getAngle();
        double delta = targetAngle - angle;
        double step = Math.min(Math.abs(delta), config.getRotationStep());
        if (step > 0) {
            angle += Math.signum(delta) * step;
        }
        model.setAngle(angle);
        model.setLimitedAngle(angle);

        for (MassBlock block : model.getBlocks()) {
            int x = block.getPosition().x;
            int y = block.getPosition().y;
            boolean withinRight = x >= config.getXRangeMinRight() && x <= config.getXRangeMaxRight()
                    && y >= yRangeMinRight && y <= yRangeMaxRight;
            boolean withinLeft = x >= config.getXRangeMinLeft() && x <= config.getXRangeMaxLeft()
                    && y >= yRangeMinLeft && y <= yRangeMaxLeft;

            if (block.getId() != draggingBlockId) {
                if (x >= config.getXRangeMinRight() && x <= config.getXRangeMaxRight() && !withinRight && y <= yRealRight) {
                    block.setPosition(x, y + 20);
                }
                if (x >= config.getXRangeMinLeft() && x <= config.getXRangeMaxLeft() && !withinLeft && y <= yRealLeft) {
                    block.setPosition(x, y + 20);
                }
            }
        }

        for (MassBlock block : model.getBlocks()) {
            int x = block.getPosition().x;
            int y = block.getPosition().y;
            boolean onLeftPlate = x >= config.getXRangeMinLeft() && x <= config.getXRangeMaxLeft() && y <= yRealLeft;
            boolean onRightPlate = x >= config.getXRangeMinRight() && x <= config.getXRangeMaxRight() && y <= yRealRight;
            boolean isAir = block.getId() != draggingBlockId && !(onLeftPlate || onRightPlate);

            if (isAir) {
                block.setPosition(block.getInitialPosition().x, config.getFloorY());
            }
        }
    }
}
