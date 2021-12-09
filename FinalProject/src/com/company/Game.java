package com.company;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class Game implements KeyListener
{
    private Menu menu;

    private int score;
    private int winCondition; // TODO never used (probably remove it and have endless snake)


    private char[][] board;

    private LinkedList<BodyPart> snake = new LinkedList<>();

    private Apple apple;

    // TODO make variable constructor
    public Game(Menu delegate)
    {
        board = new char[20][20];
        score = 0;
        winCondition = 20;
        initSnake();
        this.menu = delegate;
        printBoard();
    }


    // ================== Auxiliary methods ==================

    private void initSnake()
    {
        snake.add(new BodyPart(3,1,'@',Direction.right,0));
        snake.add(new BodyPart(2,1,'o',Direction.right,0));
        snake.add(new BodyPart(1,1,'o',Direction.right,0));
    }


    public BodyPart getHead()
    {
        return snake.getFirst();
    }



    private boolean isCollision(int x, int y)
    {

        // if border is hit then true
        if(x == 0 || x == board.length-1 || y == 0 || y == board[0].length-1)
        {
            return true;
        }
        else
        {
            for(int i = 1; i < snake.size(); i++)
            {
                // if any body-part is hit, then true
                BodyPart bp = snake.get(i);
                if(x == bp.x && y == bp.y)
                {
                    return true;
                }
            }

            // nothing 's been hit
            return false;
        }
    }

    public int getScore()
    {
        return score;
    }

    private void gameOver()
    {
        rememberScore(score,true);
        menu.mainMenu();
    }

    private boolean isGameOver()
    {
        return isCollision(getHead().x, getHead().y);
    }


    private boolean isVictory()
    {
        return score == winCondition;
    }



    // ================ Apple handling ================

    private void spawnApple()
    {
        // only spawn apple if it doesnt exist
        if(apple == null)
        {
            int randomX = ThreadLocalRandom.current().nextInt(1, board.length);
            int randomY = ThreadLocalRandom.current().nextInt(1, board.length);

            // if random coordinates would spawn apple in wall or in snake, try again
            if(isCollision(randomX, randomY))
            {
                spawnApple();
            }
            else
            {
                apple = new Apple(randomX,randomY,'A');
            }
        }
    }


    private void despawnApple()
    {
        apple = null;
    }


    private boolean didSnakeEatApple()
    {
        if(apple != null)
        {
            return getHead().x == apple.x && getHead().y == apple.y;
        }
        else
        {
            return false;
        }
    }



    // =============== Snake handling =============


    public void changeDirection(Direction newDir)
    {
        Direction oldDir = getHead().dir;

        // but head cant change direction by 180°
        if((oldDir.getCoding() & newDir.getCoding()) != 0)  // if new direction is valid
        {
            getHead().dir = newDir;
        }
        // so in case the head wanted to turn by 180, then just move in the direction head is looking
        else
        {
            getHead().dir = oldDir;
        }
    }


    public void moveSnake()
    {
        // body always moves the same way
        moveBody();
        moveHead();

        // if collision occurred after we moved the snake, then it's game over
        if(isCollision(getHead().x, getHead().y))
        {
            gameOver();
        }
        // if move was valid
        else
        {
            // check if snake ate the apple
            if(didSnakeEatApple())
            {
                despawnApple();
                enlargeSnake();
                increaseScore();
            }
        }
    }


    // moves every part of the snake except the head
    // from tail, every body part moves to where is the next body part
    private void moveBody()
    {
        for(int i = snake.size()-1; i > 0; i--)     // in reverse, we move body parts forward
        {
            BodyPart next = snake.get(i-1);
            BodyPart current = snake.get(i);
            if(current.canMoveIn() == 0)        // only move body part if it can move
            {
                current.x = next.x;
                current.y = next.y;
            }
            else                                // otherwise, the body part can wait less
            {
                current.waitLess();
            }
        }
    }


    private void moveHead()
    {
        BodyPart head = getHead();
        switch (head.dir)
        {
            case up -> head.y--;
            case down -> head.y++;
            case right -> head.x++;
            case left -> head.x--;
        }
    }


    private void enlargeSnake()
    {
        BodyPart newTail = new BodyPart(getHead().x, getHead().y, 'o',Direction.right, snake.size());
        snake.add(newTail);
    }



    // =============== Score handling =============

    private void increaseScore()
    {
        score += 1;
        rememberScore(score,false);
    }



    private void rememberScore(int score, boolean gameOver)
    {
        menu.ev_scoreChange(score, gameOver);
    }



    // ============== Printing  ==============

    private void fillBoard()
    {

        // default
        for (char[] chars : board)
        {
            Arrays.fill(chars, '.');
        }

        // TODO :probably make method, board might not be square
        // walls
        for(int i = 0; i < board.length; i++)
        {
            board[0][i] = '#';
            board[board.length-1][i] = '#';

            board[i][0] = '#';
            board[i][board.length-1] = '#';
        }

        // apple
        spawnApple(); // <- does nothing if apple already exists
        board[apple.x][apple.y] = apple.c;

        fillSnake();
    }

    private void fillWalls()
    {
        // TODO
    }

    // fills board with snake body parts in reverse order
    // (in reverse because, when Apple is eaten, then tail over-drew head)
    private void fillSnake()
    {
        for(int i = snake.size()-1; i >= 0; i--)
        {
            board[snake.get(i).x][snake.get(i).y] = snake.get(i).c;
        }
    }



    public void printBoard()
    {
        System.out.println("Score = " + score);
        fillBoard();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[i].length; j++)
            {
                sb.append(board[j][i]);
            }
            sb.append(System.lineSeparator());
        }
        System.out.println(sb.toString());
    }



    // ============== KeyListener Interface ==============

    @Override
    public void keyPressed(KeyEvent e)
    {
        switch (e.getKeyCode())
        {
            case KeyEvent.VK_UP -> {
                changeDirection(Direction.up);
                //printBoard();
            }
            case KeyEvent.VK_DOWN -> {
                changeDirection(Direction.down);
                //printBoard();
            }
            case KeyEvent.VK_LEFT -> {
                changeDirection(Direction.left);
                //printBoard();
            }
            case KeyEvent.VK_RIGHT -> {
                changeDirection(Direction.right);
                //printBoard();
            }
            case KeyEvent.VK_X -> {
                gameOver();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


}
