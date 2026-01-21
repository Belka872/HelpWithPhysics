package model.scales;

public class ScaleConfig {
    private final int rectWidth = 500;
    private final int rectHeight = 10;
    private final int centerX = 500;
    private final int centerY = 500;
    private final int handleSizeX = 10;
    private final int handleSizeY = 30;
    private final int scaleWidth = 80;
    private final int scaleHeight = 10;
    private final int cubeSize = 50;
    private final int floorY = 725;
    private final int floorStartY = 700;
    private int sceneWidth = 1920;
    private int sceneHeight = 1080;
    private final int baseX = 490;
    private final int baseY = 100;
    private final int baseWidth = 20;
    private final int baseHeight = 650;
    private final int basePlatformX = 300;
    private final int basePlatformY = 750;
    private final int basePlatformWidth = 400;
    private final int basePlatformHeight = 20;
    private final int xRangeMinRight = 630;
    private final int xRangeMaxRight = 830;
    private final int xRangeMinLeft = 192;
    private final int xRangeMaxLeft = 350;
    private final int plateYOffset = -60;
    private final double gravity = 0.005;
    private final double k = 0.01;
    private final double rotationStep = Math.PI / 48;
    private final double maxAngleScale = Math.PI / 3000;
    private final double maxTiltAngle = Math.PI / 10;
    private final int initialBlockY = 725;
    private int weightSetSize = 5;
    private int unknownCount = 1;
    private final int weightStep = 10;

    public ScaleConfig() {
        setWeightSetSize(weightSetSize);
    }

    public int getRectWidth() {
        return rectWidth;
    }

    public int getRectHeight() {
        return rectHeight;
    }

    public int getCenterX() {
        return centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public int getHandleSizeX() {
        return handleSizeX;
    }

    public int getHandleSizeY() {
        return handleSizeY;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public int getCubeSize() {
        return cubeSize;
    }

    public int getFloorY() {
        return floorY;
    }

    public int getFloorStartY() {
        return floorStartY;
    }

    public int getSceneWidth() {
        return sceneWidth;
    }

    public int getSceneHeight() {
        return sceneHeight;
    }

    public boolean setSceneSize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return false;
        }
        boolean changed = width != sceneWidth || height != sceneHeight;
        sceneWidth = width;
        sceneHeight = height;
        return changed;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public int getBaseWidth() {
        return baseWidth;
    }

    public int getBaseHeight() {
        return baseHeight;
    }

    public int getBasePlatformX() {
        return basePlatformX;
    }

    public int getBasePlatformY() {
        return basePlatformY;
    }

    public int getBasePlatformWidth() {
        return basePlatformWidth;
    }

    public int getBasePlatformHeight() {
        return basePlatformHeight;
    }

    public int getXRangeMinRight() {
        return xRangeMinRight;
    }

    public int getXRangeMaxRight() {
        return xRangeMaxRight;
    }

    public int getXRangeMinLeft() {
        return xRangeMinLeft;
    }

    public int getXRangeMaxLeft() {
        return xRangeMaxLeft;
    }

    public int getPlateYOffset() {
        return plateYOffset;
    }

    public double getGravity() {
        return gravity;
    }

    public double getK() {
        return k;
    }

    public double getRotationStep() {
        return rotationStep;
    }

    public double getMaxAngleScale() {
        return maxAngleScale;
    }

    public double getMaxTiltAngle() {
        return maxTiltAngle;
    }

    public int[] getInitialBlockX() {
        return getInitialBlockX(weightSetSize);
    }

    public int[] getInitialBlockX(int count) {
        if (count <= 0) {
            return new int[0];
        }
        int minX = Math.max(cubeSize, 20);
        int maxX = Math.max(minX, sceneWidth - cubeSize);
        int spacing = count == 1 ? 0 : (maxX - minX) / (count - 1);
        int[] positions = new int[count];
        for (int i = 0; i < count; i++) {
            positions[i] = minX + i * spacing;
        }
        return positions;
    }

    public int getInitialBlockY() {
        return initialBlockY;
    }

    public int getWeightSetSize() {
        return weightSetSize;
    }

    public void setWeightSetSize(int weightSetSize) {
        this.weightSetSize = weightSetSize;
        this.unknownCount = mapUnknownCount(weightSetSize);
    }

    public int getUnknownCount() {
        return unknownCount;
    }

    public int getWeightStep() {
        return weightStep;
    }

    private int mapUnknownCount(int size) {
        if (size == 5) {
            return 1;
        }
        if (size == 10) {
            return 2;
        }
        if (size == 15) {
            return 3;
        }
        return Math.max(1, size / 5);
    }
}
