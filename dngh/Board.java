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
     enum Tile { EMPTY, PLAYER, STONE };
     private enum Direction { UP, DOWN, LEFT, RIGHT };

     static  final int SQUARE_LEN = 30;
     private static final int BOARD_WIDTH  = 20;
     private static final int BOARD_HEIGHT = 30;
     private static final int INTERVAL     = 400;
     private static final int BASIC_SPAWN_CHANCE  = 1;
     
     private Tile[][] board;
     private Color stoneColor;

     private boolean over; 
     private boolean paused;
     private Random rand;
     private Timer timer;
     private int level;
     private int ticks;
     private int points;
     private JLabel footer;
     private BoardObject player;
     private BoardObject asteroid;
     private BoardObject background;

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
         board = new  Tile[BOARD_HEIGHT][BOARD_WIDTH];
         player = new BoardObject( BOARD_WIDTH/2,BOARD_HEIGHT/2,
                                   "dngh/images/fairydust.png", Tile.PLAYER);

         asteroid = new BoardObject(0,0,"dngh/images/asteroid.png", Tile.STONE);
         background = new BoardObject(0,0,"dngh/images/bg.jpg", Tile.EMPTY);

         stoneColor = Color.black;
         
         addKeyListener(new Controls());
         footer.setText(getFooterMessage());
         setFocusable(true);
     }
     
     /******************************************************************/


     public static boolean isOnBoard(int x, int y) {
         return !(x<0 ||  x >= BOARD_WIDTH || y < 0
                  ||  y >= BOARD_HEIGHT);
     }

     /******************************************************************/
     
   @Override
   public void paintComponent(Graphics g) {
       super.paintComponent(g);
       doDrawing(g);
   }

     /******************************************************************/

    private void drawBoardObject(Graphics g, BoardObject bo) {
        g.drawImage(bo.img, bo.x*SQUARE_LEN, bo.y*SQUARE_LEN, null);
        for(int i=bo.x; i< bo.x+bo.getWidth();i++)
            for(int j=bo.y; j< bo.y+bo.getHeight();j++) 
                board[j][i]  = bo.tile;
    }


     /******************************************************************/
    
     
     private void  drawGrid(Graphics g) {
         var size = getSize();
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            int y= i * SQUARE_LEN;
            for (int j = 0; j <= BOARD_WIDTH; j++) {
                int x= j * SQUARE_LEN;                
                g.drawRect(x, getTop()+y, SQUARE_LEN, SQUARE_LEN);
            }
        }
     }


     /******************************************************************/
     
     private void drawSquare(Graphics g, int x, int y, Color c) {
          x= x * SQUARE_LEN + 1;
          y= y * SQUARE_LEN + 1 + getTop(); 
          int a = SQUARE_LEN -1;
          int b = SQUARE_LEN - 1;
          Color t = g.getColor();
          g.setColor(c);
          g.fillRect(x,y, a,b);
          g.setColor(t);
     }
                                                        
     /******************************************************************/
     
     private int getTop() {
         return (int) getSize().getHeight() - BOARD_HEIGHT * SQUARE_LEN;
     }
     
     
     /******************************************************************/

     private void drawAsteroids(Graphics g) {
         for (int i = 0; i < BOARD_HEIGHT; i++)
             for (int j = 0; j < BOARD_WIDTH; j++)
                 if(board[i][j]==Tile.STONE) 
                     g.drawImage(asteroid.img, j*SQUARE_LEN, i*SQUARE_LEN, null);
     }
     
     /******************************************************************/

     private void doDrawing(Graphics g) {
         if(over) gameOver();
         g.drawImage(background.img, 0, 0, null);
         drawAsteroids(g);
         drawBoardObject(g,player);
         //drawGrid(g);
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
             if( board[BOARD_HEIGHT-1][i] == Tile.STONE)
                 board[BOARD_HEIGHT-1][i] = Tile.EMPTY;
         
         
         for(int i=0; i < BOARD_WIDTH; i++) 
             for(int j=BOARD_HEIGHT-2; j >= 0; j--) 
                 if (board[j][i] == Tile.STONE) {
                     board[j][i] = Tile.EMPTY;
                     board[j+1][i] = Tile.STONE;
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
                 board[0][i] = Tile.STONE;
         }
         
         if(ticks==20*HEIGHT) {
             level+=1;
             ticks=0;
         }
         
         footer.setText(getFooterMessage());

         if(board[player.y][player.x] == Tile.STONE) over = true;          
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
         int py = player.y;
         int px = player.x;
         if(over) return;
         switch (d) {
         case DOWN  -> { if(py< (BOARD_HEIGHT-1))  board[py++][px] = Tile.EMPTY; }
         case UP    -> { if(py > 0) board[py--][px] = Tile.EMPTY; }
         case LEFT  -> { if(px > 0) board[py][px--] = Tile.EMPTY; }
         case RIGHT -> { if(px < (BOARD_WIDTH-1)) board[py][px++] = Tile.EMPTY; }
         }
         if (board[py][px] == Tile.STONE) over = true;
         board[py][px] = Tile.PLAYER;
         player.x = px;
         player.y = py;
         
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
