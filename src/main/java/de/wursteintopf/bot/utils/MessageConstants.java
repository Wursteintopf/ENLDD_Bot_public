package de.wursteintopf.bot.utils;

import de.wursteintopf.bot.ENLDD;

public class MessageConstants {
    public static final String MESSAGE_MENU_MAIN = "Willkommen $user! \nAktuell findest du hier nur unsere Telegram Chats, in Zukunft sollen hier aber durchaus noch weitere Funktionen dazukommen.";
    public static final String MESSAGE_MENU_GUEST = "Willkommen $user! \nHier findest du unsere offenen Chats und unseren Gästechat:\n\nOur open chats and our guest chat:";
    public static final String MESSAGE_MENU_OPEN = "Willkommen bei " + ENLDD.TEAM_NAME + "! \nEs scheint, als hättest du keine Berechtigungen um diesen Bot zu nutzen. Wenn du neu bei " + ENLDD.TEAM_NAME + " bist, dann frage doch einfach Rechte an. Falls du Gast bist, hast du hier die Möglichkeit Gastrechte anzufragen.\n\nIf you are a guest, just request guest permissions to join our guest chat.";
    public static final String MESSAGE_MENU_OPEN_NO_BOT_RIGHTS = "Willkommen bei " + ENLDD.TEAM_NAME + "! \nHier "
            + "findest "
            + "du unsere offenen Chats.\nFalls du Gast bist, hast du hier die Möglichkeit Gastrechte anzufragen.\n\nIf"
            + " you are a guest, just request guest permissions to join our guest chat.";
    public static final String MESSAGE_MENU_CLOSED = "Menü erfolgreich geschlossen. Neustart mit /start .";

    public static final String MESSAGE_ERROR_NOUSERNAME = "Hi! Dieser Bot funktioniert leider nur mit einem festgelegten Telegram-Nutzernamen. Bitte stelle dir in deinen Telegram Einstellungen einen Nutzernamen ein und starte den Bot dann mit /start neu. Danke!";

    public static final String MESSAGE_PERMISSION_USER_REQUEST = "Der Nutzer @$user hat Berechtigungen für den Bot angefragt.";
    public static final String MESSAGE_PERMISSION_GUEST_REQUEST = "Der Nutzer @$user hat Gastrechte für den Bot angefragt.";
    public static final String MESSAGE_PERMISSION_USER_REQUEST_GRANTED = "@$user wurden von @$admin Botrechte verliehen.";
    public static final String MESSAGE_PERMISSION_GUEST_REQUEST_GRANTED = "@$user wurden von @$admin Gastrechte verliehen.";
    public static final String MESSAGE_PERMISSION_USER_REQUEST_RECEIVED = "Deine Anfrage wurde an ein Team hochtrainierter Affen weitergeleitet.\nYou request has been forwarded to a team of highly trained monkeys.";
    public static final String MESSAGE_PERMISSION_ALLREADYONWHITELIST = "Es tut mir Leid, aber du stehst bereits auf der Whitelist.";
    public static final String MESSAGE_PERMISSION_ALLREADYREQUESTED = "Geduld mein junger Padawan! Deine Anfrage wurde bereits weitergeleitet und muss noch von einem Admin bearbeitet werden.\n\nPlease be patient, your request has already been forwarded and is awaiting approval.";
    public static final String MESSAGE_PERMISSION_OPENREQUESTS = "Es gibt offene Requests von:";
    public static final String MESSAGE_PERMISSION_NO_OPENREQUESTS = "Es gibt aktuell keine offenen Requests.";
    public static final String MESSAGE_PERMISSION_OPENUSER = "@$user hat volle Rechte angefragt. Akzeptieren?";
    public static final String MESSAGE_PERMISSION_OPENUSER_GUEST = "@$user hat Gastrechte angefragt. Akzeptieren?";
    public static final String MESSAGE_PERMISSION_GRANTED = "Du hast soeben Berechtigungen für diesen Bot bekommen. Um den Bot zu nutzen starte ihn bitte mit /start neu.";
    public static final String MESSAGE_PERMISSION_GRANTED_ADMIN = "@$user wurde auf die Whitelist gesetzt.";
    public static final String MESSAGE_PERMISSION_GUEST_GRANTED = "Du hast soeben Gastrechte für diesen Bot bekommen. Um den Bot zu nutzen starte ihn bitte mit /start neu.\nYou were given guest permission for the bot. Please restart the bot using /start to proceed.";
    public static final String MESSAGE_PERMISSION_GUEST_GRANTED_ADMIN = "@$user wurde zur Gästeliste hinzugefügt.";
    public static final String MESSAGE_PERMISSION_DENIED = "Deine Nutzungsanfrage wurde abgelehnt.";
    public static final String MESSAGE_PERMISSION_DENIED_ADMIN = "Nutzungsanfrage von @$user wurde abgelehnt.";
    public static final String MESSAGE_PERMISSION_REQUEST_DENIED = "Nutzungsanfrage von @$user wurde von @$admin abgelehnt.";
    public static final String MESSAGE_PERMISSION_BLOCKED = "Du wurdest blockiert.";
    public static final String MESSAGE_PERMISSION_BLOCKED_ADMIN = "@$user wurde geblockt.";

