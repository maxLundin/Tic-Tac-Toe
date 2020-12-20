package org.example;


import org.example.connections.Connection;
import org.example.ui.Board;
import org.example.ui.Window;

import java.io.IOException;
import java.net.*;

public class Player implements AutoCloseable {
    private final Board board = new Board();
    private final Connection connection;
    private static final String STATUS_OK = "Ok";
    private static final String STATUS_ERROR = "Error";
    private static final String STATUS_QUEST = "Play";
    private final Transmitter transmitter;

    public Player(Transmitter transmitter, Connection connection) {
        this.transmitter = transmitter;
        this.connection = connection;
    }

    public Board getBoard() {
        return board;
    }

    private Board.Point getMove() {
        System.out.println("getPoints:");
        transmitter.setValid(true);
        while (transmitter.getValid()) {
        }
        return transmitter.getPoint();
    }

    private void ourMove() throws IOException {

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
            board.reset();
            System.out.println(board.getWinner());
        }
    }

    private void theirMove(Window windowInstance) throws IOException {
        updateReceived();
        windowInstance.repaintPicture();

        if (board.isGameOver()) {
            System.out.println(board.getWinner());
        }
    }

    private void acceptConnection() throws IOException {
        String status;
        do {
            status = connection.receiveStatus();
        } while (!status.equals(STATUS_QUEST));
    }

    private void establishConnection() throws IOException {
        String str;
        do {
            connection.sendBroadcast(STATUS_QUEST);
            do {
                str = connection.receiveStatus();
            } while (STATUS_QUEST.equals(str));
        } while (!str.equals(STATUS_OK));
    }

    public void start(boolean first, Window windowInstance) {
        try {
            if (first) {
                acceptConnection();
                windowInstance.setVisible(true);
                connection.sendStatus(STATUS_OK);
                System.out.println("Game started");
                while (!connection.isClosed() && !Thread.currentThread().isInterrupted()) {
                    theirMove(windowInstance);
                    ourMove();
                }
            } else {
                establishConnection();
                windowInstance.setVisible(true);
                System.out.println("Connected game started:");
                while (!connection.isClosed() && !Thread.currentThread().isInterrupted()) {
                    ourMove();
                    theirMove(windowInstance);
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

    private void updateReceived() throws IOException {
        Board.Point point = connection.receivePoint();
        String status = board.move(point.x, point.y) ? STATUS_OK : STATUS_ERROR;
        connection.sendStatus(status);
        if (!status.equals(STATUS_OK)) {
            throw new IllegalStateException(STATUS_ERROR);
        }
    }
}