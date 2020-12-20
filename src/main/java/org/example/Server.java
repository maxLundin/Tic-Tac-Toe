package org.example;


import org.example.ui.Board;
import org.example.ui.Window;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Server implements AutoCloseable {
    private final Board board = new Board();
    private DatagramSocket socket;
    private static final String STATUS_OK = "Ok";
    private static final String STATUS_ERROR = "Error";
    private static final String STATUS_QUEST = "Play";
    private final Transmitter transmitter;

    public Server(Transmitter transmitter) {
        this.transmitter = transmitter;
    }

    public Board getBoard() {
        return board;
    }

    private static String getResult(final DatagramPacket dp) {
        return new String(
                dp.getData(),
                dp.getOffset(),
                dp.getLength(),
                StandardCharsets.UTF_8);
    }

    private static DatagramPacket getDatagramPacket(int size) {
        byte[] byteArray = new byte[size];
        return new DatagramPacket(byteArray, size);
    }

    private static DatagramPacket getDatagramPacket(final byte[] data, final SocketAddress address) {
        return new DatagramPacket(data, 0, data.length, address);
    }

    private Board.Point getMove() {
        System.out.println("getPoints:");
        transmitter.setValid(true);
        while (transmitter.getValid()) {
        }
        return transmitter.getPoint();
    }

    private void ourMove(DatagramPacket packet) throws IOException {

        Board.Point move = getMove();
        final String msg = move.x + ":" + move.y;
        DatagramPacket dp = getDatagramPacket(msg.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());


        socket.send(dp);
        socket.receive(packet);

        String received = getResult(packet);
        while (!received.equals(STATUS_OK) && !received.equals(STATUS_ERROR)) {
            socket.receive(packet);
            received = getResult(packet);
        }
        if (!received.equals(STATUS_OK)) {
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

    private void theirMove(DatagramPacket packet, Window windowInstance) throws IOException {
        socket.receive(packet);
        updateReceived(packet);
        windowInstance.repaintPicture();

        if (board.isGameOver()) {
            System.out.println(board.getWinner());
        }
    }

    private void acceptConnection(DatagramSocket socket, DatagramPacket packet) throws IOException {
        do {
            socket.receive(packet);
        } while (!getResult(packet).equals(STATUS_QUEST));
    }

    private void establishConnection(DatagramSocket socket, DatagramPacket packet) throws IOException {
        socket.setBroadcast(true);
        byte[] buffer = STATUS_QUEST.getBytes();
        DatagramPacket packetBroad
                = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), socket.getLocalPort());
        do {
            socket.send(packetBroad);
            socket.receive(packet);
            String str;
            do {
                socket.receive(packet);
                str = getResult(packet);
            } while (STATUS_QUEST.equals(str));
        } while (!getResult(packet).equals(STATUS_OK));
        socket.setBroadcast(false);
    }

    public void start(int port, boolean first, Window windowInstance) {
        int bufSize;
        try {
            socket = new DatagramSocket(port);
            bufSize = socket.getReceiveBufferSize();
        } catch (SocketException e) {
            System.err.println("Socket exception: can't create socket");
            e.printStackTrace();
            return;
        }
        final DatagramPacket packet = getDatagramPacket(bufSize);
        try {
            if (first) {
                acceptConnection(socket, packet);
                windowInstance.setVisible(true);
                DatagramPacket dp = getDatagramPacket(STATUS_OK.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());
                socket.send(dp);
                System.out.println("Game started with " + packet.getSocketAddress());
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    theirMove(packet, windowInstance);
                    ourMove(packet);
                }
            } else {
                establishConnection(socket, packet);
                windowInstance.setVisible(true);
                System.out.println("Connected game started:");
                System.out.println(socket.isClosed() + " " + Thread.currentThread().isInterrupted());
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    ourMove(packet);
                    theirMove(packet, windowInstance);
                }
            }
        } catch (PortUnreachableException e) {
            System.err.println("Port unreachable on socket: " + socket.toString() + " with port " + port);
        } catch (SocketException e) {
            if (!socket.isClosed()) {
                System.err.println("Socket Exception on socket: " + socket.toString() + " with port " + port);
            }
        } catch (IOException e) {
            System.err.println("IOException on socket: " + socket.toString() + " with port " + port);
        }

    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
        socket.close();
    }

    private void updateReceived(final DatagramPacket dp) throws IOException {
        final String[] receivedMsg = getResult(dp).split(":");
        int x, y;
        try {
            x = Integer.parseInt(receivedMsg[0]);
            y = Integer.parseInt(receivedMsg[1]);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Number format error");
        }
        String status = board.move(x, y) ? STATUS_OK : STATUS_ERROR;
        final DatagramPacket sendDatagramPacket = getDatagramPacket(status.getBytes(StandardCharsets.UTF_8), dp.getSocketAddress());
        socket.send(sendDatagramPacket);
        if (!status.equals(STATUS_OK)) {
            throw new IllegalStateException(STATUS_ERROR);
        }
    }
}