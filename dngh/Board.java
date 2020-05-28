package dngh; 

import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.util.Random;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
        
 class Board extends JPanel {
     enum Tile { EMPTY, PLAYER, STONE };
     private enum Direction { UP, DOWN, LEFT, RIGHT };

     static  final int SQUARE_LEN = 30;
     private static final int BOARD_WIDTH  = 20;
     private static final int BOARD_HEIGHT = 30;
     private static final int INTERVAL     =  8;
     private static final int BASIC_SPAWN_CHANCE  = 5;
     private static final int BIG_SPAWN_CHANCE    = 5;
     
     private Tile[][] board;

     private boolean over; 
     private boolean paused;
     private Random rand;
     private Timer timer;
     private int level;
     private int ticks;
     private int points;
     private JLabel footer;
     private BoardObject player;
     private BoardObject explosion;
     private BoardObject background;

     List <BoardObject> asteroids;

     /******************************************************************/
     
     public Board(JLabel footer) {
         rand = new Random();
         this.footer = footer;
         explosion = new BoardObject(0,0,"dngh/images/explosion.png",
                                     Tile.PLAYER);
             
         background = new BoardObject(0,0,"dngh/images/bg.jpg", Tile.EMPTY);
         addKeyListener(new Controls());
         timer = new Timer(INTERVAL, new Ticks());
         initBoard();
     }
     
     
     private void initBoard(){
         over=false;
         paused=false;
         level=1;
         points=0;
         board = new  Tile[BOARD_HEIGHT][BOARD_WIDTH];
         player = new BoardObject( BOARD_WIDTH/2,BOARD_HEIGHT/2,
                                   "dngh/images/fairydust.png", Tile.PLAYER);
         asteroids = new CopyOnWriteArrayList<BoardObject>();
         footer.setText(getFooterMessage());
         setFocusable(true);
         start();
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
     
     private int getTop() {
         return (int) getSize().getHeight() - BOARD_HEIGHT * SQUARE_LEN;
     }
     
     
     /******************************************************************/

     private void drawAsteroids(Graphics g) {
         for(BoardObject a : asteroids) 
             g.drawImage(a.img, a.x, a.y, null);
     }
     
     /******************************************************************/

     private void doDrawing(Graphics g) {
         g.drawImage(background.img, 0, 0, null);
         drawAsteroids(g);
         if(over) {
             explosion.x = player.x;
             explosion.y = player.y;
             drawBoardObject(g,explosion);
         }
         else drawBoardObject(g,player);
     }
     /******************************************************************/

     public void start() {
        timer.start();
    }
     
     /******************************************************************/

     private void gameOver() {
         timer.stop();
         footer.setText("GAME OVER! " + getFooterMessage());
         JOptionPane.showMessageDialog(null, "GAME OVER!\n"+"Your score: "
                                       +points  ,"YOU GOT HIT",
                                       JOptionPane.WARNING_MESSAGE);
         initBoard();
         start();
     }
     

     /******************************************************************/

     private void fallingStones() {
         for(BoardObject a : asteroids) {
             a.y +=1;
             if( (a.y%SQUARE_LEN)==0) {
                 int x = a.x/SQUARE_LEN;
                 int y = a.y/SQUARE_LEN;
                 if(y>BOARD_HEIGHT-1) asteroids.remove(a);
                 else {
                     board[y-1][x] = Tile.EMPTY;
                     board[y][x] = Tile.STONE;
                     
                     if (a instanceof BigAsteroid) {
                         board[y-1][x+1] = Tile.EMPTY;
                         board[y][x+1]   = Tile.STONE;
                         board[y+1][x]   = Tile.STONE;
                         board[y+1][x+1] = Tile.STONE;
                     }
                 }
             }
             
         }
     }
     
     /******************************************************************/

     public void spawnAsteroids() {
         for(int i=0; i < BOARD_WIDTH; i++) {
             if (rand.nextInt(100) < (BASIC_SPAWN_CHANCE + level)) {
                 if((i< BOARD_WIDTH-1) &&
                    (rand.nextInt(100) < BIG_SPAWN_CHANCE)) {
                     asteroids.add(new BigAsteroid(i*SQUARE_LEN, 0));
                     board[0][i+1] = Tile.STONE;
                     board[1][i] = Tile.STONE;
                     board[1][i+1] = Tile.STONE;
                 }
                 else asteroids.add(new Asteroid(i*SQUARE_LEN, 0));
                 board[0][i] = Tile.STONE;
             }
         }
     }

     
     /******************************************************************/
     
     private void update() {
         if(paused || over) return;
         ticks +=1;

         fallingStones();

         if((ticks%100)==0) {
             points+=1*level;
             spawnAsteroids();
         }
         
         if((ticks % (1000*HEIGHT)) == 0)  level+=1;
         
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
         if(over) gameOver();
     }


     /******************************************************************/
     
     private void move(Direction d) {
         int py = player.y;
         int px = player.x;
         if(over) return;
         switch (d) {
         case DOWN  -> { if(py< (BOARD_HEIGHT-2))  board[py++][px] = Tile.EMPTY; }
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
