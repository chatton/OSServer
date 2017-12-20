package ie.gmit.sw.util;

import java.util.List;
import java.util.stream.Collectors;

public class ListUtils {

    private ListUtils() {}

    /*
    return the N last elements from a list.
     */
    public static <T> List<T> nLast(int nLast, List<T> list) {
        int startingPos = list.size() - nLast;
        startingPos = startingPos < 0 ? 0 : startingPos;
        return list.stream()
                .skip(startingPos) // skip all but the last n
                .collect(Collectors.toList()); // return as a list.
    }
}
