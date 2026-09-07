package org.peppox.tropicalcore.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class EmojiParser {

    private static final Map<String, String> EMOJI = new LinkedHashMap<>();

    static {
        // Il carattere \uFE0F dopo il simbolo forza la resa "a colori" del client,
        // invece del simbolo testuale monocromatico semplice.
        EMOJI.put(":cuore:", "\u2764\uFE0F");
        EMOJI.put(":heart:", "\u2764\uFE0F");
        EMOJI.put(":soldi:", "\uD83D\uDCB0");
        EMOJI.put(":money:", "\uD83D\uDCB0");
        EMOJI.put(":stella:", "\u2B50");
        EMOJI.put(":star:", "\u2B50");
        EMOJI.put(":fuoco:", "\uD83D\uDD25");
        EMOJI.put(":fire:", "\uD83D\uDD25");
        EMOJI.put(":check:", "\u2705");
        EMOJI.put(":ok:", "\u2705");
        EMOJI.put(":x:", "\u274C");
        EMOJI.put(":no:", "\u274C");
        EMOJI.put(":attenzione:", "\u26A0\uFE0F");
        EMOJI.put(":warning:", "\u26A0\uFE0F");
        EMOJI.put(":poliziotto:", "\uD83D\uDC6E");
        EMOJI.put(":casa:", "\uD83C\uDFE0");
        EMOJI.put(":auto:", "\uD83D\uDE97");
    }

    public static String parse(String testo) {
        if (testo == null || testo.isEmpty()) return testo;

        String risultato = testo;
        for (Map.Entry<String, String> entry : EMOJI.entrySet()) {
            risultato = risultato.replace(entry.getKey(), entry.getValue());
        }
        return risultato;
    }
}