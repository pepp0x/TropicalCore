package org.peppox.tropicalcore.terreno;

import java.util.UUID;

public class Terreno {

    private final String id;
    private final UUID proprietario;
    private final String world;
    private final int centroX;
    private final int centroZ;
    private String nome;

    public Terreno(String id, UUID proprietario, String world, int centroX, int centroZ) {
        this.id = id;
        this.proprietario = proprietario;
        this.world = world;
        this.centroX = centroX;
        this.centroZ = centroZ;
        this.nome = "Terreno senza nome";
    }

    public String getId() { return id; }
    public UUID getProprietario() { return proprietario; }
    public String getWorld() { return world; }
    public int getCentroX() { return centroX; }
    public int getCentroZ() { return centroZ; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}