package com.itachallenge.challenge.helper;

import com.itachallenge.challenge.exception.ConverterException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConverterAbstract <F, T> {

    private final static String SET_METHOD_PREFIX = "set";
    private final static String GET_METHOD_PREFIX = "get";

    private final List<Field> fieldsOfF;
    private final List<Field> fieldsOfT;

    private final List<Method> methodsOfF;
    private final List<Method> methodsOfT;

    private final Class<T> classToType;
    private final Class<F> classFromType;

    public ConverterAbstract(Class<F> fromClass, Class<T> toClass) {
        this.classToType = toClass;
        this.classFromType = fromClass;

        this.fieldsOfF = getAllFields(fromClass);
        this.fieldsOfT = getAllFields(toClass);

        this.methodsOfF = getAllMethods(fromClass);
        this.methodsOfT = getAllMethods(toClass);
    }

    //TODO: implement reactive Flux
    public T convert(F object) throws ConverterException {
        T instance = getInstance(classToType);

        for (Field item : fieldsOfF) {
            setValue(instance, getValue(object, item, methodsOfF), item, fieldsOfT, methodsOfT);
        }

        return instance;
    }

    /*public F convertToDto(T object) throws ConverterException {
        F instance = getInstance(classFromType);

        for (Field item : fieldsOfT) {
            setValue(instance, getValue(object, item, methodsOfT), item, fieldsOfF, methodsOfF);
        }

        return instance;
    }*/

    private Object getValue(Object object, Field field, List<Method> methods) throws ConverterException {
        Optional<Method> optionalMethod;
        Object value = null;

        if (field.canAccess(object)) {
            try {
                value = field.get(object);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();

                throw new ConverterException(e.getMessage(), e);
            }
        } else {
            optionalMethod = methods.stream().filter(
                            x -> x.getName().equals((GET_METHOD_PREFIX + capitalizeFirstChar(field.getName()))) && x.canAccess(object))
                    .findFirst();
            if (optionalMethod.isPresent()) {
                try {
                    value = optionalMethod.get().invoke(object);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();

                    throw new ConverterException(e.getMessage(), e);
                }
            }
        }

        return value;
    }

    private void setValue(Object instance, Object value, Field item, List<Field> fields, List<Method> methods)
            throws ConverterException {

        Optional<Field> optionalField = fields.stream().filter(
                        x -> x.getName().equals(item.getName()) && x.getType().equals(item.getType()) && x.canAccess(instance))
                .findFirst();

        if (optionalField.isPresent()) {
            try {
                optionalField.get().set(instance, value);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            Optional<Method> optionalMethod = methods.stream().filter(
                            x -> x.getName().equals((SET_METHOD_PREFIX + capitalizeFirstChar(item.getName()))) && x.canAccess(instance))
                    .findFirst();
            if (value != null) {
                if (optionalMethod.isPresent() && optionalMethod.get().getParameterCount() == 1
                        && optionalMethod.get().getParameters()[0].getType().isAssignableFrom(value.getClass())) {
                    try {
                        optionalMethod.get().invoke(instance, value);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();

                        throw new ConverterException(e.getMessage(), e);
                    }
                }
            }
        }
    }

    private <E> E getInstance(Class<E> instanceClass) throws ConverterException {
        E instance;
        try {
            instance = instanceClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                 | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();

            throw new ConverterException(e.getMessage(), e);
        }
        return instance;
    }

    private <E> List<Field> getAllFields(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        List<Field> filteredFields = Arrays.stream(clazz.getDeclaredFields()).collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    private <E> List<Method> getAllMethods(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Method> result = new ArrayList<>(getAllMethods(clazz.getSuperclass()));
        List<Method> filteredFields = Arrays.stream(clazz.getDeclaredMethods()).collect(Collectors.toList());
        result.addAll(filteredFields);
        return result;
    }

    private String capitalizeFirstChar(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

}
