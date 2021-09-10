import javax.swing.*;
import java.util.*;
import java.awt.*;

public class Maze extends JPanel {

    public Cell cells[]; // an array of cells

    private int gridH;
    private int gridW;

    private int pixelPerH;
    private int pixelPerW;

    public Cell current;
    public Cell next;

    public boolean doneGenerating; // boolean variable to see when the maze is done being generated
    public boolean firstSolve; // boolean variable to allow the maze to only be solved once

    public Stack<Cell> s; // create stack to be used for generation

    public Maze() {
        gridH = 30;
        gridW = 30;
        resetMaze();
    }

    // sets & gets for height & width
    public void setGridH(int newGridH) { gridH = newGridH; }
    public int getGridH() { return gridH; }
    public void setGridW(int newGridW) { gridW = newGridW; }
    public int getGridW() { return gridW; }

    public void resetMaze() {
        pixelPerH = 550/ gridH;
        pixelPerW = 550/ gridW;

        doneGenerating = false;
        firstSolve = true;

        cells = new Cell[gridH * gridW];
        int i = 0;
        for (int y = 0; y < gridH; y++) {
            for (int x = 0; x < gridW; x++) {
                cells[i] = new Cell(x,y);
                i++;
            }
        }
    }

    public void startGeneration() {
        resetMaze();
        current = cells[0];
        current.visited = true;

        s = new Stack<>();

        s.push(current);
    }

    public void startSolve() {
        current = cells[0];
        current.path = true;

        s = new Stack<>();

        s.push(current);
    }

    public void generateMaze(){
        resetMaze();
        current = cells[0];
        current.visited = true;

        s = new Stack<>();

        s.push(current);

        while (!s.isEmpty()) {
            current.isCurrent = false;
            if (checkNeighbors(current)) {
                s.push(next);
                next.visited = true;
                next.isCurrent = true;
                removeWalls(current, next);
                repaint();
                current = next;
            }
            else {
                current = s.pop();
                current.isCurrent = true;
                repaint();
            }
        }
        doneGenerating = true;
    }

    public void solveMaze() {
        current = cells[0];
        current.path = true;

        s = new Stack<>();

        s.push(current);
        while (current.x != (gridW - 1) || current.y != (gridH -1)) {
            current.isCurrent = false;
            if(checkNeighborsSolve(current)) {
                s.push(next);
                next.path = true;
                next.isCurrent = true;
                repaint();
                current = next;
            }
            else {
                current.backtrack = true;
                if (checkNeighborsSolve(s.peek())) {
                    current = s.peek();
                }
                else
                    current = s.pop();
                current.isCurrent = true;
                repaint();
            }
        }
        firstSolve = false;
    }

    public int findIndex(int x, int y) {
        if (x < 0 || y < 0 || x > gridW - 1 || y > gridH - 1) {
            return -1;
        }
        else
            return (x + y * gridW);
    }

    // function checks neighbors, assigns next to a random neighbor cell, and returns true if there are neighbors
    public boolean checkNeighbors(Cell currCell) {
        LinkedList<Cell> neighbors = new LinkedList<>();

        if (findIndex(currCell.x, currCell.y - 1) != -1) {
            Cell top = cells[findIndex(currCell.x, currCell.y - 1)];
            if (!top.visited) {
                neighbors.add(top);
            }
        }
        if (findIndex(currCell.x + 1, currCell.y) != -1) {
            Cell right = cells[findIndex(currCell.x + 1, currCell.y)];
            if (!right.visited) {
                neighbors.add(right);
            }
        }
        if (findIndex(currCell.x, currCell.y + 1) != -1) {
            Cell bottom = cells[findIndex(currCell.x, currCell.y + 1)];
            if (!bottom.visited) {
                neighbors.add(bottom);
            }
        }
        if (findIndex(currCell.x - 1, currCell.y) != -1) {
            Cell left = cells[findIndex(currCell.x - 1, currCell.y)];
            if (!left.visited) {
                neighbors.add(left);
            }
        }

        if (neighbors.size() > 0) {
            int r = (int)Math.floor(Math.random() * neighbors.size());
            next = neighbors.get(r);
            return true;
        }
        else
            return false;
    }

