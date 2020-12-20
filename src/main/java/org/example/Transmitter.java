package org.example;

import org.example.ui.Board;

public class Transmitter {
    private static class BoolHolder {
        public boolean val;
        BoolHolder(boolean val) {
            this.val = val;
        }
    }
    BoolHolder valid = new BoolHolder(Boolean.FALSE);
    Board.Point point = null;

    synchronized public boolean getValid() {
        return valid.val;
    }

    synchronized public Board.Point getPoint() {
        return point;
    }

    synchronized public void setValid(boolean valid) {
        this.valid.val = valid;
    }

    synchronized public void setPoint(Board.Point point) {
        if (valid.val) {
            this.point = point;
        }
    }
}
