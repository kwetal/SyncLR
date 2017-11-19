/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.ocwduo.autorisatie;

import java.util.*;
import org.apache.commons.collections.*;
import static org.junit.Assert.*;
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
        checkMissingLeft(syncer);
        checkMissingRight(syncer);
        checkDiffering(syncer);
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
        data.add("Theo", "Bax", "Utrecht");
        data.add("Marcella", "Gortmaker", "Bedum");
        data.add("Peter", "Bax", "Drachten");
        data.add("Arjen", "Bax", "Drachten");
        return data;
    }

    private void checkMissingLeft(Synchronizer syncer) {
        List<LREntry> missing = new ArrayList<>();
        CollectionUtils.addAll(missing, syncer.leftMissingItems());
        assertEquals(1, missing.size());
        assertThatListContainsKey(missing, "Gortmaker:Marcella");
    }

    private void checkMissingRight(Synchronizer syncer) {
        List<LREntry> missing = new ArrayList<>();
        CollectionUtils.addAll(missing, syncer.rightMissingItems());
        assertEquals(1, missing.size());
        assertThatListContainsKey(missing, "Bax:Chris");
    }

    private void checkDiffering(Synchronizer syncer) {
        List<LREntry> differing = new ArrayList<>();
        CollectionUtils.addAll(differing, syncer.differingItems());
        assertEquals(2, differing.size());
        assertThatListContainsKey(differing, "Bax:Peter");
        assertThatListContainsKey(differing, "Bax:Theo");
    }

    private void assertThatListContainsKey(List<LREntry> missing, final String testKey) {
        CollectionUtils.find(missing, new Predicate() {
            @Override
            public boolean evaluate(Object o) {
                System.out.println("compare "+((LREntry)o).getKey()+" with "+testKey);
                return ((LREntry)o).getKey().equals(testKey);
            }
        });
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

    @Override
    public String toString() {
        return "["+voornaam+" "+achternaam+":"+woonplaats+"]";
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
    public String toString() {
        String joiner = "[";
        StringBuilder sb = new StringBuilder();
        for (SyncableGebruiker item: data) {
            sb.append(joiner).append(item);
            joiner = "; ";
        }
        return sb.append("]").toString();
    }

    @Override
    public Iterable<? extends SyncableObject> getCollection() {
        return new Iterable<SyncableGebruiker>() {
            @Override
            public Iterator<SyncableGebruiker> iterator() {
                return data.iterator();
            }

        };
    }
}