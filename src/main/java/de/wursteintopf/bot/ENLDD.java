package de.wursteintopf.bot;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wursteintopf.bot.chat.ChatManager;
import de.wursteintopf.bot.handler.AdminToolsHandler;
import de.wursteintopf.bot.handler.ChatHandler;
import de.wursteintopf.bot.user.AbstractUser;
import de.wursteintopf.bot.user.Admin;
import de.wursteintopf.bot.user.Guest;
import de.wursteintopf.bot.user.UserManager;
import de.wursteintopf.bot.utils.MessageConstants;
import de.wursteintopf.bot.utils.State;
import de.wursteintopf.bot.utils.jsonUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

public class ENLDD extends TelegramLongPollingBot {

    private static Logger log = LoggerFactory.getLogger(ENLDD.class);

    public static final long ADMIN_CHAT_ID = (long) ((JSONObject) (jsonUtils.loadJson("config.json").get(0))).get(
            "masterUserId");
    public static final JSONArray OPEN_CHATS = (JSONArray) ((JSONObject) (jsonUtils.loadJson("config.json").get(0)))
            .get("openChats");
    public static final String GUEST_CHAT_JOIN_LINK = (String) ((JSONObject) (jsonUtils.loadJson("config.json").get(0)))
            .get("guestChatJoinLink");
    public static final String TEAM_NAME = (String) ((JSONObject) (jsonUtils.loadJson("config.json").get(0))).get(
            "teamname");
    public static final boolean NO_BOT_RIGHTS = (Boolean) ((JSONObject) (jsonUtils.loadJson("config.json").get(0))).get(
            "no_bot_rights_option");

    private Map<Long, State> stateMap;

    private UserManager userManager;
    private ChatManager chatManager;
    private AdminToolsHandler adminToolsHandler;
    private ChatHandler chatHandler;

