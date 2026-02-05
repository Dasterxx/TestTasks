package doczilla.com.task1.colortasks;

import doczilla.com.task1.domain.Color;

import java.util.List;

public class AlmostFullColors {

    public static List<List<Color>> createSimpleConfiguration() {
        // Почти решённая головоломка — для теста
        return List.of(
                List.of(c(1), c(1), c(1), c(1)),
                List.of(c(2), c(2), c(2), c(2)),
                List.of(c(3), c(3), c(3), c(3)),
                List.of(c(4), c(4), c(4), c(4)),
                List.of(c(5), c(5), c(5), c(5)),
                List.of(c(6), c(6), c(6), c(6)),
                List.of(c(7), c(7), c(7), c(7)),
                List.of(c(8), c(8), c(8), c(8)),
                List.of(c(9), c(9), c(9), c(9)),
                List.of(c(10), c(10), c(10), c(10)),
                List.of(c(11), c(11)),
                List.of(c(12), c(12), c(11), c(12)),
                List.of(),
                List.of()
        );
    }

    private static Color c(int v) {
        return new Color(v);
    }
}