    public static final String MESSAGE_ADMINTOOLS_WHITELIST = "Aktuelle Nutzer auf der Whitelist: \n\n";
    public static final String MESSAGE_ADMINTOOLS_BLOCKLIST = "Aktuell auf der Blocklist: \n\n";
    public static final String MESSAGE_ADMINTOOLS_LOADUSER = "Okay! Bitte schick mir den exakten Benutzernamen des Users, den du laden möchtest, oder brich mit /cancel ab.";
    public static final String MESSAGE_ADMINTOOLS_USERNOTFOUND = "Es tut mir Leid, aber ich kann diesen Nutzer nicht finden.";
    public static final String MESSAGE_ADMINTOOLS_NULLREQUEST = "Es tut mir Leid, aber scheinbar wurde diese Request bereits bearbeitet.";
    public static final String MESSAGE_ADMINTOOLS_USERFOUND = "Nutzer gefunden: <b>$user</b>\n\n Der Nutzer hat folgende Permissions:\n";
    public static final String MESSAGE_ADMINTOOLS_ADMINFOUND = "Admin gefunden: <b>$user</b>";
    public static final String MESSAGE_ADMINTOOLS_GUESTFOUND = "Gast gefunden: <b>$user</b>";
    public static final String MESSAGE_ADMINTOOLS_CANTCHANGEMASTER = "HOW DARE YOU! Es ist leider nicht möglich den Großmeister zu laden, oder seine Rechte zu ändern.";
    public static final String MESSAGE_ADMINTOOLS_ABOUTTOBLOCK = "Bist du dir ganz sicher, dass du $user blockieren möchtest?";
    public static final String MESSAGE_ADMINTOOLS_CANTCHANGESELF = "Es tut mir Leid, aber du kannst deinen eigenen Usereintrag nicht bearbeiten.";
    public static final String MESSAGE_ADMINTOOLS_BLOCKED = "$user wurde erfolgreich blockiert.";
    public static final String MESSAGE_ADMINTOOLS_UNBLOCK = "Okay! Bitte schick mir den exakten Benutzernamen des Users, den du freigeben möchtest, oder brich mit /cancel ab.";
    public static final String MESSAGE_ADMINTOOLS_USERUNBLOCKED = "Okay! $user wurde entblockt und kann eine neue Berechtigungsanfrage für den Bot stellen.";

