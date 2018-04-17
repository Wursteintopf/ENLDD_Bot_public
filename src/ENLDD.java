import org.telegram.telegrambots.api.methods.ParseMode;
import org.telegram.telegrambots.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.*;

import static java.lang.Math.toIntExact;

public class ENLDD extends TelegramLongPollingBot {

    private static final long ADMIN_CHAT_ID = /*cleaned*/;
    private static final String ADMIN_USER_NAME = "Wursteintopf";

    private static final String MESSAGE_MENU_MAIN = "Willkommen $user! \nAktuell findest du hier nur unsere Telegram Chats, in Zukunft sollen hier aber durchaus noch weitere Funktionen dazukommen.";
    private static final String MESSAGE_MENU_GUEST = "Willkommen $user! \nHier findest du unseren offenen Chat und unseren Gästechat:";
    private static final String MESSAGE_MENU_OPEN = "Willkommen beim ENLDD_Bot! \nEs scheint, als hättest du keine Berechtigungen um diesen Bot zu nutzen. Wenn du neu bei der Enlightened Dresden bist, dann frage doch einfach Rechte an. Falls du Gast bist, hast du hier die Möglichkeit Gastrechte anzufragen.";
    private static final String MESSAGE_MENU_CLOSED = "Menü erfolgreich geschlossen.";

    private static final String MESSAGE_PERMISSION_USER_REQUEST = "Der Nutzer @$user hat Berechtigungen für den Bot angefragt.";
    private static final String MESSAGE_PERMISSION_GUEST_REQUEST = "Der Nutzer @$user hat Gastrechte für den Bot angefragt.";
    private static final String MESSAGE_PERMISSION_USER_REQUEST_RECEIVED = "Deine Anfrage wurde an ein Team hochtrainierter Affen weitergeleitet.";
    private static final String MESSAGE_PERMISSION_ALLREADYONWHITELIST = "Es tut mir Leid, aber du stehst bereits auf der Whitelist.";
    private static final String MESSAGE_PERMISSION_ALLREADYREQUESTED = "Geduld mein junger Padawan! Deine Anfrage wurde bereits weitergeleitet und muss noch von einem Admin bearbeitet werden.";
    private static final String MESSAGE_PERMISSION_OPENREQUESTS = "Es gibt offene Requests von:";
    private static final String MESSAGE_PERMISSION_NO_OPENREQUESTS = "Es gibt aktuell keine offenen Requests.";
    private static final String MESSAGE_PERMISSION_OPENUSER = "@$user hat volle Rechte angefragt. Akzeptieren?";
    private static final String MESSAGE_PERMISSION_OPENUSER_GUEST = "@$user hat Gastrechte angefragt. Akzeptieren?";
    private static final String MESSAGE_PERMISSION_GRANTED = "Du hast soeben Berechtigungen für diesen Bot bekommen. Um den Bot zu nutzen starte ihn bitte mit /start neu.";
    private static final String MESSAGE_PERMISSION_GRANTED_ADMIN = "@$user wurde auf die Whitelist gesetzt.";
    private static final String MESSAGE_PERMISSION_GUEST_GRANTED = "Du hast soeben Gastrechte für diesen Bot bekommen. Um den Bot zu nutzen starte ihn bitte mit /start neu.";
    private static final String MESSAGE_PERMISSION_GUEST_GRANTED_ADMIN = "@$user wurde zur Gästeliste hinzugefügt.";
    private static final String MESSAGE_PERMISSION_DENIED = "Deine Nutzungsanfrage wurde abgelehnt.";
    private static final String MESSAGE_PERMISSION_DENIED_ADMIN = "Nutzungsanfrage von @$user wurde abgelehnt.";
    private static final String MESSAGE_PERMISSION_BLOCKED = "Du wurdest blockiert.";
    private static final String MESSAGE_PERMISSION_BLOCKED_ADMIN = "@$user wurde geblockt.";

    private static final String MESSAGE_ADMINTOOLS_WHITELIST = "Aktuelle Nutzer auf der Whitelist: \n\n";
    private static final String MESSAGE_ADMINTOOLS_BLOCKLIST = "Aktuell auf der Blocklist: \n\n";
    private static final String MESSAGE_ADMINTOOLS_LOADUSER = "Okay! Bitte schick mir den exakten Benutzernamen des Users, den du laden möchtest, oder brich mit /cancel ab.";
    private static final String MESSAGE_ADMINTOOLS_USERNOTFOUND = "Es tut mir Leid, aber ich kann diesen Nutzer nicht finden.";
    private static final String MESSAGE_ADMINTOOLS_NULLREQUEST = "Es tut mir Leid, aber scheinbar wurde diese Request bereits bearbeitet.";
    private static final String MESSAGE_ADMINTOOLS_USERFOUND = "Ich habe folgenden Nutzer gefunden:\n\n<b>$user</b>\nAdmin: <b>$admin</b>\nMod: <b>$mod</b>\nOperator: <b>$operator</b>\nHightrust: <b>$hightrust</b>\n\nWas möchtest du mit diesem Nutzer tun?";
    private static final String MESSAGE_ADMINTOOLS_GUESTFOUND = "Ich habe den Gast $user gefunden.\n\nWas möchtest du mit diesem User tun?";
    private static final String MESSAGE_ADMINTOOLS_USER_CHANGED = "$status erfolgreich geändert.\n\n<b>$user</b>\nAdmin: <b>$admin</b>\nMod: <b>$mod</b>\nOperator: <b>$operator</b>\nHightrust: <b>$hightrust</b>\n\nMöchtest du noch mehr tun?";
    private static final String MESSAGE_ADMINTOOLS_CANTCHANGEOWNADMIN = "Entschuldigung, aber du kannst nicht deinen eigenen Adminstatus ändern.";
    private static final String MESSAGE_ADMINTOOLS_CANTCHANGEMASTER = "HOW DARE YOU! Es ist leider nicht möglich dem Großmeister seine Adminrechte zu entziehen!";
    private static final String MESSAGE_ADMINTOOLS_ABOUTTOBLOCK = "Bist du dir ganz sicher, dass du $user blockieren möchtest?";
    private static final String MESSAGE_ADMINTOOLS_CANTBLOCKSELF = "Es tut mir Leid, aber du kannst dich nicht selbst blockieren.";
    private static final String MESSAGE_ADMINTOOLS_CANTBLOCKMASTER = "PFUI! Hörst du wohl auf, zu versuchen den Großmeister zu blockieren!";
    private static final String MESSAGE_ADMINTOOLS_CANTGUESTSELF = "Es tut mir Leid, aber du kannst dich nicht selbst zum Gast machen.";
    private static final String MESSAGE_ADMINTOOLS_CANTGUESTMASTER = "PFUI! Hörst du wohl auf, zu versuchen den Großmeister zum Gast zu degradieren!";
    private static final String MESSAGE_ADMINTOOLS_BLOCKED = "$user wurde erfolgreich blockiert.";
    private static final String MESSAGE_ADMINTOOLS_UNBLOCK = "Okay! Bitte schick mir den exakten Benutzernamen des Users, den du freigeben möchtest, oder brich mit /cancel ab.";
    private static final String MESSAGE_ADMINTOOLS_USERUNBLOCKED = "Okay! $user wurde entblockt und kann eine neue Berechtigungsanfrage für den Bot stellen.";

