package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Window extends JFrame {
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private Panel panel;

    private static final int TEST_BOARD_SIZE = 10;

    private Window() {
        panel = createPanel();
        setResizable(false);
        setSize(new Dimension(WIDTH, HEIGHT + 38));
        setTitle("PPO Tic Tac Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private Panel createPanel() {
        Panel panel = new Panel();
        Container cp = getContentPane();
        cp.add(panel);
        return panel;
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
        @Override
        protected void paintComponent(Graphics g) {
            int cell_height = Window.HEIGHT / TEST_BOARD_SIZE;
            int cell_width = Window.WIDTH / TEST_BOARD_SIZE;
            for (int i = 0; i < TEST_BOARD_SIZE - 1; i++) {
                g.drawLine(0, cell_height * (i + 1), Window.WIDTH, cell_height * (i + 1));
            }
            for (int i = 0; i < TEST_BOARD_SIZE - 1; i++) {
                g.drawLine(cell_width * (i + 1), 0, cell_width * (i + 1), Window.HEIGHT);
            }
            g.drawImage(getImage("o"), 0, 0, cell_width, cell_height, null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Window::new);
    }

}
