package de.wursteintopf.bot.handler;

import static java.lang.Math.toIntExact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.wursteintopf.bot.ENLDD;
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

public class AdminToolsHandler {

    private static Logger log = LoggerFactory.getLogger(AdminToolsHandler.class);

    private Map<Long, State> stateMap;
    private Map<Long, AbstractUser> open_requests;

    private UserManager user_manager;
    private ENLDD enldd;

    public AdminToolsHandler(ENLDD enldd, UserManager userManager, Map<Long, State> stateMap) {
        this.enldd = enldd;
        this.user_manager = userManager;
        this.stateMap = stateMap;
        this.open_requests = new HashMap<>();
    }

    public void onUpdateReceived(Update update) {

        /*----------Update has text----------*/

        if (update.hasMessage() && update.getMessage().hasText()) {

            long user_id = update.getMessage().getFrom().getId();
            String user_name = update.getMessage().getFrom().getUserName();

            String text = update.getMessage().getText();

            State state = stateMap.get(user_id);

            /*----------Admin sent username to find a user----------*/

            if (user_manager.checkAdmin(user_id) && state == State.ADMIN_LOADUSER && !text.equals("/cancel")) {
                loadUserEntry(text, user_id);
            }

            /*----------Admin sent username to unblock a user----------*/

            else if (user_manager.checkAdmin(user_id) && state == State.ADMIN_UNBLOCKUSER && !text.equals("/cancel")) {
                Blocked user = user_manager.loadBlockedUser(text);

                // User wasn't found
                if (user == null) {
                    SendMessage message = new SendMessage().setChatId(user_id).setParseMode(ParseMode.HTML).setText(
                            MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                    enldd.send_message(message);
                } else {
                    stateMap.remove(user_id);

                    user_manager.unblock(user.getId());

                    SendMessage message = new SendMessage();

                    InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                    String message_text = MessageConstants.MESSAGE_ADMINTOOLS_USERUNBLOCKED.replace("$user",
                            user.getUserName());

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setReplyMarkup(keyboard).setText(
                            message_text);

                    enldd.send_message(message);
                }
            }
        }

        /*----------Update has a Callback Query (aka Button was clicked)----------*/

        if (update.hasCallbackQuery()) {

            String call_data = update.getCallbackQuery().getData();

            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            long user_id = update.getCallbackQuery().getFrom().getId();
            String user_name = update.getCallbackQuery().getFrom().getUserName();

            State state = stateMap.get(user_id);

            /*----------Permission handling----------*/

            if (call_data.startsWith("permission") && !user_manager.checkBlocklist(user_id) && state == null) {
                String[] call_data_parts = call_data.split(" ");

                // User is allready on whitelist
                if (user_manager.checkWhitelist(user_id)) {
                    EditMessageText message = new EditMessageText();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setText(MessageConstants.MESSAGE_PERMISSION_ALLREADYONWHITELIST);

                    enldd.edit_message(message);
                }

                // User allready requested Permission
                else if (open_requests.keySet().contains(user_id)) {
                    EditMessageText message = new EditMessageText();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setText(MessageConstants.MESSAGE_PERMISSION_ALLREADYREQUESTED);

                    enldd.edit_message(message);
                }

                // New User requested permission
                else if (call_data_parts[1].equals("request")) {

                    // Add user request to List of open requests
                    open_requests.put(user_id, new User(user_id, user_name));

                    // Edit user message to tell him, that his request got received
                    EditMessageText message = new EditMessageText();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setText(MessageConstants.MESSAGE_PERMISSION_USER_REQUEST_RECEIVED);

                    enldd.edit_message(message);

                    // Send message to Admingroup, that there is a new request
                    SendMessage admin_message = new SendMessage();

                    String admin_message_text = MessageConstants.MESSAGE_PERMISSION_USER_REQUEST.replace("$user",
                            user_name);

                    admin_message.setParseMode(ParseMode.HTML).setText(admin_message_text);

                    enldd.send_message_toadmins(admin_message);
                }

                // New Guest requested permission
                else if (call_data_parts[1].equals("requestguest")) {

                    // Add user request to List of open requests
                    Guest guest = new Guest(user_id, user_name);
                    open_requests.put(user_id, guest);

                    // Edit user message to tell him, that his request got received
                    EditMessageText message = new EditMessageText();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setText(MessageConstants.MESSAGE_PERMISSION_USER_REQUEST_RECEIVED);

                    enldd.edit_message(message);

                    // Send message to Admingroup, that there is a new request
                    SendMessage admin_message = new SendMessage();

                    String admin_message_text = MessageConstants.MESSAGE_PERMISSION_GUEST_REQUEST.replace("$user",
                            user_name);

                    admin_message.setParseMode(ParseMode.HTML).setText(admin_message_text);

                    enldd.send_message_toadmins(admin_message);
                }
            }

            /*----------Admintools----------*/

            else if (call_data.startsWith("admintools") && user_manager.checkAdmin(user_id) && state == null) {
                String[] call_data_parts = call_data.split(" ");

                // Load admin tools menu
                if (call_data_parts[1].equals("menu")) {
                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildAdminMenu();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard).setText("Admin Tools:");

                    enldd.edit_message(message);
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

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_PERMISSION_NO_OPENREQUESTS);

                        enldd.edit_message(message);
                    } else {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildOpenRequests();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_PERMISSION_OPENREQUESTS);

                        enldd.edit_message(message);
                    }
                }