    private static final String MESSAGE_CHATS_MENU = "Unsere Chats sind in 4 Kategorien aufgeteilt:";
    private static final String MESSAGE_CHATS_COMMON = "Allgemeine Ingresschats:";
    private static final String MESSAGE_CHATS_SPECIFIC = "Spezifische Ingresschats:";
    private static final String MESSAGE_CHATS_NONE = "None Ingresschats:";
    private static final String MESSAGE_CHATS_CITY = "Stadtteilchats:";
    private static final String MESSAGE_CHATS_MUSIC = "Der Musikchannel bietet aktuell leider keinen Invitelink an. Wende dich bitte an Baumi, wenn du beitreten möchtest.";
    private static final String MESSAGE_CHATS_NORIGHTS = "Es tut mir Leid, aber für diesen Chat brauchst du spezielle Berechtigungen. Wende dich bitte an einen der Mods, wenn du diesem Chat beitreten möchtest.";
    private static final String MESSAGE_CHATS_NOTFOUND = "ERROR 404, Chatlink not found. Bitte wende dich an einen der Mods.";

    private static final String MESSAGE_HELP = "Hallo! Ich bin ein Bot der Enlightened Dresden! Wenn du mich nutzen willst, dann starte mich bitte mit /start.";

    private static final String MESSAGE_STATE_CANCELED = "Erfolgreich abgebrochen.";

    private static final String MESSAGE_BLOCKED = "Du bist für diesen Bot geblockt.";

    private static final String URL_CHAT_MOD = /*cleaned*/;
    private static final String URL_CHAT_ALARM = /*cleaned*/;
    private static final String URL_CHAT_NEWS = /*cleaned*/;
    private static final String URL_CHAT_INTERN = /*cleaned*/;
    private static final String URL_CHAT_EVENTS = /*cleaned*/;
    private static final String URL_CHAT_OPEN = /*cleaned*/;
    private static final String URL_CHAT_PASSCODES = /*cleaned*/;
    private static final String URL_CHAT_TRADING = /*cleaned*/;

    private static final String URL_CHAT_FIGHT = /*cleaned*/;
    private static final String URL_CHAT_GRUENFLAECHE = /*cleaned*/;
    private static final String URL_CHAT_OPS = /*cleaned*/;
    private static final String URL_CHAT_BADGE = /*cleaned*/;
    private static final String URL_CHAT_VISITORS = /*cleaned*/;
    private static final String URL_CHAT_MISSIONS = /*cleaned*/;
    private static final String URL_CHAT_V = /*cleaned*/;
    private static final String URL_CHAT_CARGRESS = /*cleaned*/;
    private static final String URL_CHAT_CODES = /*cleaned*/;

    private static final String URL_CHAT_QMS = /*cleaned*/;
    private static final String URL_CHAT_PINGUINE = /*cleaned*/;
    private static final String URL_CHAT_TGBOTS = /*cleaned*/;
    private static final String URL_CHAT_MEME = /*cleaned*/;

    private static final String URL_CHAT_UNI = /*cleaned*/;
    private static final String URL_CHAT_RSML = /*cleaned*/;
    private static final String URL_CHAT_KPTM = /*cleaned*/;
    private static final String URL_CHAT_LAEP = /*cleaned*/;
    private static final String URL_CHAT_LOEBTAU = /*cleaned*/;
    private static final String URL_CHAT_STRIESEN = /*cleaned*/;
    private static final String URL_CHAT_GOCO = /*cleaned*/;
    private static final String URL_CHAT_JOTOWN = /*cleaned*/;
    private static final String URL_CHAT_NORD = /*cleaned*/;
    private static final String URL_CHAT_WS = /*cleaned*/;

    private Map<Long, User> open_requests = new HashMap<>();

    private Map<Long, State> stateMap = new HashMap<>();

    private UserManager user_manager;


    public ENLDD() {
        user_manager = new UserManager();
    }

