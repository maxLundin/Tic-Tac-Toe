package org.example.connections;

import org.example.ui.Board;

import java.io.IOException;

public interface Connection extends AutoCloseable {

    default boolean isClosed() {
        return false;
    }

    void sendStatus(String status) throws IOException;

    String receiveStatus() throws IOException;

    void sendBroadcast(String str) throws IOException;

    default void sendPoint(Board.Point point) throws IOException {
        String msg = point.x + ":" + point.y;
        sendStatus(msg);
    }

    default Board.Point receivePoint() throws IOException {
        final String[] receivedMsg = receiveStatus().split(":");
        int x, y;
        try {
            x = Integer.parseInt(receivedMsg[0]);
            y = Integer.parseInt(receivedMsg[1]);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Number format error");
        }
        return new Board.Point(x, y);
    }

    @Override
    default void close() {
    }
}
