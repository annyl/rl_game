package ru.hse.checker.controller.io;

import java.util.ArrayList;
import java.util.List;

import ru.hse.checker.model.Cell;
import ru.hse.checker.utils.Pair;

public interface IOController {

    Cell selectSrcCell();
    Cell selectDstCell(ArrayList<Cell> tos);

    default Cell selectSrcCell(List<Pair<Cell, Cell>> paths) {
        Cell cell = null;
        while (cell == null) {
            cell = selectSrcCell();
            for (Pair<Cell, Cell> pair : paths) {
                if (cell == pair.first)
                    return cell;
            }
        }
        return null;
    }


}