    public boolean checkNeighborsSolve(Cell currCell) {
        // check if it can move right
        if (findIndex(currCell.x + 1, currCell.y) != -1 && !currCell.right) {
            Cell right = cells[findIndex(currCell.x + 1, currCell.y)];
            if (!right.path) {
                next = right;
                return true;
            }
        }
        // check if it can move down
        if (findIndex(currCell.x, currCell.y + 1) != -1 && !currCell.bottom) {
            Cell bottom = cells[findIndex(currCell.x, currCell.y + 1)];
            if (!bottom.path) {
                next = bottom;
                return true;
            }
        }
        // check if it can move left
        if (findIndex(currCell.x - 1, currCell.y) != -1 && !currCell.left) {
            Cell left = cells[findIndex(currCell.x - 1, currCell.y)];
            if (!left.path) {
                next = left;
                return true;
            }
        }
        // check if it can move up
        if (findIndex(currCell.x, currCell.y - 1) != -1 && !currCell.top) {
            Cell top = cells[findIndex(currCell.x, currCell.y - 1)];
            if (!top.path) {
                next = top;
                return true;
            }
        }
        return false;
    }

    public void removeWalls(Cell currCell, Cell nextCell) {
        int x = currCell.x - nextCell.x;
        if (x == 1) {
            currCell.left = false;
            nextCell.right = false;
        }
        else if (x == -1) {
            currCell.right = false;
            nextCell.left = false;
        }
        int y = currCell.y - nextCell.y;
        if (y == 1) {
            currCell.top = false;
            nextCell.bottom = false;
        }
        else if (y == -1) {
            currCell.bottom = false;
            nextCell.top = false;
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int offset = 10; // offset to make it look nicer

        for (int i = 0; i < gridH * gridW; i++) {
            if (cells[i].isCurrent) {
                g.setColor(Color.blue);
                g.fillRect(((cells[i].x * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), pixelPerW, pixelPerH);
                g.setColor(Color.black);
            } else if (cells[i].backtrack) {
                g.setColor(Color.lightGray);
                g.fillRect(((cells[i].x * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), pixelPerW, pixelPerH);
                g.setColor(Color.black);
            } else if (cells[i].path) {
                g.setColor(Color.green);
                g.fillRect(((cells[i].x * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), pixelPerW, pixelPerH);
                g.setColor(Color.black);
            } else if (cells[i].visited) {
                g.setColor(Color.gray);
                g.fillRect(((cells[i].x * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), pixelPerW, pixelPerH);
                g.setColor(Color.black);
            }

            if (cells[i].top) {
                g.drawLine(((cells[i].x * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), (((cells[i].x + 1) * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset));
            }
            if (cells[i].bottom) {
                g.drawLine(((cells[i].x * pixelPerW) + offset), (((cells[i].y + 1) * pixelPerH) + offset), (((cells[i].x + 1) * pixelPerW) + offset), (((cells[i].y + 1) * pixelPerH) + offset));
            }
            if (cells[i].left) {
                g.drawLine(((cells[i].x * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), ((cells[i].x * pixelPerW) + offset), (((cells[i].y + 1) * pixelPerH) + offset));
            }
            if (cells[i].right) {
                g.drawLine((((cells[i].x + 1) * pixelPerW) + offset), ((cells[i].y * pixelPerH) + offset), (((cells[i].x + 1) * pixelPerW) + offset), (((cells[i].y + 1) * pixelPerH) + offset));
            }
        }
        if (doneGenerating) { // draw red final square
            g.setColor(Color.red);
            g.fillRect(((cells[gridH * gridW - 1].x * pixelPerW) + offset), ((cells[gridH * gridW - 1].y * pixelPerH) + offset), pixelPerW, pixelPerH);
            g.setColor(Color.black);
        }
    }
}