    @Override
    public void onUpdateReceived(Update update) {

//******************************Update has Text******************************//

        if (update.hasMessage() && update.getMessage().hasText()) {

            long user_id = update.getMessage().getFrom().getId();
            long chat_id = update.getMessage().getChatId();

            String user_name = update.getMessage().getFrom().getUserName();
            String text = update.getMessage().getText();

            State state = stateMap.get(user_id);

//------------------------------User is known, build Main Menu------------------------------//

            if (user_id == chat_id && text.equalsIgnoreCase("/start") && !user_manager.checkAttribute(user_id, "guest") && user_manager.checkWhitelist(user_id) && state == null) {

                SendMessage message = new SendMessage();

                InlineKeyboardMarkup buttons = buildMainMenu(user_id);

                String message_text = MESSAGE_MENU_MAIN.replace("$user", user_name);

                message
                        .setChatId(user_id)
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(buttons)
                        .setText(message_text);

                send_message(message);
            }

//------------------------------User is a guest------------------------------//

            else if (user_id == chat_id && text.equalsIgnoreCase("/start") && user_manager.checkAttribute(user_id, "guest") && user_manager.checkWhitelist(user_id) && state == null) {

                SendMessage message = new SendMessage();

                InlineKeyboardMarkup buttons = buildGuestMenu();

                String message_text = MESSAGE_MENU_GUEST.replace("$user", user_name);

                message
                        .setChatId(user_id)
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(buttons)
                        .setText(message_text);

                send_message(message);
            }

//------------------------------User is unknown, show Main Chats and send Request------------------------------//

            else if (user_id == chat_id && text.equalsIgnoreCase("/start") && !user_manager.checkBlocklist(user_id) && state == null) {

                SendMessage message = new SendMessage();

                InlineKeyboardMarkup buttons = buildOpenMenu();

                message
                        .setChatId(user_id)
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(buttons)
                        .setText(MESSAGE_MENU_OPEN);

                send_message(message);
            }

//------------------------------Admin sent username to find a user------------------------------//

            else if (user_id == chat_id && user_manager.checkAdmin(user_id) && state == State.ADMINLOADUSER && !text.equals("/cancel")) {
                User user = user_manager.loadUser(text);

                // User wasn't found
                if (user == null) {
                    SendMessage message = new SendMessage()
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                    send_message(message);
                }

                // User isn't a guest
                else if (!user_manager.checkAttribute(user.getId(), "guest")){
                    stateMap.remove(user_id);

                    SendMessage message = new SendMessage();

                    InlineKeyboardMarkup keyboard = buildUserMenu(user);

                    String message_text = MESSAGE_ADMINTOOLS_USERFOUND
                            .replace("$user", user.getUser_name())
                            .replace("$admin", String.valueOf(user.getAdmin()))
                            .replace("$mod", String.valueOf(user.getMod()))
                            .replace("$operator", String.valueOf(user.getOperator()))
                            .replace("$hightrust", String.valueOf(user.getHightrust()));

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    send_message(message);
                }

                //User is a guest
                else if (user_manager.checkAttribute(user.getId(), "guest")) {
                    stateMap.remove(user_id);

                    SendMessage message = new SendMessage();

                    InlineKeyboardMarkup keyboard = buildGuest(user);

                    String message_text = MESSAGE_ADMINTOOLS_GUESTFOUND.replace("$user", user.getUser_name());

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    send_message(message);
                }
            }

//------------------------------Admin sent username to unblock a user------------------------------//

            else if (user_id == chat_id && user_manager.checkAdmin(user_id) && state == State.ADMINUNBLOCK && !text.equals("/cancel")) {
                User user = user_manager.loadBlockedUser(text);

                // User wasn't found
                if (user == null) {
                    SendMessage message = new SendMessage()
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                    send_message(message);
                }

                else {
                    stateMap.remove(user_id);

                    user_manager.unblock(user.getId());

                    SendMessage message = new SendMessage();

                    InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                    String message_text = MESSAGE_ADMINTOOLS_USERUNBLOCKED.replace("$user", user.getUser_name());

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    send_message(message);
                }
            }

//------------------------------/help------------------------------//

            else if (text.equalsIgnoreCase("/help") && !user_manager.checkBlocklist(user_id)) {
                SendMessage message = new SendMessage();

                message
                        .setChatId(chat_id)
                        .setParseMode(ParseMode.HTML)
                        .setText(MESSAGE_HELP);

                send_message(message);
            }

//------------------------------load link and generate link------------------------------//

            /*else if (text.equalsIgnoreCase("/link") && user_manager.checkAdmin(user_id)) {
                SendMessage message = new SendMessage();

                String message_text = "Aktueller Invitelink: " + update.getMessage().getChat().getInviteLink();

                message
                        .setChatId(chat_id)
                        .setParseMode(ParseMode.HTML)
                        .setText(message_text);

                send_message(message);
            }

            else if (text.equalsIgnoreCase("/generate") && user_manager.checkAdmin(user_id)) {

            }*/

//------------------------------Cancel any state------------------------------//

            else if (state != null && text.equals("/cancel")) {
                stateMap.remove(user_id);

                InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

                List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                List<InlineKeyboardButton> row = new ArrayList<>();

                row.add(new InlineKeyboardButton().setText("Hauptmenü").setCallbackData("mainmenu"));

                buttons.add(row);

                keyboard.setKeyboard(buttons);

                SendMessage message = new SendMessage()
                        .setChatId(user_id)
                        .setParseMode(ParseMode.HTML)
                        .setReplyMarkup(keyboard)
                        .setText(MESSAGE_STATE_CANCELED);

                send_message(message);
            }
        }

//******************************Update has Callback Query aka Button was clicked******************************//

        else if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();

            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            long user_id= update.getCallbackQuery().getFrom().getId();

            String user_name = update.getCallbackQuery().getFrom().getUserName();

            State state = stateMap.get(user_id);

//------------------------------Permission handling------------------------------//

            if (call_data.startsWith("permission") && !user_manager.checkBlocklist(user_id) && state == null) {
                String[] call_data_parts = call_data.split(" ");

                // User is allready on whitelist
                if (user_manager.checkWhitelist(user_id)) {
                    EditMessageText message = new EditMessageText();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setText(MESSAGE_PERMISSION_ALLREADYONWHITELIST);

                    edit_message(message);
                }

                // User allready requested Permission
                else if (allreadyRequested(user_id)) {
                    EditMessageText message = new EditMessageText();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setText(MESSAGE_PERMISSION_ALLREADYREQUESTED);

                    edit_message(message);
                }


                // New User requested permission
                else if (call_data_parts[1].equals("request")) {

                    // Add user request to List of open requests
                    open_requests.put(user_id, new User(user_id, user_name));

                    // Edit user message to tell him, that his request got received
                    EditMessageText message = new EditMessageText();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setText(MESSAGE_PERMISSION_USER_REQUEST_RECEIVED);

                    edit_message(message);

                    // Send message to Admingroup, that there is a new request
                    SendMessage admin_message = new SendMessage();

                    String admin_message_text = MESSAGE_PERMISSION_USER_REQUEST.replace("$user", user_name);

                    admin_message
                            .setParseMode(ParseMode.HTML)
                            .setText(admin_message_text);

                    send_message_toadmins(admin_message);
                }

                // New Guest requested permission
                else if (call_data_parts[1].equals("requestguest")) {

                    // Add user request to List of open requests
                    User guest = new User(user_id, user_name);
                    guest.setGuest(true);
                    open_requests.put(user_id, guest);

                    // Edit user message to tell him, that his request got received
                    EditMessageText message = new EditMessageText();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setText(MESSAGE_PERMISSION_USER_REQUEST_RECEIVED);

                    edit_message(message);

                    // Send message to Admingroup, that there is a new request
                    SendMessage admin_message = new SendMessage();

                    String admin_message_text = MESSAGE_PERMISSION_GUEST_REQUEST.replace("$user", user_name);

                    admin_message
                            .setParseMode(ParseMode.HTML)
                            .setText(admin_message_text);

                    send_message_toadmins(admin_message);
                }
            }

//------------------------------Chats handling------------------------------//

            else if (call_data.startsWith("chats") && user_manager.checkWhitelist(user_id) && !user_manager.checkAttribute(user_id, "guest") && state == null) {
                String[] call_data_parts = call_data.split(" ");

                // Load main chat menu
                if (call_data_parts[1].equals("menu")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildChatsMenu();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_MENU);

                    edit_message(message);
                }

                // Load common ingress chats
                else if (call_data_parts[1].equals("common")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildChatsCommon(user_id);

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_COMMON);

                    edit_message(message);
                }

