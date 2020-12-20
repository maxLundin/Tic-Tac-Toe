package org.example.connections;

import org.example.ui.Board;

import java.io.IOException;

public interface Connection extends AutoCloseable {

    default boolean isClosed() {
        return false;
    }

    void sendPoint(Board.Point point) throws IOException;

    Board.Point receivePoint() throws IOException;

    void sendStatus(String status) throws IOException;

    String receiveStatus() throws IOException;

    void sendBroadcast(String str) throws IOException;

    @Override
    default void close() {
    }
}
