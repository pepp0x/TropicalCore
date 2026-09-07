package org.peppox.tropicalcore.jobs;

public enum JobType {
    DISOCCUPATO("Disoccupato", "&7", "tropicalcore.job.disoccupato"),
    POLIZIA("Agente di Polizia", "&1", "tropicalcore.job.polizia"),
    MEDICO("Medico", "&c", "tropicalcore.job.medico"),
    MECCANICO("Meccanico", "&6", "tropicalcore.job.meccanico"),
    DISCOTECA("Staff Discoteca", "&d", "tropicalcore.job.discoteca");

    private final String nomeFormattato;
    private final String colore;
    private final String permesso;

    JobType(String nomeFormattato, String colore, String permesso) {
        this.nomeFormattato = nomeFormattato;
        this.colore = colore;
        this.permesso = permesso;
    }

    public String getNomeFormattato() {
        return nomeFormattato;
    }

    public String getColore() {
        return colore;
    }

    public String getPermesso() {
        return permesso;
    }
}