    public ENLDD() {
        this.stateMap = new HashMap<>();
        this.userManager = new UserManager();
        this.chatManager = new ChatManager();
        this.adminToolsHandler = new AdminToolsHandler(this, this.userManager, this.stateMap);
        this.chatHandler = new ChatHandler(this, this.userManager, this.chatManager, this.stateMap);
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Received update " + update);

        //Check if user, who triggered the update has a username
        if (update.hasMessage() && update.getMessage().getFrom().getUserName() == null) {
            SendMessage message = new SendMessage().setChatId(update.getMessage().getChatId()).setParseMode(
                    ParseMode.HTML).setText(MessageConstants.MESSAGE_ERROR_NOUSERNAME);
            send_message(message);
        }

        //Check if user is blocked
        if (update.hasMessage() && userManager.checkBlocklist(update.getMessage().getFrom().getId())) {
            return;
        }

        //Throw update to all used Handlers
        adminToolsHandler.onUpdateReceived(update);
        chatHandler.onUpdateReceived(update);

        /*----------Update has Text----------*/

        if (update.hasMessage() && update.getMessage().hasText()) {

            String text = update.getMessage().getText();

            long userId = update.getMessage().getFrom().getId();
            String userName = update.getMessage().getFrom().getUserName();

            State state = stateMap.get(userId);

            //Different Options to build the Main Menu

            /*----------User is known, build main Menu----------*/

            if (text.equalsIgnoreCase("/start") && userManager.checkWhitelist(userId) && state == null) {
                AbstractUser abstractUser = userManager.loadAbstractUser(userName);

                SendMessage message = new SendMessage();
                String message_text;
                InlineKeyboardMarkup buttons;

                if (abstractUser instanceof Guest) {
                    buttons = buildGuestMenu();
                    message_text = MessageConstants.MESSAGE_MENU_GUEST;
                } else {
                    buttons = buildMainMenu(userId);
                    message_text = MessageConstants.MESSAGE_MENU_MAIN;
                }

                message_text = message_text.replace("$user", userName);

                message.setChatId(userId).setParseMode(ParseMode.HTML).setReplyMarkup(buttons).setText(message_text);

                send_message(message);
            }

            /*----------User is unknown, build Menu with Open Chat and
            possibillities to request rights----------*/

            else if (state == null && text.equalsIgnoreCase("/start") && !userManager.checkBlocklist(userId)) {
                SendMessage message = new SendMessage();

                InlineKeyboardMarkup buttons = buildOpenMenu();

                if (NO_BOT_RIGHTS) {
                    message.setChatId(userId).setParseMode(ParseMode.HTML).setReplyMarkup(buttons).setText(
                            MessageConstants.MESSAGE_MENU_OPEN_NO_BOT_RIGHTS);
                } else {
                    message.setChatId(userId).setParseMode(ParseMode.HTML).setReplyMarkup(buttons).setText(
                            MessageConstants.MESSAGE_MENU_OPEN);
                }

                send_message(message);
            }

            /*----------/help----------*/

            else if (text.equalsIgnoreCase("/help") && !userManager.checkBlocklist(userId) && state == null) {
                SendMessage message = new SendMessage();

                message.setChatId(userId).setParseMode(ParseMode.HTML).setText(MessageConstants.MESSAGE_HELP);

                send_message(message);
            }

            /*----------/changelog---------*/

            else if (text.equalsIgnoreCase("/changelog") && userManager.checkWhitelist(userId) && state == null) {
                SendMessage message = new SendMessage();

                message.setChatId(userId).setParseMode(ParseMode.HTML).setText(MessageConstants.MESSAGE_CHANGELOG);

                send_message(message);
            }

            /*----------Cancel any state----------*/

            else if (state != null && text.equals("/cancel")) {
                stateMap.remove(userId);

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();

                row.add(new InlineKeyboardButton().setText("Hauptmenü").setCallbackData("mainmenu"));

                buttons.add(row);

                keyboard.setKeyboard(buttons);

                SendMessage message = new SendMessage().setChatId(userId).setParseMode(ParseMode.HTML).setReplyMarkup(
                        keyboard).setText(MessageConstants.MESSAGE_STATE_CANCELED);

                send_message(message);
            }
        }

        /*----------Update has Callback Query, aka as Button was clicked----------*/

        else if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();

            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long user_id = update.getCallbackQuery().getFrom().getId();

            String user_name = update.getCallbackQuery().getFrom().getUserName();

            State state = stateMap.get(user_id);

            //Go back to Main Menu
            if (call_data.equals("mainmenu") && userManager.checkWhitelist(user_id)) {
                EditMessageText message = new EditMessageText();

                InlineKeyboardMarkup keyboard = buildMainMenu(user_id);

                String message_text = MessageConstants.MESSAGE_MENU_MAIN.replace("$user", user_name);

                message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                        .setReplyMarkup(keyboard).setText(message_text);

                edit_message(message);
            }

            //Close Menu
            else if (call_data.equals("close")) {
                EditMessageText message = new EditMessageText().setChatId(user_id).setMessageId(toIntExact(message_id))
                        .setText(MessageConstants.MESSAGE_MENU_CLOSED);

                edit_message(message);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return (String) ((JSONObject) (jsonUtils.loadJson("config.json").get(0))).get("botUserName");
    }

    @Override
    public String getBotToken() {
        return (String) ((JSONObject) (jsonUtils.loadJson("config.json").get(0))).get("botToken");
    }

    /*----------Help Methods to send and edit messages----------*/

    public void send_message(SendMessage message) {
        log.info("Send message " + message);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void edit_message(EditMessageText message) {
        log.info("Send edit message " + message);
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send_message_toadmins(SendMessage message) {
        log.info("Send message to admins" + message);
        List<Admin> admin_list = userManager.buildAdminList();

        for (Admin admin : admin_list) {
            long admin_id = admin.getId();
            message.setChatId(admin_id);
            send_message(message);
        }
    }

    /*----------Help Methods to build Menus----------*/

    private InlineKeyboardMarkup buildOpenMenu() {
        log.info("Building open menu");

        InlineKeyboardMarkup open_menu = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (Object chat : OPEN_CHATS) {
            JSONObject chatJSON = (JSONObject) chat;
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(new InlineKeyboardButton().setText((String) chatJSON.get("name"))
                    .setUrl((String) chatJSON.get("url")));
            buttons.add(row);
        }

        if (!NO_BOT_RIGHTS) {
            List<InlineKeyboardButton> row2 = new ArrayList<>();
            row2.add(new InlineKeyboardButton().setText("Rechte anfragen").setCallbackData("permission request"));
            buttons.add(row2);
        }

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(new InlineKeyboardButton().setText("Gastrechte anfragen / Request guest permissions")
                .setCallbackData("permission requestguest"));
        buttons.add(row3);

        open_menu.setKeyboard(buttons);

        return open_menu;
    }

    private InlineKeyboardMarkup buildMainMenu(long user_id) {
        log.info("Building main menu");

        InlineKeyboardMarkup main_menu = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (Object chat : OPEN_CHATS) {
            JSONObject chatJSON = (JSONObject) chat;
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(new InlineKeyboardButton().setText((String) chatJSON.get("name"))
                    .setUrl((String) chatJSON.get("url")));
            buttons.add(row);
        }

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(new InlineKeyboardButton().setText("Chats").setCallbackData("chats menu"));

        buttons.add(row1);

        if (userManager.checkAdmin(user_id)) {
            List<InlineKeyboardButton> adminbutton = new ArrayList<>();
            adminbutton.add(new InlineKeyboardButton().setText("AdminTools").setCallbackData("admintools menu"));
            buttons.add(adminbutton);
        }

        List<InlineKeyboardButton> close = new ArrayList<>();
        close.add(new InlineKeyboardButton().setText("Menü schließen").setCallbackData("close"));

        buttons.add(close);

        main_menu.setKeyboard(buttons);

        return main_menu;
    }

    private InlineKeyboardMarkup buildGuestMenu() {
        log.info("Building guest menu");

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(new InlineKeyboardButton().setText("Gästechat / Guest chat").setUrl(GUEST_CHAT_JOIN_LINK));
        buttons.add(row2);

        for (Object chat : OPEN_CHATS) {
            JSONObject chatJSON = (JSONObject) chat;
            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(new InlineKeyboardButton().setText((String) chatJSON.get("name"))
                    .setUrl((String) chatJSON.get("url")));
            buttons.add(row);
        }

        List<InlineKeyboardButton> row3 = new ArrayList<>();
        row3.add(new InlineKeyboardButton().setText("Menü schließen / Close menu").setCallbackData("close"));
        buttons.add(row3);

        keyboard.setKeyboard(buttons);

        return keyboard;
    }
}
