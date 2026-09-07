package org.peppox.tropicalcore.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility per la colorazione dei messaggi.
 *
 * <p>Espone due modalita:
 * <ul>
 *   <li>{@link #colora(String)}: produce una stringa legacy (con il carattere {@code §}), comoda
 *       per {@code sendMessage(String)};</li>
 *   <li>{@link #componente(String)}: produce un {@link Component} Adventure, necessario per i
 *       titoli/lore degli inventari su Paper 1.21+.</li>
 * </ul>
 *
 * <p>Supporta sia i codici classici ({@code &a}, {@code &c}, ...) sia quelli esadecimali in
 * formato {@code &#RRGGBB}.
 */
public final class Testi {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#[a-fA-F0-9]{6}");
    private static final String CODICI = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx";
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private Testi() {
    }

    /**
     * Converte i codici colore classici ({@code &a}, {@code &c}, ...) ed esadecimali
     * ({@code &#RRGGBB}) in una stringa legacy pronta per {@code sendMessage(String)}.
     */
    public static String colora(String messaggio) {
        if (messaggio == null || messaggio.isEmpty()) {
            return "";
        }

        String risultato = messaggio;
        Matcher matcher = HEX_PATTERN.matcher(risultato);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            String hex = risultato.substring(matcher.start() + 2, matcher.end());
            StringBuilder sezione = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                sezione.append('§').append(c);
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(sezione.toString()));
        }
        matcher.appendTail(buffer);

        return traduciAlternato('&', buffer.toString());
    }

    /**
     * Come {@link #colora(String)}, ma restituisce un {@link Component} Adventure.
     * Da usare per displayName/lore/titoli degli inventari.
     */
    public static Component componente(String messaggio) {
        return LEGACY.deserialize(colora(messaggio));
    }

    private static String traduciAlternato(char codiceAlt, String testo) {
        char[] caratteri = testo.toCharArray();
        for (int i = 0; i < caratteri.length - 1; i++) {
            if (caratteri[i] == codiceAlt && CODICI.indexOf(caratteri[i + 1]) > -1) {
                caratteri[i] = '§';
                caratteri[i + 1] = Character.toLowerCase(caratteri[i + 1]);
            }
        }
        return new String(caratteri);
    }
}
