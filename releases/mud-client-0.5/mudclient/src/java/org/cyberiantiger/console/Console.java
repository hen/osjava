package org.cyberiantiger.console;

public interface Console {

    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int YELLOW = 2;
    public static final int GREEN = 3;
    public static final int CYAN = 4;
    public static final int BLUE = 5;
    public static final int MAGENTA = 6;
    public static final int WHITE = 7;

    public static final int BOLD = 1 << 16;
    public static final int FLASH = 1 << 17;
    public static final int REVERSE = 1 << 18;

    public static final int DEFAULT = (BLACK<<8) | WHITE;

    public int getBufferSize();

    public int getWidth();
    
    public int getHeight();

    public void resize(int width, int height);

    public void moveCursorX(int x);

    public void moveCursorY(int x);

    public int getCursorX();

    public int getCursorY();

    public void setCursorX(int x);

    public void setCursorY(int y);

    public void drawString(String str);

    public void drawString(char[] chars);

    public void drawString(char[] chars, int offset, int len);

    public void setForeground(int color);

    public void setBackground(int color);

    public void setBold(boolean bold);

    public void setFlash(boolean flash);

    public void setReverse(boolean reverse);

    public void clearScreen();

    public void addAction(ConsoleAction action);

    public void beep();

}
