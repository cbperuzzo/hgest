package com.lumem.hgest.model.Role;

import com.lumem.hgest.model.Role.Interface.AuthorityBoolean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

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

    public List<String> authorities(){

        List<Method> methods = Arrays.stream(this.getClass().getMethods()).toList();

        methods = methods.stream().filter(method -> method.getAnnotation(AuthorityBoolean.class) != null).toList();

        methods = methods.stream().filter(method -> {
            boolean result = false;
            try {
                result = (boolean) method.invoke(this);
            } catch (IllegalAccessException | InvocationTargetException ignored) {

            }
            return result;
        }).toList();

        return methods.stream().map(method -> MethodFormater.removeFirstTwo(method.getName()).toUpperCase()).toList();
    }

    @AuthorityBoolean
    public boolean isEdit() {
        return edit;
    }

    @AuthorityBoolean
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

class MethodFormater{

    static String removeFirstTwo(String methodName){
        StringBuilder sb = new StringBuilder(methodName);
        sb.deleteCharAt(0);
        sb.deleteCharAt(0);
        return sb.toString();
    }
}
