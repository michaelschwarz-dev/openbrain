package dev.michaelschwarz.knotenhirn.dtos;

public enum DataType {

    FACT("Fact"),
    PRINCIPLE("Principle"),
    EXPERIENCE("Experience");

    public final String label;

    DataType(String label) {
        this.label = label;
    }
}
