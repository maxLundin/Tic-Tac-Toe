package org.example.players.bot;

import org.example.ui.Board;

public interface Bot {
    Board.Point decide(Board board);
}
