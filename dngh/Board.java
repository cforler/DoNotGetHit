package dngh; 

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.util.Random;
import java.awt.Color;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

 class Board extends JPanel {
     private enum Direction { UP, DOWN, LEFT, RIGHT };
     
     private final int EMPTY   = 0;
     private final int PLAYER  = 1;
     private final int STONE   = 2;
     private final int BOARD_WIDTH  = 30;
     private final int BOARD_HEIGHT = 30;
     private final int INTERVAL     = 400;
     private final int BASIC_SPAWN_CHANCE  = 1;
     
     private int[][] board;

     private Color playerColor;
     private Color stoneColor;

     private boolean over; 
     private boolean paused;
     private Random rand;
     private Timer timer;
     private int level;
     private int ticks;
     private int points;
     private JLabel footer;
     private int px;
     private int py;

     /******************************************************************/
     
     public Board(JLabel footer) {
         rand = new Random();
         this.footer = footer;
         over=false;
         paused=false;
         level=1;
         initBoard();
     }
     
     
     private void initBoard(){
         board = new  int[BOARD_WIDTH][BOARD_HEIGHT];
         px = BOARD_WIDTH/2;
         py = BOARD_HEIGHT/2;
         board[py][px]= PLAYER;
         playerColor = new Color(100, 100, 200);
         stoneColor = Color.black;

         addKeyListener(new Controls());
         footer.setText(getFooterMessage());
         setFocusable(true);
     }
     
     
     /******************************************************************/
     
    private int squareWidth() {
        return (int) getSize().getWidth() / BOARD_WIDTH;
    }

     /******************************************************************/

     private int squareHeight() {
         return (int) getSize().getHeight() / BOARD_HEIGHT;
     }
    

     /******************************************************************/
     

   @Override
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       doDrawing(g);
   }

     /******************************************************************/
     
     private void  drawGrid(Graphics g) {
         var size = getSize();
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            int y= i * squareHeight();
            for (int j = 0; j <= BOARD_WIDTH; j++) {
                int x= j * squareWidth();                
                g.drawRect(x, getTop()+y, squareWidth(), squareHeight());
            }
        }
     }


     /******************************************************************/
     
     private void drawSquare(Graphics g, int x, int y, Color c) {
          x= x * squareHeight() + 1;
          y= y * squareHeight() + 1 + getTop(); 
          int a = squareWidth()-1;
          int b = squareHeight() - 1;
          Color t = g.getColor();
          g.setColor(c);
          g.fillRect(x,y, a,b);
          g.setColor(t);
     }
                                                        
     /******************************************************************/
     
     private int getTop() {
         return (int) getSize().getHeight() - BOARD_HEIGHT * squareHeight();
     }
     
     
     /******************************************************************/

     private void drawSquares(Graphics g) {
         for (int i = 0; i < BOARD_HEIGHT; i++)
             for (int j = 0; j < BOARD_WIDTH; j++)
                 if(board[j][i]==STONE) drawSquare(g, i, j, stoneColor);
                 else  drawSquare(g, i, j, Color.white);
         drawSquare(g, px, py, playerColor);
     }
     
     /******************************************************************/

     private void doDrawing(Graphics g) {
         if(over) gameOver();

         drawSquares(g);
         drawGrid(g);
     }
     /******************************************************************/

     public void start() {
        timer = new Timer(INTERVAL, new Ticks());
        timer.start();
    }
     
     /******************************************************************/

     private void gameOver() {
         timer.stop();
         footer.setText("GAME OVER!" + getFooterMessage());
     }
     

     /******************************************************************/

     private void fallingStones() {
         for(int i=0; i < BOARD_WIDTH; i++) 
             if( board[BOARD_HEIGHT-1][i] == STONE)
                 board[BOARD_HEIGHT-1][i] = EMPTY;
         
         
         for(int i=0; i < BOARD_WIDTH; i++) 
             for(int j=BOARD_HEIGHT-2; j >= 0; j--) 
                 if (board[j][i] == STONE) {
                     board[j][i] = EMPTY;
                     board[j+1][i] = STONE;
                 }
     }
     

     /******************************************************************/
     
     private void update() {
         if(paused) return;
         ticks +=1;
         points+=1*level;
         fallingStones();
            
              
         for(int i=0; i < BOARD_WIDTH; i++) {
             if (rand.nextInt(100) < (BASIC_SPAWN_CHANCE + level))
                 board[0][i] = STONE;
         }
         
         if(ticks==20*HEIGHT) {
             level+=1;
             ticks=0;
         }
         
         footer.setText(getFooterMessage());

         if(board[py][px] == STONE) over = true;          
     }

     /******************************************************************/

     private String getFooterMessage() {
         return "Level: " + level +"   Score: " + points;
     }
     

     /******************************************************************/

     private void pause() {
         paused = !paused;
         if(paused) footer.setText("Paused " + getFooterMessage());
         else footer.setText(getFooterMessage());
    }

     /******************************************************************/

     private class Ticks implements ActionListener {
         @Override
         public void actionPerformed(ActionEvent e) { tick(); }
     }
     
     /******************************************************************/
     
     private void tick() {
         update();
         repaint();
     }


     /******************************************************************/
     
     private void move(Direction d) {
         if(over) return;
         switch (d) {
         case DOWN  -> { if(py< (BOARD_HEIGHT-1))  board[py++][px] = EMPTY; }
         case UP    -> { if(py > 0) board[py--][px] = EMPTY; }
         case LEFT  -> { if(px > 0) board[py][px--] = EMPTY; }
         case RIGHT -> { if(px < (BOARD_WIDTH-1)) board[py][px++] = EMPTY; }
         }
         if (board[py][px] == STONE) over = true;
         board[py][px] = PLAYER;
         
     }
     
     class Controls extends KeyAdapter {
         @Override
         public void keyPressed(KeyEvent e) {
             switch (e.getKeyCode()) {
             case KeyEvent.VK_P -> pause();
             case KeyEvent.VK_SPACE -> pause();
             case KeyEvent.VK_UP    -> move(Direction.UP);
             case KeyEvent.VK_DOWN  -> move(Direction.DOWN);
             case KeyEvent.VK_LEFT  -> move(Direction.LEFT);
             case KeyEvent.VK_RIGHT -> move(Direction.RIGHT);

             case KeyEvent.VK_W -> move(Direction.UP);
             case KeyEvent.VK_S -> move(Direction.DOWN);
             case KeyEvent.VK_A -> move(Direction.LEFT);
             case KeyEvent.VK_D -> move(Direction.RIGHT);
             }
         }
     }
 }
