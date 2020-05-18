package com.allandroidprojects.ecomsample.data.models;

public class ShopMenuOption {

    private String menuName;
    private int menuIcon;

    public ShopMenuOption() {
    }

    public ShopMenuOption(String name, int icon) {
        this.menuName = name;
        this.menuIcon = icon;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public int getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(int menuIcon) {
        this.menuIcon = menuIcon;
    }
}
