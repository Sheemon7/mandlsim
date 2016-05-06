package cz.cvut.fel.memorice.model.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import cz.cvut.fel.memorice.model.entities.entries.Entry;
import cz.cvut.fel.memorice.model.util.TermAlreadyUsedException;

/**
 * Created by sheemon on 18.3.16.
 */
public class Set extends Entity implements Serializable {

    private java.util.Set entries = new HashSet<>();

    public Set(String name) {
        super(name);
    }

    @Override
    public EntityEnum getType() {
        return EntityEnum.GROUP;
    }

    public boolean addEntry(Entry entry) throws TermAlreadyUsedException {
        if (!entries.contains(entry)) {
            return entries.add(entry);
        } else {
            throw new TermAlreadyUsedException();
        }
    }

    public boolean removeEntry(Entry entry) {
        return entries.remove(entry);
    }

    @Override
    public int size() {
        return entries.size();
    }

    @Override
    public Iterator iterator() {
        return entries.iterator();
    }

    @Override
    public List<Entry> getListOfEntries() {
        return new ArrayList<Entry>(entries);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Set set = (Set) o;

        return !(entries != null ? !entries.equals(set.entries) : set.entries != null);

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Set: " + getName() +
                "\nentries= ");
        for (Entry entry:
                entries) {
            builder.append(entry.toString()+ "\n");
        }
        return builder.toString();
    }
}