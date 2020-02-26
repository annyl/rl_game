package ru.hse.checker.model.intelligence;

import java.util.Iterator;

public class Players implements Iterable<Player> {

    private Player[] players;

    public Players(Player... players) {
        this.players = players;
    }


    @Override
    public Iterator<Player> iterator() {
        return new Iterator<Player>() {
            int counter = 0;
            @Override
            public boolean hasNext() {
                return players.length > 0;
            }

            @Override
            public synchronized Player next() {
                return players[counter++ % players.length];
            }
        };
    }
}