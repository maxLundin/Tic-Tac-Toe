package org.example.players.human;

import org.example.connections.Connection;
import org.example.players.Player;
import org.example.players.human.Transmitter;
import org.example.ui.Board;
import org.example.ui.Window;


public class HumanPlayer extends Player {
    private final Transmitter transmitter;
    private final Window windowInstance;

    public HumanPlayer(Connection connection, Transmitter transmitter, Window windowInstance) {
        super(connection, windowInstance.getBoard());
        this.transmitter = transmitter;
        this.windowInstance = windowInstance;
    }

    @Override
    protected Board.Point getMove() {
        System.out.println("getPoints:");
        transmitter.setValid(true);
        while (transmitter.getValid()) {
        }
        return transmitter.getPoint();
    }

    @Override
    protected void afterConnect() {
        windowInstance.setVisible(true);
    }

    @Override
    protected void afterEnemyMove() {
        windowInstance.repaintPicture();
    }
}
