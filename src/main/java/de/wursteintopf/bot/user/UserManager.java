package de.wursteintopf.bot.user;

import java.util.ArrayList;
import java.util.List;

import de.wursteintopf.bot.utils.jsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserManager {

    private static Logger log = LoggerFactory.getLogger(UserManager.class);

    private static final String WHITELIST_FILENAME = "whitelist.json";
    private static final String BLOCKLIST_FILENAME = "blocklist.json";

    //Method to whitelist new users
    public void whitelistUser(AbstractUser user) {
        log.info("Adding user " + user.getUserName() + " to whitelist");
        JSONArray whitelist = jsonUtils.loadJson(WHITELIST_FILENAME);
        JSONArray blocklist = jsonUtils.loadJson(BLOCKLIST_FILENAME);

        //Check if user is not already on whitelist
        for (Object o : whitelist) {
            JSONObject test = (JSONObject) o;
            long testId = (long) test.get("id");
            if (testId == user.getId()) {
                return;
            }
        }

        //Check if user is not already on blocklist
        for (Object o : blocklist) {
            JSONObject test = (JSONObject) o;
            long testId = (long) test.get("id");
            if (testId == user.getId()) {
                return;
            }
        }

        // Save user to whitelist
        JSONObject new_user = new JSONObject();
        new_user.put("id", user.getId());
        new_user.put("user_name", user.getUserName());

        if (user instanceof User) {
            new_user.put("role", "User");
            new_user.put("permissions", new JSONArray());
        } else {
            new_user.put("role", "Guest");
        }

        whitelist.add(new_user);

        jsonUtils.saveJson(whitelist, WHITELIST_FILENAME);
    }

    //Method to save a changed User
    public void changeUser(AbstractUser user) {
        log.info("Changing user " + user.getUserName());
        JSONArray whitelist = jsonUtils.loadJson(WHITELIST_FILENAME);
        JSONArray new_whitelist = new JSONArray();

        for (Object o : whitelist) {
            JSONObject search = (JSONObject) o;
            long searchId = (long) search.get("id");

            if (searchId == user.getId()) {
                JSONObject changed = new JSONObject();

                changed.put("id", user.getId());
                changed.put("user_name", user.getUserName());
                if (user instanceof Admin) {
                    changed.put("role", "Admin");
                } else if (user instanceof Guest) {
                    changed.put("role", "Guest");
                } else {
                    User isUser = (User) user;
                    JSONArray permissions = new JSONArray();

                    for (UserPermission permission : isUser.getPermissions()) {
                        permissions.add(permission.toString());
                    }

                    changed.put("role", "User");
                    changed.put("permissions", permissions);
                }

                new_whitelist.add(changed);
            } else {
                new_whitelist.add(search);
            }
        }

        jsonUtils.saveJson(new_whitelist, WHITELIST_FILENAME);
    }

    //Method to remove a user from the whitelist and write him on the blocklist
    public void blockUser(AbstractUser user) {
        log.info("Adding user " + user.getUserName() + " to blocklist");
        JSONArray whitelist = jsonUtils.loadJson(WHITELIST_FILENAME);
        JSONArray blocklist = jsonUtils.loadJson(BLOCKLIST_FILENAME);
        JSONArray new_whitelist = new JSONArray();

        //Load id and username
        long id = user.getId();
        String user_name = user.getUserName();

        // Make sure, that the user you want to block gets removed from whitelist
        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            long userjson_id = (long) userjson.get("id");
            if (userjson_id != id) {
                new_whitelist.add(userjson);
            }
        }

        // Save user to blocklist
        JSONObject new_user = new JSONObject();
        new_user.put("id", id);
        new_user.put("user_name", user_name);
        blocklist.add(new_user);

        jsonUtils.saveJson(new_whitelist, WHITELIST_FILENAME);
        jsonUtils.saveJson(blocklist, BLOCKLIST_FILENAME);
    }

    //Method to unblock a user
    public void unblock(long id) {
        log.info("Removing user " + id + " from blocklist");
        JSONArray blocklist = jsonUtils.loadJson("blocklist.json");
        JSONArray new_blocklist = new JSONArray();

        for (Object o : blocklist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");

            if (user_id != id) {
                new_blocklist.add(userjson);
            }
        }
        jsonUtils.saveJson(new_blocklist, "blocklist.json");
    }

    //Method to check, if a user is on the whitelist
    public boolean checkWhitelist(long check_id) {
        log.info("Checking if user " + check_id + " is on whitelist");
        JSONArray whitelist = jsonUtils.loadJson(WHITELIST_FILENAME);
        for (Object o : whitelist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            if (user_id == check_id) {
                return true;
            }
        }
        return false;
    }

    //Method to check, if a user is on the blocklist
    public boolean checkBlocklist(long check_id) {
        log.info("Checking if user " + check_id + " is on blocklist");
        JSONArray blocklist = jsonUtils.loadJson(BLOCKLIST_FILENAME);
        for (Object o : blocklist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            if (user_id == check_id) {
                return true;
            }
        }
        return false;
    }

    //Method to check, if a user is an Admin
    public boolean checkAdmin(long check_id) {
        log.info("Checking if user " + check_id + " is admin");
        JSONArray whitelist = jsonUtils.loadJson(WHITELIST_FILENAME);
        for (Object o : whitelist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            String role = (String) user.get("role");

            if (user_id == check_id && role.equals("Admin")) {
                return true;
            }
        }
        return false;
    }

    //Method to promote a user to an Admin
    public void promoteToAdmin(Admin admin) {
        log.info("Promoting " + admin.getUserName() + " to admin");
        JSONArray whitelist = jsonUtils.loadJson(WHITELIST_FILENAME);
        JSONArray new_whitelist = new JSONArray();
        for (Object o : whitelist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            if (user_id == admin.getId()) {
                JSONObject adminJSON = new JSONObject();
                adminJSON.put("id", admin.getId());
                adminJSON.put("user_name", admin.getUserName());
                adminJSON.put("role", "Admin");
                new_whitelist.add(adminJSON);
            } else {
                new_whitelist.add(user);
            }
        }
        jsonUtils.saveJson(new_whitelist, WHITELIST_FILENAME);
    }

    //Method to load all admins to a Userlist
    public List<Admin> buildAdminList() {
        log.info("Build admin list");
        JSONArray whitelist = jsonUtils.loadJson("whitelist.json");
        List<Admin> admins = new ArrayList<>();
        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");
            String user_name = (String) userjson.get("user_name");
            String role = (String) userjson.get("role");

            if (role.equals("Admin")) {
                admins.add(new Admin(user_id, user_name));
            }
        }
        return admins;
    }

    //Method to load all users to a Userlist
    public List<User> buildUserList() {
        log.info("Build user list");
        JSONArray whitelist = jsonUtils.loadJson("whitelist.json");
        List<User> users = new ArrayList<>();

        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");
            String user_name = (String) userjson.get("user_name");
            String role = (String) userjson.get("role");

            if (role.equals("User")) {
                JSONArray permissions = (JSONArray) userjson.get("permissions");
                User user = new User(user_id, user_name);

                for (Object p : permissions) {
                    String permissionString = (String) p;
                    UserPermission permission = UserPermission.valueOf(permissionString);
                    user.addPermission(permission);
                }

                users.add(user);
            }
        }
        return users;
    }

    //Method to load all guests to a Userlist
    public List<Guest> buildGuestList() {
        log.info("Build guest list");
        JSONArray whitelist = jsonUtils.loadJson("whitelist.json");
        List<Guest> guests = new ArrayList<>();

        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");
            String user_name = (String) userjson.get("user_name");
            String role = (String) userjson.get("role");

            if (role.equals("Guest")) {
                guests.add(new Guest(user_id, user_name));
            }
        }
        return guests;
    }

    //Method to load all blocked to a Userlist
    public List<Blocked> buildBlockList() {
        log.info("Build block list");
        JSONArray blocklist = jsonUtils.loadJson("blocklist.json");
        List<Blocked> blocked = new ArrayList<>();

        for (Object o : blocklist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");
            String user_name = (String) userjson.get("user_name");
            blocked.add(new Blocked(user_id, user_name));
        }
        return blocked;
    }

    //Load AbstractUser from whitelist
    public AbstractUser loadAbstractUser(String user_name) {
        log.info("Loading user " + user_name);
        JSONArray whitelist = jsonUtils.loadJson("whitelist.json");

        for (Object o : whitelist) {
            JSONObject search = (JSONObject) o;

            long search_id = (long) search.get("id");
            String search_name = (String) search.get("user_name");
            String role = (String) search.get("role");

            if (user_name.equalsIgnoreCase(search_name)) {
                if (role.equals("Admin")) {
                    return new Admin(search_id, search_name);
                } else if (role.equals("Guest")) {
                    return new Guest(search_id, search_name);
                } else {
                    JSONArray permissions = (JSONArray) search.get("permissions");
                    User user = new User(search_id, search_name);

                    for (Object p : permissions) {
                        String permissionString = (String) p;
                        UserPermission permission = UserPermission.valueOf(permissionString);
                        user.addPermission(permission);
                    }

                    return user;
                }
            }
        }
        return null;
    }

    //Load abstractUser by id
    public AbstractUser loadAbstractUserById(long userId) {
        log.info("Loading user by id " + userId);
        JSONArray whitelist = jsonUtils.loadJson("whitelist.json");

        for (Object o : whitelist) {
            JSONObject search = (JSONObject) o;

            long search_id = (long) search.get("id");
            String search_name = (String) search.get("user_name");
            String role = (String) search.get("role");

            if (search_id == userId) {
                if (role.equals("Admin")) {
                    return new Admin(search_id, search_name);
                } else if (role.equals("Guest")) {
                    return new Guest(search_id, search_name);
                } else {
                    JSONArray permissions = (JSONArray) search.get("permissions");
                    User user = new User(search_id, search_name);

                    for (Object p : permissions) {
                        String permissionString = (String) p;
                        UserPermission permission = UserPermission.valueOf(permissionString);
                        user.addPermission(permission);
                    }

                    return user;
                }
            }
        }
        return null;
    }

    //Method to load a blocker User
    public Blocked loadBlockedUser(String user_name) {
        log.info("Load blocked user " + user_name);
        JSONArray blocklist = jsonUtils.loadJson(BLOCKLIST_FILENAME);

        for (Object o : blocklist) {
            JSONObject search = (JSONObject) o;
            String search_name = (String) search.get("user_name");
            long search_id = (long) search.get("id");

            if (user_name.equalsIgnoreCase(search_name)) {
                return new Blocked(search_id, search_name);
            }
        }
        return null;
    }
}
