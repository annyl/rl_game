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

    private void sleepModel() {
        synchronized (model.getCurrentPlayer()) {
            try {
                model.getCurrentPlayer().wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void wakeupModel() {
        synchronized (model.getCurrentPlayer()) {
            model.getCurrentPlayer().notify();
        }
    }

    @Override
    public Cell selectSrcCell() {
        sleepModel();
        return selected;
    }

    @Override
    public Cell selectDstCell(ArrayList<Cell> tos) {
        do {
            sleepModel();
        } while(!tos.contains(selected));
        return selected;
    }

    @Override
    public void onSelectedCell(int x, int y) {
        selected = model.getBoard().getCell(x, y);
        wakeupModel();
    }

    @Override
    public void beginAnimation() {
        sleepModel();
    }

    @Override
    public void endAnimation() {
        wakeupModel();
    }
}
