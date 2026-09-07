package org.peppox.tropicalcore.trade;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class TradeSession {

    public static final int SLOTS_PER_PLAYER = 9;

    private final UUID playerA;
    private final UUID playerB;

    private final ItemStack[] offerA = new ItemStack[SLOTS_PER_PLAYER];
    private final ItemStack[] offerB = new ItemStack[SLOTS_PER_PLAYER];

    private double moneyA = 0;
    private double moneyB = 0;

    private boolean confirmedA = false;
    private boolean confirmedB = false;

    public TradeSession(Player a, Player b) {
        this.playerA = a.getUniqueId();
        this.playerB = b.getUniqueId();
    }

    public UUID getPlayerA() { return playerA; }
    public UUID getPlayerB() { return playerB; }

    public boolean isPlayerA(UUID uuid) { return uuid.equals(playerA); }
    public boolean isPlayerB(UUID uuid) { return uuid.equals(playerB); }

    public ItemStack[] getOffer(UUID uuid) {
        return isPlayerA(uuid) ? offerA : offerB;
    }

    public void setOfferSlot(UUID uuid, int slot, ItemStack item) {
        ItemStack[] offer = isPlayerA(uuid) ? offerA : offerB;
        offer[slot] = item;
        resetConfirmations();
    }

    public double getMoney(UUID uuid) {
        return isPlayerA(uuid) ? moneyA : moneyB;
    }

    public void setMoney(UUID uuid, double amount) {
        if (isPlayerA(uuid)) {
            moneyA = amount;
        } else {
            moneyB = amount;
        }
        resetConfirmations();
    }

    public void confirm(UUID uuid) {
        if (isPlayerA(uuid)) {
            confirmedA = true;
        } else {
            confirmedB = true;
        }
    }

    public boolean isConfirmed(UUID uuid) {
        return isPlayerA(uuid) ? confirmedA : confirmedB;
    }

    public boolean bothConfirmed() {
        return confirmedA && confirmedB;
    }

    private void resetConfirmations() {
        confirmedA = false;
        confirmedB = false;
    }
}