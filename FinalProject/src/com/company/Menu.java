package com.company;

import com.sun.jdi.event.ThreadStartEvent;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Scanner;

public class Menu
{
    Game game;
    private File statistics;
    JFrame jFrame;
    public boolean gameRunning = false;

    public Menu(String statisticsFilePath)
    {
        statistics = new File(statisticsFilePath);
        initStatistics();
    }

    private void initStatistics()
    {
        String[] lines = getStatistics().split(System.lineSeparator());
        if(lines.length != 3)
        {
            try
            {
                FileWriter fw = new FileWriter(statistics);
                String s =  "0" + System.lineSeparator() + "0" + System.lineSeparator() + "0" + System.lineSeparator();
                fw.write(s);
                fw.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }



    public void mainMenu()
    {
        Scanner s = new Scanner(System.in);
        String input;

            System.out.println("\n" +
                    "  ____              _        \n" +
                    " / ___| _ __   __ _| | _____ \n" +
                    " \\___ \\| '_ \\ / _` | |/ / _ \\\n" +
                    "  ___) | | | | (_| |   <  __/\n" +
                    " |____/|_| |_|\\__,_|_|\\_\\___|\n" +
                    "                             \n" +
                    "Enter N for NewGame\n" +
                    "Enter S to see Statistics\n" +
                    "Enter X to Exit");

            input = s.nextLine();
            input = input.toLowerCase(Locale.ROOT);
        switch (input)
        {
            case "s" -> {
                System.out.println("Last game score = " + getLastGameScore());
                System.out.println("All-time best score = " + getAllTimeBestScore());
                mainMenu();
            }

            case "n" -> {
                game = new Game(this);
                gameRunning = true;
                setupWindowForKeyListener(game);
                // Run thread that will move snake and refresh board
                Thread thread = new Thread(() ->
                {
                    while (gameRunning)
                    {
                        game.moveSnake();
                        game.printBoard();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();

            }

            case "x" -> {
                System.exit(0);
            }

            default -> {
                mainMenu();
            }
        }
    }


    // Returns 3 lines where 1st is current score, 2nd is last game score, 3rd is all-time best score
    // if exception, returns empty string
    public String getStatistics()
    {
        StringBuilder sb = new StringBuilder();
        try
        {
            Scanner myReader = new Scanner(statistics);

            while (myReader.hasNextLine())
            {
                sb.append(myReader.nextLine());
                sb.append(System.lineSeparator());
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            // do nothing
            return "";
        }
    }


    private int getAllTimeBestScore()
    {
        String stats = getStatistics();
        if(stats.equals(""))
        {
            return -1;
        }
        else
        {
            String[] lines = stats.split(System.lineSeparator());
            String bestScore = lines[2];
            try
            {
                return Integer.parseInt(bestScore);
            }
            catch (NumberFormatException ex)
            {
                return -1;
            }
        }
    }

    // TODO: this method is almost duplicate of "getAllTimeBestScore", try to merge it
    private int getLastGameScore()
    {
        String stats = getStatistics();
        if(stats.equals(""))
        {
            return -1;
        }
        else
        {
            String[] lines = stats.split(System.lineSeparator());
            String bestScore = lines[1];
            try
            {
                return Integer.parseInt(bestScore);
            }
            catch (NumberFormatException ex)
            {
                return -1;
            }
        }
    }



    // Reaction to events of score change (also reaction to event GameOver)
    public void ev_scoreChange(int score, boolean gameOver)
    {
        String stats = getStatistics();
        String[] lines = stats.split(System.lineSeparator());
        if(gameOver)    // MAIN reaction to GAME OVER event
        {
            gameRunning = false;
            lines[0] = "0";
            lines[1] = String.valueOf(score);
            closeWindowForKeyListener(game);
            if(score > Integer.parseInt(lines[2]))
            {
                lines[2] = String.valueOf(score);
            }
            writeScore(lines);
            System.out.println("\n" +
                    " #=======================================================# \n" +
                    " _____ ____  _      _____   ____  _     _____ ____ \n" +
                    "/  __//  _ \\/ \\__/|/  __/  /  _ \\/ \\ |\\/  __//  __\\\n" +
                    "| |  _| / \\|| |\\/|||  \\    | / \\|| | //|  \\  |  \\/|\n" +
                    "| |_//| |-||| |  |||  /_   | \\_/|| \\// |  /_ |    /\n" +
                    "\\____\\\\_/ \\|\\_/  \\|\\____\\  \\____/\\__/  \\____\\\\_/\\_\\\n" +
                    " #=======================================================# \n" +
                    "                                                   \n");
            mainMenu();
        }
        else // This else block exists only because one can improperly exit the game by ATL+F4 and we would still like to remember score
        {
            lines[0] = String.valueOf(score);
            if(score > Integer.parseInt(lines[2]))
            {
                lines[2] = String.valueOf(score);
            }
            writeScore(lines);
        }
    }

    private void writeScore(String[] lines)
    {
        try
        {
            FileWriter fw = new FileWriter(statistics);
            for(String s : lines)
            {
                fw.write(s + System.lineSeparator());
            }
            fw.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // setting up a JFrame to capture key events
    private void setupWindowForKeyListener(KeyListener k)
    {
        jFrame = new JFrame();
        jFrame.setVisible(true);
        jFrame.setSize(100, 100);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // FIXME: this cant be EXIT_ON_CLOSE because it terminates whole program, but DISPOSE also doesnt work as intended
        jFrame.addKeyListener(k);
        // TODO: force to foregorund
        //jFrame.setAlwaysOnTop(true); // <- doesnt work
        //jFrame.toFront(); // <- doesnt work
    }


    // Should work the same as pressing the 'X' button of the Window
    private void closeWindowForKeyListener(KeyListener k)
    {
        if(jFrame != null)
        {
            if(k != null)
            {
                jFrame.removeKeyListener(k);
            }
            jFrame.dispatchEvent(new WindowEvent(jFrame, WindowEvent.WINDOW_CLOSING));
        }
    }

}
