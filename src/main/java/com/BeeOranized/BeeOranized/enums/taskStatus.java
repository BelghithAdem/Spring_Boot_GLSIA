package com.BeeOranized.BeeOranized.enums;

public enum taskStatus {
    NEW, PENDING, BLOQUED, FINISHED;


    public String toLowerCase() {
        return name().toLowerCase();
    }
}
