package de.wursteintopf.bot.chat;

import java.util.ArrayList;
import java.util.List;

import de.wursteintopf.bot.utils.jsonUtils;
import de.wursteintopf.bot.user.UserPermission;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatManager {

    private static Logger log = LoggerFactory.getLogger(ChatManager.class);
    private static final String CHATS_FILENAME = "chats.json";

    //Add a new Chat Category
    public void addCategory(ChatCategory category) {
        log.info("Adding chat category " + category.getName());
        JSONArray categories = jsonUtils.loadJson(CHATS_FILENAME);

        //Check if the uid is actually unique
        if (checkCategoryUID(category.getUid())) {
            return;
        }

        JSONObject newCategory = new JSONObject();
        newCategory.put("uid", category.getUid());
        newCategory.put("name", category.getName());
        newCategory.put("chats", new JSONArray());

        categories.add(newCategory);

        jsonUtils.saveJson(categories, CHATS_FILENAME);
    }

    //Load a List with all Chat Categories
    public List<ChatCategory> loadCategories() {
        log.info("Loading chat categories from file");
        JSONArray categoriesJson = jsonUtils.loadJson(CHATS_FILENAME);
        List<ChatCategory> categories = new ArrayList<>();

        for (Object o : categoriesJson) {
            JSONObject category = (JSONObject) o;
            long uid = (long) category.get("uid");
            String name = (String) category.get("name");

            categories.add(new ChatCategory(uid, name));
        }

        return categories;
    }

    public ChatCategory loadCategory(long uid) {
        log.info("Loading chat category by uid " + uid);
        JSONArray categories = jsonUtils.loadJson(CHATS_FILENAME);

        for (Object o : categories) {
            JSONObject category = (JSONObject) o;
            long searchUid = (long) category.get("uid");

            if (searchUid == uid) {
                String name = (String) category.get("name");
                JSONArray chatsJson = (JSONArray) category.get("chats");
                List<Chat> chats = new ArrayList<>();

                for (Object c : chatsJson) {
                    JSONObject chatJson = (JSONObject) c;
                    long chatUid = (long) chatJson.get("uid");
                    String chatName = (String) chatJson.get("name");
                    String chatUrl = (String) chatJson.get("url");
                    String permissionJson = (String) chatJson.get("permissionNeeded");
                    UserPermission permissionNeeded = UserPermission.valueOf(permissionJson);

                    chats.add(new Chat(chatUid, chatName, chatUrl, permissionNeeded));
                }

                ChatCategory found = new ChatCategory(uid, name);
                found.setChatList(chats);

                return found;
            }
        }
        return null;
    }

    public void deleteCategory(long uid) {
        JSONArray categories = jsonUtils.loadJson("chats.json");
        JSONArray newCategories = new JSONArray();

        for (Object o : categories) {
            JSONObject category = (JSONObject)o;
            long searchUid = (Long) category.get("uid");

            if (searchUid != uid) { newCategories.add(category);
            }
        }
        jsonUtils.saveJson(newCategories, "chats.json");
    }

    //Check if a ChatCategory uid already exists
    public boolean checkCategoryUID(long uid) {
        log.info("Checking if chat category " + uid + " already exists");
        JSONArray categories = jsonUtils.loadJson(CHATS_FILENAME);

        for (Object o : categories) {
            JSONObject category = (JSONObject) o;
            long searchUid = (long) category.get("uid");

            if (uid == searchUid) {
                return true;
            }
        }
        return false;
    }

    public boolean checkChatUID(long uid) {
        JSONArray categories = jsonUtils.loadJson("chats.json");

        for (Object o : categories) {
            JSONObject category = (JSONObject)o;
            JSONArray chats = (JSONArray)category.get("chats");

            for (Object c : chats) {
                JSONObject chat = (JSONObject) c;
                long chatUid = (long) chat.get("uid");

                if (uid == chatUid) return true;
            }
        }
        return false;
    }

    //Get biggest chatCategory Uid
    public long getBiggestChatCategoryUid() {
        log.info("Get biggest chat category uid");
        JSONArray categories = jsonUtils.loadJson(CHATS_FILENAME);
        long biggestUid = 0;

        for (Object o : categories) {
            JSONObject category = (JSONObject) o;
            long uid = (long) category.get("uid");
            if (uid > biggestUid) {
                biggestUid = uid;
            }
        }

        return biggestUid;
    }

    public void saveChat(Chat chat, long categoryUid)
    {
        JSONArray categories = jsonUtils.loadJson("chats.json");

        for (Object o : categories) {
            JSONObject category = (JSONObject)o;
            JSONArray chats = (JSONArray)category.get("chats");
            long searchCategoryUid = (Long) category.get("uid");

            if (searchCategoryUid == categoryUid) {
                for (Object c : chats) {
                    JSONObject searchChat = (JSONObject)c;
                    long searchChatUid = (Long) searchChat.get("uid");

                    if (searchChatUid == chat.getUid()) {
                        searchChat.put("uid", chat.getUid());
                        searchChat.put("name", chat.getName());
                        searchChat.put("url", chat.getUrl());
                        searchChat.put("permissionNeeded", chat.getPermissionNeeded().toString());
                        jsonUtils.saveJson(categories, "chats.json");
                        return;
                    }
                }
                JSONObject chatJson = new JSONObject();
                chatJson.put("uid", chat.getUid());
                chatJson.put("name", chat.getName());
                chatJson.put("url", chat.getUrl());
                chatJson.put("permissionNeeded", chat.getPermissionNeeded().toString());
                chats.add(chatJson);
            }
        }
        jsonUtils.saveJson(categories, "chats.json");
    }

    public Chat loadChatFromCategory(long chatUid, long categoryUid)
    {
        JSONArray categories = jsonUtils.loadJson("chats.json");

        for (Object o : categories) {
            JSONObject category = (JSONObject)o;
            long searchCategoryUid = (Long) category.get("uid");

            if (searchCategoryUid == categoryUid) {
                JSONArray chats = (JSONArray)category.get("chats");

                for (Object c : chats) {
                    JSONObject chat = (JSONObject)c;
                    long searchChatUid = (Long) chat.get("uid");

                    if (searchChatUid == chatUid) {
                        String name = (String)chat.get("name");
                        String url = (String)chat.get("url");
                        String permissionString = (String)chat.get("permissionNeeded");
                        UserPermission permissionNeeded = UserPermission.valueOf(permissionString);

                        return new Chat(chatUid, name, url, permissionNeeded);
                    }
                }
            }
        }
        return null;
    }

    public void deleteChat(long chatUid)
    {
        JSONArray categories = jsonUtils.loadJson("chats.json");

        for (Object o : categories) {
            JSONObject category = (JSONObject)o;
            JSONArray chats = (JSONArray)category.get("chats");
            JSONArray newChats = new JSONArray();

            for (Object c : chats) {
                JSONObject searchChat = (JSONObject)c;
                long searchChatUid = (Long) searchChat.get("uid");

                if (searchChatUid != chatUid) {
                    newChats.add(searchChat);
                }
            }

            category.put("chats", newChats);
        }
        jsonUtils.saveJson(categories, "chats.json");
    }

    public void changeUid(Chat chat, long categoryUid, long oldUid)
    {
        JSONArray categories = jsonUtils.loadJson("chats.json");

        for (Object o : categories) {
            JSONObject category = (JSONObject)o;
            JSONArray chats = (JSONArray)category.get("chats");
            long searchCategoryUid = (Long) category.get("uid");

            if (searchCategoryUid == categoryUid) {
                for (Object c : chats) {
                    JSONObject searchChat = (JSONObject)c;
                    long searchChatUid = (Long) searchChat.get("uid");

                    if (searchChatUid == oldUid) {
                        searchChat.put("uid", chat.getUid());
                        searchChat.put("name", chat.getName());
                        searchChat.put("url", chat.getUrl());
                        searchChat.put("permissionNeeded", chat.getPermissionNeeded().toString());
                    }
                }
            }
        }
        jsonUtils.saveJson(categories, "chats.json");
    }
}
