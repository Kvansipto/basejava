package com.urise.webapp;

import com.urise.webapp.model.Resume;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainReflection {

    public static void main(String[] args) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Resume r = new Resume("123");
        Class<? extends Resume> aClass = r.getClass();
//        Field field = r.getClass().getDeclaredFields()[0];
//        field.setAccessible(true);
//        System.out.println(field.getName());
//        System.out.println(field.get(r));
//        field.set(r, "new_uuid");
        Method toStringMethod = aClass.getMethod("toString");
        System.out.println(toStringMethod.invoke(r));
    }
}
