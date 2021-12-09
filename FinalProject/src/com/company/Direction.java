package com.company;

public enum Direction
{
    // Used binary representation, so I can easily and quickly check if changing directions is allowed
    // changing directions is allowed only if old.dir & new.dir != 0
    up    (0b10001),
    down  (0b01010),
    left  (0b00011),
    right (0b11000);

    private final int coding;

    Direction(int coding)
    {
        this.coding = coding;
    }

    public int getCoding()
    {
        return coding;
    }
}
