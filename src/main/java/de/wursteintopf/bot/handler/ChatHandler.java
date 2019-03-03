package de.wursteintopf.bot.handler;

import static java.lang.Math.toIntExact;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wursteintopf.bot.ENLDD;
import de.wursteintopf.bot.chat.Chat;
import de.wursteintopf.bot.chat.ChatCategory;
import de.wursteintopf.bot.chat.ChatManager;
import de.wursteintopf.bot.user.*;
import de.wursteintopf.bot.utils.MessageConstants;
import de.wursteintopf.bot.utils.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.net.MalformedURLException;
import org.apache.commons.validator.routines.UrlValidator;


public class ChatHandler {

    private static Logger log = LoggerFactory.getLogger(ChatHandler.class);

    private ENLDD enldd;
    private UserManager userManager;
    private ChatManager chatManager;

    private Map<Long, State> stateMap;
    private Map<Long, Object> adminAddChatMap;
    private Map<Long, Long> adminCurrentCategoryMap;

    public ChatHandler(ENLDD enldd, UserManager userManager, ChatManager chatManager, Map<Long, State> stateMap) {
        this.enldd = enldd;
        this.userManager = userManager;
        this.chatManager = chatManager;
        this.stateMap = stateMap;
        this.adminAddChatMap = new HashMap<>();
        this.adminCurrentCategoryMap = new HashMap<>();
    }

    public void onUpdateReceived(Update update) {
        log.info("Received update " + update);

        if (update.hasMessage() && update.getMessage().hasText()) {
            long userId = update.getMessage().getFrom().getId();
            String text = update.getMessage().getText();

            State state = stateMap.get(userId);

            // Admin is about to create a new Category
            if (userManager.checkAdmin(userId) && state == State.ADMIN_ADDCHATCATEGORY) {
                long uid = chatManager.getBiggestChatCategoryUid() + 1;
                chatManager.addCategory(new ChatCategory(uid, text));

                InlineKeyboardMarkup keyboardMarkup = buildCategoriesMenu(userId);

                SendMessage message = new SendMessage().setChatId(userId).setParseMode(ParseMode.HTML).setReplyMarkup(
                        keyboardMarkup).setText(MessageConstants.MESSAGE_CHATS_CATEGORYADDED);

                stateMap.remove(userId);
                enldd.send_message(message);
            }

            // Admin sent a UID for a new chat
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_ADDCHATUID) && (!text.equalsIgnoreCase("/cancel"))) {
                //Check if UID is actually a number
                if (!text.matches("\\d+")) {

                    SendMessage message = new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userId)
                            .setText(MessageConstants.MESSAGE_CHATS_NOTANUMBER);

                    enldd.send_message(message);

                }
                //Check if a chat with that UID might allready exist
                else if (chatManager.checkChatUID(Long.valueOf(text))) {

                    SendMessage message = new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userId)
                            .setText(MessageConstants.MESSAGE_CHATS_CHATEXISTSALLREADY);

