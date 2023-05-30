import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.*;

// Enum for cell types
enum CellType {
    START, FINISH, MINE, EMPTY, CHECKED, FINALPATH
}

/*
 * CS441 AI Project
 */

public class Path {

    private JFrame frame;

    private int cells = 10;
    private int delay = 1000;
    private double density = (cells * cells) * .25;
    private int startX = -1;
    private int startY = -1;
    private int finishX = -1;
    private int finishY = -1;
    private int tool = 0;
    private int selectedAlgorithm = 0;
    private int WIDTH = 850;
    private final int HEIGHT = 1000;
    private final int M_SIZE = 800;
    private int C_SIZE = M_SIZE / cells;

    // Selections
    private String[] algorithms = { "Dijkstra", "A*" };
    private String[] tools = { "Starting", "Destination", "Mine", "Remove Mine" };

    private boolean solving = false;

    private Node[][] map;
    private Algorithm Alg = new Algorithm();
    private Random r = new Random();

    // Labels
    private JLabel algorithmLabel = new JLabel("Algorithms");
    private JLabel toolLabel = new JLabel("Cell Type");

    private JLabel x0 = new JLabel("0");
    private JLabel x1 = new JLabel("1");
    private JLabel x2 = new JLabel("2");
    private JLabel x3 = new JLabel("3");
    private JLabel x4 = new JLabel("4");
    private JLabel x5 = new JLabel("5");
    private JLabel x6 = new JLabel("6");
    private JLabel x7 = new JLabel("7");
    private JLabel x8 = new JLabel("8");
    private JLabel x9 = new JLabel("9");

    private JLabel y0 = new JLabel("0");
    private JLabel y1 = new JLabel("1");
    private JLabel y2 = new JLabel("2");
    private JLabel y3 = new JLabel("3");
    private JLabel y4 = new JLabel("4");
    private JLabel y5 = new JLabel("5");
    private JLabel y6 = new JLabel("6");
    private JLabel y7 = new JLabel("7");
    private JLabel y8 = new JLabel("8");
    private JLabel y9 = new JLabel("9");

    // buttons
    private JButton searchButton;
    private JButton resetButton;
    private JButton randomMapButton;
    private JButton clearMapButton;

    // button images
    ImageIcon startBtnImg = new ImageIcon("images/b1.png");
    ImageIcon resetBtnImg = new ImageIcon("images/b2.png");
    ImageIcon genMapBBtnImg = new ImageIcon("images/b3.png");
    ImageIcon clearMapBBtnImg = new ImageIcon("images/b4.png");

    // Radio buttons
    JRadioButton button1 = new JRadioButton(algorithms[0]);
    JRadioButton button2 = new JRadioButton(algorithms[1]);

    JRadioButton button3 = new JRadioButton(tools[0]);
    JRadioButton button4 = new JRadioButton(tools[1]);
    JRadioButton button5 = new JRadioButton(tools[2]);
    JRadioButton button6 = new JRadioButton(tools[3]);

    private ButtonGroup group = new ButtonGroup();

    private ButtonGroup group2 = new ButtonGroup();

    // PANELS
    private JPanel toolP = new JPanel();
    private JPanel toolP2 = new JPanel();

    // CANVAS
    private Map canvas;

    public static void main(String[] args) {
        new Path();
    }

    private Path() {
        clearMap();
        initialize();
    }

    private void randomMap() {
        clearMap();
        for (int i = 0; i < density; i++) {
            Node current;
            do {
                int x = r.nextInt(cells);
                int y = r.nextInt(cells);
                current = map[x][y];
            } while (current.getType() == 2); // if there's already mine, find another
            current.setType(2); // type 2 is 'mine'
        }
    }

