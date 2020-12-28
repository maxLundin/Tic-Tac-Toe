package org.example.connections;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class BotConnection implements Connection {
    private BotConnection other;
    private String tag;
    private final Queue<String> messages = new LinkedList<>();
    private final ReentrantLock mutex = new ReentrantLock();

    public void setOtherConnection(BotConnection connection, String tagg) {
        other = connection;
        tag = tagg;
    }

    public String pollFromMessages() {
        while (true) {
            try {
                mutex.lock();
                if (!messages.isEmpty()) {
//                    System.out.println(tag + "#return poll " + messages.peek());
                    return messages.poll();
                }
            } finally {
                mutex.unlock();
            }
        }
    }

    @Override
    public void sendStatus(String status) throws IOException {
//        System.out.println(tag + "#send " + status);
        mutex.lock();
        messages.add(status);
        mutex.unlock();
    }

    @Override
    public String receiveStatus() throws IOException {
        return other.pollFromMessages();
    }

    @Override
    public void sendBroadcast(String str) throws IOException {
        sendStatus(str);
    }
}
