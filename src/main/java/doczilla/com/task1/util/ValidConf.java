package doczilla.com.task1.util;

import doczilla.com.task1.domain.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidConf {

    public List<List<Color>> generateValidConfiguration(int colors, int emptyTubes, int capacity) {
        List<Color> allDrops = new ArrayList<>();
        for (int i = 1; i <= colors; i++) {
            for (int j = 0; j < capacity; j++) {
                allDrops.add(new Color(i));
            }
        }
        Collections.shuffle(allDrops);

        List<List<Color>> result = new ArrayList<>();
        for (int i = 0; i < colors; i++) {
            List<Color> tube = new ArrayList<>();
            for (int j = 0; j < capacity; j++) {
                tube.add(allDrops.remove(0));
            }
            result.add(tube);
        }
        for (int i = 0; i < emptyTubes; i++) {
            result.add(List.of());
        }
        return result;
    }
}
