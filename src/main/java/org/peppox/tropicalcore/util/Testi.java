package org.peppox.tropicalcore.util;

import net.md_5.bungee.api.ChatColor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Testi {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");

    /**
     * Converte i codici colore classici (&a, &c, ecc.) e quelli esadecimali (&#RRGGBB).
     */
    public static String colora(String messaggio) {
        if (messaggio == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(messaggio);
        while (matcher.find()) {
            String color = messaggio.substring(matcher.start() + 1, matcher.end());
            messaggio = messaggio.replace("&" + color, ChatColor.of(color) + "");
            matcher = HEX_PATTERN.matcher(messaggio);
        }
        return ChatColor.translateAlternateColorCodes('&', messaggio);
    }
}