    public static final String MESSAGE_CHATS_MENU = "Chatkategorien:";
    public static final String MESSAGE_CHATS_ADDCATEGORY = "Okay! Bitte gib mir einen Namen für die neue Kategorie.";
    public static final String MESSAGE_CHATS_CATEGORYADDED = "Okay, Kategorie hinzugefügt. \n\nChatkategorien:";
    public static final String MESSAGE_CHATS_NORIGHTS = "Es tut mir Leid, aber für diesen Chat brauchst du spezielle Berechtigungen. Wende dich bitte an einen der Mods, wenn du diesem Chat beitreten möchtest.";
    public static final String MESSAGE_CHATS_NOTFOUND = "Es tut mir Leid, aber ich kann diesen Chat nicht finden.";
    public static final String MESSAGE_CHATS_ADMINEDITCATEGORY = "Okay, du möchtest die Kategorie $category bearbeiten.";
    public static final String MESSAGE_CHATS_ADDCHATUID = "Okay! Bitte gib mit zuerst eine noch nicht verwendete Chatnummer (maximal 4 stelliger Integerwert) für den hat.";
    public static final String MESSAGE_CHATS_ADDCHATNAME = "Danke! Nun gib mir bitte den Namen des Chats.";
    public static final String MESSAGE_CHATS_NOTANUMBER = "Es tut mir Leid, aber das ist keine valide Nummer. Bitte versuche es noch einmal.";
    public static final String MESSAGE_CHATS_NOTAURL = "Es tut mir Leid, aber das ist keine valide URL. Bitte versuche es noch einmal.";
    public static final String MESSAGE_CHATS_CHATEXISTSALLREADY = "Es tut mir Leid, aber dieser Chat existiert bereits. Bitte versuche es noch einmal.";
    public static final String MESSAGE_CHATS_ADDURL = "Nun sende mir bitte die Chat URL.";
    public static final String MESSAGE_CHATS_CHATNUMBERTOLONG = "Es tut mir Leid, aber diese Nummer ist zu lang. Bitte versuche es noch einmal.";
    public static final String MESSAGE_CHATS_ADDPERMISSION = "Alles klar! Braucht der Chat spezielle Rechte?";
    public static final String MESSAGE_CHATS_CHATADDED = "Chat erfolgreich hinzugefügt.";
    public static final String MESSAGE_CHATS_ADMINDELETECATEGORY = "Bist du dir ganz sicher, dass du die Kategorie <b>$category</b> löschen möchtest? Das kann nicht rückgängig gemacht werden!";
    public static final String MESSAGE_CHATS_ADMINCATEGORYDELETED = "Kategorie erfolgreich gelöscht.";
    public static final String MESSAGE_CHATS_LOADCHAT = "Bitte sende mir die exakte ID des Chats, den du bearbeiten möchtest.";
    public static final String MESSAGE_CHATS_CHATFOUND = "<b>Chat gefunden: $fullname</b> \n\nBenötigte Permission: $permission\nURL: $url";
    public static final String MESSAGE_CHATS_CHATDELETED = "Chat erfolgreich gelöscht.";
    public static final String MESSAGE_CHATS_CHANGEUID = "Okay, sende mir die neue UID.";
    public static final String MESSAGE_CHATS_CHANGENAME = "Okay, sende mir den neuen Namen.";
    public static final String MESSAGE_CHATS_CHANGEPERMISSION = "Welche Permission benötigt der Chat?";
    public static final String MESSAGE_CHATS_CHANGEURL = "Okay, sende mir die neue URL.";


    public static final String MESSAGE_HELP = "Hallo! Ich bin ein Bot der " + ENLDD.TEAM_NAME + "! Wenn du mich "
            + "nutzen willst, dann "
            + "starte mich bitte mit /start.";

    public static final String MESSAGE_CHANGELOG = "<b>Changelog " + ENLDD.TEAM_NAME + "_bot</b>\n" +
            "\n" +
            "<i>Ver 1.0.0</i>\n" +
            "Initial Version\n" +
            "\n" +
            "<i>Ver 1.0.1</i>\n" +
            "Added temporary Anomaly Chats";

    public static final String MESSAGE_STATE_CANCELED = "Erfolgreich abgebrochen.";

    public static final String MESSAGE_BLOCKED = "Du bist für diesen Bot geblockt.";
}
