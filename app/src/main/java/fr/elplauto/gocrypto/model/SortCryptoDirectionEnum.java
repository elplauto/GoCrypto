package fr.elplauto.gocrypto.model;

import java.util.HashMap;
import java.util.Map;

public enum SortCryptoDirectionEnum {

    ASC(1),
    DESC(2);

    private int value;
    private static Map<Integer, SortCryptoDirectionEnum> map = new HashMap<Integer, SortCryptoDirectionEnum>();

    private SortCryptoDirectionEnum(int value) {
        this.value = value;
    }

    static {
        for (SortCryptoDirectionEnum criteria : SortCryptoDirectionEnum.values()) {
            map.put(criteria.value, criteria);
        }
    }

    public static SortCryptoDirectionEnum valueOf(int direction) {
        return (SortCryptoDirectionEnum) map.get(direction);
    }

    public int getValue() {
        return value;
    }
}