                // Load specific ingress chats
                else if (call_data_parts[1].equals("specific")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildChatsSpecific(user_id);

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_SPECIFIC);

                    edit_message(message);
                }

                // Load specific ingress chats
                else if (call_data_parts[1].equals("none")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildChatsNone();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_NONE);

                    edit_message(message);
                }

                // Load city chats
                else if (call_data_parts[1].equals("city")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildChatsCity();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_CITY);

                    edit_message(message);
                }

                // User has no rights for this chat
                else if (call_data_parts[1].equals("norights")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildBackChatsMenu();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_NORIGHTS);

                    edit_message(message);
                }

                // Chatlinks wasnt found
                else if (call_data_parts[1].equals("notfound")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildBackChatsMenu();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_NOTFOUND);

                    edit_message(message);
                }

                // Music chat
                else if (call_data_parts[1].equals("music")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildBackChatsMenu();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(MESSAGE_CHATS_MUSIC);

                    edit_message(message);
                }
            }

//------------------------------Admin tools------------------------------//

            else if (call_data.startsWith("admintools") && user_manager.checkAdmin(user_id) && state == null) {
                String[] call_data_parts = call_data.split(" ");

                // Load admin tools menu
                if (call_data_parts[1].equals("menu")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildAdminMenu();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText("Admin Tools:");

                    edit_message(message);
                }

                // Load currenty open Requests
                else if (call_data_parts[1].equals("loadrequests")) {

                    if (open_requests.size() == 0) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools menu"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_PERMISSION_NO_OPENREQUESTS);

                        edit_message(message);
                    }

                    else {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildOpenRequests();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_PERMISSION_OPENREQUESTS);

                        edit_message(message);
                    }
                }

                // Load specific request
                else if (call_data_parts[1].equals("loadopenuser")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);

                    User open_user = open_requests.get(open_user_id);

                    String open_user_name = open_user.getUser_name();

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildRequestAnswers(open_user_id);

                    String message_text;
                    if (open_user.getGuest()) {
                        message_text = MESSAGE_PERMISSION_OPENUSER_GUEST.replace("$user", open_user_name);
                    } else {
                        message_text = MESSAGE_PERMISSION_OPENUSER.replace("$user", open_user_name);
                    }

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    edit_message(message);
                }

                // Permission was granted
                else if (call_data_parts[1].equals("grant")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);
                    User user = open_requests.get(open_user_id);

                    //What if open request doesnt exist anymore?
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_NULLREQUEST);

                        edit_message(message);
                    }

                    else {
                        open_requests.remove(open_user_id);

                        user_manager.whitelistUser(user);

                        // Send message to user, to tell him, he was whitelisted
                        SendMessage message = new SendMessage();

                        String message_text;
                        if (user.getGuest()) {
                            message_text = MESSAGE_PERMISSION_GUEST_GRANTED;
                        } else {
                            message_text = MESSAGE_PERMISSION_GRANTED;
                        }

                        message
                                .setChatId(user.getId())
                                .setParseMode(ParseMode.HTML)
                                .setText(message_text);

                        send_message(message);

                        // Edit admin message to tell him, he is done
                        EditMessageText new_message = new EditMessageText();

                        String new_message_text;
                        if (user.getGuest()) {
                            new_message_text = MESSAGE_PERMISSION_GUEST_GRANTED_ADMIN.replace("$user", user.getUser_name());
                        } else {
                            new_message_text = MESSAGE_PERMISSION_GRANTED_ADMIN.replace("$user", user.getUser_name());
                        }

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools loadrequests"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        new_message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(new_message_text);

                        edit_message(new_message);
                    }
                }

                // Permission was denied
                else if (call_data_parts[1].equals("deny")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);
                    User user = open_requests.get(open_user_id);

                    //What if open request doesnt exist anymore?
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_NULLREQUEST);

                        edit_message(message);
                    }

                    else {
                        open_requests.remove(open_user_id);

                        // Send message to user, to tell him, that his request was denied
                        SendMessage message = new SendMessage();

                        message
                                .setChatId(user.getId())
                                .setParseMode(ParseMode.HTML)
                                .setText(MESSAGE_PERMISSION_DENIED);

                        send_message(message);

                        // Edit admin message to tell him, he is done
                        EditMessageText new_message = new EditMessageText();

                        String new_message_text = MESSAGE_PERMISSION_DENIED_ADMIN.replace("$user", user.getUser_name());

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools loadrequests"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        new_message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(new_message_text);

                        edit_message(new_message);
                    }
                }

                // Permission was denied and user was blocked
                else if (call_data_parts[1].equals("denyandblock")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);
                    User user = open_requests.get(open_user_id);

                    //What if open request doesnt exist anymore?
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_NULLREQUEST);

                        edit_message(message);
                    }

                    else {
                        open_requests.remove(open_user_id);

                        user_manager.blockUser(user);

                        // Send message to user, to tell him, that his request was denied and he got blocked
                        SendMessage message = new SendMessage();

                        message
                                .setChatId(user.getId())
                                .setParseMode(ParseMode.HTML)
                                .setText(MESSAGE_PERMISSION_BLOCKED);

                        send_message(message);

                        // Edit admin message to tell him, he is done
                        EditMessageText new_message = new EditMessageText();

                        String new_message_text = MESSAGE_PERMISSION_BLOCKED_ADMIN.replace("$user", user.getUser_name());

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools loadrequests"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        new_message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(new_message_text);

                        edit_message(new_message);
                    }
                }

                //Load current whitelist
                else if (call_data_parts[1].equals("whitelist")) {
                    List<User> whitelist = user_manager.buildUserList();
                    List<User> guestlist = user_manager.buildGuestList();

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildWhitelistMenu();

                    String message_text = MESSAGE_ADMINTOOLS_WHITELIST;

                    for (User user : whitelist) {
                        message_text += "<code>" + user.getUser_name() + "</code>     ";
                        if (user_manager.checkAdmin(user.getId())) {
                            message_text += "<i>A</i> ";
                        }
                        if (user_manager.checkAttribute(user.getId(), "mod")) {
                            message_text += "<i>M</i> ";
                        }
                        if (user_manager.checkAttribute(user.getId(), "operator")) {
                            message_text += "<i>O</i> ";
                        }
                        if (user_manager.checkAttribute(user.getId(), "hightrust")) {
                            message_text += "<i>H</i> ";
                        }
                        message_text += "\n";
                    }

                    message_text += "\n Gäste: \n\n";

                    for (User user : guestlist) {
                        message_text += "<code>" + user.getUser_name() + "</code>\n";
                    }

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    edit_message(message);
                }

                // Load specific user on whitelist
                else if (call_data_parts[1].equals("loaduser")) {
                    stateMap.put(user_id, State.ADMINLOADUSER);

                    EditMessageText message = new EditMessageText();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setText(MESSAGE_ADMINTOOLS_LOADUSER);

                    edit_message(message);
                }

                // Change Admin Status
                else if (call_data_parts[1].equals("changeadmin")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    // Cant change own admin status
                    else if (user_id == user.getId()) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackToSelf();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_CANTCHANGEOWNADMIN);

                        edit_message(message);
                    }

                    // Cant change master admin
                    else if (user.getId() == ADMIN_CHAT_ID) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackToMaster();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_CANTCHANGEMASTER);

                        edit_message(message);
                    }

                    // Change Admin Status
                    else {
                        user.changeAdmin();
                        user_manager.saveUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildUserMenu(user);

                        String message_text = MESSAGE_ADMINTOOLS_USER_CHANGED
                                .replace("$status", "Adminstatus")
                                .replace("$user", user.getUser_name())
                                .replace("$admin", String.valueOf(user.getAdmin()))
                                .replace("$mod", String.valueOf(user.getMod()))
                                .replace("$operator", String.valueOf(user.getOperator()))
                                .replace("$hightrust", String.valueOf(user.getHightrust()));

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                // Change Mod Status
                else if (call_data_parts[1].equals("changemod")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    // Change Mod Status
                    else {
                        user.changeMod();
                        user_manager.saveUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildUserMenu(user);

                        String message_text = MESSAGE_ADMINTOOLS_USER_CHANGED
                                .replace("$status", "Modstatus")
                                .replace("$user", user.getUser_name())
                                .replace("$admin", String.valueOf(user.getAdmin()))
                                .replace("$mod", String.valueOf(user.getMod()))
                                .replace("$operator", String.valueOf(user.getOperator()))
                                .replace("$hightrust", String.valueOf(user.getHightrust()));

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                // Change Operator Status
                else if (call_data_parts[1].equals("changeoperator")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    // Change Operator Status
                    else {
                        user.changeOperator();
                        user_manager.saveUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildUserMenu(user);

                        String message_text = MESSAGE_ADMINTOOLS_USER_CHANGED
                                .replace("$status", "Operatorstatus")
                                .replace("$user", user.getUser_name())
                                .replace("$admin", String.valueOf(user.getAdmin()))
                                .replace("$mod", String.valueOf(user.getMod()))
                                .replace("$operator", String.valueOf(user.getOperator()))
                                .replace("$hightrust", String.valueOf(user.getHightrust()));

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                // Change Hightrust Status
                else if (call_data_parts[1].equals("changehightrust")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    // Change Hightrust Status
                    else {
                        user.changeHightrust();
                        user_manager.saveUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildUserMenu(user);

                        String message_text = MESSAGE_ADMINTOOLS_USER_CHANGED
                                .replace("$status", "Hightruststatus")
                                .replace("$user", user.getUser_name())
                                .replace("$admin", String.valueOf(user.getAdmin()))
                                .replace("$mod", String.valueOf(user.getMod()))
                                .replace("$operator", String.valueOf(user.getOperator()))
                                .replace("$hightrust", String.valueOf(user.getHightrust()));

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                // Change Guest to regular user or the other way around
                else if (call_data_parts[1].equals("changeguest")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    // Make sure an admin cant change himself to guest
                    else if (user_id == user.getId()) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackToSelf();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_CANTGUESTSELF);

                        edit_message(message);
                    }

                    //Make sure an admin cant block the master either
                    else if (user.getId() == ADMIN_CHAT_ID) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackToMaster();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_CANTGUESTMASTER);

                        edit_message(message);
                    }

                    // Change Guest to regular user
                    else if (user.getGuest()){
                        user.changeGuest();
                        user.setAdmin(false);
                        user.setOperator(false);
                        user.setMod(false);
                        user.setHightrust(false);
                        user_manager.saveUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildUserMenu(user);

                        String message_text = MESSAGE_ADMINTOOLS_USER_CHANGED
                                .replace("$status", "Gaststatus")
                                .replace("$user", user.getUser_name())
                                .replace("$admin", String.valueOf(user.getAdmin()))
                                .replace("$mod", String.valueOf(user.getMod()))
                                .replace("$operator", String.valueOf(user.getOperator()))
                                .replace("$hightrust", String.valueOf(user.getHightrust()));

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }

                    // Change regular user to guest
                    else if (!user.getGuest()){
                        user.changeGuest();
                        user.setAdmin(false);
                        user.setOperator(false);
                        user.setMod(false);
                        user.setHightrust(false);
                        user_manager.saveUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildGuest(user);

                        String message_text = MESSAGE_ADMINTOOLS_GUESTFOUND
                                .replace("$user", user.getUser_name());

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                else if (call_data_parts[1].equals("block")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    // Make sure an admin cant block himself
                    else if (user_id == user.getId()) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackToSelf();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_CANTBLOCKSELF);

                        edit_message(message);
                    }

                    //Make sure an admin cant block the master either
                    else if (user.getId() == ADMIN_CHAT_ID) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackToMaster();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_CANTBLOCKMASTER);

                        edit_message(message);
                    }

                    else {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBlockOptions(user);

                        String message_text = MESSAGE_ADMINTOOLS_ABOUTTOBLOCK.replace("$user", user.getUser_name());

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                else if (call_data_parts[1].equals("blockapproval")) {
                    User user = user_manager.loadUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        edit_message(message);
                    }

                    else {
                        user_manager.blockUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        String message_text = MESSAGE_ADMINTOOLS_BLOCKED.replace("$user", user.getUser_name());

                        message
                                .setChatId(user_id)
                                .setParseMode(ParseMode.HTML)
                                .setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard)
                                .setText(message_text);

                        edit_message(message);
                    }
                }

                // Go back to self after Error
                else if (call_data_parts[1].equals("backtoself")) {
                    User user = user_manager.loadUser(user_name);

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildUserMenu(user);

                    String message_text = MESSAGE_ADMINTOOLS_USERFOUND
                            .replace("$user", user.getUser_name())
                            .replace("$admin", String.valueOf(user.getAdmin()))
                            .replace("$mod", String.valueOf(user.getMod()))
                            .replace("$operator", String.valueOf(user.getOperator()))
                            .replace("$hightrust", String.valueOf(user.getHightrust()));

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    edit_message(message);
                }

                // Go back to master  after Error
                else if (call_data_parts[1].equals("backtomaster")) {
                    User user = user_manager.loadUser(ADMIN_USER_NAME);

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildUserMenu(user);

                    String message_text = MESSAGE_ADMINTOOLS_USERFOUND
                            .replace("$user", user.getUser_name())
                            .replace("$admin", String.valueOf(user.getAdmin()))
                            .replace("$mod", String.valueOf(user.getMod()))
                            .replace("$operator", String.valueOf(user.getOperator()))
                            .replace("$hightrust", String.valueOf(user.getHightrust()));

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    edit_message(message);
                }

                //Load current blocklist
                else if (call_data_parts[1].equals("blocklist")) {
                    List<User> blocklist = user_manager.buildBlockList();

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildBlocklistMenu();

                    String message_text = MESSAGE_ADMINTOOLS_BLOCKLIST;

                    for (User user : blocklist) {
                        message_text += "<code>" + user.getUser_name() + "</code>\n";
                    }

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard)
                            .setText(message_text);

                    edit_message(message);
                }

                // Load specific user on whitelist
                else if (call_data_parts[1].equals("unblock")) {
                    stateMap.put(user_id, State.ADMINUNBLOCK);

                    EditMessageText message = new EditMessageText();

                    message
                            .setChatId(user_id)
                            .setParseMode(ParseMode.HTML)
                            .setMessageId(toIntExact(message_id))
                            .setText(MESSAGE_ADMINTOOLS_UNBLOCK);

                    edit_message(message);
                }
            }

