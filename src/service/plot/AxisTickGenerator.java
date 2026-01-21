package service.plot;

import java.util.ArrayList;
import java.util.List;

public class AxisTickGenerator {
    public List<Integer> generateTicks(int maxValue, int pixelLength) {
        int step = computeStep(maxValue, pixelLength);
        List<Integer> ticks = new ArrayList<>();
        if (step <= 0) {
            return ticks;
        }
        int count = (int) Math.ceil(maxValue / (double) step);
        for (int i = 1; i <= count; i++) {
            ticks.add(i * step);
        }
        return ticks;
    }

    public int computeStep(int maxValue, int pixelLength) {
        if (maxValue <= 0 || pixelLength <= 0) {
            return 1;
        }
        int targetTicks = Math.max(2, pixelLength / 80);
        double rawStep = maxValue / (double) targetTicks;
        double magnitude = Math.pow(10, Math.floor(Math.log10(rawStep)));
        double normalized = rawStep / magnitude;
        double nice;
        if (normalized <= 1) {
            nice = 1;
        } else if (normalized <= 2) {
            nice = 2;
        } else if (normalized <= 5) {
            nice = 5;
        } else {
            nice = 10;
        }
        return Math.max(1, (int) Math.round(nice * magnitude));
    }
}
