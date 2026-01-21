package service.scales;

import model.scales.ScaleConfig;
import model.scales.ScaleModel;

public interface WeightPuzzleProvider {
    ScaleModel createPuzzle(ScaleConfig config);
}
