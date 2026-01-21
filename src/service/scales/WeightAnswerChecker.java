package service.scales;

import model.scales.MassBlock;
import model.scales.ScaleModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class WeightAnswerChecker {
    public boolean isCorrect(List<Integer> guesses, ScaleModel model) {
        List<MassBlock> hidden = model.getHiddenBlocks();
        if (guesses == null || guesses.size() != hidden.size()) {
            return false;
        }
        List<Integer> expected = new ArrayList<>();
        for (MassBlock block : hidden) {
            expected.add(block.getMass());
        }
        List<Integer> sortedGuesses = new ArrayList<>(guesses);
        Collections.sort(expected);
        Collections.sort(sortedGuesses);
        return expected.equals(sortedGuesses);
    }

    public List<Integer> getAnswers(ScaleModel model) {
        List<MassBlock> hidden = new ArrayList<>(model.getHiddenBlocks());
        hidden.sort(Comparator.comparingInt(MassBlock::getId));
        List<Integer> answers = new ArrayList<>();
        for (MassBlock block : hidden) {
            answers.add(block.getMass());
        }
        return answers;
    }
}
