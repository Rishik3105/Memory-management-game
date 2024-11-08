import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MemoryMatchingGame {
    // GUI components for the game
    private JFrame gameFrame;
    private JPanel gamePanel;
    private JButton[] buttons;
    private JButton startButton;
    private JTextArea gameBoard;
    private List<String> leaderboard;
    private String currentPlayer;
    private int score;

    // Game variables
    private boolean[] revealed;
    private String[] cardValues;
    private int numPairs = 8;
    private int firstCard = -1;
    private int secondCard = -1;

    public MemoryMatchingGame() {
        // Initialize variables
        leaderboard = new ArrayList<>();
        score = 0;
        revealed = new boolean[numPairs * 2];
        cardValues = new String[numPairs * 2];
        
        // Initialize card values for a matching game
        for (int i = 0; i < numPairs; i++) {
            cardValues[i] = String.valueOf(i + 1);
            cardValues[i + numPairs] = String.valueOf(i + 1);
        }

        // Convert cardValues array into a mutable ArrayList and shuffle it
        List<String> cardList = new ArrayList<>(List.of(cardValues));
        Collections.shuffle(cardList);

        // Update cardValues with shuffled values
        cardList.toArray(cardValues);

        // Set up the game frame and UI components
        gameFrame = new JFrame("Memory Matching Game");
        gameFrame.setSize(400, 400);
        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setLayout(new BorderLayout());

        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(4, 4));

        buttons = new JButton[numPairs * 2];
        for (int i = 0; i < numPairs * 2; i++) {
            buttons[i] = new JButton("?");
            buttons[i].setFont(new Font("Arial", Font.BOLD, 24));
            buttons[i].setFocusable(false);
            buttons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton clickedButton = (JButton) e.getSource();
                    int index = -1;
                    for (int i = 0; i < buttons.length; i++) {
                        if (buttons[i] == clickedButton) {
                            index = i;
                            break;
                        }
                    }

                    if (!revealed[index]) {
                        revealCard(index);
                    }
                }
            });
            gamePanel.add(buttons[i]);
        }

        // Start button
        startButton = new JButton("Start New Game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        // Text area for game board and leaderboard
        gameBoard = new JTextArea();
        gameBoard.setEditable(false);
        gameBoard.setPreferredSize(new Dimension(400, 100));

        // Add components to the frame
        gameFrame.add(gamePanel, BorderLayout.CENTER);
        gameFrame.add(startButton, BorderLayout.SOUTH);
        gameFrame.add(new JScrollPane(gameBoard), BorderLayout.NORTH);
        gameFrame.setVisible(true);
    }

    // Start the game
    private void startGame() {
        // Reset game state
        score = 0;
        currentPlayer = JOptionPane.showInputDialog("Enter Player Name: ");
        leaderboard.clear();  // Clear previous leaderboard

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText("?");
            revealed[i] = false;
        }

        gameBoard.setText("Player: " + currentPlayer + " | Score: " + score);
    }

    // Reveal card logic
    private void revealCard(int index) {
        buttons[index].setText(cardValues[index]);
        revealed[index] = true;

        // Check if two cards are revealed
        if (firstCard == -1) {
            firstCard = index;
        } else {
            secondCard = index;

            // Check if the two cards match
            if (cardValues[firstCard].equals(cardValues[secondCard])) {
                score += 10;
                gameBoard.setText("Player: " + currentPlayer + " | Score: " + score);
                checkGameOver();
            } else {
                // If no match, hide the cards again after a short delay
                Timer timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        buttons[firstCard].setText("?");
                        buttons[secondCard].setText("?");
                        revealed[firstCard] = false;
                        revealed[secondCard] = false;
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
            firstCard = -1;
            secondCard = -1;
        }
    }

    // Check if the game is over
    private void checkGameOver() {
        boolean allRevealed = true;
        for (boolean b : revealed) {
            if (!b) {
                allRevealed = false;
                break;
            }
        }

        if (allRevealed) {
            gameBoard.setText("Game Over! Player " + currentPlayer + " | Final Score: " + score);
            leaderboard.add(currentPlayer + " - " + score);
            saveLeaderboard();
        }
    }

    // Save leaderboard
    private void saveLeaderboard() {
        leaderboard.sort((a, b) -> {
            int scoreA = Integer.parseInt(a.split(" - ")[1]);
            int scoreB = Integer.parseInt(b.split(" - ")[1]);
            return Integer.compare(scoreB, scoreA);
        });

        gameBoard.append("\nLeaderboard:\n");
        for (String entry : leaderboard) {
            gameBoard.append(entry + "\n");
        }
    }

    public static void main(String[] args) {
        new MemoryMatchingGame();  // Start the game
    }
}
