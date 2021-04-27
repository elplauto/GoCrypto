package fr.elplauto.gocrypto.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Wallet {

    private Map<Integer, Float> mapCryptoIdToAmount;

    public Wallet() {
        this.mapCryptoIdToAmount = new HashMap<>();
    }

    private Float getAmount(Integer cryptoId) {
        return mapCryptoIdToAmount.get(cryptoId);
    }

    private Float setAmount(Integer cryptoId, Float amount) {
        return this.mapCryptoIdToAmount.put(cryptoId, amount);
    }

    private void clear() {
        this.mapCryptoIdToAmount.clear();
    }

    private Float remove(Integer cryptoId) {
        return this.mapCryptoIdToAmount.remove(cryptoId);
    }

    private Set<Integer> getAllCrypto() {
        return this.mapCryptoIdToAmount.keySet();
    }
}
