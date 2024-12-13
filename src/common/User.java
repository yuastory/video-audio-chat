package common;

import java.io.Serializable;

public class User implements Serializable
{
    private static final long serialVersionUID = 5566L;
    final String              userId;
    final String              ip;
    int                       position;                                                                                                                                                                    // 포트, 위치 결정
    // startingPort 해당 유저의 포트 시작 대역(Maximum 6명이니 position은 0-5 사이가 됨). 상대방 포트 계산하기 - startingPort + 상대방position -> 1:N 통신에서 상대방과 1:1 포트가 나옴.
    boolean                   calling;
//    Room                      room;

    public User(String ip, String userId)
    {
        this.ip = ip;
        this.userId = userId;
        calling = false;
//        room = null;
    }

//    public void enterRoom(Room room)
//    {
//        this.room = room;
        // this.position = room.findEmptyPosition();
        // startingPort = Preferences.PORT_VIDEO_DEFAULT * position;
//        calling = true;
//    }

//    public void exitRoom()
//    {
//        if (room != null)
//            room = null;
//        calling = false;
//    }

    /* Getters and Setters */
    public String getIp()
    {
        return ip;
    }

    public String getUserId()
    {
        return userId;
    }

    public int getPosition()
    {
        return position;
    }

    public void setPosition(int position)
    {
        this.position = position;
    }

    public boolean isCalling()
    {
        return calling;
    }

    public void setCalling(boolean calling)
    {
        this.calling = calling;
    }

//    public Room getRoom()
//    {
//        return room;
//    }
//
//    public void setRoom(Room room)
//    {
//        this.room = room;
//    }

    @Override
    public String toString()
    {
        return userId;
    }

}
