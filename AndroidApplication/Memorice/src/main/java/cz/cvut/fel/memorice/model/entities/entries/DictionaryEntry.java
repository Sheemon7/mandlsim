package cz.cvut.fel.memorice.model.entities.entries;

import java.io.Serializable;

/**
 * Created by sheemon on 18.3.16.
 */
public class DictionaryEntry extends Entry implements Serializable{

    private final String definition;

    public DictionaryEntry(String key, String value) {
        super(value);
        this.definition = key;
    }

    @Override
    public String toString() {
        return super.toString() + ": " + definition;
    }

    public String getDefinition() {
        return definition;
    }
}
