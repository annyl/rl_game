package ru.hse.checker;

import android.content.Context;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.hse.checker.model.Board;
import ru.hse.checker.model.Cell;
import ru.hse.checker.model.RawModel;
import ru.hse.checker.model.intelligence.Player;
import ru.hse.checker.utils.Pair;

public class AndroidAI extends Player {
    private Module module;
//    private final String MODEL_FILENAME = "medium.pt";
    private final String MODEL_FILENAME = "white_3501.pt";
//    private final String MODEL_FILENAME = "white.pt";
    private int[] flatten = new int[65];
    private long[] shape = {65};

    AndroidAI(Context context, NumPlayer numPlayer) {
        super(numPlayer, null);
        try {
            module = Module.load(assetFilePath(context, MODEL_FILENAME));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Tensor getLegalActions(List<Pair<Cell, Cell>> paths) {
        Collections.sort(paths, new Comparator<Pair<Cell, Cell>>() {
            @Override
            public int compare(Pair<Cell, Cell> o1, Pair<Cell, Cell> o2) {
                int from1 = RawModel.index(o1.first.x, o1.first.y);
                int to1 = RawModel.index(o1.second.x, o1.second.y);
                int from2 = RawModel.index(o2.first.x, o2.first.y);
                int to2 = RawModel.index(o2.second.x, o2.second.y);
                if (from1 < from2)
                    return -1;
                if (from1 == from2)
                    if (to1 > to2)
                        return -1;
                return 1;
            }
        });
        int[] legalActions = new int[paths.size()*2];
        for (int i = 0; i < paths.size(); ++i) {
            Pair<Cell, Cell> path = paths.get(i);
            legalActions[2*i] = RawModel.numCell(path.first.x, path.first.y);
            legalActions[2*i+1] = RawModel.numCell(path.second.x, path.second.y);
        }
        return Tensor.fromBlob(legalActions, new long[]{paths.size(), 2});
    }

    private Pair<Cell, Cell> chooseAction(Board board, Tensor legalActions) {
        RawModel rawModel = board.getRaw();
        flatten[64] = 0;
        System.arraycopy(rawModel.getFlatten(), 0, flatten, 0, rawModel.getFlatten().length);
        Tensor inputTensor = Tensor.fromBlob(flatten, shape);

        final Tensor outputTensor = module.runMethod("choose_action", IValue.from(inputTensor), IValue.from(legalActions)).toTensor();
        long[] pathsFromTensor = outputTensor.getDataAsLongArray();
        Pair<Integer, Integer> fromCoords = RawModel.coords((int) pathsFromTensor[0]);
        Pair<Integer, Integer> toCoords = RawModel.coords((int) pathsFromTensor[1]);
        Cell from = board.getCell(fromCoords.first, fromCoords.second);
        Cell to = board.getCell(toCoords.first, toCoords.second);
        return new Pair<>(from, to);
    }

    @Override
    public Pair<Cell, Cell> getMove(Board board, List<Pair<Cell, Cell>> legalActions, boolean mustHit) {
        return chooseAction(board, getLegalActions(legalActions));
    }

    private static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
