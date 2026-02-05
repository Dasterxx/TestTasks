package doczilla.com.task1.colortasks;

import doczilla.com.task1.domain.Color;

import java.util.List;

public class HardConfiguration {

    public static List<List<Color>> createHardConfiguration() {
        // Максимально перемешанная головоломка
        // Каждый цвет разбросан по 4 разным пробиркам
        return List.of(
                // Пробирка 0: все разные цвета
                List.of(c(12), c(11), c(10), c(9)),
                // Пробирка 1: все разные цвета
                List.of(c(8), c(7), c(6), c(5)),
                // Пробирка 2: все разные цвета
                List.of(c(4), c(3), c(2), c(1)),
                // Пробирка 3: повторы, но перемешаны
                List.of(c(12), c(8), c(4), c(1)),
                // Пробирка 4: повторы, но перемешаны
                List.of(c(11), c(7), c(3), c(2)),
                // Пробирка 5: повторы, но перемешаны
                List.of(c(10), c(6), c(5), c(1)),
                // Пробирка 6: повторы, но перемешаны
                List.of(c(9), c(8), c(3), c(2)),
                // Пробирка 7: повторы, но перемешаны
                List.of(c(12), c(7), c(6), c(4)),
                // Пробирка 8: повторы, но перемешаны
                List.of(c(11), c(10), c(5), c(1)),
                // Пробирка 9: повторы, но перемешаны
                List.of(c(9), c(6), c(4), c(3)),
                // Пробирка 10: почти полная, но неправильный порядок
                List.of(c(12), c(11), c(10), c(9)),
                // Пробирка 11: почти полная, но неправильный порядок
                List.of(c(8), c(7), c(6), c(5)),
                // Пробирка 12: пустая
                List.of(),
                // Пробирка 13: пустая
                List.of()
        );
    }

    private static Color c(int v) {
        return new Color(v);
    }
}
