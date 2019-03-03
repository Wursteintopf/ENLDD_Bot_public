package de.wursteintopf.bot.chat;

import de.wursteintopf.bot.user.UserPermission;

public class Chat {
    private long uid;
    private String name;
    private String url;
    private UserPermission permissionNeeded;

    public Chat(long uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public Chat(long uid, String name, String url, UserPermission permissionNeeded) {
        this.uid = uid;
        this.name = name;
        this.url = url;
        this.permissionNeeded = permissionNeeded;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getUid() {
        return uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setPermissionNeeded(UserPermission permissionNeeded) {
        this.permissionNeeded = permissionNeeded;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return "[" + String.valueOf(uid) + "] " + name;
    }

    public String getUrl() {
        return url;
    }

    public UserPermission getPermissionNeeded() {
        return permissionNeeded;
    }
}
