package fr.elplauto.gocrypto.model;

public class CryptoMerge {

    private Crypto crypto;
    private CryptoInWallet cryptoInWallet;

    public CryptoMerge() {}

    public CryptoMerge(Crypto crypto, CryptoInWallet cryptoInWallet) {
        this.crypto = crypto;
        this.cryptoInWallet = cryptoInWallet;
    }

    public Crypto getCrypto() {
        return crypto;
    }

    public void setCrypto(Crypto crypto) {
        this.crypto = crypto;
    }

    public CryptoInWallet getCryptoInWallet() {
        return cryptoInWallet;
    }

    public void setCryptoInWallet(CryptoInWallet cryptoInWallet) {
        this.cryptoInWallet = cryptoInWallet;
    }
}
