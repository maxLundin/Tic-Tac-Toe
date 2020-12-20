package org.example;


import org.example.ui.Board;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements AutoCloseable {
    private final Board board = new Board();
    private ExecutorService receiver;
    private DatagramSocket socket;
    private Scanner scanner = new Scanner(System.in);

    static String getResult(final DatagramPacket dp) {
        return new String(
                dp.getData(),
                dp.getOffset(),
                dp.getLength(),
                StandardCharsets.UTF_8);
    }

    static DatagramPacket getDatagramPacket(int size) {
        byte[] byteArray = new byte[size];
        return new DatagramPacket(byteArray, size);
    }

    static DatagramPacket getDatagramPacket(final byte[] data, final SocketAddress address) {
        return new DatagramPacket(data, 0, data.length, address);
    }

    public Board.Point getMove() {
        System.out.println("getPoints:");
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        return new Board.Point(x, y);
    }

    public void start(int port, boolean first) {
//        receiver = Executors.newSingleThreadExecutor();
        int bufSize;
        try {
            socket = new DatagramSocket(port);
            bufSize = socket.getReceiveBufferSize();
        } catch (SocketException e) {
            System.err.println("Socket exception: can't create socket");
            e.printStackTrace();
            return;
        }
        if (first) {
            final DatagramPacket packet = getDatagramPacket(bufSize);
            try {
                do {
                    socket.receive(packet);
                } while (!getResult(packet).equals("Play"));
                String msg = "Ok";
                DatagramPacket dp = getDatagramPacket(msg.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());
                socket.send(dp);
                System.out.println("Game started with " + packet.getSocketAddress());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                try {
                    socket.receive(packet);
                    runnableRun(packet);
                    System.out.println(board.toString());
                    if (board.isGameOver()) {
                        System.out.println(board.getWinner());
                        break;
                    }
                    Board.Point move = getMove();
                    String msg = move.x + ":" + move.y;
                    DatagramPacket dp = getDatagramPacket(msg.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());
                    socket.send(dp);
                    board.move(move.x, move.y);
                    System.out.println(board.toString());
                    socket.receive(packet);
                    String received = getResult(packet);
                    if (!received.equals("Ok")) {
                        System.err.println("Error!");
                        break;
                    }
                    if (board.isGameOver()) {
                        System.out.println(board.getWinner());
                        break;
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

        } else {
            final DatagramPacket packet = getDatagramPacket(bufSize);
            try {
                String msg = "Play";
                DatagramSocket socketBroad = new DatagramSocket();
                socketBroad.setBroadcast(true);
                byte[] buffer = msg.getBytes();
                DatagramPacket packetBroad
                        = new DatagramPacket(buffer, buffer.length, InetAddress.getByName("255.255.255.255"), 8090);
                do {
                    socketBroad.send(packetBroad);
                    socketBroad.receive(packet);
                } while (!getResult(packet).equals("Ok"));
                socketBroad.close();
                System.out.println("Connected game started:");
            } catch (IOException e) {
                e.printStackTrace();
            }


            while (!socket.isClosed() && !Thread.currentThread().isInterrupted()) {
                try {
                    System.out.println(board.toString());
                    Board.Point move = getMove();
                    String msg = move.x + ":" + move.y;

                    DatagramPacket dp = getDatagramPacket(msg.getBytes(StandardCharsets.UTF_8), packet.getSocketAddress());
                    socket.send(dp);
                    board.move(move.x, move.y);
                    socket.receive(packet);
                    String received = getResult(packet);
                    if (!received.equals("Ok")) {
                        System.err.println("Error!");
                        break;
                    }
                    if (board.isGameOver()) {
                        System.out.println(board.getWinner());
                        break;
                    }
                    System.out.println(board.toString());
                    socket.receive(packet);
                    runnableRun(packet);
                    if (board.isGameOver()) {
                        System.out.println(board.getWinner());
                        break;
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

    }

    /**
     * Stops server and deallocates all resources.
     */
    @Override
    public void close() {
//        receiver.shutdownNow();
        socket.close();
    }

    private void runnableRun(final DatagramPacket dp) {
        final String[] receivedMsg = getResult(dp).split(":");
        int x = Integer.parseInt(receivedMsg[0]);
        int y = Integer.parseInt(receivedMsg[1]);
        String res = "Ok";
        boolean resmove = board.move(x, y);
        if (!resmove) {
            res = "Error";
        }
        final DatagramPacket sendDatagramPacket = getDatagramPacket(res.getBytes(StandardCharsets.UTF_8), dp.getSocketAddress());
        try {
            socket.send(sendDatagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!resmove) {
            throw new IllegalArgumentException("huinya");
        }
    }
}