import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener {

    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 800;
    private static final int UNIT_SIZE = 20; // Size of each snake segment and food
    private static final int GAME_UNITS = (PANEL_WIDTH * PANEL_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    private static final int DELAY = 100; // Delay between snake movements

    private final int[] x = new int[GAME_UNITS]; // X coordinates of snake body
    private final int[] y = new int[GAME_UNITS]; // Y coordinates of snake body
    private int snakeLength; // Length of the snake

    private int foodX; // X coordinate of food
    private int foodY; // Y coordinate of food
    private char direction; // Direction ('U' = up, 'D' = down, 'L' = left, 'R' = right)
    private boolean running; // Game running state
    private Timer timer; // Timer for game loop

    private Random random;

    public SnakeGame() {
        random = new Random();
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        startGame();
    }

    private void startGame() {
        // Initialize the game variables
        snakeLength = 5;
        for (int i = 0; i < snakeLength; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        direction = 'R';
        running = true;
        placeFood();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void restartGame() {
        // Reset the game state
        snakeLength = 5;
        direction = 'R';
        running = true;

        // Reset the snake's position
        for (int i = 0; i < snakeLength; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        // Place new food and restart the timer
        placeFood();
        timer.restart();
        repaint();
    }

    private void placeFood() {
        // Generate random position for food
        foodX = random.nextInt((int) (PANEL_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((int) (PANEL_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    private void move() {
        // Move the snake body
        for (int i = snakeLength; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Change direction of snake's head
        switch (direction) {
            case 'U' -> y[0] -= UNIT_SIZE; // Move up
            case 'D' -> y[0] += UNIT_SIZE; // Move down
            case 'L' -> x[0] -= UNIT_SIZE; // Move left
            case 'R' -> x[0] += UNIT_SIZE; // Move right
        }
    }

    private void checkFood() {
        // Check if the snake's head is at the food's position
        if ((x[0] == foodX) && (y[0] == foodY)) {
            snakeLength++; // Grow the snake
            placeFood(); // Place new food
        }
    }

    private void checkCollisions() {
        // Check if the head collides with the body
        for (int i = snakeLength; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // Stop the game
            }
        }

        // Check if the head collides with the walls
        if (x[0] < 0 || x[0] >= PANEL_WIDTH || y[0] < 0 || y[0] >= PANEL_HEIGHT) {
            running = false; // Stop the game
        }

        // Stop the timer if the game is no longer running
        if (!running) {
            timer.stop();
        }
    }

    private void gameOver(Graphics g) {
        // Display "Game Over" text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (PANEL_WIDTH - metrics.stringWidth("Game Over")) / 2, PANEL_HEIGHT / 2);

        // Display "Press 'R' to Restart" text
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString("Press 'R' to Restart", (PANEL_WIDTH - metrics.stringWidth("Press 'R' to Restart")) / 2, PANEL_HEIGHT / 2 + 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        if (running) {
            // Draw the food
            g.setColor(Color.RED);
            g.fillOval(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake
            for (int i = 0; i < snakeLength; i++) {
                if (i == 0) { // Head of the snake
                    g.setColor(Color.GREEN);
                    g.fillRect(x[0], y[0], UNIT_SIZE, UNIT_SIZE);
                } else { // Body of the snake
                    g.setColor(new Color(45, 180, 0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
        } else {
            gameOver(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    private class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_R: // If 'R' is pressed, restart the game
                    if (!running) {
                        restartGame();
                    }
                    break;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame gamePanel = new SnakeGame();

        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
