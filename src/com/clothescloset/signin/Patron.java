package com.clothescloset.signin;

class Patron {
    protected String name;
    protected int id;

    protected int familySize;

    protected Patron(int id, String name, int familySize) {
        this.name = name;
        this.id = id;
        this.familySize = familySize;
    }
}