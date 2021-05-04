package fr.elplauto.gocrypto.model;

import java.util.HashMap;
import java.util.Map;

public enum SortCryptoCriteriaEnum {

    NAME(1),
    PRICE(2),
    PERCENT_CHANGE(3);

    private int value;
    private static Map<Integer, SortCryptoCriteriaEnum> map = new HashMap<Integer, SortCryptoCriteriaEnum>();

    private SortCryptoCriteriaEnum(int value) {
        this.value = value;
    }

    static {
        for (SortCryptoCriteriaEnum criteria : SortCryptoCriteriaEnum.values()) {
            map.put(criteria.value, criteria);
        }
    }

    public static SortCryptoCriteriaEnum valueOf(int criteria) {
        return (SortCryptoCriteriaEnum) map.get(criteria);
    }

    public int getValue() {
        return value;
    }
}


