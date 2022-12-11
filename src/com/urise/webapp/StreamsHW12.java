package com.urise.webapp;
/*
Java 8 Streams:
        реализовать метод через стрим int minValue(int[] values).
        Метод принимает массив цифр от 1 до 9, надо выбрать уникальные и вернуть минимально возможное число, составленное из этих уникальных цифр. Не использовать преобразование в строку и обратно. Например {1,2,3,3,2,3} вернет 123, а {9,8} вернет 89

        реализовать метод List<Integer> oddOrEven(List<Integer> integers) если сумма всех чисел нечетная - удалить все нечетные, если четная - удалить все четные. Сложность алгоритма должна быть O(N). Optional - решение в один стрим.
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamsHW12 {
    public static void main(String[] args) {
        int[] values = {5, 5, 6, 2, 2, 3, 2};
        List<Integer> integers = new ArrayList<>(values.length) {
        };
        for (int value : values) {
            integers.add(value);
        }
        System.out.println(minValue(values));
        System.out.println(oddOrEven(integers));
    }

    private static int minValue(int[] values) {
        return IntStream.of(values)
                .distinct()
                .sorted()
                .reduce(0, (sum, p) -> sum = sum * 10 + p);
    }

    private static List<Integer> oddOrEven(List<Integer> integers) {
        Map<Boolean, List<Integer>> map = integers
                .stream()
                .collect(Collectors
                        .partitioningBy(s1 -> s1 % 2 == 0));
        return map.get(false).size() % 2 > 0 ? map.get(false) : map.get(true);
    }
}
