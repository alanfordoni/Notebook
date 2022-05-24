package com.brzimetrokliziretro.notebook.models;

public class ShoppingList {
    boolean box;
    String text;

    public ShoppingList(){
        this.box = box;
        this.text = text;
    }

    public boolean getBox(){
        return this.box;
    }

    public String getText() {
        return text;
    }

    public void setBox(boolean box){
        this.box = box;
    }

    public void setText(String text){
        this.text = text;
    }
}
