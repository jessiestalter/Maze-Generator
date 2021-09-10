import javax.swing.*;

public class Cell extends JPanel {
    // variables to describe its location in the grid:
    public int x;
    public int y;

    public boolean top, bottom, left, right; // boolean variables for walls
    public boolean visited, isCurrent; // boolean variables for generation
    public boolean path, backtrack; // boolean variables for solve

    public Cell(int xNew, int yNew) {
        top = true;
        bottom = true;
        left = true;
        right = true;

        visited = false;
        isCurrent = false;

        x = xNew;
        y = yNew;
    }
}
