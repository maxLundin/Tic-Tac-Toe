package org.example.connections;

import org.example.ui.Board;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class SocketConnection implements Connection {
    private final DatagramSocket ds;
    private final int bufSize;
    final DatagramPacket packet;

    private static DatagramPacket getDatagramPacket(int size) {
        byte[] byteArray = new byte[size];
        return new DatagramPacket(byteArray, size);
    }


    private static String getResult(final DatagramPacket dp) {
        return new String(
                dp.getData(),
                dp.getOffset(),
                dp.getLength(),
                StandardCharsets.UTF_8);
    }

    private static DatagramPacket getDatagramPacket(final byte[] data, final SocketAddress address) {
        return new DatagramPacket(data, 0, data.length, address);
    }


    public SocketConnection() {
        try {
            ds = new DatagramSocket(8090);
            bufSize = ds.getReceiveBufferSize();
            packet = getDatagramPacket(bufSize);
        } catch (SocketException e) {
            throw new IllegalStateException("Socket fail", e);
        }
    }

    @Override
    public boolean isClosed() {
        return ds.isClosed();
    }

    @Override
    public void sendPoint(Board.Point point) throws IOException {
        String msg = point.x + ":" + point.y;
        sendStatus(msg);
    }

    @Override
    public Board.Point receivePoint() throws IOException {
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
    public void sendStatus(String status) throws IOException {
        DatagramPacket dp = getDatagramPacket(status.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());
        ds.send(dp);
    }

    @Override
    public String receiveStatus() throws IOException {
        ds.receive(packet);
        return getResult(packet);
    }

    private static final String STATUS_QUEST = "Play";

    @Override
    public void sendBroadcast(String str) throws IOException {
        ds.setBroadcast(true);
        byte[] buffer = str.getBytes();
        DatagramPacket packetBroad
                = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), ds.getLocalPort());

        ds.send(packetBroad);

        ds.setBroadcast(false);
    }

    @Override
    public void close() {
        ds.close();
    }

    @Override
    public String toString() {
        return ds.toString();
    }
}
