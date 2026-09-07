package org.peppox.tropicalcore.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.peppox.tropicalcore.util.EmojiParser;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        String testoOriginale = PlainTextComponentSerializer.plainText().serialize(event.message());
        String testoConEmoji = EmojiParser.parse(testoOriginale);

        event.message(Component.text(testoConEmoji));
    }
}
