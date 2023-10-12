package me.rikmentink.dp.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVChipkaart {
    private int kaartnummer;
    private LocalDate geldigTot;
    private int klasse;
    private double saldo;

    private Reiziger reiziger;
    private List<Integer> producten = new ArrayList<>();

    public OVChipkaart(int kaartnummer, LocalDate geldigTot, int klasse, double saldo, Reiziger reiziger) {
        this.kaartnummer = kaartnummer;
        this.geldigTot = geldigTot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger = reiziger;
    }

    public int getKaartnummer() {
        return this.kaartnummer;
    }

    public void setKaartnummer(int kaartnummer) {
        this.kaartnummer = kaartnummer;
    }

    public LocalDate getGeldigTot() {
        return this.geldigTot;
    }

    public void setGeldigTot(LocalDate geldigTot) {
        this.geldigTot = geldigTot;
    }

    public int getKlasse() {
        return this.klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public double getSaldo() {
        return this.saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public int getReizigerId() {
        return this.reiziger.getId();
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    public void addProduct(Product product) {
        int productNummer = product.getProductNummer();
        if (!this.producten.contains(productNummer)) {
            producten.add(productNummer);
            product.getKaarten().add(this.kaartnummer);
        }
    }

    public void removeProduct(Product product) {
        int productNummer = product.getProductNummer();
        if (producten.contains(productNummer)) {
            producten.remove(Integer.valueOf(productNummer));
            product.getKaarten().remove(Integer.valueOf(this.kaartnummer));
        }
    }

    public List<Integer> getProducten() {
        return this.producten;
    }

    public void setProducten(List<Integer> producten) { 
        this.producten = producten;
    }

    @Override
    public String toString() {
        return "OVChipkaart [kaartnummer=" + kaartnummer + ", geldigTot=" + geldigTot + ", klasse=" + klasse
                + ", saldo=" + saldo + ", reiziger=" + reiziger + ", producten=" + producten + "]";
    }
}