                // Load specific request
                else if (call_data_parts[1].equals("loadopenuser")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);

                    AbstractUser open_user = open_requests.get(open_user_id);

                    String open_user_name = open_user.getUserName();

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildRequestAnswers(open_user_id);

                    String message_text;
                    if (open_user instanceof Guest) {
                        message_text = MessageConstants.MESSAGE_PERMISSION_OPENUSER_GUEST.replace("$user",
                                open_user_name);
                    } else {
                        message_text = MessageConstants.MESSAGE_PERMISSION_OPENUSER.replace("$user", open_user_name);
                    }

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard).setText(message_text);

                    enldd.edit_message(message);
                }

                // Permission was granted
                else if (call_data_parts[1].equals("grant")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);
                    AbstractUser user = open_requests.get(open_user_id);

                    //What if open request doesnt exist anymore?
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_NULLREQUEST);

                        enldd.edit_message(message);
                    } else {
                        open_requests.remove(open_user_id);

                        user_manager.whitelistUser(user);

                        // Send message to user, to tell him, he was whitelisted
                        SendMessage message = new SendMessage();

                        String message_text;
                        if (user instanceof Guest) {
                            message_text = MessageConstants.MESSAGE_PERMISSION_GUEST_GRANTED;
                        } else {
                            message_text = MessageConstants.MESSAGE_PERMISSION_GRANTED;
                        }

                        message.setChatId(user.getId()).setParseMode(ParseMode.HTML).setText(message_text);

                        enldd.send_message(message);

                        // Inform admins that a request was granted
                        SendMessage admin_message = new SendMessage();
                        String admin_message_text;
                        if (user instanceof Guest) {
                            admin_message_text = MessageConstants.MESSAGE_PERMISSION_GUEST_REQUEST_GRANTED.replace(
                                    "$user", user.getUserName()).replace("$admin", user_name);
                        } else {
                            admin_message_text = MessageConstants.MESSAGE_PERMISSION_USER_REQUEST_GRANTED.replace(
                                    "$user", user.getUserName()).replace("$admin", user_name);
                        }

                        admin_message.setParseMode(ParseMode.HTML).setText(admin_message_text);

                        enldd.send_message_toadmins(admin_message);

                        // Edit admin message to tell him, he is done
                        EditMessageText new_message = new EditMessageText();

                        String new_message_text;
                        if (user instanceof Guest) {
                            new_message_text = MessageConstants.MESSAGE_PERMISSION_GUEST_GRANTED_ADMIN.replace("$user",
                                    user.getUserName());
                        } else {
                            new_message_text = MessageConstants.MESSAGE_PERMISSION_GRANTED_ADMIN.replace("$user",
                                    user.getUserName());
                        }

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück")
                                .setCallbackData("admintools loadrequests"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        new_message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(new_message_text);

                        enldd.edit_message(new_message);
                    }
                }

                // Permission was denied
                else if (call_data_parts[1].equals("deny")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);
                    AbstractUser user = open_requests.get(open_user_id);

                    //What if open request doesnt exist anymore?
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_NULLREQUEST);

                        enldd.edit_message(message);
                    } else {
                        open_requests.remove(open_user_id);

                        // Send message to user, to tell him, that his request was denied
                        SendMessage message = new SendMessage();

                        message.setChatId(user.getId()).setParseMode(ParseMode.HTML).setText(
                                MessageConstants.MESSAGE_PERMISSION_DENIED);

                        enldd.send_message(message);

                        // Inform admins that a request was denied
                        SendMessage admin_message = new SendMessage();
                        String admin_message_text = MessageConstants.MESSAGE_PERMISSION_REQUEST_DENIED.replace(
                                    "$user", user.getUserName()).replace("$admin", user_name);


                        admin_message.setParseMode(ParseMode.HTML).setText(admin_message_text);
                        enldd.send_message_toadmins(admin_message);

                        // Edit admin message to tell him, he is done
                        EditMessageText new_message = new EditMessageText();

                        String new_message_text = MessageConstants.MESSAGE_PERMISSION_DENIED_ADMIN.replace("$user",
                                user.getUserName());

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück")
                                .setCallbackData("admintools loadrequests"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        new_message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(new_message_text);

                        enldd.edit_message(new_message);
                    }
                }

                // Permission was denied and user was blocked
                else if (call_data_parts[1].equals("denyandblock")) {
                    long open_user_id = Long.valueOf(call_data_parts[2]);
                    AbstractUser user = open_requests.get(open_user_id);

                    //What if open request doesnt exist anymore?
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_NULLREQUEST);

                        enldd.edit_message(message);
                    } else {
                        open_requests.remove(open_user_id);

                        user_manager.blockUser(user);

                        // Send message to user, to tell him, that his request was denied and he got blocked
                        SendMessage message = new SendMessage();

                        message.setChatId(user.getId()).setParseMode(ParseMode.HTML).setText(
                                MessageConstants.MESSAGE_PERMISSION_BLOCKED);

                        enldd.send_message(message);

                        // Edit admin message to tell him, he is done
                        EditMessageText new_message = new EditMessageText();

                        String new_message_text = MessageConstants.MESSAGE_PERMISSION_BLOCKED_ADMIN.replace("$user",
                                user.getUserName());

                        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
                        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
                        List<InlineKeyboardButton> row = new ArrayList<>();

                        row.add(new InlineKeyboardButton().setText("Zurück")
                                .setCallbackData("admintools loadrequests"));

                        buttons.add(row);

                        keyboard.setKeyboard(buttons);

                        new_message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(new_message_text);

                        enldd.edit_message(new_message);
                    }
                }

                //Load current whitelist
                else if (call_data_parts[1].equals("whitelist")) {
                    List<Admin> adminlist = user_manager.buildAdminList();
                    List<User> whitelist = user_manager.buildUserList();
                    List<Guest> guestlist = user_manager.buildGuestList();

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildWhitelistMenu();

                    String message_text = MessageConstants.MESSAGE_ADMINTOOLS_WHITELIST;

                    message_text += "Admins: \n";
                    for (Admin admin : adminlist) {
                        message_text += "<code>" + admin.getUserName() + "</code>\n";
                    }

                    message_text += "\nUsers: \n";
                    for (User user : whitelist) {
                        message_text += "<code>" + user.getUserName() + "</code>     ";

                        for (UserPermission permission : user.getPermissions()) {
                            message_text += "<i>" + String.valueOf(permission.toString().charAt(0)) + "</i> ";
                        }

                        message_text += "\n";
                    }

                    message_text += "\nGäste: \n";

                    for (Guest guest : guestlist) {
                        message_text += "<code>" + guest.getUserName() + "</code>\n";
                    }

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard).setText(message_text);

                    enldd.edit_message(message);
                }

                // Load specific user on whitelist
                else if (call_data_parts[1].equals("loaduser")) {
                    stateMap.put(user_id, State.ADMIN_LOADUSER);

                    EditMessageText message = new EditMessageText();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setText(MessageConstants.MESSAGE_ADMINTOOLS_LOADUSER);

                    enldd.edit_message(message);
                }

                // Change Admin Status
                else if (call_data_parts[1].equals("changeadmin")) {
                    AbstractUser abstractUser = user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (abstractUser == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    }

                    // If Admin change to regular user
                    else if (abstractUser instanceof Admin) {
                        user_manager.changeUser(new User(abstractUser.getId(), abstractUser.getUserName()));
                        reloadUserEntry(message_id, abstractUser.getUserName(), user_id);
                    }

                    // Otherwise change to Admin
                    else {
                        user_manager.changeUser(new Admin(abstractUser.getId(), abstractUser.getUserName()));
                        reloadUserEntry(message_id, abstractUser.getUserName(), user_id);
                    }
                }

                // Change Mod Status
                else if (call_data_parts[1].equals("changemod")) {
                    User user = (User) user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    }

                    // Change Mod Status
                    else {
                        user.changePermission(UserPermission.MOD);
                        user_manager.changeUser(user);
                        reloadUserEntry(message_id, user.getUserName(), user_id);
                    }
                }

                // Change Operator Status
                else if (call_data_parts[1].equals("changeoperator")) {
                    User user = (User) user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    }

                    // Change Operator Status
                    else {
                        user.changePermission(UserPermission.OPERATOR);
                        user_manager.changeUser(user);
                        reloadUserEntry(message_id, user.getUserName(), user_id);
                    }
                }

                // Change Hightrust Status
                else if (call_data_parts[1].equals("changehightrust")) {
                    User user = (User) user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    }

                    // Change Hightrust Status
                    else {
                        user.changePermission(UserPermission.HIGHTRUST);
                        user_manager.changeUser(user);
                        reloadUserEntry(message_id, user.getUserName(), user_id);
                    }
                }

                // Change Guest to regular user or the other way around
                else if (call_data_parts[1].equals("changeguest")) {
                    AbstractUser abstractUser = user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (abstractUser == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    }

                    // If abstract User is a guest, change to regular user
                    else if (abstractUser instanceof Guest) {
                        user_manager.changeUser(new User(abstractUser.getId(), abstractUser.getUserName()));
                        reloadUserEntry(message_id, abstractUser.getUserName(), user_id);
                    }

                    // Otherwise change the abstract user to a guest
                    else {
                        user_manager.changeUser(new Guest(abstractUser.getId(), abstractUser.getUserName()));
                        reloadUserEntry(message_id, abstractUser.getUserName(), user_id);
                    }
                } else if (call_data_parts[1].equals("block")) {
                    AbstractUser user = user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    } else {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBlockOptions(user);

                        String message_text = MessageConstants.MESSAGE_ADMINTOOLS_ABOUTTOBLOCK.replace("$user",
                                user.getUserName());

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(message_text);

                        enldd.edit_message(message);
                    }
                } else if (call_data_parts[1].equals("blockapproval")) {
                    AbstractUser user = user_manager.loadAbstractUser(call_data_parts[2]);

                    // User cannot be found
                    if (user == null) {
                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

                        enldd.edit_message(message);
                    } else {
                        user_manager.blockUser(user);

                        EditMessageText message = new EditMessageText();

                        InlineKeyboardMarkup keyboard = buildBackButtonAdmintools();

                        String message_text = MessageConstants.MESSAGE_ADMINTOOLS_BLOCKED.replace("$user",
                                user.getUserName());

                        message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                                .setReplyMarkup(keyboard).setText(message_text);

                        enldd.edit_message(message);
                    }
                }

                //Load current blocklist
                else if (call_data_parts[1].equals("blocklist")) {
                    List<Blocked> blocklist = user_manager.buildBlockList();

                    EditMessageText message = new EditMessageText();

                    InlineKeyboardMarkup keyboard = buildBlocklistMenu();

                    String message_text = MessageConstants.MESSAGE_ADMINTOOLS_BLOCKLIST;

                    for (Blocked user : blocklist) {
                        message_text += "<code>" + user.getUserName() + "</code>\n";
                    }

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setReplyMarkup(keyboard).setText(message_text);

                    enldd.edit_message(message);
                }

                // Load specific user on blocklist to unblock
                else if (call_data_parts[1].equals("unblock")) {
                    stateMap.put(user_id, State.ADMIN_UNBLOCKUSER);

                    EditMessageText message = new EditMessageText();

                    message.setChatId(user_id).setParseMode(ParseMode.HTML).setMessageId(toIntExact(message_id))
                            .setText(MessageConstants.MESSAGE_ADMINTOOLS_UNBLOCK);

                    enldd.edit_message(message);
                }
            }
        }
    }

    /*----------Help Methods to load or reload a User Entry----------*/

    private void loadUserEntry(String userName, long adminId) {
        AbstractUser abstractUser = user_manager.loadAbstractUser(userName);

        //Send Error message if no user is found
        if (abstractUser == null) {
            SendMessage message = new SendMessage().setChatId(adminId).setParseMode(ParseMode.HTML).setText(
                    MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

            enldd.send_message(message);
        }

        //Check that no one is able to load himself
        if (abstractUser.getId() == adminId) {
            InlineKeyboardMarkup keyboardMarkup = buildBackButtonWhitelist();

            SendMessage messageText = new SendMessage().setChatId(adminId).setParseMode(ParseMode.HTML).setReplyMarkup(
                    keyboardMarkup).setText(MessageConstants.MESSAGE_ADMINTOOLS_CANTCHANGESELF);

            stateMap.remove(adminId);
            enldd.send_message(messageText);
        }

        //Check that no one is able to load the Master
        else if (abstractUser.getId() == enldd.ADMIN_CHAT_ID) {
            InlineKeyboardMarkup keyboardMarkup = buildBackButtonWhitelist();

            SendMessage message = new SendMessage().setChatId(adminId).setParseMode(ParseMode.HTML).setReplyMarkup(
                    keyboardMarkup).setText(MessageConstants.MESSAGE_ADMINTOOLS_CANTCHANGEMASTER);

            stateMap.remove(adminId);
            enldd.send_message(message);
        }

        //Load User
        else if (abstractUser instanceof Admin) {
            InlineKeyboardMarkup keyboardMarkup = buildAdminFound(abstractUser);

            String text = MessageConstants.MESSAGE_ADMINTOOLS_ADMINFOUND.replace("$user", abstractUser.getUserName());

            SendMessage message = new SendMessage().setChatId(adminId).setParseMode(ParseMode.HTML).setReplyMarkup(
                    keyboardMarkup).setText(text);

            stateMap.remove(adminId);
            enldd.send_message(message);
        } else if (abstractUser instanceof Guest) {
            InlineKeyboardMarkup keyboardMarkup = buildGuestFound(abstractUser);

            String text = MessageConstants.MESSAGE_ADMINTOOLS_GUESTFOUND.replace("$user", abstractUser.getUserName());

            SendMessage message = new SendMessage().setChatId(adminId).setParseMode(ParseMode.HTML).setReplyMarkup(
                    keyboardMarkup).setText(text);

            stateMap.remove(adminId);
            enldd.send_message(message);
        } else if (abstractUser instanceof User) {
            InlineKeyboardMarkup keyboardMarkup = buildUserFound(abstractUser);

            String text = MessageConstants.MESSAGE_ADMINTOOLS_USERFOUND.replace("$user", abstractUser.getUserName());

            for (UserPermission permission : ((User) abstractUser).getPermissions()) {
                text += "\n" + permission.toString();
            }

            SendMessage message = new SendMessage().setChatId(adminId).setParseMode(ParseMode.HTML).setReplyMarkup(
                    keyboardMarkup).setText(text);

            stateMap.remove(adminId);
            enldd.send_message(message);
        }
    }

    private void reloadUserEntry(long messageId, String userName, long adminId) {
        AbstractUser abstractUser = user_manager.loadAbstractUser(userName);

        //Send Error message if no user is found
        if (abstractUser == null) {
            EditMessageText message = new EditMessageText().setChatId(adminId).setMessageId(toIntExact(messageId))
                    .setParseMode(ParseMode.HTML).setText(MessageConstants.MESSAGE_ADMINTOOLS_USERNOTFOUND);

            enldd.edit_message(message);
        }

        //Load User
        if (abstractUser instanceof Admin) {
            InlineKeyboardMarkup keyboardMarkup = buildAdminFound(abstractUser);

            String text = MessageConstants.MESSAGE_ADMINTOOLS_ADMINFOUND.replace("$user", abstractUser.getUserName());

            EditMessageText message = new EditMessageText().setChatId(adminId).setMessageId(toIntExact(messageId))
                    .setParseMode(ParseMode.HTML).setReplyMarkup(keyboardMarkup).setText(text);

            enldd.edit_message(message);
        } else if (abstractUser instanceof Guest) {
            InlineKeyboardMarkup keyboardMarkup = buildGuestFound(abstractUser);

            String text = MessageConstants.MESSAGE_ADMINTOOLS_GUESTFOUND.replace("$user", abstractUser.getUserName());

            EditMessageText message = new EditMessageText().setChatId(adminId).setMessageId(toIntExact(messageId))
                    .setParseMode(ParseMode.HTML).setReplyMarkup(keyboardMarkup).setText(text);

            enldd.edit_message(message);
        } else if (abstractUser instanceof User) {
            InlineKeyboardMarkup keyboardMarkup = buildUserFound(abstractUser);

            String text = MessageConstants.MESSAGE_ADMINTOOLS_USERFOUND.replace("$user", abstractUser.getUserName());

            for (UserPermission permission : ((User) abstractUser).getPermissions()) {
                text += "\n" + permission.toString();
            }

            EditMessageText message = new EditMessageText().setChatId(adminId).setMessageId(toIntExact(messageId))
                    .setParseMode(ParseMode.HTML).setReplyMarkup(keyboardMarkup).setText(text);

            enldd.edit_message(message);
        }
    }

    /*----------Help Methods to build Menus----------*/

    private InlineKeyboardMarkup buildAdminMenu() {

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

        for (int i = 0; i < open_requests.size(); i++) {
            buttons.add(new ArrayList<>());
        }

        int i = 0;
        for (long user_id : open_requests.keySet()) {
            AbstractUser user = open_requests.get(user_id);
            String user_name = user.getUserName();
            String buttontext;
            if (user instanceof Guest) {
                buttontext = "Gastanfrage: " + user_name;
            } else {
                buttontext = user_name;
            }
            InlineKeyboardButton user_button = new InlineKeyboardButton().setText(buttontext).setCallbackData(
                    "admintools loadopenuser " + String.valueOf(user_id));
            buttons.get(i).add(user_button);
            i++;
        }

        List<InlineKeyboardButton> lastbutton = new ArrayList<>();
        lastbutton.add(new InlineKeyboardButton().setText("Zurück.").setCallbackData("admintools menu"));
        buttons.add(lastbutton);

        requests.setKeyboard(buttons);

        return requests;
    }

    private InlineKeyboardMarkup buildRequestAnswers(long user_id) {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Ja")
                .setCallbackData("admintools grant " + String.valueOf(user_id)));
        row.add(new InlineKeyboardButton().setText("Nein")
                .setCallbackData("admintools deny " + String.valueOf(user_id)));
        row.add(new InlineKeyboardButton().setText("Block")
                .setCallbackData("admintools denyandblock " + String.valueOf(user_id)));
        row2.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools loadrequests"));

        buttons.add(row);
        buttons.add(row2);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildWhitelistMenu() {

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

    private InlineKeyboardMarkup buildBlocklistMenu() {

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

    private InlineKeyboardMarkup buildAdminFound(AbstractUser user) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Degradieren")
                .setCallbackData("admintools changeadmin " + user.getUserName()));
        row2.add(new InlineKeyboardButton().setText("Zu Gast ändern")
                .setCallbackData("admintools changeguest " + user.getUserName()));
        row3.add(new InlineKeyboardButton().setText("Blockieren")
                .setCallbackData("admintools block " + user.getUserName()));
        row4.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildGuestFound(AbstractUser user) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zum Admin berufen")
                .setCallbackData("admintools changeadmin " + user.getUserName()));
        row2.add(new InlineKeyboardButton().setText("Zu normalem Nutzer machen")
                .setCallbackData("admintools changeguest " + user.getUserName()));
        row3.add(new InlineKeyboardButton().setText("Blockieren")
                .setCallbackData("admintools block " + user.getUserName()));
        row4.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildUserFound(AbstractUser user) {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        List<InlineKeyboardButton> row3 = new ArrayList<>();
        List<InlineKeyboardButton> row4 = new ArrayList<>();
        List<InlineKeyboardButton> row5 = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zum Admin berufen")
                .setCallbackData("admintools changeadmin " + user.getUserName()));
        row2.add(new InlineKeyboardButton().setText("Zu Gast ändern")
                .setCallbackData("admintools changeguest " + user.getUserName()));
        row2.add(new InlineKeyboardButton().setText("Mod ändern")
                .setCallbackData("admintools changemod " + user.getUserName()));
        row3.add(new InlineKeyboardButton().setText("Operator ändern")
                .setCallbackData("admintools changeoperator " + user.getUserName()));
        row3.add(new InlineKeyboardButton().setText("Hightrust ändern")
                .setCallbackData("admintools changehightrust " + user.getUserName()));
        row4.add(new InlineKeyboardButton().setText("Blockieren")
                .setCallbackData("admintools block " + user.getUserName()));
        row5.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);
        buttons.add(row2);
        buttons.add(row3);
        buttons.add(row4);
        buttons.add(row5);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBlockOptions(AbstractUser user) {
        String user_name = user.getUserName();

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("BLOCK").setCallbackData("admintools blockapproval " + user_name));
        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBackButtonAdmintools() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools menu"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }

    private InlineKeyboardMarkup buildBackButtonWhitelist() {

        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        List<InlineKeyboardButton> row = new ArrayList<>();

        row.add(new InlineKeyboardButton().setText("Zurück").setCallbackData("admintools whitelist"));

        buttons.add(row);

        keyboard.setKeyboard(buttons);
        return keyboard;
    }
}
