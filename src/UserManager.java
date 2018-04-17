import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private JSONArray loadJson(String filename){
        try {
            JSONParser parser = new JSONParser();
            JSONArray whitelist = (JSONArray) parser.parse(new FileReader("/path/to/json/" + filename));
            return whitelist;
        } catch (Exception e) {
            e.printStackTrace();
            JSONArray whitelist = new JSONArray();
            return whitelist;
        }

    }

    private void saveJson(JSONArray json, String filename){
        try (FileWriter file = new FileWriter("/path/to/json/" + filename)) {
            file.write(json.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkWhitelist(long check_id){
        JSONArray whitelist = loadJson("whitelist.json");
        for (Object o : whitelist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            if (user_id == check_id) {
                return true;
            }
        }
        return false;
    }

    public boolean checkBlocklist(long check_id){
        JSONArray blocklist = loadJson("blocklist.json");
        for (Object o : blocklist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            if (user_id == check_id) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAdmin(long check_id){
        JSONArray whitelist = loadJson("whitelist.json");
        for (Object o : whitelist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            boolean admin = (boolean) user.get("admin");
            if (user_id == check_id && admin) {
                return true;
            }
        }
        return false;
    }

    public boolean checkAttribute(long check_id, String attribute){
        JSONArray whitelist = loadJson("whitelist.json");
        for (Object o : whitelist) {
            JSONObject user = (JSONObject) o;
            long user_id = (long) user.get("id");
            boolean bool = (boolean) user.get(attribute);
            if (user_id == check_id && bool) {
                return true;
            }
        }
        return false;
    }

    public List<User> buildAdminList() {
        JSONArray whitelist = loadJson("whitelist.json");
        List<User> admins = new ArrayList<>();
        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            String user_name = (String) userjson.get("user_name");
            boolean admin = (boolean) userjson.get("admin");


            if (admin) {
                User user = loadUser(user_name);
                admins.add(user);
            }
        }
        return admins;
    }

    public List<User> buildUserList() {
        JSONArray whitelist = loadJson("whitelist.json");
        List<User> users = new ArrayList<>();

        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            String user_name = (String) userjson.get("user_name");
            boolean guest = (boolean) userjson.get("guest");

            if (!guest) {
                User user = loadUser(user_name);
                users.add(user);
            }
        }
        return users;
    }

    public List<User> buildGuestList() {
        JSONArray whitelist = loadJson("whitelist.json");
        List<User> users = new ArrayList<>();

        for (Object o : whitelist) {
            JSONObject userjson = (JSONObject) o;
            String user_name = (String) userjson.get("user_name");
            boolean guest = (boolean) userjson.get("guest");

            if (guest) {
                User user = loadUser(user_name);
                users.add(user);
            }
        }
        return users;
    }

    public List<User> buildBlockList() {
        JSONArray blocklist = loadJson("blocklist.json");
        List<User> users = new ArrayList<>();

        for (Object o : blocklist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");
            String user_name = (String) userjson.get("user_name");
            users.add(new User(user_id, user_name));
        }
        return users;
    }

    public void whitelistUser(User user) {
        JSONArray whitelist = loadJson("whitelist.json");
        JSONArray blocklist = loadJson("blocklist.json");
        JSONArray new_blocklist = new JSONArray();

        //Load id and username
        long id = user.getId();
        String user_name = user.getUser_name();
        boolean admin = user.getAdmin();
        boolean mod = user.getMod();
        boolean operator = user.getOperator();
        boolean hightrust = user.getHightrust();
        boolean guest = user.getGuest();

        // Make sure, that the user you want to whitelist is not on the blocklist
        for (Object o : blocklist) {
            JSONObject userjson = (JSONObject) o;
            long userjson_id = (long) userjson.get("id");
            if (userjson_id != id) {
                new_blocklist.add(userjson);
            }
        }

        // Save user to whitelist
        JSONObject new_user = new JSONObject();
        new_user.put("id", id);
        new_user.put("user_name", user_name);
        new_user.put("admin", admin);
        new_user.put("mod", mod);
        new_user.put("operator", operator);
        new_user.put("hightrust", hightrust);
        new_user.put("guest", guest);
        whitelist.add(new_user);

        saveJson(whitelist, "whitelist.json");
        saveJson(new_blocklist, "blocklist.json");
    }

    public void blockUser(User user) {
        JSONArray whitelist = loadJson("whitelist.json");
        JSONArray blocklist = loadJson("blocklist.json");
        JSONArray new_whitelist = new JSONArray();

        //Load id and username
        long id = user.getId();
        String user_name = user.getUser_name();

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

        saveJson(new_whitelist, "whitelist.json");
        saveJson(blocklist, "blocklist.json");
    }

    public void unblock(long id) {
        JSONArray blocklist = loadJson("blocklist.json");
        JSONArray new_blocklist = new JSONArray();

        for (Object o : blocklist) {
            JSONObject userjson = (JSONObject) o;
            long user_id = (long) userjson.get("id");

            if (user_id != id) {
                new_blocklist.add(userjson);
            }
        }
        saveJson(new_blocklist, "blocklist.json");
    }

    public User loadUser(String user_name) {
        JSONArray whitelist = loadJson("whitelist.json");

        for (Object o : whitelist) {
            JSONObject search = (JSONObject) o;
            String search_name = (String) search.get("user_name");
            long search_id = (long) search.get("id");
            boolean admin = (boolean) search.get("admin");
            boolean mod = (boolean) search.get("mod");
            boolean operator = (boolean) search.get("operator");
            boolean hightrust = (boolean) search.get("hightrust");
            boolean guest = (boolean) search.get("guest");

            if (user_name.equalsIgnoreCase(search_name)) {
                User user = new User(search_id, search_name);
                user.setAdmin(admin);
                user.setMod(mod);
                user.setOperator(operator);
                user.setHightrust(hightrust);
                user.setGuest(guest);
                return user;
            }
        }
        return null;
    }

    public User loadBlockedUser(String user_name) {
        JSONArray blocklist = loadJson("blocklist.json");

        for (Object o : blocklist) {
            JSONObject search = (JSONObject) o;
            String search_name = (String) search.get("user_name");
            long search_id = (long) search.get("id");

            if (user_name.equalsIgnoreCase(search_name)) {
                User user = new User(search_id, search_name);
                return user;
            }
        }
        return null;
    }

    public void saveUser(User user) {
        JSONArray whitelist = loadJson("whitelist.json");
        JSONArray new_whitelist = new JSONArray();

        for (Object o : whitelist) {
            JSONObject search = (JSONObject) o;
            long search_id = (long) search.get("id");

            if (search_id != user.getId()) {
                new_whitelist.add(search);
            } else {
                search.replace("user_name", user.getUser_name());
                search.replace("admin", user.getAdmin());
                search.replace("mod", user.getMod());
                search.replace("operator", user.getOperator());
                search.replace("hightrust", user.getHightrust());
                search.replace("guest", user.getGuest());
                new_whitelist.add(search);
            }
        }

        saveJson(new_whitelist, "whitelist.json");
    }
}
