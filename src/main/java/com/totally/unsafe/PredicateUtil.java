package com.totally.unsafe;

import java.util.function.Predicate;

public class PredicateUtil {
    public static Predicate<Integer> isSuccess = Predicate.isEqual(0);
}
