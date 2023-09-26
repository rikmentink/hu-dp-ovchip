package me.rikmentink.dp.models;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int product_nummer;
    private String naam;
    private String beschrijving;
    private double prijs;

    private List<OVChipkaart> kaarten;

    public Product(int product_nummer, String naam, String beschrijving, double prijs) {
        this.product_nummer = product_nummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
        this.kaarten = new ArrayList<OVChipkaart>();
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

    public List<OVChipkaart> getKaarten() {
        return this.kaarten;
    }

    public void addKaart(OVChipkaart kaart) {
        if (!this.kaarten.contains(kaart)) {
            this.kaarten.add(kaart);
            kaart.addProduct(this);
        }
    }

    public void setKaarten(List<OVChipkaart> kaarten) {
        this.kaarten = kaarten;
    }

    public void removeKaart(OVChipkaart kaart) {
        if (this.kaarten.contains(kaart)) {
            this.kaarten.remove(kaart);
            kaart.removeProduct(this);
        }
    }

    @Override
    public String toString() {
        return "Product [product_nummer=" + product_nummer + ", naam=" + naam + ", beschrijving=" + beschrijving
                + ", prijs=" + prijs + "]";
    }
}
