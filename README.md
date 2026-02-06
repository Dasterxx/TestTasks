TASK-1
# Water Sort Puzzle Solver
Java-приложение для решения головоломки "Сортировка жидкостей" (Water Sort Puzzle) с использованием алгоритмов поиска пути.
doczilla.com.task1/
├── domain/                 # Доменные сущности
│   ├── Color.java         # Цвет капли (value: 1-12)
│   ├── Move.java          # Ход (fromTube, toTube)
│   ├── Tube.java          # Пробирка с жидкостью
│   ├── PuzzleState.java   # Состояние головоломки
│   ├── Solution.java      # Решение (список ходов)
│   └── ColorSegment.java  # Сегмент капель одного цвета
│
├── strategy/              # Стратегии решения
│   ├── SolvingStrategy.java           # Интерфейс стратегии
│   ├── fastsolver/                    # A* алгоритм
│   │   ├── FastSolverStrategy.java    # Главный класс
│   │   ├── AStarSearch.java           # Логика поиска
│   │   ├── HeuristicCalculator.java   # Эвристическая функция
│   │   ├── StateApplier.java          # Применение ходов
│   │   └── Node.java                  # Узел поиска (record)
│   │
│   └── quicksolver/                   # Жадный алгоритм
│       ├── QuickSolverStrategy.java   # Главный класс
│       ├── GreedySearch.java          # Логика поиска
│       ├── MoveScorer.java            # Оценка ходов
│       └── StateApplier.java          # Применение ходов
│
├── service/               # Сервисный слой
│   ├── SolverService.java     # Оркестрация стратегий
│   ├── SolverConfig.java      # Конфигурация (какие стратегии использовать)
│   ├── SolutionVerifier.java  # Проверка корректности решения
│   └── ResultPrinter.java     # Вывод результата
│
├── util/                  # Утилиты и конфигурации
│   └── DifferentStrategies.java   # Генерация начальных состояний
│
├── visualization/         # Визуализация
│   └── PuzzleVisualizer.java      # Форматированный вывод состояния
│
└── WaterSortApplication.java      # Точка входа (Main)


## Алгоритмы

### 1. QuickSolverStrategy (Жадный алгоритм)
- **Сложность**: O(maxMoves × возможные_ходы)
- **Преимущества**: Очень быстрый (20-50 мс)
- **Недостатки**: Не гарантирует оптимальность
- **Использование**: Первичный быстрый поиск

### 2. FastSolverStrategy (A* поиск)
- **Сложность**: O(nodes × log(nodes))
- **Преимущества**: Находит оптимальное решение
- **Недостатки**: Требует больше памяти
- **Использование**: Резервный алгоритм при неудаче QuickSolver

## Конфигурация
*Конфигурация ставится в main классе там все 3 вызова 1 из которых не закоментирован default - new SolverService().solve(initialState);
Выбор стратегий через `SolverConfig`:

```java
// Оба алгоритма (по умолчанию)
new SolverService().solve(initialState);

// Только A*
new SolverService(SolverConfig.fastOnly()).solve(initialState);

// Только QuickSolver
new SolverService(SolverConfig.quickOnly()).solve(initialState);
