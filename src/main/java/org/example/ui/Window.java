package org.example.ui;

import org.example.Transmitter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Window extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private Panel panel;
    private Board board;
    private final Transmitter transmitter;

    public Window(Transmitter transmitter) {
        this.transmitter = transmitter;
        board = new Board();
        panel = createPanel();
        setResizable(false);
        setSize(new Dimension(WIDTH, HEIGHT + 38));
        setTitle("PPO Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public Window(Board board, Transmitter transmitter) {
        this(transmitter);
        this.board = board;
    }

    private Panel createPanel() {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        panel.addMouseListener(new MyMouseAdapter());
        return panel;
    }

    private int getCellWidth() {
        return WIDTH / board.size();
    }

    private int getCellHeight() {
        return HEIGHT / board.size();
    }

    private static BufferedImage getImage(String path) {
        BufferedImage image;
        try {
            image = ImageIO.read(Window.class.getResource("/" + path + ".png"));
        } catch (IOException ex) {
            throw new RuntimeException("Image could not be loaded.");
        }
        return image;
    }

    private class Panel extends JPanel {

        private void paintGrid(Graphics g) {
            int cellHeight = getCellHeight();
            int cellWidth = getCellWidth();
            for (int i = 0; i < board.size() - 1; i++) {
                g.drawLine(0, cellHeight * (i + 1), Window.WIDTH, cellHeight * (i + 1));
            }
            for (int i = 0; i < board.size() - 1; i++) {
                g.drawLine(cellWidth * (i + 1), 0, cellWidth * (i + 1), Window.HEIGHT);
            }
        }

        private void drawCell(Graphics g, int x, int y, String path) {
            int minCellHeight = getCellHeight() * x;
            int minCellWidth = getCellWidth() * (y);

            g.drawImage(getImage(path), minCellWidth, minCellHeight, getCellWidth(), getCellHeight(), null);
        }

        private void paintImages(Graphics g) {
            for (int i = 0; i < board.size(); i++) {
                for (int j = 0; j < board.size(); j++) {
                    Board.State cellState = board.getCell(i, j);
                    if (cellState.equals(Board.State.X)) {
                        drawCell(g, i, j, "x");
                    } else if (cellState.equals(Board.State.O)) {
                        drawCell(g, i, j, "o");
                    }
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintGrid(g);
            paintImages(g);
        }
    }

    /**
     * For detecting mouse clicks.
     */
    private class MyMouseAdapter extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            if (!transmitter.getValid()) {
                return;
            }
            super.mouseClicked(e);
            System.out.println("ya popal");
            if (board.isGameOver()) {

                board.reset();
                panel.repaint();
            } else {
                Board.Point point = playMove(e);
                transmitter.setPoint(point);
            }
            transmitter.setValid(false);
        }

        private Board.Point toCellCoord(Point move) {
            return new Board.Point(move.y / getCellHeight(), move.x / getCellWidth());
        }

        private Board.Point playMove(MouseEvent e) {
            Board.Point move = toCellCoord(e.getPoint());

            board.move(move.x, move.y);
            panel.repaint();
            return move;
        }

    }


//    public static void main(String[] args) {
//
//        SwingUtilities.invokeLater(Window::new);
//    }

}