    private void clearMap() {
        finishX = -1;
        finishY = -1;
        startX = -1;
        startY = -1;
        map = new Node[cells][cells];
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                map[x][y] = new Node(3, x, y);
            }
        }
        reset();
    }

    private void resetMap() {
        for (int x = 0; x < cells; x++) {
            for (int y = 0; y < cells; y++) {
                Node current = map[x][y];
                // Either checked or final path
                if (current.getType() == 4 || current.getType() == 5)
                    map[x][y] = new Node(3, x, y);
            }
        }
        if (startX > -1 && startY > -1) {
            map[startX][startY] = new Node(0, startX, startY);
            map[startX][startY].setHops(0);
        }
        if (finishX > -1 && finishY > -1)
            map[finishX][finishY] = new Node(1, finishX, finishY);
        reset(); // RESET SOME VARIABLES
    }

    private void initialize() {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setSize(WIDTH, HEIGHT);
        frame.setTitle("CS441 Project");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // coordinates locations (for x)
        x0.setBounds(40, 125, 20, 20);
        x1.setBounds(120, 125, 20, 20);
        x2.setBounds(200, 125, 20, 20);
        x3.setBounds(280, 125, 20, 20);
        x4.setBounds(360, 125, 20, 20);
        x5.setBounds(440, 125, 20, 20);
        x6.setBounds(520, 125, 20, 20);
        x7.setBounds(600, 125, 20, 20);
        x8.setBounds(680, 125, 20, 20);
        x9.setBounds(760, 125, 20, 20);

        toolP.add(x0);
        toolP.add(x1);
        toolP.add(x2);
        toolP.add(x3);
        toolP.add(x4);
        toolP.add(x5);
        toolP.add(x6);
        toolP.add(x7);
        toolP.add(x8);
        toolP.add(x9);

        // coordinates locations (for y)
        y0.setBounds(10, 185, 20, 20);
        y1.setBounds(10, 265, 20, 20);
        y2.setBounds(10, 345, 20, 20);
        y3.setBounds(10, 425, 20, 20);
        y4.setBounds(10, 505, 20, 20);
        y5.setBounds(10, 585, 20, 20);
        y6.setBounds(10, 665, 20, 20);
        y7.setBounds(10, 745, 20, 20);
        y8.setBounds(10, 825, 20, 20);
        y9.setBounds(10, 905, 20, 20);

        frame.add(y0);
        frame.add(y1);
        frame.add(y2);
        frame.add(y3);
        frame.add(y4);
        frame.add(y5);
        frame.add(y6);
        frame.add(y7);
        frame.add(y8);
        frame.add(y9);

        Color myColor = new Color(232, 209, 176);
        // Color myColor = new Color(207, 133, 133);
        button1.setBackground(myColor);
        button2.setBackground(myColor);
        button3.setBackground(myColor);
        button4.setBackground(myColor);
        button5.setBackground(myColor);
        button6.setBackground(myColor);
        frame.getContentPane().setBackground(myColor);
        toolP.setBackground(myColor);

        toolP.setLayout(null);
        toolP.setBounds(21, 10, 805, 145);

        toolP2.setLayout(null);
        toolP2.setBounds(0, 155, 21, 800);

        searchButton = new JButton(startBtnImg);

        searchButton.setBounds(665, 10, 120, 105);
        toolP.add(searchButton);

        resetButton = new JButton(resetBtnImg);

        resetButton.setBounds(440, 10, 120, 25);
        toolP.add(resetButton);

        randomMapButton = new JButton(genMapBBtnImg);

        randomMapButton.setBounds(440, 50, 120, 25);
        toolP.add(randomMapButton);

        clearMapButton = new JButton(clearMapBBtnImg);

        clearMapButton.setBounds(440, 90, 120, 25);
        toolP.add(clearMapButton);

        algorithmLabel.setBounds(20, 25, 120, 25);
        toolP.add(algorithmLabel);

        button1.setSelected(true);
        group.add(button1);
        group.add(button2);
        button1.setBounds(20, 75, 120, 25);
        button2.setBounds(20, 50, 120, 25);
        toolP.add(button1);
        toolP.add(button2);

        button3.setBounds(220, 25, 120, 25);
        button4.setBounds(220, 50, 120, 25);
        button5.setBounds(220, 75, 120, 25);
        button6.setBounds(220, 100, 120, 25);
        group2.add(button3);
        group2.add(button4);
        group2.add(button5);
        group2.add(button6);
        button3.setSelected(true);
        toolP.add(button3);
        toolP.add(button4);
        toolP.add(button5);
        toolP.add(button6);

        toolLabel.setBounds(220, 0, 120, 25);
        toolP.add(toolLabel);

        // toolBx.setBounds(240, 50 + 25, 120, 25);
        // toolP.add(toolBx);

        frame.getContentPane().add(toolP);

        canvas = new Map();
        canvas.setBounds(22, 155, M_SIZE + 1, M_SIZE + 1);
        frame.getContentPane().add(canvas);

        System.out.println("--- Selected Algorithm: " + algorithms[selectedAlgorithm] + " ---");

        // frame.setBackground(Color.GREEN);
        // toolP.setBackground(Color.orange);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedAlgorithm = 0;
                System.out.println("--- Selected Algorithm: " + algorithms[selectedAlgorithm] + " ---");
                Update();
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedAlgorithm = 1;
                System.out.println("--- Selected Algorithm: " + algorithms[selectedAlgorithm] + " ---");
                Update();
            }
        });

        searchButton.addActionListener(new ActionListener() { // ACTION LISTENERS
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
                if ((startX > -1 && startY > -1) && (finishX > -1 && finishY > -1)) {
                    solving = true;
                    System.out.println("--- Search Started ---");
                } else {
                    System.out.println("--- ERR: Select Start and Finish! ---");
                }
            }
        });
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("--- Map Reset ---");
                resetMap();
                Update();
            }
        });
        randomMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("--- Random Map is ready ---");
                randomMap();
                Update();
            }
        });
        clearMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("--- Map is cleared ---");
                clearMap();
                Update();
            }
        });

        button3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tool = 0;
            }
        });

        button4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tool = 1;
            }
        });

        button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tool = 2;
            }
        });

        button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tool = 3;
            }
        });

        // toolBx.addItemListener(new ItemListener() {
        // @Override
        // public void itemStateChanged(ItemEvent e) {
        // tool = toolBx.getSelectedIndex();
        // }
        // });
        startSearch();
    }

    private void startSearch() {
        if (solving) {
            switch (selectedAlgorithm) {
                case 0:
                    Alg.Dijkstra();
                    break;
                case 1:
                    Alg.AStar();
                    break;
            }
        }
        pause();
    }

    private void pause() {
        int i = 0;
        while (!solving) {
            i++;
            if (i > 500)
                i = 0;
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }
        }
        startSearch();
    }

    private void Update() {
        canvas.repaint();
    }

    private void reset() {
        solving = false;
    }

    private void delay() {
        try {
            Thread.sleep(delay);
        } catch (Exception ignored) {
        }

    }

    class Map extends JPanel implements MouseListener, MouseMotionListener {

        Map() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (int x = 0; x < cells; x++) {
                for (int y = 0; y < cells; y++) {
                    switch (map[x][y].getType()) {
                        case 0:
                            BufferedImage imageStart = null;
                            try {
                                imageStart = ImageIO.read(new File("images/start.png"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            g.drawImage(imageStart, x * C_SIZE + 8, y * C_SIZE + 8, null);
                            break;
                        case 1:
                            BufferedImage imageFinish = null;
                            try {
                                imageFinish = ImageIO.read(new File("images/finish.png"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            g.drawImage(imageFinish, x * C_SIZE + 8, y * C_SIZE + 8, null);
                            break;
                        case 2:
                            BufferedImage imageMine = null;
                            try {
                                imageMine = ImageIO.read(new File("images/mine.png"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            g.drawImage(imageMine, x * C_SIZE + 8, y * C_SIZE + 8, null);
                            break;
                        case 3:
                            g.setColor(Color.WHITE);
                            g.fillRect(x * C_SIZE, y * C_SIZE, C_SIZE, C_SIZE);
                            break;
                        case 4:
                            BufferedImage imageCheck = null;
                            try {
                                imageCheck = ImageIO.read(new File("images/check.png"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            g.drawImage(imageCheck, x * C_SIZE + 8, y * C_SIZE + 8, null);
                            //
                            break;
                        case 5:
                            BufferedImage imageFound = null;
                            try {
                                imageFound = ImageIO.read(new File("images/found.png"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            g.drawImage(imageFound, x * C_SIZE + 8, y * C_SIZE + 8, null);
                            break;
                    }
                    g.setColor(new Color(195, 195, 195));
                    g.drawRect(x * C_SIZE, y * C_SIZE, C_SIZE, C_SIZE);
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            resetMap();
            try {
                int x = e.getX() / C_SIZE;
                int y = e.getY() / C_SIZE;
                System.out.println("Cell Type: " + tools[tool] + " | Coordinate: (" + x + ", " + y + ")");
                Node current = map[x][y];
                switch (tool) {
                    // Start node
                    case 0: {
                        // If not a mine TODO: refactor the code later.
                        if (current.getType() != 2) {
                            if (startX > -1 && startY > -1) {
                                map[startX][startY].setType(3);
                                map[startX][startY].setHops(-1);
                            }
                            current.setHops(0);
                            startX = x;
                            startY = y;
                            current.setType(0);
                        }
                        break;
                    }
                    // Finish node
                    case 1: {
                        if (current.getType() != 2) { // IF NOT MINE
                            if (finishX > -1 && finishY > -1) // IF FINISH EXISTS SET IT TO EMPTY
                                map[finishX][finishY].setType(3);
                            finishX = x; // SET THE FINISH X AND Y
                            finishY = y;
                            current.setType(1); // SET THE NODE CLICKED TO BE FINISH
                        }
                        break;
                    }
                    default:
                        if (current.getType() != 0 && current.getType() != 1)
                            current.setType(tool);
                        break;
                }
                Update();
            } catch (Exception ignored) {
            } // EXCEPTION HANDLER
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        }
    }

    class Algorithm {

        // dijkstra algorithm works by spreading outwards until it discovers the
        // endpoint, then it traces back to determine the path. It utilizes a priority
        // queue to manage the nodes that require exploration. Each node in the priority
        // queue is examined, and its neighboring nodes are added to the queue. Once a
        // node has been explored, it is removed from the queue. The priority queue is
        // represented by an ArrayList. Another ArrayList is generated by a function
        // that explores the neighbors of a node. This ArrayList contains all the
        // explored nodes, which are subsequently added to the queue. The "hops"
        // variable in each node signifies the number of nodes traversed from the
        // starting point.
        void Dijkstra() {
            ArrayList<Node> priority = new ArrayList<Node>();
            priority.add(map[startX][startY]);
            while (solving) {
                // If the priority queue is empty, then no path exists
                if (priority.size() <= 0) {
                    solving = false;
                    break;
                }
                int hops = priority.get(0).getHops() + 1;
                ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);
                if (explored.size() > 0) {
                    // System.out.println("[" + priority.get(0).x + ", " + priority.get(0).y + "]");
                    priority.remove(0);

                    priority.addAll(explored);
                    Update();
                    delay();
                } else {
                    priority.remove(0);
                }
            }
        }

        // A* operates similarly to Dijkstra's algorithm by establishing a priority
        // queue and expanding outward until it locates the endpoint. However, A*
        // integrates a heuristic that considers the distance from each node to the
        // destination. As a result, nodes that are nearer to the finish are given
        // higher priority for exploration. This heuristic is incorporated by sorting
        // the queue according to the sum of hops and distance until reaching the
        // finish.

        void AStar() {
            ArrayList<Node> priority = new ArrayList<Node>();
            priority.add(map[startX][startY]);
            while (solving) {
                if (priority.size() <= 0) {
                    solving = false;
                    break;
                }
                int hops = priority.get(0).getHops() + 1;
                ArrayList<Node> explored = exploreNeighbors(priority.get(0), hops);
                if (explored.size() > 0) {
                    priority.remove(0);
                    priority.addAll(explored);
                    Update();
                    delay();
                } else {
                    priority.remove(0);
                }
                sortQue(priority); // SORT THE PRIORITY QUE
            }
        }

        void sortQue(ArrayList<Node> sort) { // SORT PRIORITY QUE
            int c = 0;
            double distanceTemp = Integer.MAX_VALUE;
            int xTemp = -1;
            int yTemp = -1;

            while (c < sort.size()) {

                int sm = c;

                for (int i = c + 1; i < sort.size(); i++) {
                    if (sort.get(i).getBirdFlightDistance()
                            + sort.get(i).getHops() < sort.get(sm).getBirdFlightDistance() + sort.get(sm).getHops())
                        sm = i;
                }
                if (c != sm) {
                    Node temp = sort.get(c);
                    sort.set(c, sort.get(sm));
                    sort.set(sm, temp);
                }

                // System.out.println("> Bird flight distance: = " +
                // sort.get(c).getBirdFlightDistance() + "\n[Node coordinate: (" + sort.get(c).y
                // + ", " + sort.get(c).x + ")]");
                // System.out.printf("> Bird flight distance: %.2f \n> [Node coordinate: (%d ,
                // %d)]\n", sort.get(c).getBirdFlightDistance(), sort.get(c).x, sort.get(c).y);

                if (distanceTemp > sort.get(c).getBirdFlightDistance()) {
                    distanceTemp = sort.get(c).getBirdFlightDistance();
                    xTemp = sort.get(c).x;
                    yTemp = sort.get(c).y;
                }

                c++;

            }

            System.out.printf(" - - - SELECTED: Minimum distance: %.2f, at coordinate (%d, %d) - - -\n", distanceTemp,
                    xTemp, yTemp);

        }

        ArrayList<Node> exploreNeighbors(Node current, int hops) {
            ArrayList<Node> explored = new ArrayList<Node>();
            for (int a = -1; a <= 1; a++) {
                for (int b = -1; b <= 1; b++) {
                    int xbound = current.getX() + a;
                    int ybound = current.getY() + b;
                    if ((xbound > -1 && xbound < cells) && (ybound > -1 && ybound < cells)) {
                        Node neighbor = map[xbound][ybound];
                        // If node is not a mine and has not been explored
                        if ((neighbor.getHops() == -1 || neighbor.getHops() > hops) && neighbor.getType() != 2) {

                            System.out.println("EXPLORED NODE: coordinate (" + xbound + ", " + ybound + ")");
                            if (selectedAlgorithm == 1) {
                                System.out.printf("> Bird flight distance: %.2f\n",
                                        Math.sqrt(pow(abs(xbound - finishX), 2) + pow(abs(ybound - finishY), 2)));
                            }
                            // System.out.println(" Test bird: " + Math.sqrt(pow(abs(xbound - finishX), 2) +
                            // pow(abs(ybound - finishY), 2)) + ")");
                            explore(neighbor, current.getX(), current.getY(), hops);
                            explored.add(neighbor);
                        }
                    }
                }
            }
            return explored;
        }

        void explore(Node current, int lastx, int lasty, int hops) {
            if (current.getType() != 0 && current.getType() != 1)
                current.setType(4);
            current.setLastNode(lastx, lasty);
            current.setHops(hops);
            if (current.getType() == 1) {
                backtrack(current.getLastX(), current.getLastY(), hops);
            }
        }

        void backtrack(int lx, int ly, int hops) {
            while (hops > 1) {
                Node current = map[lx][ly];
                current.setType(5);
                lx = current.getLastX();
                ly = current.getLastY();
                hops--;
            }
            solving = false;
        }
    }

    class Node {

        // 0 = start, 1 = finish, 2 = MINE, 3 = empty, 4 = checked, 5 = finalpath
        private int cellType = 0;
        private int hops;
        public int x;
        public int y;
        private int lastX;
        private int lastY;

        Node(int type, int x, int y) {
            cellType = type;
            this.x = x;
            this.y = y;
            hops = -1;
        }

        double getBirdFlightDistance() {
            int xdif = abs(x - finishX);
            int ydif = abs(y - finishY);
            double sqrt = sqrt((xdif * xdif) + (ydif * ydif));
            return sqrt;
        }

        int getX() {
            return x;
        } // GET METHODS

        int getY() {
            return y;
        }

        int getLastX() {
            return lastX;
        }

        int getLastY() {
            return lastY;
        }

        int getType() {
            return cellType;
        }

        int getHops() {
            return hops;
        }

        void setType(int type) {
            cellType = type;
        } // SET METHODS

        void setLastNode(int x, int y) {
            lastX = x;
            lastY = y;
        }

        void setHops(int hops) {
            this.hops = hops;
        }
    }
}
