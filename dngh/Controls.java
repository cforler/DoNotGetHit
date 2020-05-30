package dngh;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class Controls extends KeyAdapter {
    Board board;

    private enum Direction { UP, DOWN, LEFT, RIGHT };
    
    public Controls(Board board) {
        this.board = board;
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
        case KeyEvent.VK_P: 
        case KeyEvent.VK_SPACE: pause(); break;
        case KeyEvent.VK_W:
        case KeyEvent.VK_UP: move(Direction.UP); break;
        case KeyEvent.VK_S:
        case KeyEvent.VK_DOWN: move(Direction.DOWN); break;
        case KeyEvent.VK_A:
        case KeyEvent.VK_LEFT: move(Direction.LEFT); break;
        case KeyEvent.VK_D:
        case KeyEvent.VK_RIGHT: move(Direction.RIGHT); break;
        }
    }
    
    private void move(Direction d) {
        if(board.paused) return;
        int py = board.player.y;
        int px = board.player.x;
        if(board.over) return;

        switch (d) {
        case DOWN:
            if(py< (Board.BOARD_HEIGHT-2))
                board.board[py++][px] = Board.Tile.EMPTY;
            break;
        case UP:
            if(py > 0)
                board.board[py--][px] = Board.Tile.EMPTY;
            break;
        case LEFT:
            if(px > 0) board.board[py][px--] =
                                 Board.Tile.EMPTY;
            break;
        case RIGHT:
            if(px < (Board.BOARD_WIDTH-1))
                board.board[py][px++] = Board.Tile.EMPTY;
            break;
        }
        if (board.board[py][px] == Board.Tile.STONE) board.over = true;
        board.board[py][px] = Board.Tile.PLAYER;
        board.player.x = px;
        board.player.y = py;
    }

    private void pause() {
        board.paused = !board.paused;
        if(board.paused)
            board.footer.setText("Paused " + board.getFooterMessage());
        else
            board.footer.setText(board.getFooterMessage());
    }
}