//------------------------------Go back to main menu------------------------------//

            else if (call_data.equals("mainmenu") && user_manager.checkWhitelist(user_id)) {
                EditMessageText message = new EditMessageText();

                InlineKeyboardMarkup keyboard = buildMainMenu(user_id);

                String message_text = MESSAGE_MENU_MAIN.replace("$user", user_name);

                message
                        .setChatId(user_id)
                        .setParseMode(ParseMode.HTML)
                        .setMessageId(toIntExact(message_id))
                        .setReplyMarkup(keyboard)
                        .setText(message_text);

                edit_message(message);
            }

//------------------------------Option to close Menu------------------------------//

            else if (call_data.equals("close")) {
                EditMessageText message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(MESSAGE_MENU_CLOSED);

                edit_message(message);
            }

//------------------------------Make sure, blocked users cant use old and still open menus------------------------------//

            else if (user_manager.checkBlocklist(user_id)) {
                EditMessageText message = new EditMessageText()
                        .setChatId(chat_id)
                        .setMessageId(toIntExact(message_id))
                        .setText(MESSAGE_BLOCKED);

                edit_message(message);
            }
        }

    }

    @Override
    public String getBotUsername() {
        return /*cleaned*/;
    }

    @Override
    public String getBotToken() {
        return /*cleaned*/;
    }

