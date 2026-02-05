package doczilla.com.task1.util;

import doczilla.com.task1.domain.Color;

import java.util.List;

public class ValidConf {

    // Легкая - решается за 15-25 ходов
    public  List<List<Color>> easy() {
        return List.of(
                List.of(c(1), c(1), c(1), c(2)),
                List.of(c(2), c(2), c(2), c(3)),
                List.of(c(3), c(3), c(3), c(4)),
                List.of(c(4), c(4), c(4), c(1)),
                List.of(c(5), c(5), c(5), c(6)),
                List.of(c(6), c(6), c(6), c(7)),
                List.of(c(7), c(7), c(7), c(8)),
                List.of(c(8), c(8), c(8), c(5)),
                List.of(c(9), c(9), c(9), c(10)),
                List.of(c(10), c(10), c(10), c(11)),
                List.of(c(11), c(11), c(11), c(12)),
                List.of(c(12), c(12), c(12), c(9)),
                List.of(),
                List.of()
        );
    }

    // Средняя - решается за 40-60 ходов
    public  List<List<Color>> medium() {
        return List.of(
                List.of(c(1), c(2), c(3), c(4)),
                List.of(c(1), c(2), c(3), c(5)),
                List.of(c(1), c(2), c(4), c(6)),
                List.of(c(1), c(3), c(5), c(7)),
                List.of(c(2), c(4), c(6), c(8)),
                List.of(c(3), c(5), c(7), c(8)),
                List.of(c(4), c(6), c(7), c(9)),
                List.of(c(5), c(8), c(9), c(10)),
                List.of(c(6), c(9), c(10), c(11)),
                List.of(c(7), c(10), c(11), c(12)),
                List.of(c(8), c(11), c(12), c(12)),
                List.of(c(9), c(10), c(11), c(12)),
                List.of(),
                List.of()
        );
    }

    // Сложная - решается за 80-120 ходов
    public  List<List<Color>> hard() {
        return List.of(
                List.of(c(12), c(11), c(10), c(9)),
                List.of(c(8), c(7), c(6), c(5)),
                List.of(c(4), c(3), c(2), c(1)),
                List.of(c(12), c(8), c(4), c(1)),
                List.of(c(11), c(7), c(3), c(2)),
                List.of(c(10), c(6), c(5), c(1)),
                List.of(c(9), c(8), c(3), c(2)),
                List.of(c(12), c(7), c(6), c(4)),
                List.of(c(11), c(10), c(5), c(9)),
                List.of(c(9), c(6), c(4), c(3)),
                List.of(c(5), c(2), c(1), c(10)),
                List.of(c(11), c(12), c(7), c(8)),
                List.of(),
                List.of()
        );
    }

    // Очень сложная - решается за 150+ ходов или не решается быстро
    public  List<List<Color>> veryHard() {
        return List.of(
                List.of(c(1), c(2), c(3), c(4)),
                List.of(c(2), c(3), c(4), c(5)),
                List.of(c(3), c(4), c(5), c(6)),
                List.of(c(4), c(5), c(6), c(7)),
                List.of(c(5), c(6), c(7), c(8)),
                List.of(c(6), c(7), c(8), c(9)),
                List.of(c(7), c(8), c(9), c(10)),
                List.of(c(8), c(9), c(10), c(11)),
                List.of(c(9), c(10), c(11), c(12)),
                List.of(c(10), c(11), c(12), c(1)),
                List.of(c(11), c(12), c(1), c(2)),
                List.of(c(12), c(1), c(2), c(3)),
                List.of(),
                List.of()
        );
    }

    // Реально сложная — хаотичное перемешивание
    public List<List<Color>> extreme() {
        return List.of(
                List.of(c(12), c(5), c(9), c(3)),
                List.of(c(1), c(8), c(4), c(11)),
                List.of(c(7), c(2), c(10), c(6)),
                List.of(c(3), c(11), c(7), c(1)),
                List.of(c(9), c(4), c(12), c(8)),
                List.of(c(6), c(1), c(5), c(10)),
                List.of(c(2), c(9), c(3), c(7)),
                List.of(c(11), c(6), c(8), c(4)),
                List.of(c(5), c(10), c(2), c(12)),
                List.of(c(4), c(3), c(6), c(9)),
                List.of(c(8), c(12), c(1), c(5)),
                List.of(c(10), c(7), c(11), c(2)),
                List.of(),
                List.of()
        );
    }

    // Почти решаемая, но с ловушками
    public List<List<Color>> trap() {
        return List.of(
                // Почти готовые, но мешают друг другу
                List.of(c(1), c(1), c(1), c(2)),  // не хватает 1, мешает 2
                List.of(c(2), c(2), c(2), c(3)),  // не хватает 2, мешает 3
                List.of(c(3), c(3), c(3), c(4)),  // и так далее...
                List.of(c(4), c(4), c(4), c(1)),
                List.of(c(5), c(5), c(5), c(6)),
                List.of(c(6), c(6), c(6), c(7)),
                List.of(c(7), c(7), c(7), c(8)),
                List.of(c(8), c(8), c(8), c(5)),
                // Остальные перемешаны
                List.of(c(9), c(10), c(11), c(12)),
                List.of(c(9), c(10), c(11), c(12)),
                List.of(c(9), c(10), c(11), c(12)),
                List.of(c(9), c(10), c(11), c(12)),
                List.of(),
                List.of()
        );
    }

    private  Color c(int v) {
        return new Color(v);
    }
}