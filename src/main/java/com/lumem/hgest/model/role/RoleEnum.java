package com.lumem.hgest.model.role;

public enum RoleEnum {
    SUPERVISOR("SUPERVISOR",(short) 1),
    WORKER("WORKER",(short) 0),
    ADMIN("ADMIN",(short) 2),
    DEV("DEV", (short) 3);

    private final String name;
    private final short power;

    RoleEnum(String stringName,short power){
        name = stringName;
        this.power = power;
    }

    public boolean hasControlOver(RoleEnum target){
        return this.power > target.power;
    }

    public String getName() {
        return name;
    }

    public static RoleEnum getRoleByName(String name){
        return switch (name.toUpperCase()) {
            case "SUPERVISOR" -> RoleEnum.SUPERVISOR;
            case "WORKER" -> RoleEnum.WORKER;
            case "ADMIN" -> RoleEnum.ADMIN;
            case "DEV" -> RoleEnum.DEV;
            default -> null;
        };
    }


}

