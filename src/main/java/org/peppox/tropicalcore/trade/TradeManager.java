package org.peppox.tropicalcore.trade;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.peppox.tropicalcore.util.Testi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TradeManager implements Listener {

    private final Economy economy;

    /** Richieste in sospeso: destinatario -> mittente. */
    private final Map<UUID, UUID> richiesteScambio = new ConcurrentHashMap<>();
    /** Sessioni attive per partecipante. */
    private final Map<UUID, TradeSession> sessioniByPlayer = new ConcurrentHashMap<>();
    /** Sessioni attive per id. */
    private final Map<UUID, TradeSession> sessioniById = new ConcurrentHashMap<>();
    /** Menu associato a ogni sessione. */
    private final Map<UUID, TradeMenu> menuBySession = new ConcurrentHashMap<>();
    /** Player in attesa di digitare l'importo denaro: player -> id sessione. */
    private final Map<UUID, UUID> pendingMoney = new ConcurrentHashMap<>();

    public TradeManager(@NotNull Economy economy) {
        this.economy = economy;
    }

    // ---------------------------------------------------------------- richieste

    public void richiediScambio(@NotNull Player mittente, @NotNull Player destinatario) {
        if (sessioniByPlayer.containsKey(mittente.getUniqueId())) {
            mittente.sendMessage(Testi.colora("&cHai gia' uno scambio in corso."));
            return;
        }
        if (sessioniByPlayer.containsKey(destinatario.getUniqueId())) {
            mittente.sendMessage(Testi.colora("&c" + destinatario.getName() + " ha gia' uno scambio in corso."));
            return;
        }

        richiesteScambio.put(destinatario.getUniqueId(), mittente.getUniqueId());

        mittente.sendMessage(Component.text("Richiesta di scambio inviata a " + destinatario.getName(), NamedTextColor.GREEN));
        destinatario.sendMessage(Component.text()
                .append(Component.text(mittente.getName(), NamedTextColor.YELLOW))
                .append(Component.text(" ti ha chiesto di scambiare. Scrivi ", NamedTextColor.GRAY))
                .append(Component.text("/trade accept", NamedTextColor.GREEN))
                .append(Component.text(" per accettare o ", NamedTextColor.GRAY))
                .append(Component.text("/trade deny", NamedTextColor.RED))
                .append(Component.text(" per rifiutare.", NamedTextColor.GRAY))
                .build());
    }

    public void accettaScambio(@NotNull Player destinatario) {
        UUID mittenteId = richiesteScambio.remove(destinatario.getUniqueId());
        if (mittenteId == null) {
            destinatario.sendMessage(Component.text("Non hai nessuna richiesta di scambio in sospeso.", NamedTextColor.RED));
            return;
        }

        Player mittente = Bukkit.getPlayer(mittenteId);
        if (mittente == null || !mittente.isOnline()) {
            destinatario.sendMessage(Component.text("Il giocatore che ti ha chiesto lo scambio non e' piu' online.", NamedTextColor.RED));
            return;
        }

        if (sessioniByPlayer.containsKey(mittente.getUniqueId()) || sessioniByPlayer.containsKey(destinatario.getUniqueId())) {
            destinatario.sendMessage(Component.text("Uno dei due giocatori ha gia' uno scambio in corso.", NamedTextColor.RED));
            return;
        }

        creaSessione(mittente, destinatario);
    }

    public void rifiutaScambio(@NotNull Player destinatario) {
        UUID mittenteId = richiesteScambio.remove(destinatario.getUniqueId());
        if (mittenteId == null) {
            destinatario.sendMessage(Component.text("Non hai nessuna richiesta di scambio da rifiutare.", NamedTextColor.RED));
            return;
        }

        Player mittente = Bukkit.getPlayer(mittenteId);
        destinatario.sendMessage(Component.text("Hai rifiutato lo scambio.", NamedTextColor.RED));
        if (mittente != null && mittente.isOnline()) {
            mittente.sendMessage(Component.text(destinatario.getName() + " ha rifiutato la tua richiesta di scambio.", NamedTextColor.RED));
        }
    }

    // ---------------------------------------------------------------- sessioni

    private void creaSessione(@NotNull Player a, @NotNull Player b) {
        UUID id = UUID.randomUUID();
        TradeSession session = new TradeSession(id, a, b);
        TradeMenu menu = new TradeMenu(this, session);

        sessioniById.put(id, session);
        sessioniByPlayer.put(a.getUniqueId(), session);
        sessioniByPlayer.put(b.getUniqueId(), session);
        menuBySession.put(id, menu);

        menu.open(a);
        menu.open(b);

        a.sendMessage(Testi.colora("&aScambio avviato con &e" + b.getName() + "&a. Metti gli item nella tua riga e premi la lana verde."));
        b.sendMessage(Testi.colora("&aScambio avviato con &e" + a.getName() + "&a. Metti gli item nella tua riga e premi la lana verde."));
    }

    /**
     * Ritorna la sessione attiva del giocatore, o {@code null}.
     */
    @Nullable
    public TradeSession getSessione(@NotNull Player player) {
        return sessioniByPlayer.get(player.getUniqueId());
    }

    @Nullable
    public TradeSession getSessione(@NotNull UUID id) {
        return sessioniById.get(id);
    }

    @Nullable
    private TradeMenu getMenu(@NotNull TradeSession session) {
        return menuBySession.get(session.getId());
    }

    /**
     * Gestisce i click sui pulsanti della finestra di scambio.
     */
    public void handleTradeClick(@NotNull TradeSession session, int slot, @NotNull Player player) {
        if (session.isFinished()) {
            return;
        }
        boolean isA = session.isPlayerA(player.getUniqueId());

        if (slot == TradeSession.SLOT_CONFERMA_A && isA) {
            session.confirm(player.getUniqueId());
            refresh(session);
            if (session.bothConfirmed()) {
                completa(session);
            }
            return;
        }
        if (slot == TradeSession.SLOT_CONFERMA_B && !isA) {
            session.confirm(player.getUniqueId());
            refresh(session);
            if (session.bothConfirmed()) {
                completa(session);
            }
            return;
        }
        if (slot == TradeSession.SLOT_SOLDI_A && isA) {
            pendingMoney.put(player.getUniqueId(), session.getId());
            player.sendMessage(Testi.colora("&eScrivi in chat l'importo di denaro che vuoi offrire:"));
            return;
        }
        if (slot == TradeSession.SLOT_SOLDI_B && !isA) {
            pendingMoney.put(player.getUniqueId(), session.getId());
            player.sendMessage(Testi.colora("&eScrivi in chat l'importo di denaro che vuoi offrire:"));
            return;
        }
    }

    private void refresh(@NotNull TradeSession session) {
        TradeMenu menu = getMenu(session);
        if (menu != null) {
            menu.refresh();
        }
    }

    /**
     * Completa lo scambio: trasferisce denaro e item tra i due giocatori.
     */
    private void completa(@NotNull TradeSession session) {
        if (session.isFinished()) {
            return;
        }

        Player a = Bukkit.getPlayer(session.getPlayerA());
        Player b = Bukkit.getPlayer(session.getPlayerB());

        // Un partecipante e' uscito durante lo scambio: annulla con rimborso
        if (a == null || !a.isOnline() || b == null || !b.isOnline()) {
            cancella(session);
            return;
        }

        TradeMenu menu = getMenu(session);
        Inventory inv = menu != null ? menu.getInventory() : null;

        List<ItemStack> itemsA = new ArrayList<>();
        List<ItemStack> itemsB = new ArrayList<>();
        if (inv != null) {
            for (int i = 0; i < TradeSession.SLOTS_PER_PLAYER; i++) {
                ItemStack itemA = inv.getItem(TradeSession.START_A + i);
                ItemStack itemB = inv.getItem(TradeSession.START_B + i);
                if (itemA != null) {
                    itemsA.add(itemA.clone());
                }
                if (itemB != null) {
                    itemsB.add(itemB.clone());
                }
            }
        }

        double moneyA = session.getMoneyA();
        double moneyB = session.getMoneyB();

        // Verifica i fondi PRIMA di marcare la sessione come conclusa
        if (economy != null && moneyA > 0 && !economy.has(a, moneyA)) {
            notificaErrore(session, a, b, "non ha abbastanza soldi");
            return;
        }
        if (economy != null && moneyB > 0 && !economy.has(b, moneyB)) {
            notificaErrore(session, b, a, "non ha abbastanza soldi");
            return;
        }

        session.markFinished();

        // Trasferimento denaro
        if (economy != null) {
            if (moneyA > 0) {
                economy.withdrawPlayer(a, moneyA);
                economy.depositPlayer(b, moneyA);
            }
            if (moneyB > 0) {
                economy.withdrawPlayer(b, moneyB);
                economy.depositPlayer(a, moneyB);
            }
        }

        // Trasferimento item
        for (ItemStack item : itemsB) {
            consegna(a, item);
        }
        for (ItemStack item : itemsA) {
            consegna(b, item);
        }

        // Pulisci l'inventario condiviso per evitare duplicazioni
        if (inv != null) {
            inv.clear();
        }

        a.sendMessage(Testi.colora("&aScambio completato con successo con &e" + b.getName()));
        b.sendMessage(Testi.colora("&aScambio completato con successo con &e" + a.getName()));

        cleanup(session, a, b, true);
    }

    private void notificaErrore(@NotNull TradeSession session, @NotNull Player mancante, @Nullable Player altro, String motivo) {
        mancante.sendMessage(Testi.colora("&cScambio annullato: non hai abbastanza soldi."));
        if (altro != null && altro.isOnline()) {
            altro.sendMessage(Testi.colora("&cScambio annullato: " + mancante.getName() + " " + motivo + "."));
        }
        cancella(session);
    }

    /**
     * Annulla una sessione (notificando i giocatori) senza scambiare nulla.
     */
    public void cancella(@NotNull TradeSession session) {
        cancella(session, null);
    }

    /**
     * Annulla una sessione. Se {@code chiEsce} non e' {@code null}, viene usato come
     * riferimento diretto al giocatore (utile durante l'evento di quit, quando
     * {@link Bukkit#getPlayer(UUID)} potrebbe gia' restituire {@code null}).
     */
    public void cancella(@NotNull TradeSession session, @Nullable Player chiEsce) {
        if (session.isFinished()) {
            return;
        }
        session.markFinished();

        Player a = risolvi(session.getPlayerA(), chiEsce);
        Player b = risolvi(session.getPlayerB(), chiEsce);

        if (a != null && a.isOnline()) {
            a.sendMessage(Testi.colora("&cLo scambio e' stato annullato."));
        }
        if (b != null && b.isOnline()) {
            b.sendMessage(Testi.colora("&cLo scambio e' stato annullato."));
        }

        cleanup(session, a, b, true);
    }

    @Nullable
    private Player risolvi(@NotNull UUID uuid, @Nullable Player chiEsce) {
        if (chiEsce != null && chiEsce.getUniqueId().equals(uuid)) {
            return chiEsce;
        }
        return Bukkit.getPlayer(uuid);
    }

    private void cleanup(@NotNull TradeSession session, @Nullable Player a, @Nullable Player b, boolean chiudiInventari) {
        // Restituisci gli item rimasti nell'inventario condiviso ai rispettivi proprietari
        TradeMenu menu = getMenu(session);
        if (menu != null && menu.getInventory() != null && a != null && b != null && !session.bothConfirmed()) {
            Inventory inv = menu.getInventory();
            for (int i = 0; i < TradeSession.SLOTS_PER_PLAYER; i++) {
                ItemStack itemA = inv.getItem(TradeSession.START_A + i);
                ItemStack itemB = inv.getItem(TradeSession.START_B + i);
                if (itemA != null) {
                    consegna(a, itemA);
                }
                if (itemB != null) {
                    consegna(b, itemB);
                }
            }
            inv.clear();
        }

        if (chiudiInventari) {
            if (a != null && a.isOnline()) {
                a.closeInventory();
            }
            if (b != null && b.isOnline()) {
                b.closeInventory();
            }
        }

        sessioniByPlayer.remove(session.getPlayerA());
        sessioniByPlayer.remove(session.getPlayerB());
        sessioniById.remove(session.getId());
        menuBySession.remove(session.getId());
        pendingMoney.remove(session.getPlayerA());
        pendingMoney.remove(session.getPlayerB());
    }

    /**
     * Consegna un item al giocatore; se l'inventario e' pieno, lascia il resto a terra.
     */
    private void consegna(@NotNull Player player, @NotNull ItemStack item) {
        Map<Integer, ItemStack> rimanenti = player.getInventory().addItem(item);
        for (ItemStack resto : rimanenti.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), resto);
        }
    }

    /**
     * Chiude tutte le sessioni attive (es. al disable del plugin).
     */
    public void closeAllSessions() {
        for (TradeSession session : List.copyOf(sessioniById.values())) {
            cancella(session);
        }
        sessioniById.clear();
        sessioniByPlayer.clear();
        menuBySession.clear();
        pendingMoney.clear();
        richiesteScambio.clear();
    }

    // ---------------------------------------------------------------- listener

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        TradeSession session = getSessione(player);
        if (session == null || session.isFinished()) {
            return;
        }

        // Blocca shift-click e doppio click, che potrebbero spostare item tra i lati
        if (event.isShiftClick() || event.getClick() == ClickType.DOUBLE_CLICK) {
            event.setCancelled(true);
            return;
        }

        // Click nell'inventario del giocatore (parte bassa): sempre consentito
        if (event.getRawSlot() >= TradeSession.SIZE) {
            session.resetConfirmations();
            refresh(session);
            return;
        }

        event.setCancelled(true);
        int slot = event.getSlot();

        // Slot offerta di A
        if (slot >= TradeSession.START_A && slot < TradeSession.START_A + TradeSession.SLOTS_PER_PLAYER) {
            if (session.isPlayerA(player.getUniqueId())) {
                event.setCancelled(false);
                session.resetConfirmations();
                refresh(session);
            }
            return;
        }

        // Slot offerta di B
        if (slot >= TradeSession.START_B && slot < TradeSession.START_B + TradeSession.SLOTS_PER_PLAYER) {
            if (session.isPlayerB(player.getUniqueId())) {
                event.setCancelled(false);
                session.resetConfirmations();
                refresh(session);
            }
            return;
        }

        // Pulsanti
        TradeMenu menu = getMenu(session);
        if (menu != null) {
            menu.onClick(slot, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        TradeSession session = getSessione(player);
        if (session == null || session.isFinished()) {
            return;
        }

        // Consenti il drag solo all'interno dell'inventario del giocatore
        for (int rawSlot : event.getRawSlots()) {
            if (rawSlot < TradeSession.SIZE) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }
        pendingMoney.remove(player.getUniqueId());
        TradeSession session = getSessione(player);
        if (session == null || session.isFinished()) {
            return;
        }
        // Chiusura manuale della finestra => annullo lo scambio
        cancella(session, player);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        UUID sessionId = pendingMoney.remove(player.getUniqueId());
        if (sessionId == null) {
            return;
        }

        event.setCancelled(true);

        TradeSession session = getSessione(sessionId);
        if (session == null || session.isFinished()) {
            player.sendMessage(Testi.colora("&cLa sessione di scambio non e' piu' attiva."));
            return;
        }

        String testo = PlainTextComponentSerializer.plainText().serialize(event.message()).trim().replace(',', '.');
        double importo;
        try {
            importo = Double.parseDouble(testo);
        } catch (NumberFormatException e) {
            player.sendMessage(Testi.colora("&cImporto non valido. Inserisci un numero (es. 100 oppure 50.5)."));
            pendingMoney.put(player.getUniqueId(), sessionId);
            return;
        }

        if (!Double.isFinite(importo) || importo < 0) {
            player.sendMessage(Testi.colora("&cL'importo deve essere un numero positivo."));
            pendingMoney.put(player.getUniqueId(), sessionId);
            return;
        }

        session.setMoney(player.getUniqueId(), importo);
        refresh(session);
        player.sendMessage(Testi.colora("&aHai impostato l'offerta di denaro a &e€" + String.format("%.2f", importo)));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        pendingMoney.remove(player.getUniqueId());
        richiesteScambio.remove(player.getUniqueId());

        TradeSession session = getSessione(player);
        if (session != null) {
            cancella(session, player);
        }
    }
}
