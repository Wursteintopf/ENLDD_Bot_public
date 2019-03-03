package de.wursteintopf.bot.user;

import java.util.ArrayList;
import java.util.List;

public class User extends AbstractUser {
    private List<UserPermission> permissions;

    public User(long id, String userName) {
        super(id, userName);

        permissions = new ArrayList<>();
    }

    public void addPermission(UserPermission permission){
        if (!permissions.contains(permission)) permissions.add(permission);
    }

    public void changePermission(UserPermission permission){
        if (permissions.contains(permission)) {
            permissions.remove(permission);
        } else {
            permissions.add(permission);
        }
    }

    public boolean checkPermission(UserPermission permission){
        return permissions.contains(permission);
    }

    public List<UserPermission> getPermissions() {
        return permissions;
    }
}
