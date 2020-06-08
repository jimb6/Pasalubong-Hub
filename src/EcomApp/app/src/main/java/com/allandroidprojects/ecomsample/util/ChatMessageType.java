package com.allandroidprojects.ecomsample.util;

public enum ChatMessageType {
    SELLER(0), CUSTOMER(1), PRODUC_ITEM(2);

    private int data;

    ChatMessageType(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }
}
