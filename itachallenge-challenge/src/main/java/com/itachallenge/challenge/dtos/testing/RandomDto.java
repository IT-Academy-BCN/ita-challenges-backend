package com.itachallenge.challenge.dtos.testing;

import java.util.Set;

public class RandomDto {
    private String name;
    private int[] values;
    private boolean happy;
    private AssociatedRandomDto other;
    private Set<AssociatedRandomDto> others;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    public AssociatedRandomDto getOther() {
        return other;
    }

    public void setOther(AssociatedRandomDto other) {
        this.other = other;
    }

    public Set<AssociatedRandomDto> getOthers() {
        return others;
    }

    public void setOthers(Set<AssociatedRandomDto> others) {
        this.others = others;
    }
}
