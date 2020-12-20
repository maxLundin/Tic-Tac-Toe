package org.example.connections;

import org.example.ui.Board;

import java.io.IOException;

public class BotConnection implements Connection {
    @Override
    public void sendPoint(Board.Point point) throws IOException {

    }

    @Override
    public Board.Point receivePoint() throws IOException {
        return null;
    }

    @Override
    public void sendStatus(String status) throws IOException {

    }

    @Override
    public String receiveStatus() throws IOException {
        return null;
    }

    @Override
    public void sendBroadcast(String str) throws IOException {

    }
}
