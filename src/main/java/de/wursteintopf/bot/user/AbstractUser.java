package de.wursteintopf.bot.user;

public abstract class AbstractUser {
    private long id;
    private String userName;

    public AbstractUser(long id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }
}
