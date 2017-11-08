/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.ocwduo.autorisatie;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.junit.Test;

/**
 *
 * @author arjen
 */
public class SynchronizerTest
{

    public SynchronizerTest() {
    }

    @Test
    public void testSynchronizer() {
        SyncableObjectProvider left = setupTestSetLeft();
        SyncableObjectProvider right = setupTestSetRight();
        Synchronizer syncer = new Synchronizer(left, right);
        syncer.collectTheSets();
        syncer.synchronizeLeftToRight();
        System.out.println("Resultaat:\n"+right);
    }

    private SyncableObjectProvider setupTestSetLeft() {
        GebruikerProvider data = new GebruikerProvider();
        data.add("Arjen", "Bax", "Drachten");
        data.add("Peter", "Bax", "Akkrum");
        data.add("Chris", "Bax", "Bedum");
        data.add("Theo", "Bax", "Norg");
        return data;
    }

    private SyncableObjectProvider setupTestSetRight() {
        GebruikerProvider data = new GebruikerProvider();
        data.add("Arjen", "Bax", "Drachten");
        data.add("Peter", "Bax", "Drachten");
        data.add("Theo", "Bax", "Drachten");
        data.add("Marcella", "Gortmaker", "Bedum");
        return data;
    }

}


class Gebruiker {
    final String voornaam;
    final String achternaam;
    String woonplaats;
    Gebruiker(String vn, String an) {
        voornaam = vn;
        achternaam = an;
    }
}

class SyncableGebruiker extends Gebruiker implements SyncableObject {

    public SyncableGebruiker(String vn, String an) {
        super(vn, an);
    }

    @Override
    public String getKey() {
        return achternaam+":"+voornaam;
    }

    @Override
    public boolean isEqualTo(SyncableObject obj) {
        Gebruiker that = (Gebruiker)obj;
        return Objects.equals(voornaam, that.voornaam)
            && Objects.equals(achternaam, that.achternaam)
            && Objects.equals(woonplaats, that.woonplaats);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj
            || obj != null
            && obj instanceof SyncableGebruiker
            && getKey().equals(((SyncableGebruiker)obj).getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getKey());
    }
}

class GebruikerProvider implements SyncableObjectProvider {
    private final List<SyncableGebruiker> data = new ArrayList<>();
    public void add(String vn, String an, String wp) {
        SyncableGebruiker u = new SyncableGebruiker(vn, an);
        u.woonplaats = wp;
        data.add(u);
    }

    @Override
    public void createItemFrom(SyncableObject item) {
        SyncableGebruiker u = (SyncableGebruiker)item;
        data.add(u);
    }

    @Override
    public void delete(SyncableObject item) {
        SyncableGebruiker u = (SyncableGebruiker)item;
        data.remove(u);
    }

    @Override
    public void updateFrom(SyncableObject leftItem, SyncableObject rightItem) {
        SyncableGebruiker leftUser = (SyncableGebruiker)leftItem;
        SyncableGebruiker rightUser = (SyncableGebruiker)rightItem;
        int index = data.indexOf(rightUser);
        data.set(index, leftUser);
    }

    @Override
    public Iterable<SyncableObject> getCollection() {
        return new Iterable<SyncableObject>() {
            @Override
            public Iterator<SyncableObject> iterator() {
                return new MyIterator();
            }
        };
    }

    private class MyIterator implements Iterator<SyncableObject>
    {
        Iterator<SyncableGebruiker> dataIterator = data.iterator();
        @Override
        public boolean hasNext() {
            return dataIterator.hasNext();
        }

        @Override
        public SyncableObject next() {
            return dataIterator.next();
        }
    }


}