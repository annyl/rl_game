package ru.hse.checker.intelligence;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Logic;

public interface Player {
    Logic.Path step(Board board);
}
