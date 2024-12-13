package common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Room implements Serializable
{
    private static final long serialVersionUID = 5567L;
    int                       number;
    String                    title;
    boolean                   recording;
    User                      host;
    int                       maxUserNum;
    int                       currentUserNum;
    String[]                  users;
    Map<String, User>         banned;

    public Room(String title, boolean recording, int maxUserNum)
    {
        this.title = title;
        this.recording = recording;
        this.maxUserNum = maxUserNum;
        users = new String[maxUserNum];
        banned = new ConcurrentHashMap<>();

    }

    public int getUserPosition(String userId)
    {
        for (int i = 0; i < users.length; i++)
            if (users[i].equals(userId))
                return i;
        return -1;
    }

    /**
     * 해당 유저가 이 방에 참여하고 있는지 확인
     * @param userId
     * @return
     */
    public boolean participateIn(String userId)
    {
        for (String id : users)
        {
            if (id == null)
                continue;
            if (id.equals(userId))
                return true;
        }
        return false;
    }

    public boolean isBanned(User user)
    {
        return banned.containsKey(user.userId);
    }

    public void ban(User user)
    {
        banned.put(user.userId, user);
    }

    public User addUser(User user)
    {
        int position = findEmptyPosition();
        if (position == -1)
            return null;
        users[position] = user.getUserId();
        user.setPosition(position);
        user.setCalling(true);
        if (currentUserNum++ == 0)
            host = user;
        return user;
    }

    public User removeUser(User user)
    {
        for (int i = 0; i < users.length; i++)
        {
            if (users[i] != null && users[i].equals(user.userId))
            {
                users[i] = null;
                user.setCalling(false);
                // user.setPosition(-1);
                currentUserNum--;
                return user;
            }
        }
        return null;
    }

    public int findEmptyPosition()
    {
        int position = -1;
        for (int i = 0; i < users.length; i++)
        {
            if (users[i] == null)
            {
                position = i;
                break;
            }
        }
        return position;
    }

    /* Getters And Setters */
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public boolean isRecording()
    {
        return recording;
    }

    public void setRecording(boolean recording)
    {
        this.recording = recording;
    }

    public User getHost()
    {
        return host;
    }

    public void setHost(User host)
    {
        this.host = host;
    }

    public int getMaxUserNum()
    {
        return maxUserNum;
    }

    public void setMaxUserNum(int maxUserNum)
    {
        this.maxUserNum = maxUserNum;
    }

    public int getCurrentUserNum()
    {
        return currentUserNum;
    }

    public void setCurrentUserNum(int currentUserNum)
    {
        this.currentUserNum = currentUserNum;
    }

    public String[] getUsers()
    {
        return users;
    }
    /* Getters And Setters Ends */
}
