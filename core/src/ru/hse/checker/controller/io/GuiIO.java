package ru.hse.checker.controller.io;

import java.util.ArrayList;

import ru.hse.checker.actor.BoardStage;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.GameModel;

public class GuiIO implements IOController, BoardStage.IViewListener {
    private GameModel model;
    private Cell selected = null;

    public GuiIO(GameModel model) {
        this.model = model;
    }

    @Override
    public Cell selectSrcCell() {
        synchronized (model.getCurrentPlayer()) {
            try {
                model.getCurrentPlayer().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return selected;
    }

    @Override
    public Cell selectDstCell(ArrayList<Cell> tos) {
        do {
            synchronized (model.getCurrentPlayer()) {
                try {
                    model.getCurrentPlayer().wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while(!tos.contains(selected));
        return selected;
    }

    @Override
    public void onSelectedCell(int x, int y) {
        synchronized (model.getCurrentPlayer()) {
            selected = model.getCurrentPlayer().relativeBoard().getCell(x, y);
            model.getCurrentPlayer().notify();
        }
    }
}
