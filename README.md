//=========================================================//
ВСЕ 3 ЗАДАЧИ ДОЛЖНЫ ЗАПУСКАТЬСЯ ЧЕРЕЗ .bat
билд проекта нужен только для 3 задачи из-за зависимостей
//=========================================================//

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


 Оба алгоритма (по умолчанию)
Optional<Solution> solution = SolverServiceFactory.defaultService().solve(initialState);

 Только A*
Optional<Solution> solution = SolverServiceFactory.fastOnly().solve(initialState);

 Только QuickSolver
Optional<Solution> solution = SolverServiceFactory.quickOnly().solve(initialState);

TASK 2 

## Стек технологий

- **Java 21** (Virtual Threads, Records, Pattern Matching)
- **Встроенный HTTP Server** (`com.sun.net.httpserver`) - без Spring
- **Чистая архитектура** - никаких фреймворков в domain/application

## Быстрый старт

### Требования

- JDK 21+
- Maven или Gradle (или компиляция вручную)

# Запуск через bat скрипты - task2.bat

TASK 3 

Стек
Java 21 (без Spring Framework)
Встроенный HTTP сервер (com.sun.net.httpserver)
Jackson — JSON парсинг
JFreeChart — генерация графиков
Open-Meteo API — данные о погоде

Требования
JDK 21 или выше
Gradle (для сборки) или готовый JAR

