package org.example;


import org.example.ui.Board;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
//import java.util.concurrent.ExecutorService;

public class Server implements AutoCloseable {
    private final Board board = new Board();
    //    private ExecutorService receiver;
    private DatagramSocket socket;
    private final Scanner scanner = new Scanner(System.in);
    private static final String STATUS_OK = "Ok";
    private static final String STATUS_ERROR = "Error";
    private static final String STATUS_QUEST = "Play";

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
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        return new Board.Point(x, y);
    }

    private boolean ourMove(DatagramPacket packet) throws IOException {
        boolean valid;
        DatagramPacket dp;

        do {
            Board.Point move = getMove();
            String msg = move.x + ":" + move.y;
            dp = getDatagramPacket(msg.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());

            valid = board.move(move.x, move.y);
        } while (!valid);

        socket.send(dp);
        socket.receive(packet);

        String received = getResult(packet);
        while (!received.equals(STATUS_OK) && !received.equals(STATUS_ERROR)) {
            socket.receive(packet);
            received = getResult(packet);
        }
        if (!received.equals(STATUS_OK)) {
            System.err.println(STATUS_ERROR);
            return false;
        }
        if (board.isGameOver()) {
            System.out.println(board.getWinner());
            return false;
        }
        return true;
    }

    private boolean theirMove(DatagramPacket packet) throws IOException {
        socket.receive(packet);
        updateReceived(packet);
        if (board.isGameOver()) {
            System.out.println(board.getWinner());
            return false;
        }
        return true;
    }

    public void start(int port, boolean first) {
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
        if (first) {
            try {
                do {
                    socket.receive(packet);
                } while (!getResult(packet).equals(STATUS_QUEST));
                DatagramPacket dp = getDatagramPacket(STATUS_OK.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());
                socket.send(dp);
                System.out.println("Game started with " + packet.getSocketAddress());

                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    boolean success;
                    success = theirMove(packet);
                    if (!success) break;
                    System.out.println(board);
                    success = ourMove(packet);
                    if (!success) break;
                    System.out.println(board);
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
        } else {
            try {
                socket.setBroadcast(true);
                byte[] buffer = STATUS_QUEST.getBytes();
                DatagramPacket packetBroad
                        = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), port);
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
                System.out.println("Connected game started:");
                boolean success;
                while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                    success = ourMove(packet);
                    if (!success) break;
                    System.out.println(board);
                    success = theirMove(packet);
                    if (!success) break;
                    System.out.println(board);
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

    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
//        receiver.shutdownNow();
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