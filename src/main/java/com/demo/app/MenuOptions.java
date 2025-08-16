package com.demo.app;

public enum MenuOptions {
    //make for each case in the big switch in the main
    CHECK_ALL_LOANS(1), CHECK_A_LOAN(2), ADD_LOAN(3), DEACTIVATE_LOAN(4), ADD_CLIENT(5), REMOVE_CLIENT(6), ADD_BOOK(7), REMOVE_BOOK(8), EXIT(9);
    private final int value;
    MenuOptions(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public static MenuOptions fromInt(int value){
        for(MenuOptions i:MenuOptions.values()){
            if(i.getValue() == value){
                return i;
            }
        }
        return null;
    }
}
