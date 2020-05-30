package dngh;

import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;

public class DoNotGetHit extends JFrame {
    private Board board;
    private final int WIDTH=600;
    private final int HEIGHT=900;

    
    public DoNotGetHit() {
        JLabel footer = new JLabel();
        add(footer, BorderLayout.SOUTH);      
        board = new Board(footer);
        board.setBorder(BorderFactory.createLineBorder(Color.black));
        add(board, BorderLayout.CENTER);
        setTitle("Don't get hit");
        setSize(WIDTH+2, HEIGHT+40);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }


    public static void main(String[] args) {
        new DoNotGetHit().setVisible(true);
    }
    
}
