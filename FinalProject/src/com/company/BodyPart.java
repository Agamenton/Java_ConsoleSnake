package com.company;

public class BodyPart
{
    public int x;
    public int y;
    public char c;
    public Direction dir;

    private int timer;

    public BodyPart(int x, int y, char c, Direction d, int timer)
    {
        this.x = x;
        this.y = y;
        this.c = c;
        this.dir = d;
        this.timer = timer;
    }

    public int canMoveIn()
    {
        return timer;
    }

    public void waitLess()
    {
        timer -= 1;
    }
}
