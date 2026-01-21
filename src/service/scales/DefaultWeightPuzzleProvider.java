package service.scales;

import model.scales.MassBlock;
import model.scales.ScaleConfig;
import model.scales.ScaleModel;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DefaultWeightPuzzleProvider implements WeightPuzzleProvider {
    private final Random random;

    public DefaultWeightPuzzleProvider(Random random) {
        this.random = random;
    }

    @Override
    public ScaleModel createPuzzle(ScaleConfig config) {
        int count = config.getWeightSetSize();
        int unknownCount = Math.min(config.getUnknownCount(), count);
        int step = config.getWeightStep();

        int maxMultiplier = Math.max(count + 4, count);
        List<Integer> values = new ArrayList<>();
        for (int i = 1; i <= maxMultiplier; i++) {
            values.add(i * step);
        }
        Collections.shuffle(values, random);

        List<Integer> masses = new ArrayList<>(values.subList(0, count));
        Collections.shuffle(masses, random);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, random);
        Set<Integer> hiddenIndices = new HashSet<>(indices.subList(0, unknownCount));

        int[] initialX = config.getInitialBlockX(count);
        int initialY = config.getInitialBlockY();
        List<MassBlock> blocks = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            boolean hidden = hiddenIndices.contains(i);
            blocks.add(new MassBlock(i + 1, masses.get(i), hidden, new Point(initialX[i], initialY)));
        }

        return new ScaleModel(blocks);
    }
}