                    enldd.send_message(message);

                }
                //Check if UID is only 4 chars long
                else if (text.length() > 4) {
                    SendMessage message = new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userId)
                            .setText(MessageConstants.MESSAGE_CHATS_CHATNUMBERTOLONG);

                    enldd.send_message(message);
                }
                //UID seems okay, save it and ask for name
                else {
                    long uid = Long.valueOf(text);
                    adminAddChatMap.put(userId, uid);

                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_ADDCHATNAME);

                    stateMap.remove(userId);
                    stateMap.put(userId, State.ADMIN_ADDCHATNAME);

                    enldd.send_message(message);
                }
            }

            // Admin send a Chatname (anything is okay as a chatname, so just save it and ask for URL)
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_ADDCHATNAME) && (!text.equalsIgnoreCase("/cancel"))) {
                long uid = (long) adminAddChatMap.get(userId);

                adminAddChatMap.remove(userId);
                adminAddChatMap.put(userId, new Chat(uid, text));

                SendMessage message = new SendMessage()
                        .setChatId(userId)
                        .setParseMode(ParseMode.HTML)
                        .setText(MessageConstants.MESSAGE_CHATS_ADDURL);

                stateMap.remove(userId);
                stateMap.put(userId, State.ADMIN_ADDCHATURL);

                enldd.send_message(message);
            }

            //Admin send a URL for a chat
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_ADDCHATURL) && (!text.equalsIgnoreCase("/cancel"))) {
                try {
                    URL url = new URL(text);
                    UrlValidator validator = new UrlValidator();

                    if (validator.isValid(text)) {
                        Chat chat = (Chat) adminAddChatMap.get(userId);
                        chat.setUrl(text);

                        InlineKeyboardMarkup keyboardMarkup = buildPossiblePermissions();
                        SendMessage message = new SendMessage()
                                .setChatId(userId)
                                .setParseMode(ParseMode.HTML)
                                .setReplyMarkup(keyboardMarkup)
                                .setText(MessageConstants.MESSAGE_CHATS_ADDPERMISSION);

                        stateMap.remove(userId);
                        enldd.send_message(message);
                    }
                    else {
                        SendMessage message = new SendMessage()
                                .setChatId(userId)
                                .setParseMode(ParseMode.HTML)
                                .setText(MessageConstants.MESSAGE_CHATS_NOTAURL);

                        enldd.send_message(message);
                    }
                }
                catch (MalformedURLException e) {
                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_NOTAURL);

                    enldd.send_message(message);
                }
            }

            //
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_EDITCHAT) && (!text.equalsIgnoreCase("/cancel"))) {
                if (!text.matches("\\d+")) {
                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_NOTFOUND);

                    enldd.send_message(message);
                    return;
                }

                Chat chat = chatManager.loadChatFromCategory(Long.valueOf(text), adminCurrentCategoryMap.get(userId));

                if (chat == null)
                {
                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_NOTFOUND);

                    enldd.send_message(message);
                } else {
                    InlineKeyboardMarkup keyboardMarkup = buildEditChat(chat, adminCurrentCategoryMap.get(userId));

                    String respond = MessageConstants.MESSAGE_CHATS_CHATFOUND.replace("$fullname", chat.getFullName()).replace("$permission", chat.getPermissionNeeded().toString()).replace("$url", chat.getUrl());

                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkup)
                            .setText(respond);

                    enldd.send_message(message);
                }

            }
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_CHANGECHATUID) && (!text.equalsIgnoreCase("/cancel"))) {
                if (!text.matches("\\d+"))
                {
                    SendMessage message = new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userId)
                            .setText(MessageConstants.MESSAGE_CHATS_NOTANUMBER);

                    enldd.send_message(message);
                } else if (chatManager.checkChatUID(Long.valueOf(text)))
                {
                    SendMessage message = new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userId)
                            .setText(MessageConstants.MESSAGE_CHATS_CHATEXISTSALLREADY);

                    enldd.send_message(message);
                } else if (text.length() > 4)
                {
                    SendMessage message = new SendMessage()
                            .setParseMode(ParseMode.HTML)
                            .setChatId(userId)
                            .setText(MessageConstants.MESSAGE_CHATS_CHATNUMBERTOLONG);

                    enldd.send_message(message);
                } else {
                    long uid = Long.valueOf(text);

                    Chat chat = (Chat)adminAddChatMap.get(userId);
                    long oldUid = chat.getUid();
                    chat.setUid(uid);

                    chatManager.changeUid(chat, adminCurrentCategoryMap.get(userId), oldUid);

                    String respond = MessageConstants.MESSAGE_CHATS_CHATFOUND.replace("$fullname", chat.getFullName()).replace("$permission", chat.getPermissionNeeded().toString()).replace("$url", chat.getUrl());

                    InlineKeyboardMarkup keyboardMarkup = buildEditChat(chat, adminCurrentCategoryMap.get(userId));

                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkup).setText(respond);

                    stateMap.remove(userId);
                    adminAddChatMap.remove(userId);
                    adminCurrentCategoryMap.remove(userId);

                    enldd.send_message(message);
                }

            }
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_CHANGECHATNAME) && (!text.equalsIgnoreCase("/cancel"))) {
                Chat chat = (Chat)adminAddChatMap.get(userId);
                chat.setName(text);

                chatManager.saveChat(chat, adminCurrentCategoryMap.get(userId));

                String respond = "<b>Chat gefunden: $fullname</b> \n\nBenötigte Permission: $permission\nURL: $url".replace("$fullname", chat.getFullName()).replace("$permission", chat.getPermissionNeeded().toString()).replace("$url", chat.getUrl());

                InlineKeyboardMarkup keyboardMarkup = buildEditChat(chat, adminCurrentCategoryMap.get(userId));

                SendMessage message = new SendMessage()
                        .setChatId(userId)
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(keyboardMarkup)
                        .setText(respond);

                stateMap.remove(userId);
                adminAddChatMap.remove(userId);
                adminCurrentCategoryMap.remove(userId);

                enldd.send_message(message);

            }
            else if ((userManager.checkAdmin(userId)) && (state == State.ADMIN_CHANGECHATURL) && (!text.equalsIgnoreCase("/cancel"))) {
                try {
                    URL url = new URL(text);
                    UrlValidator validator = new UrlValidator();

                    if (validator.isValid(text)) {
                        Chat chat = (Chat)adminAddChatMap.get(userId);
                        chat.setUrl(text);
                        chatManager.saveChat(chat, adminCurrentCategoryMap.get(userId));
                        String respond = "<b>Chat gefunden: $fullname</b> \n\nBenötigte Permission: $permission\nURL: $url".replace("$fullname", chat.getFullName()).replace("$permission", chat.getPermissionNeeded().toString()).replace("$url", chat.getUrl());

                        InlineKeyboardMarkup keyboardMarkup = buildEditChat(chat, adminCurrentCategoryMap.get(userId));

                        SendMessage message = new SendMessage()
                                .setChatId(userId)
                                .setParseMode(ParseMode.HTML)
                                .setReplyMarkup(keyboardMarkup)
                                .setText(respond);

                        stateMap.remove(userId);
                        adminAddChatMap.remove(userId);
                        adminCurrentCategoryMap.remove(userId);

                        enldd.send_message(message);
                    }
                    else
                    {
                        SendMessage message = new SendMessage()
                                .setChatId(userId)
                                .setParseMode(ParseMode.HTML)
                                .setText("Es tut mir Leid, aber das ist keine valide URL. Bitte versuche es noch einmal.");
                        enldd.send_message(message);
                    }
                }
                catch (MalformedURLException e)
                {
                    SendMessage message = new SendMessage()
                            .setChatId(userId)
                            .setParseMode(ParseMode.HTML)
                            .setText("Es tut mir Leid, aber das ist keine valide URL. Bitte versuche es noch einmal.");

                    enldd.send_message(message);
                }
            }
        }

        /*----------Update has Callback Query aka Button was clicked----------*/

        if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();

            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long user_id = update.getCallbackQuery().getFrom().getId();

            if (call_data.startsWith("chats")) {
                String[] call_data_parts = call_data.split(" ");

                /*----------Load all Categories----------*/

                if (call_data_parts[1].equalsIgnoreCase("menu")) {
                    InlineKeyboardMarkup keyboardMarkup = buildCategoriesMenu(user_id);

                    EditMessageText messageText = new EditMessageText().setChatId(user_id).setMessageId(
                            toIntExact(message_id)).setParseMode(ParseMode.HTML).setReplyMarkup(keyboardMarkup).setText(
                            MessageConstants.MESSAGE_CHATS_MENU);

                    enldd.edit_message(messageText);
                }

                /*----------Load specific category----------*/

                else if (call_data_parts[1].equalsIgnoreCase("loadcategory")) {
                    long uid = Long.valueOf(call_data_parts[2]);
                    ChatCategory category = chatManager.loadCategory(uid);

                    InlineKeyboardMarkup keyboardMarkup = buildCategory(category, user_id);

                    String messageText = category.getName() + ":";

                    EditMessageText message = new EditMessageText().setChatId(user_id).setMessageId(
                            toIntExact(message_id)).setParseMode(ParseMode.HTML).setReplyMarkup(keyboardMarkup).setText(
                            messageText);

                    enldd.edit_message(message);
                }

                else if (call_data_parts[1].equalsIgnoreCase("permissionmissing")) {
                    long uid = Long.valueOf(call_data_parts[2]);
                    ChatCategory category = chatManager.loadCategory(uid);

                    InlineKeyboardMarkup keyboardMarkup = buildBackToCategory(category);

                    EditMessageText message = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML).setReplyMarkup(keyboardMarkup)
                            .setText(MessageConstants.MESSAGE_CHATS_NORIGHTS);

                    enldd.edit_message(message);
                }

                /*----------Admin Stuff----------*/

                else if ((call_data_parts[1].equalsIgnoreCase("addcategory")) && (userManager.checkAdmin(user_id)))
                {
                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_ADDCATEGORY);

                    stateMap.put(user_id, State.ADMIN_ADDCHATCATEGORY);
                    enldd.edit_message(messageText);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("addchat")) && (userManager.checkAdmin(user_id)))
                {
                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setText("Okay! Bitte gib mir zuerst eine noch nicht verwendete Chatnummer (maximal 4-stellige Ganzzahl) für den Chat.");

                    adminCurrentCategoryMap.put(user_id, Long.valueOf(call_data_parts[2]));
                    stateMap.put(user_id, State.ADMIN_ADDCHATUID);
                    enldd.edit_message(messageText);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("editcategory")) && (userManager.checkAdmin(user_id))) {
                    long uid = Long.valueOf(call_data_parts[2]);
                    ChatCategory category = chatManager.loadCategory(uid);

                    InlineKeyboardMarkup keyboardMarkup = buildEditCategory(category);

                    String text = MessageConstants.MESSAGE_CHATS_ADMINEDITCATEGORY.replace("$category", category.getName());

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkup)
                            .setText(text);

                    enldd.edit_message(messageText);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("editchat")) && (userManager.checkAdmin(user_id)))
                {
                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_LOADCHAT);

                    adminCurrentCategoryMap.put(user_id, Long.valueOf(call_data_parts[2]));
                    stateMap.put(user_id, State.ADMIN_EDITCHAT);
                    enldd.edit_message(messageText);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("deletechat")) && (userManager.checkAdmin(user_id))) {
                    long chatId = Long.valueOf(call_data_parts[2]);
                    long categoryId = Long.valueOf(call_data_parts[3]);

                    chatManager.deleteChat(chatId);

                    InlineKeyboardMarkup keyboard = buildEditCategory(chatManager.loadCategory(categoryId));

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_CHATDELETED);

                    enldd.edit_message(messageText);
                }
                else if ((call_data_parts[1].equalsIgnoreCase("deletecategory")) && (userManager.checkAdmin(user_id))) {
                    long uid = Long.valueOf(call_data_parts[2]);
                    ChatCategory category = chatManager.loadCategory(uid);

                    InlineKeyboardMarkup keyboardMarkup = buildConfirmCategoryDelete(category);

                    String text = MessageConstants.MESSAGE_CHATS_ADMINDELETECATEGORY.replace("$category", category.getName());

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkup)
                            .setText(text);

                    enldd.edit_message(messageText);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("deletecategoryconfirm")) && (userManager.checkAdmin(user_id))) {
                    long uid = Long.valueOf(call_data_parts[2]);
                    chatManager.deleteCategory(uid);

                    InlineKeyboardMarkup keyboardMarkup = buildCategoriesMenu(user_id);

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkup)
                            .setText(MessageConstants.MESSAGE_CHATS_ADMINCATEGORYDELETED);

                    enldd.edit_message(messageText);
                }
                else if ((call_data_parts[1].equalsIgnoreCase("addpermission")) && (userManager.checkAdmin(user_id))) {
                    Chat chat = (Chat) adminAddChatMap.get(user_id);
                    adminAddChatMap.remove(user_id);

                    UserPermission permission = UserPermission.valueOf(call_data_parts[2]);
                    chat.setPermissionNeeded(permission);

                    ChatCategory category = chatManager.loadCategory((adminCurrentCategoryMap.get(user_id)));
                    adminCurrentCategoryMap.remove(user_id);

                    chatManager.saveChat(chat, category.getUid());

                    InlineKeyboardMarkup keyboardMarkup = buildEditCategory(category);

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboardMarkup)
                            .setText(MessageConstants.MESSAGE_CHATS_CHATADDED);

                    enldd.edit_message(messageText);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("changeuid")) && (userManager.checkAdmin(user_id))) {
                    adminCurrentCategoryMap.put(user_id, Long.valueOf(call_data_parts[3]));
                    adminAddChatMap.put(user_id, chatManager.loadChatFromCategory(Long.valueOf(call_data_parts[2]), Long.valueOf(call_data_parts[3])));

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_CHANGEUID);

                    enldd.edit_message(messageText);

                    stateMap.put(user_id, State.ADMIN_CHANGECHATUID);
                }
                else if ((call_data_parts[1].equalsIgnoreCase("changeurl")) && (userManager.checkAdmin(user_id))) {
                    adminCurrentCategoryMap.put(user_id, Long.valueOf(call_data_parts[3]));
                    adminAddChatMap.put(user_id, chatManager.loadChatFromCategory(Long.valueOf(call_data_parts[2]), Long.valueOf(call_data_parts[3])));

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_CHANGEURL);

                    enldd.edit_message(messageText);

                    stateMap.put(user_id, State.ADMIN_CHANGECHATURL);
                }
                else if ((call_data_parts[1].equalsIgnoreCase("changename")) && (userManager.checkAdmin(user_id))) {
                    adminCurrentCategoryMap.put(user_id, Long.valueOf(call_data_parts[3]));
                    adminAddChatMap.put(user_id, chatManager.loadChatFromCategory(Long.valueOf(call_data_parts[2]), Long.valueOf(call_data_parts[3])));

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_CHANGENAME);

                    enldd.edit_message(messageText);

                    stateMap.put(user_id, State.ADMIN_CHANGECHATNAME);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("changepermission")) && (userManager.checkAdmin(user_id))) {
                    adminCurrentCategoryMap.put(user_id, Long.valueOf(call_data_parts[3]));
                    adminAddChatMap.put(user_id, chatManager.loadChatFromCategory(Long.valueOf(call_data_parts[2]), Long.valueOf(call_data_parts[3])));

                    InlineKeyboardMarkup keyboardMarkup = buildChangePermissions();

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setReplyMarkup(keyboardMarkup)
                            .setParseMode(ParseMode.HTML)
                            .setText(MessageConstants.MESSAGE_CHATS_CHANGEPERMISSION);

                    enldd.edit_message(messageText);

                    stateMap.put(user_id, State.ADMIN_CHANGECHATUID);

                }
                else if ((call_data_parts[1].equalsIgnoreCase("changepermissionconfirm")) && (userManager.checkAdmin(user_id))) {
                    UserPermission permission = UserPermission.valueOf(call_data_parts[2]);

                    Chat chat = (Chat)adminAddChatMap.get(user_id);
                    chat.setPermissionNeeded(permission);

                    chatManager.saveChat(chat, adminCurrentCategoryMap.get(user_id));
                    InlineKeyboardMarkup keyboardMarkup = buildEditChat(chat, adminCurrentCategoryMap.get(user_id));

                    adminAddChatMap.remove(user_id);
                    adminCurrentCategoryMap.remove(user_id);
                    stateMap.remove(user_id);

                    String respond = MessageConstants.MESSAGE_CHATS_CHATFOUND.replace("$fullname", chat.getFullName()).replace("$permission", chat.getPermissionNeeded().toString()).replace("$url", chat.getUrl());

                    EditMessageText messageText = new EditMessageText()
                            .setChatId(user_id)
                            .setMessageId(Math.toIntExact(message_id))
                            .setReplyMarkup(keyboardMarkup)
                            .setParseMode(ParseMode.HTML)
                            .setText(respond);

                    enldd.edit_message(messageText);
                }
            }
        }
    }

    /*----------Help Methods to build the menus----------*/

    private InlineKeyboardMarkup buildCategoriesMenu(long user_id) {
        log.info("Building categories menu for user " + user_id);
        List<ChatCategory> categories = chatManager.loadCategories();

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (int i = 0; i < categories.size(); i++) {
            buttons.add(new ArrayList<>());
        }

        for (int i = 0; i < categories.size(); i++) {
            buttons.get(i).add(new InlineKeyboardButton().setText(categories.get(i).getName())
                    .setCallbackData("chats loadcategory " + categories.get(i).getUid()));
        }

        if (userManager.checkAdmin(user_id)) {
            List<InlineKeyboardButton> adminButtons = new ArrayList<>();

            adminButtons.add(
                    new InlineKeyboardButton().setText("Kategorie hinzufügen").setCallbackData("chats addcategory"));

            buttons.add(adminButtons);
        }

        List<InlineKeyboardButton> backButton = new ArrayList<>();
        backButton.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("mainmenu"));
        buttons.add(backButton);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildCategory(ChatCategory category, long userId) {
        log.info("Building category " + category.getName() + " for user " + userId);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (int i = 0; i < (category.getChatList().size() + 1) / 2; i++) {
            buttons.add(new ArrayList<>());
        }

        for (int i = 0; i < category.getChatList().size(); i++) {
            Chat chat = category.getChatList().get(i);
            InlineKeyboardButton button = new InlineKeyboardButton();
            AbstractUser abstractUser = userManager.loadAbstractUserById(userId);

            if (abstractUser instanceof Admin) {
                if (chat.getUrl() == null) {
                    button.setText(chat.getName()).setCallbackData("chats notfound");
                } else {
                    button.setText(chat.getName()).setUrl(chat.getUrl());
                }
            } else {
                User user = (User) abstractUser;
                if (chat.getPermissionNeeded() != null && user.getPermissions().contains(chat.getPermissionNeeded())) {
                    if (chat.getUrl() == null) {
                        button.setText(chat.getName()).setCallbackData("chats notfound");
                    } else {
                        button.setText(chat.getName()).setUrl(chat.getUrl());
                    }
                } else if (chat.getPermissionNeeded() == null) {
                    if (chat.getUrl() == null) {
                        button.setText(chat.getName()).setCallbackData("chats notfound");
                    } else {
                        button.setText(chat.getName()).setUrl(chat.getUrl());
                    }
                } else {
                    button.setText(chat.getName()).setCallbackData("chats permissionmission");
                }
            }

            buttons.get(i / 2).add(button);
        }

        if (userManager.checkAdmin(userId)) {
            List<InlineKeyboardButton> adminButton = new ArrayList<>();
            adminButton.add(new InlineKeyboardButton().setText("Kategorie bearbeiten")
                    .setCallbackData("chats editcategory " + category.getUid()));
            buttons.add(adminButton);
        }

        List<InlineKeyboardButton> backButton = new ArrayList<>();
        backButton.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats menu"));
        buttons.add(backButton);

        keyboardMarkup.setKeyboard(buttons);
        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildEditCategory(ChatCategory category) {
        log.info("Building edit category for " + category.getName());
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Chat hinzufügen").setCallbackData("chats addchat " + category.getUid()));
        row.add(new InlineKeyboardButton().setText("Chat bearbeiten").setCallbackData("chats editchat " + category.getUid()));
        row2.add(new InlineKeyboardButton().setText("Kategorie löschen").setCallbackData("chats deletecategory " + category.getUid()));
        row3.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats loadcategory " + category.getUid()));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildPossiblePermissions() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Keine spezielle Berechtigung").setCallbackData("chats addpermission NORMAL"));
        row.add(new InlineKeyboardButton().setText("Hightrust").setCallbackData("chats addpermission HIGHTRUST"));
        row2.add(new InlineKeyboardButton().setText("Mod").setCallbackData("chats addpermission MOD"));
        row2.add(new InlineKeyboardButton().setText("Operator").setCallbackData("chats addpermission OPERATOR"));

        buttons.add(row);
        buttons.add(row2);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildBackToCategory(ChatCategory category) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats loadcategory " + category.getUid()));

        buttons.add(row);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildChangePermissions() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Keine spezielle Berechtigung").setCallbackData("chats changepermissionconfirm NORMAL"));
        row.add(new InlineKeyboardButton().setText("Hightrust").setCallbackData("chats changepermissionconfirm HIGHTRUST"));
        row2.add(new InlineKeyboardButton().setText("Mod").setCallbackData("chats changepermissionconfirm MOD"));
        row2.add(new InlineKeyboardButton().setText("Operator").setCallbackData("chats changepermissionconfirm OPERATOR"));

        buttons.add(row);
        buttons.add(row2);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildEditChat(Chat chat, long categoryUid) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("UID ändern").setCallbackData("chats changeuid " + chat.getUid() + " " + categoryUid));
        row.add(new InlineKeyboardButton().setText("Name ändern").setCallbackData("chats changename " + chat.getUid() + " " + categoryUid));
        row2.add(new InlineKeyboardButton().setText("Permission ändern").setCallbackData("chats changepermission " + chat.getUid() + " " + categoryUid));
        row2.add(new InlineKeyboardButton().setText("Url ändern").setCallbackData("chats changeurl " + chat.getUid() + " " + categoryUid));
        row3.add(new InlineKeyboardButton().setText("Löschen").setCallbackData("chats deletechat " + chat.getUid() + " " + categoryUid));
        row3.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats editcategory " + categoryUid + " " + categoryUid));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }

    private InlineKeyboardMarkup buildConfirmCategoryDelete(ChatCategory category) {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("LÖSCHEN").setCallbackData("chats deletecategoryconfirm " + category.getUid()));
        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats editcategory " + category.getUid()));

        buttons.add(row);

        keyboardMarkup.setKeyboard(buttons);

        return keyboardMarkup;
    }
}