//******************************Help methods******************************//

    private void send_message(SendMessage message){
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void edit_message(EditMessageText message){
        try {
            execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void send_message_toadmins(SendMessage message){
        List<User> admin_list = user_manager.buildAdminList();

        for (User user : admin_list) {
            long admin_id = user.getId();
            message.setChatId(admin_id);
            send_message(message);
        }
    }

    private boolean allreadyRequested(long user_id) {
        for (User user : open_requests.values()) {
            if (user_id == user.getId()) {
                return true;
            }
        }
        return false;
    }

    private InlineKeyboardMarkup buildMainMenu(long user_id) {

        InlineKeyboardMarkup main_menu = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("Chats").setCallbackData("chats menu"));

        buttons.add(row1);

        if (user_manager.checkAdmin(user_id)){
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

    private InlineKeyboardMarkup buildOpenMenu() {

        InlineKeyboardMarkup open_menu = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("Offener Chat der ENLDD").setUrl(URL_CHAT_OPEN));
        row2.add(new InlineKeyboardButton().setText("Rechte anfragen").setCallbackData("permission request"));
        row3.add(new InlineKeyboardButton().setText("Gastrechte anfragen").setCallbackData("permission requestguest"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);

        open_menu.setKeyboard(buttons);

        return open_menu;
    }

    private InlineKeyboardMarkup buildGuestMenu() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("Offener Chat der ENLDD").setUrl(URL_CHAT_OPEN));
        row2.add(new InlineKeyboardButton().setText("Gästechat").setUrl(URL_CHAT_VISITORS));
        row3.add(new InlineKeyboardButton().setText("Menü schließen").setCallbackData("close"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);

        keyboard.setKeyboard(buttons);

        return keyboard;
    }

    private InlineKeyboardMarkup buildAdminMenu(){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Offene Requests").setCallbackData("admintools loadrequests"));
        row2.add(new InlineKeyboardButton().setText("Whitelist").setCallbackData("admintools whitelist"));
        row2.add(new InlineKeyboardButton().setText("Blocklist").setCallbackData("admintools blocklist"));
        row3.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("mainmenu"));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildOpenRequests() {

        InlineKeyboardMarkup requests = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for(int i = 0; i < open_requests.size(); i++) {
            buttons.add(new ArrayList<>());
        }

        int i = 0;
        for (long user_id : open_requests.keySet()) {
            User user = open_requests.get(user_id);
            String user_name = user.getUser_name();
            String buttontext;
            if (user.getGuest()) {
                buttontext = "Gastanfrage: " + user_name;
            } else {
                buttontext = user_name;
            }
            InlineKeyboardButton user_button = new InlineKeyboardButton()
                    .setText(buttontext)
                    .setCallbackData("admintools loadopenuser " + String.valueOf(user_id));
            buttons.get(i).add(user_button);
            i++;
        }

        List<InlineKeyboardButton> lastbutton = new ArrayList<>();
        lastbutton.add(new InlineKeyboardButton().setText("Zurück.").setCallbackData("admintools menu"));
        buttons.add(lastbutton);

        requests.setKeyboard(buttons);

        return requests;
    }

    private InlineKeyboardMarkup buildRequestAnswers(long user_id){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Ja").setCallbackData("admintools grant " + String.valueOf(user_id)));
        row.add(new InlineKeyboardButton().setText("Nein").setCallbackData("admintools deny " + String.valueOf(user_id)));
        row.add(new InlineKeyboardButton().setText("Block").setCallbackData("admintools denyandblock " + String.valueOf(user_id)));
        row2.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools loadrequests"));

        buttons.add(row);
        buttons.add(row2);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildUserMenu(User user){
        String user_name = user.getUser_name();
        boolean admin = user.getAdmin();
        boolean mod = user.getMod();
        boolean operator = user.getOperator();
        boolean hightrust = user.getHightrust();

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Set Admin " + String.valueOf(!admin)).setCallbackData("admintools changeadmin " + user_name));
        row.add(new InlineKeyboardButton().setText("Set Mod " + String.valueOf(!mod)).setCallbackData("admintools changemod " + user_name));
        row2.add(new InlineKeyboardButton().setText("Set Operator " + String.valueOf(!operator)).setCallbackData("admintools changeoperator " + user_name));
        row2.add(new InlineKeyboardButton().setText("Set Hightrust " + String.valueOf(!hightrust)).setCallbackData("admintools changehightrust " + user_name));
        row3.add(new InlineKeyboardButton().setText("Change to guest").setCallbackData("admintools changeguest " + user_name));
        row3.add(new InlineKeyboardButton().setText("Block").setCallbackData("admintools block " + user_name));
        row4.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildGuest(User user) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Change to regular user").setCallbackData("admintools changeguest " + user.getUser_name()));
        row2.add(new InlineKeyboardButton().setText("Block").setCallbackData("admintools block " + user.getUser_name()));
        row3.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildWhitelistMenu(){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Spezifischen Nutzer laden").setCallbackData("admintools loaduser"));
        row2.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools menu"));

        buttons.add(row);
        buttons.add(row2);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBlocklistMenu(){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Unblock").setCallbackData("admintools unblock"));
        row2.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools menu"));

        buttons.add(row);
        buttons.add(row2);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBlockOptions(User user) {
        String user_name = user.getUser_name();

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("BLOCK").setCallbackData("admintools blockapproval " + user_name));
        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBackButtonAdmintools(){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools menu"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBackToSelf(){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools backtoself"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBackToMaster(){

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools backtomaster"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildChatsMenu() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("Allgemein Ingress").setCallbackData("chats common"));
        row1.add(new InlineKeyboardButton().setText("Spezifisch Ingress").setCallbackData("chats specific"));
        row2.add(new InlineKeyboardButton().setText("None Ingress").setCallbackData("chats none"));
        row2.add(new InlineKeyboardButton().setText("Stadtteilchats").setCallbackData("chats city"));
        row3.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("mainmenu"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildChatsCommon(long user_id) {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();

        if (user_manager.checkAttribute(user_id, "mod")) {
            row1.add(new InlineKeyboardButton().setText("[0] Mod").setUrl(URL_CHAT_MOD));
        } else {
            row1.add(new InlineKeyboardButton().setText("[0] Mod").setCallbackData("chats norights"));
        }
        row1.add(new InlineKeyboardButton().setText("[1] Alarm").setUrl(URL_CHAT_ALARM));
        row2.add(new InlineKeyboardButton().setText("[2] News").setUrl(URL_CHAT_NEWS));
        row2.add(new InlineKeyboardButton().setText("[3] Intern").setUrl(URL_CHAT_INTERN));
        row3.add(new InlineKeyboardButton().setText("[4] Events").setUrl(URL_CHAT_EVENTS));
        row3.add(new InlineKeyboardButton().setText("[5] Open").setUrl(URL_CHAT_OPEN));
        row4.add(new InlineKeyboardButton().setText("[6] Passcodes").setUrl(URL_CHAT_PASSCODES));
        row4.add(new InlineKeyboardButton().setText("[7] Trading").setUrl(URL_CHAT_TRADING));
        row5.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats menu"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);
        buttons.add(row5);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildChatsSpecific(long user_id) {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();

        if (user_manager.checkAttribute(user_id, "hightrust")) {
            row1.add(new InlineKeyboardButton().setText("[10] Fight4CP").setUrl(URL_CHAT_FIGHT));
            row1.add(new InlineKeyboardButton().setText("[11] Grünflächenamt").setUrl(URL_CHAT_GRUENFLAECHE));
        } else {
            row1.add(new InlineKeyboardButton().setText("[10] Fight4CP").setCallbackData("chats norights"));
            row1.add(new InlineKeyboardButton().setText("[11] Grünflächenamt").setCallbackData("chats norights"));
        }

        if (user_manager.checkAttribute(user_id, "operator")) {
            row2.add(new InlineKeyboardButton().setText("[12] OPs").setUrl(URL_CHAT_OPS));
        } else {
            row2.add(new InlineKeyboardButton().setText("[12] OPs").setCallbackData("chats norights"));
        }

        row2.add(new InlineKeyboardButton().setText("[13.37] Badge Bitchez").setUrl(URL_CHAT_BADGE));
        row3.add(new InlineKeyboardButton().setText("[14] Visitors").setUrl(URL_CHAT_VISITORS));
        row3.add(new InlineKeyboardButton().setText("[15] Missions").setUrl(URL_CHAT_MISSIONS));
        row4.add(new InlineKeyboardButton().setText("[16] V").setUrl(URL_CHAT_V));
        row4.add(new InlineKeyboardButton().setText("[brum] Cargress").setUrl(URL_CHAT_CARGRESS));
        row5.add(new InlineKeyboardButton().setText("[23] Codes").setUrl(URL_CHAT_CODES));
        row5.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats menu"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);
        buttons.add(row5);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildChatsNone() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("[100] QmS").setUrl(URL_CHAT_QMS));
        row1.add(new InlineKeyboardButton().setText("[101] Meme").setUrl(URL_CHAT_MEME));
        row2.add(new InlineKeyboardButton().setText("[314] Pinguine").setUrl(URL_CHAT_PINGUINE));
        row2.add(new InlineKeyboardButton().setText("[404] TG-Bots").setUrl(URL_CHAT_TGBOTS));
        row3.add(new InlineKeyboardButton().setText("[420] Music").setCallbackData("chats music"));
        row3.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats menu"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildChatsCity() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();
        List<InlineKeyboardButton> row6 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("ENLDD Uni+SVS").setCallbackData("chats notfound"));
        row1.add(new InlineKeyboardButton().setText("RSML").setUrl(URL_CHAT_RSML));
        row2.add(new InlineKeyboardButton().setText("ENL 4 KPTM").setCallbackData("chats norights"));
        row2.add(new InlineKeyboardButton().setText("LAEP").setUrl(URL_CHAT_LAEP));
        row3.add(new InlineKeyboardButton().setText("Plauen-Löbtau").setUrl(URL_CHAT_LOEBTAU));
        row3.add(new InlineKeyboardButton().setText("Striesen/Tolkewitz").setCallbackData("chats norights"));
        row4.add(new InlineKeyboardButton().setText("ENL4GOCO").setUrl(URL_CHAT_GOCO));
        row4.add(new InlineKeyboardButton().setText("Jotown").setCallbackData("chats norights"));
        row5.add(new InlineKeyboardButton().setText("ENL DD Nord").setUrl(URL_CHAT_NORD));
        row5.add(new InlineKeyboardButton().setText("ENL 4 WS").setUrl(URL_CHAT_WS));
        row6.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats menu"));

        buttons.add(row1);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);
        buttons.add(row5);
        buttons.add(row6);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBackChatsMenu() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row1 = new ArrayList<>();

        row1.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("chats menu"));

        buttons.add(row1);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private enum State{
        ADMINLOADUSER, ADMINUNBLOCK
    }
}