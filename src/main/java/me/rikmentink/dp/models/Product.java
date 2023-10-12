package me.rikmentink.dp.models;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int product_nummer;
    private String naam;
    private String beschrijving;
    private double prijs;

    private List<Integer> kaarten = new ArrayList<>();

    public Product(int product_nummer, String naam, String beschrijving, double prijs) {
        this.product_nummer = product_nummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    public int getProductNummer() {
        return this.product_nummer;
    }

    public void setProduct_nummer(int product_nummer) {
        this.product_nummer = product_nummer;
    }

    public String getNaam() {
        return this.naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return this.beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public double getPrijs() {
        return this.prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

    public void addKaart(OVChipkaart kaart) {
        if (!this.getKaarten().contains(kaart.getKaartnummer())) {
            this.kaarten.add(kaart.getKaartnummer());
            kaart.getProducten().add(this.product_nummer);
        }
    }

    public void removeKaart(OVChipkaart kaart) {
        if (kaarten.contains(kaart.getKaartnummer())) {
            kaarten.remove(Integer.valueOf(kaart.getKaartnummer()));
            kaart.getProducten().remove(Integer.valueOf(this.product_nummer));
        }
    }

    public List<Integer> getKaarten() {
        return this.kaarten;
    }

    public void setKaarten(List<Integer> kaarten) {
        this.kaarten = kaarten;
    }

    @Override
    public String toString() {
        return "Product [product_nummer=" + product_nummer + ", naam=" + naam + ", beschrijving=" + beschrijving
                + ", prijs=" + prijs + ", kaarten=" + kaarten + "]";
    }
}
