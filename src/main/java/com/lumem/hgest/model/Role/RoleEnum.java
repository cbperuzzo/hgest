package com.lumem.hgest.model.Role;

public enum RoleEnum {
    SUPERVISOR("SUPERVISOR",true,false),
    WORKER("WORKER",false,false),
    ADMIN("ADMIN",true,true),
    DEV("DEV",true,true);

    private final String name;
    private final boolean edit;
    private final boolean stats;

    RoleEnum(String stringName,boolean e,boolean s){
        name = stringName;
        edit = e;
        stats = s;
    }

    public String getName() {
        return name;
    }

    public boolean isEdit() {
        return edit;
    }

    public boolean isStats() {
        return stats;
    }

    @Override
    public String toString() {
        return "RoleEnum{" +
                "name='" + name + '\'' +
                ", edit=" + edit +
                ", stats=" + stats +
                '}';
    }
}

