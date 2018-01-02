package com.example.kaya.billtech;

/**
 * Created by KAYA on 3.12.2017.
 */

public class BoardClass {
    private final String boardName;
    private final String boardLocationx;
    private final String boardLocationy;

    public String getBoardName() {
        return boardName;
    }

    public String getBoardLocationx() {
        return boardLocationx;
    }

    public String getBoardLocationy() {
        return boardLocationy;
    }

    public BoardClass(String boardName, String boardLocationx, String boardLocationy) {
        this.boardName = boardName;
        this.boardLocationx = boardLocationx;
        this.boardLocationy = boardLocationy;

    }

}
