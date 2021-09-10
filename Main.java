import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;

public class Main extends JFrame implements ActionListener, ChangeListener {

    // set up panels
    private JPanel buttonPanel;
    private JPanel speedPanel, heightPanel, widthPanel;
    private JPanel bottomPanel;

    // set up buttons
    private JButton generateButton;
    private JButton solveButton;
    private JButton stopButton;

    // set up JCheckBox
    private JCheckBox showAnimation;

    // set up sliders
    private JSlider speedSlider;
    private JSlider heightSlider;
    private JSlider widthSlider;

    // set up labels
    private JLabel percentLabel;
    private JLabel speedLabel;
    private JLabel heightLabel;
    private JLabel widthLabel;

    private static DecimalFormat df = new DecimalFormat("0.0");

    Maze maze; // set up maze object

    private boolean isGenerating, isSolving; // boolean variables to determine which process is running

    private int timerSpeed; // variable to hold the speed of the timer

    public Main() {
        super("Maze"); // add title

        isGenerating = false;
        isSolving = false;

        percentLabel = new JLabel("Visited: 0.0%", SwingConstants.CENTER);
        heightLabel = new JLabel("Height: 30", SwingConstants.CENTER);
        widthLabel = new JLabel("Width: 30", SwingConstants.CENTER);
        speedLabel = new JLabel("Speed", SwingConstants.CENTER);

        buttonPanel = new JPanel();

        generateButton = new JButton("Generate");
        generateButton.addActionListener(this);
        solveButton = new JButton("Solve");
        solveButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);

        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(2,1));

        showAnimation = new JCheckBox("Show Animation", true);
        showAnimation.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(showAnimation);
        bottomPanel.add(percentLabel);

        speedSlider = new JSlider(1,300);
        speedSlider.addChangeListener(this);
        heightSlider = new JSlider(10, 50);
        heightSlider.addChangeListener(this);
        heightSlider.setPaintTicks(true);
        heightSlider.setPaintLabels(true);
        heightSlider.setMajorTickSpacing(5);
        heightSlider.setMinorTickSpacing(1);
        heightSlider.setSnapToTicks(true);
        widthSlider = new JSlider(10, 50);
        widthSlider.addChangeListener(this);
        widthSlider.setPaintTicks(true);
        widthSlider.setPaintLabels(true);
        widthSlider.setMajorTickSpacing(5);
        widthSlider.setMinorTickSpacing(1);
        widthSlider.setSnapToTicks(true);

        speedPanel = new JPanel();
        heightPanel = new JPanel();
        widthPanel = new JPanel();

        speedPanel.setLayout(new GridLayout(1, 2));
        speedPanel.add(speedLabel);
        speedPanel.add(speedSlider);

        heightPanel.setLayout(new GridLayout(1, 2));
        heightPanel.add(heightLabel);
        heightPanel.add(heightSlider);

        widthPanel.setLayout(new GridLayout(1, 2));
        widthPanel.add(widthLabel);
        widthPanel.add(widthSlider);

        buttonPanel.setLayout(new GridLayout(7, 1));
        buttonPanel.add(generateButton);
        buttonPanel.add(solveButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(speedPanel);
        buttonPanel.add(heightPanel);
        buttonPanel.add(widthPanel);
        buttonPanel.add(bottomPanel);

        maze = new Maze();

        Container c = getContentPane();
        c.setLayout(new BorderLayout());

        c.add(maze, BorderLayout.CENTER);
        c.add(buttonPanel, BorderLayout.EAST);

        setSize(new Dimension(975, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        Main M = new Main();

        M.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(generateButton)) {
            if (!showAnimation.isSelected()) {
                maze.generateMaze();
                isGenerating = false;
                percentLabel.setText("Visited: 100.0%");
            }
            else {
                maze.startGeneration();
                isGenerating = true;
                timer.start();
            }
        }

        if (e.getSource().equals(solveButton)) {
            if (maze.doneGenerating && maze.firstSolve && !isSolving) {
                if (!showAnimation.isSelected()) {
                    maze.solveMaze();
                    isSolving = false;
                    percentLabel.setText("Visited: 100.0%");
                }
                else {
                    maze.startSolve();
                    isSolving = true;
                    timer.start();
                }
            }
        }

        if (e.getSource().equals(stopButton)) {
            timer.stop();
            isGenerating = false;
            isSolving = false;
        }
    }

    public void stateChanged(ChangeEvent e) {
        if (e.getSource().equals(speedSlider)) {
            timerSpeed = speedSlider.getValue();
            timer.stop();
            timer.setDelay((301 - timerSpeed));
            timer.start();
        }
        if (e.getSource().equals(heightSlider)) {
            if (!isGenerating && !isSolving) {
                maze.setGridH(heightSlider.getValue());
                heightLabel.setText("Height: " + heightSlider.getValue());
                maze.resetMaze();
                maze.repaint();
            }
        }
        if (e.getSource().equals(widthSlider)) {
            if (!isGenerating && !isSolving) {
                maze.setGridW(widthSlider.getValue());
                widthLabel.setText("Width: " + widthSlider.getValue());
                maze.resetMaze();
                maze.repaint();
            }
        }
    }

    public double calculatePercentGenerated() {
        double numVisited = 0;
        for (int i = 0; i < (maze.getGridH() * maze.getGridW()); i++) {
            if (maze.cells[i].visited)
                numVisited++;
        }
        double percentVisited = (((numVisited / (maze.getGridH() * maze.getGridW()))) * 100);
        return percentVisited;
    }

    public double calculatePercentSolved() {
        double numVisited = 0;
        for (int i = 0; i < (maze.getGridH() * maze.getGridW()); i++) {
            if (maze.cells[i].path)
                numVisited++;
        }
        double percentVisited = (((numVisited / (maze.getGridH() * maze.getGridW()))) * 100);
        return percentVisited;
    }

    ActionListener taskPerformer = new ActionListener() {
        public void actionPerformed(ActionEvent t) {
            if (isGenerating && showAnimation.isSelected()) {
                percentLabel.setText("Visited: " + df.format(calculatePercentGenerated()) + "%");
                if (!maze.s.isEmpty()) {
                    maze.current.isCurrent = false;
                    if (maze.checkNeighbors(maze.current)) {
                        maze.s.push(maze.next);
                        maze.next.visited = true;
                        maze.next.isCurrent = true;
                        maze.removeWalls(maze.current, maze.next);
                        maze.repaint();
                        maze.current = maze.next;
                    }
                    else {
                        maze.current = maze.s.pop();
                        maze.current.isCurrent = true;
                        maze.repaint();
                    }
                }
                else {
                    isGenerating = false;
                    maze.doneGenerating = true;
                    maze.repaint();
                }
            }
            else if (isSolving && showAnimation.isSelected()) {
                percentLabel.setText("Visited: " + df.format(calculatePercentSolved()) + "%");

                if (maze.current.x != (maze.getGridW() - 1) || maze.current.y != (maze.getGridH() - 1)) { // if it hasn't reached the end
                    maze.current.isCurrent = false;
                    if(maze.checkNeighborsSolve(maze.current)) {
                        maze.s.push(maze.next);
                        maze.next.path = true;
                        maze.next.isCurrent = true;
                        maze.repaint();
                        maze.current = maze.next;
                    }
                    else {
                        maze.current.backtrack = true;
                        if (maze.checkNeighborsSolve(maze.s.peek())) { // doesn't remove the cell from the stack if it has more neighbors
                            maze.current = maze.s.peek();
                        }
                        else
                            maze.current = maze.s.pop();
                        maze.current.isCurrent = true;
                        maze.repaint();
                    }
                }
                else {
                    isSolving = false;
                    maze.firstSolve = false;
                }
            }
        }
    };
    Timer timer = new Timer(200, taskPerformer);
}