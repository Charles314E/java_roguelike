package com;

import com.GUI.GUI;

public class main {
    //Run the applications, passing a window with and height of 1024 and 720.
    public static void main(String[] args) {
        GUI gui = new GUI("Game made by Charles Knight", 1024, 720, 11, 7);
        gui.setVisible(true);
    }
}