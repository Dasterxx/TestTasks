TASK-1
# Water Sort Puzzle Solver
Java-приложение для решения головоломки "Сортировка жидкостей" (Water Sort Puzzle) с использованием алгоритмов поиска пути.

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
*Конфигурация ставится в main классе там все 3 вызова 1 из которых не закоментирован 
default - SolverServiceFactory.defaultService().solve(initialState);

Выбор стратегий через `SolverConfig` - `SolverServiceFactory`:

```java
// Оба алгоритма (по умолчанию)
Optional<Solution> solution = SolverServiceFactory.defaultService().solve(initialState);

// Только A*
Optional<Solution> solution = SolverServiceFactory.fastOnly().solve(initialState);

// Только QuickSolver
Optional<Solution> solution = SolverServiceFactory.quickOnly().solve(initialState);
