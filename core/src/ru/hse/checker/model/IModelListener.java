package ru.hse.checker.model;

import ru.hse.checker.model.intelligence.Player;

public interface IModelListener extends Player.ActionListener, Board.ICheckerChangeListener {
}
