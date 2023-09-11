package me.rikmentink.dp.models;

import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Reiziger {
    private int id;
    private String voorletter;
    private String tussenvoegsel;
    private String achternaam;
    private LocalDate geboortedatum;

    public Reiziger(int id, String voorletter, String tussenvoegsel, String achternaam, LocalDate geboortedatum) {
        this.id = id;
        this.voorletter = voorletter;
        this.tussenvoegsel = tussenvoegsel;
        this.achternaam = achternaam;
        this.geboortedatum = geboortedatum;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVoorletter() {
        return this.voorletter;
    }

    public void setVoorletter(String voorletter) {
        this.voorletter = voorletter;
    }

    public String getTussenvoegsel() {
        return this.tussenvoegsel;
    }

    public void setTussenvoegsel(String tussenvoegsel) {
        this.tussenvoegsel = tussenvoegsel;
    }

    public String getAchternaam() {
        return this.achternaam;
    }

    public void setAchternaam(String achternaam) {
        this.achternaam = achternaam;
    }

    public LocalDate getGeboortedatum() {
        return this.geboortedatum;
    }

    public void setGeboortedatum(LocalDate geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    public String getVoorletterWithDots() {
        return String.join(".", this.getVoorletter()) + ".";
    }

    public String getNaam() {
        return Stream.of(this.getVoorletterWithDots(), this.getTussenvoegsel(), this.getAchternaam())
                .filter(v -> v != null && !v.isEmpty()).collect(Collectors.joining(" "));
    }

    @Override
    public String toString() {
        return "Reiziger [id=" + id + ", naam=" + this.getNaam() + ", geboortedatum=" + geboortedatum + "]";
    }
}
