package de.wursteintopf.bot.chat;

import java.util.ArrayList;
import java.util.List;

public class ChatCategory {
    private long uid;
    private String name;
    private List<Chat> chatList;

    public ChatCategory(long uid, String name) {
        this.uid = uid;
        this.name = name;
        chatList = new ArrayList<>();
    }

    public long getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Chat> getChatList() {
        return chatList;
    }

    public void setChatList(List<Chat> chatList) {
        this.chatList = chatList;
    }

    public void addChat(Chat chat) {
        chatList.add(chat);
    }

    public void removeChat(Chat chat) {
        chatList.remove(chat);
    }
}
