package com.company;

import javax.swing.*;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {


    public static void main(String[] args)
    {
        Menu mainMenu = new Menu("statistics.txt");
        mainMenu.mainMenu();
    }

}
