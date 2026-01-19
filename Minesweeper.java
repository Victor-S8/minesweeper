import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.Timer;

public class Minesweeper{
    static JFrame frame;
    static JPanel board;
    static JTextField rowsField, colsField, minesField;
    static JComboBox<String> difficultyBox;
    static JButton[][] buttons;
    static int[][] grid;
    static int rows, cols;
    static JLabel timerLabel;
    static Timer timer;
    static int elapsedSeconds = 0;

    static Color backgroundColor = new Color(28, 28, 30);
    static Color cellColor = new Color(44, 44, 46);
    static Color cellOpenColor = new Color(58, 58, 60);

    static ImageIcon flagIcon;
    static ImageIcon bombIcon;

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            loadIcons();
            setup();
        });
    }

    public static void loadIcons()
    {
        flagIcon = new ImageIcon("flag.png");
        flagIcon = new ImageIcon(flagIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));

        bombIcon = new ImageIcon("bomb.png");
        bombIcon = new ImageIcon(bombIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
    }

    public static void setup()
    {
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(backgroundColor);

        JPanel setup = new JPanel();
        setup.setBackground(backgroundColor);
        setup.setLayout(new BoxLayout(setup, BoxLayout.Y_AXIS));
        setup.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel difficultyPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        difficultyPanel.setBackground(backgroundColor);

        difficultyBox = new JComboBox<>(new String[]
        {
            "Easy (10x10, 12 mines)",
            "Medium (16x16, 40 mines)",
            "Hard (20x20, 100 mines)",
            "Custom"
        });

        difficultyBox.setBackground(cellColor);
        difficultyBox.setForeground(Color.WHITE);

        difficultyPanel.add(label("Difficulty"));
        difficultyPanel.add(difficultyBox);

        JPanel customPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        customPanel.setBackground(backgroundColor);

        rowsField = field();
        colsField = field();
        minesField = field();

        customPanel.add(label("Rows"));
        customPanel.add(rowsField);
        customPanel.add(label("Columns"));
        customPanel.add(colsField);
        customPanel.add(label("Mines"));
        customPanel.add(minesField);

        customPanel.setVisible(false);

        difficultyBox.addActionListener(e ->
        {
            customPanel.setVisible(difficultyBox.getSelectedIndex() == 3);
            frame.revalidate();
            frame.repaint();
        });

        JPanel leaderboardPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        leaderboardPanel.setBackground(backgroundColor);

        leaderboardPanel.add(label("Easy best"));
        leaderboardPanel.add(label(getBestTime(0)));
        leaderboardPanel.add(label("Medium best"));
        leaderboardPanel.add(label(getBestTime(1)));
        leaderboardPanel.add(label("Hard best"));
        leaderboardPanel.add(label(getBestTime(2)));

        JButton start = new JButton("Start");
        styleButton(start);
        start.setAlignmentX(Component.CENTER_ALIGNMENT);
        start.addActionListener(e -> startGame());

        setup.add(difficultyPanel);
        setup.add(Box.createRigidArea(new Dimension(0, 10)));
        setup.add(customPanel);
        setup.add(Box.createRigidArea(new Dimension(0, 10)));
        setup.add(leaderboardPanel);
        setup.add(Box.createRigidArea(new Dimension(0, 20)));
        setup.add(start);

        frame.setContentPane(setup);
        frame.setVisible(true);
    }

    public static void startGame()
    {
        int choice = difficultyBox.getSelectedIndex();
        int mines = 0;

        if (choice == 0)
        {
            rows = 10;
            cols = 10;
            mines = 12;
        }
        else if (choice == 1)
        {
            rows = 16;
            cols = 16;
            mines = 40;
        }
        else if (choice == 2)
        {
            rows = 20;
            cols = 20;
            mines = 100;
        }
        else
        {
            rows = Integer.parseInt(rowsField.getText());
            cols = Integer.parseInt(colsField.getText());
            mines = Integer.parseInt(minesField.getText());
        }

        grid = Variables.createPlayingGrid(rows, cols, mines);
        buttons = new JButton[rows][cols];

        elapsedSeconds = 0;

        timerLabel = new JLabel("Time: 0 s", SwingConstants.CENTER);
        timerLabel.setForeground(Color.WHITE);
        timerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        timerLabel.setOpaque(true);
        timerLabel.setBackground(cellColor);
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        timer = new Timer(1000, e ->
        {
            elapsedSeconds++;
            timerLabel.setText("Time: " + elapsedSeconds + " s");
        });
        timer.start();

        int cellSize = 40;
        int gridWidth = cols * cellSize + (cols - 1) * 2 + 20;
        int gridHeight = rows * cellSize + (rows - 1) * 2 + 80;

        frame.setSize(gridWidth, gridHeight);
        frame.setLocationRelativeTo(null);

        board = new JPanel(new BorderLayout());

        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 2, 2));
        gridPanel.setBackground(backgroundColor);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int y = 1; y <= rows; y++)
        {
            for (int x = 1; x <= cols; x++)
            {
                JButton cellButton = new JButton()
                {
                    public Dimension getPreferredSize()
                    {
                        return new Dimension(cellSize, cellSize);
                    }
                };

                cellButton.setFocusPainted(false);
                cellButton.setBackground(cellColor);
                cellButton.setForeground(Color.WHITE);
                cellButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
                cellButton.setMargin(new Insets(0, 0, 0, 0));
                cellButton.setHorizontalAlignment(SwingConstants.CENTER);
                cellButton.setVerticalAlignment(SwingConstants.CENTER);

                int cx = x;
                int cy = y;

                cellButton.addMouseListener(new MouseAdapter()
                {
                    public void mousePressed(MouseEvent e)
                    {
                        if (SwingUtilities.isLeftMouseButton(e)) open(cx, cy);
                        else if (SwingUtilities.isRightMouseButton(e)) flag(cx, cy);

                        refresh();
                        checkWin();
                    }
                });

                buttons[y - 1][x - 1] = cellButton;
                gridPanel.add(cellButton);
            }
        }

        board.add(timerLabel, BorderLayout.NORTH);
        board.add(gridPanel, BorderLayout.CENTER);

        frame.setContentPane(board);
        frame.revalidate();
        frame.repaint();
    }

    public static void open(int x, int y)
    {
        int state = Variables.getCellState(grid, x, y);

        if (state == 0 || state == 3)
        {
            Variables.chainOpen(grid, x, y);
        }
        else if (state == 1 || state == 4)
        {
            revealMines();
            timer.stop();

            int choice = JOptionPane.showConfirmDialog(
                frame,
                "You hit a mine.\nRetry?",
                "Game Over",
                JOptionPane.YES_NO_OPTION
            );

            frame.dispose();

            if (choice == JOptionPane.YES_OPTION)
            {
                SwingUtilities.invokeLater(Minesweeper::setup);
            }
            else
            {
                System.exit(0);
            }
        }
    }

    public static void flag(int x, int y)
    {
        int state = Variables.getCellState(grid, x, y);

        if (state == 0) Variables.modifyCellState(grid, x, y, 3);
        else if (state == 1) Variables.modifyCellState(grid, x, y, 4);
        else if (state == 3) Variables.modifyCellState(grid, x, y, 0);
        else if (state == 4) Variables.modifyCellState(grid, x, y, 1);
    }

    public static void refresh()
    {
        for (int i = 0; i < grid.length; i++)
        {
            int x = grid[i][0] - 1;
            int y = grid[i][1] - 1;
            int state = grid[i][2];

            JButton cellButton = buttons[y][x];

            cellButton.setIcon(null);
            cellButton.setText("");
            cellButton.setBackground(state == 2 ? cellOpenColor : cellColor);

            if (state == 3 || state == 4)
            {
                cellButton.setIcon(flagIcon);
            }
            else if (state == 2)
            {
                int nearbyMines = Variables.getMinesInProximity(grid, i);

                if (nearbyMines > 0)
                {
                    cellButton.setText(Integer.toString(nearbyMines));
                    cellButton.setForeground(numColor(nearbyMines));
                }
            }
        }
    }

    public static void revealMines()
    {
        for (int i = 0; i < grid.length; i++)
        {
            if (grid[i][2] == 1 || grid[i][2] == 4)
            {
                int x = grid[i][0] - 1;
                int y = grid[i][1] - 1;

                buttons[y][x].setIcon(bombIcon);
                buttons[y][x].setBackground(new Color(90, 30, 30));
            }
        }
    }

    public static void checkWin()
    {
        for (int i = 0; i < grid.length; i++)
        {
            if (grid[i][2] == 0 || grid[i][2] == 3) return;
        }

        timer.stop();

        int choice = JOptionPane.showConfirmDialog(
            frame,
            "You Win!\nTime: " + elapsedSeconds + " s\nPlay again?",
            "Victory",
            JOptionPane.YES_NO_OPTION
        );

        int difficultyIndex = difficultyBox.getSelectedIndex();

        if (difficultyIndex >= 0 && difficultyIndex <= 2)
        {
            saveBestTime(difficultyIndex, elapsedSeconds);
        }

        frame.dispose();

        if (choice == JOptionPane.YES_OPTION)
        {
            SwingUtilities.invokeLater(Minesweeper::setup);
        }
        else
        {
            System.exit(0);
        }
    }

    public static void saveBestTime(int difficultyIndex, int time)
    {
        try
        {
            File leaderboardFile = new File("leaderboard.txt");
            int[] bestTimes = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE };

            Scanner wordScan = new Scanner(leaderboardFile);
            int index = 0;

            while (wordScan.hasNextInt() && index < 3)
            {
                bestTimes[index++] = wordScan.nextInt();
            }

            wordScan.close();

            if (time < bestTimes[difficultyIndex])
            {
                bestTimes[difficultyIndex] = time;
            }

            PrintWriter printer = new PrintWriter(leaderboardFile);

            for (int i = 0; i < bestTimes.length; i++)
            {
                printer.println(bestTimes[i]);
            }

            printer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getBestTime(int difficultyIndex)
    {
        try
        {
            File leaderboardFile = new File("leaderboard.txt");
            Scanner wordScan = new Scanner(leaderboardFile);

            int[] times = new int[3];
            int index = 0;

            while (wordScan.hasNextInt() && index < 3)
            {
                times[index++] = wordScan.nextInt();
            }

            wordScan.close();

            int difficultyTime = times[difficultyIndex];

            if (difficultyTime == Integer.MAX_VALUE)
            {
                return "no records yet";
            }
            else
            {
                return difficultyTime + " s";
            }
        }
        catch (Exception e)
        {
            return "no records yet";
        }
    }

    public static JLabel label(String text)
    {
        JLabel label = new JLabel(text);
        label.setForeground(Color.LIGHT_GRAY);
        return label;
    }

    public static JTextField field()
    {
        JTextField textField = new JTextField();
        textField.setBackground(cellColor);
        textField.setForeground(Color.WHITE);
        textField.setCaretColor(Color.WHITE);
        textField.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        return textField;
    }

    public static void styleButton(JButton button)
    {
        button.setBackground(new Color(72, 72, 74));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public static Color numColor(int n)
    {
        if (n == 1) return new Color(52, 152, 219);
        else if (n == 2) return new Color(46, 204, 113);
        else if (n == 3) return new Color(231, 76, 60);
        else if (n == 4) return new Color(155, 89, 182);
        else if (n == 5) return new Color(230, 126, 34);
        else if (n == 6) return new Color(26, 188, 156);
        else return Color.LIGHT_GRAY;
    }
}
