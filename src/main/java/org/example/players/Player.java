package org.example.players;


import org.example.connections.Connection;
import org.example.ui.Board;

import java.io.IOException;
import java.net.*;

public abstract class Player implements AutoCloseable {
    protected final Board board;
    protected final Connection connection;
    protected static final String STATUS_OK = "Ok";
    protected static final String STATUS_ERROR = "Error";
    protected static final String STATUS_QUEST = "Play";

    public Player(Connection connection, Board board) {
        this.connection = connection;
        this.board = board;
    }

    public Board getBoard() {
        return board;
    }

    protected abstract Board.Point getMove();

    protected void ourMove() throws IOException {

        Board.Point move = getMove();
        connection.sendPoint(move);


        String status;
        do {
            status = connection.receiveStatus();
        } while (!status.equals(STATUS_OK) && !status.equals(STATUS_ERROR));

        if (!status.equals(STATUS_OK)) {
            System.err.println(STATUS_ERROR);
            throw new IllegalStateException("Desks unsynchronized");
        }
        if (board.isGameOver()) {
            try {
                Thread.sleep(1000, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(board.getWinner());
            board.reset();
        }
    }

    protected void theirMove() throws IOException {
        updateReceived();
        if (board.isGameOver()) {
            System.out.println(board.getWinner());
            try {
                Thread.sleep(1000, 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            board.reset();
        }
    }

    protected void acceptConnection() throws IOException {
        String status;
        do {
            status = connection.receiveStatus();
        } while (!status.equals(STATUS_QUEST));
    }

    protected void establishConnection() throws IOException {
        String str;
        do {
            connection.sendBroadcast(STATUS_QUEST);
            do {
                str = connection.receiveStatus();
            } while (STATUS_QUEST.equals(str));
        } while (!str.equals(STATUS_OK));
    }

    protected void afterConnect() {
    }

    protected void afterEnemyMove() {
    }

    public void start(boolean first) {
        try {
            if (first) {
                acceptConnection();
                afterConnect();
                connection.sendStatus(STATUS_OK);
                System.out.println("Game started");
                while (!connection.isClosed() && !Thread.currentThread().isInterrupted()) {
                    theirMove();
                    afterEnemyMove();
                    ourMove();
                }
            } else {
                establishConnection();
                afterConnect();
                System.out.println("Connected game started:");
                while (!connection.isClosed() && !Thread.currentThread().isInterrupted()) {
                    ourMove();
                    theirMove();
                    afterEnemyMove();
                }
            }
        } catch (PortUnreachableException e) {
            System.err.println("Port unreachable on connection: " + connection.toString());
        } catch (SocketException e) {
            if (!connection.isClosed()) {
                System.err.println("Socket Exception on connection: " + connection.toString());
            }
        } catch (IOException e) {
            System.err.println("IOException on connection: " + connection.toString());
        }
    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        connection.close();
    }

    protected void updateReceived() throws IOException {
        Board.Point point = connection.receivePoint();
        String status = board.move(point.x, point.y) ? STATUS_OK : STATUS_ERROR;
        connection.sendStatus(status);
        if (!status.equals(STATUS_OK)) {
            throw new IllegalStateException(STATUS_ERROR);
        }
    }
}