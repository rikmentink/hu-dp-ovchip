package me.rikmentink.dp.models;

public class Product {
    private int product_nummer;
    private String naam;
    private String beschrijving;
    private double prijs;

    private OVChipkaart kaart;

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

    @Override
    public String toString() {
        return "Product [product_nummer=" + product_nummer + ", naam=" + naam + ", beschrijving=" + beschrijving
                + ", prijs=" + prijs + "]";
    }

    public int getKaartNummer() {
        return this.kaart.getKaartnummer();
    }

    public void setKaart(OVChipkaart kaart) {
        this.kaart = kaart;
    }

    public void removeKaart() {
        if (kaart != null) {
            kaart.removeProduct(this);
            this.kaart = null;
        }
    }
}
