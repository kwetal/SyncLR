package nl.ocwduo.autorisatie.domain;

public class Gebruiker
{

    private final String voornaam;
    private final String achternaam;
    private String woonplaats;

    public Gebruiker(String vn, String an) {
        voornaam = vn;
        achternaam = an;
    }

    @Override
    public String toString() {
        return "[" + voornaam + " " + achternaam + ":" + woonplaats + "]";
    }

    public String getVoornaam() {
        return voornaam;
    }

    public String getAchternaam() {
        return achternaam;
    }

    public String getWoonplaats() {
        return woonplaats;
    }

    public void setWoonplaats(String woonplaats) {
        this.woonplaats = woonplaats;
    }
}
