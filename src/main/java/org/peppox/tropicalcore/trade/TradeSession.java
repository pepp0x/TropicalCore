package org.peppox.tropicalcore.trade;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Stato di una sessione di scambio tra due giocatori.
 * Gli item offerti vivono nell'inventario condiviso gestito da {@link TradeMenu};
 * qui vengono tracciati solo denaro, conferme e stato di completamento.
 */
public class TradeSession {

    public static final int SIZE = 27;
    public static final int SLOTS_PER_PLAYER = 9;
    public static final int START_A = 0;
    public static final int START_B = 18;
    public static final int SLOT_SOLDI_A = 11;
    public static final int SLOT_CONFERMA_A = 12;
    public static final int SLOT_INFO = 13;
    public static final int SLOT_CONFERMA_B = 14;
    public static final int SLOT_SOLDI_B = 15;

    private final UUID id;
    private final UUID playerA;
    private final UUID playerB;

    private double moneyA;
    private double moneyB;

    private boolean confirmedA;
    private boolean confirmedB;
    private boolean finished;

    public TradeSession(@NotNull UUID id, @NotNull Player a, @NotNull Player b) {
        this.id = id;
        this.playerA = a.getUniqueId();
        this.playerB = b.getUniqueId();
    }

    public UUID getId() {
        return id;
    }

    public UUID getPlayerA() {
        return playerA;
    }

    public UUID getPlayerB() {
        return playerB;
    }

    public boolean isPlayerA(UUID uuid) {
        return playerA.equals(uuid);
    }

    public boolean isPlayerB(UUID uuid) {
        return playerB.equals(uuid);
    }

    public boolean partecipa(UUID uuid) {
        return isPlayerA(uuid) || isPlayerB(uuid);
    }

    public double getMoneyA() {
        return moneyA;
    }

    public double getMoneyB() {
        return moneyB;
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

    public void resetConfirmations() {
        confirmedA = false;
        confirmedB = false;
    }

    public boolean isFinished() {
        return finished;
    }

    public void markFinished() {
        finished = true;
    }
}
