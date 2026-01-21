package service.scales;

import model.scales.MassBlock;
import model.scales.ScaleConfig;
import model.scales.ScaleModel;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ScaleModelFactory {
    private final Random random;

    public ScaleModelFactory(Random random) {
        this.random = random;
    }

    public ScaleModel createDefault(ScaleConfig config) {
        int step = config.getWeightStep();
        int[] baseMasses = {step, step * 2, step * 3, step};
        int[] possibleValues = {step, step * 2, step * 3, step * 4, step * 5};
        int secretMass = possibleValues[random.nextInt(possibleValues.length)];

        List<MassBlock> blocks = new ArrayList<>();
        int[] initialX = config.getInitialBlockX(baseMasses.length + 1);
        int initialY = config.getInitialBlockY();

        for (int i = 0; i < baseMasses.length; i++) {
            blocks.add(new MassBlock(i + 1, baseMasses[i], false, new Point(initialX[i], initialY)));
        }
        blocks.add(new MassBlock(baseMasses.length + 1, secretMass, true,
                new Point(initialX[baseMasses.length], initialY)));

        return new ScaleModel(blocks);
    }
}
