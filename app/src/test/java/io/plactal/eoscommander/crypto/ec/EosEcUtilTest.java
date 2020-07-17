package io.plactal.eoscommander.crypto.ec;

import org.junit.Test;

import java.util.Locale;

import io.plactal.eoscommander.crypto.digest.Sha256;

import static org.junit.Assert.*;

public class EosEcUtilTest {

    @Test
    public void encodeEosCrypto() {
        EosPrivateKey eosPrivateKey = new EosPrivateKey("5Jg3KtArcxdsk2opXpyBNqKeZ7ah9SFLPg2Xx8vHFGCnfRGffkD");
        Sha256 signData = Sha256.from("Starteos".getBytes());
        String signStr = eosPrivateKey.sign(signData,true).toString();
        System.out.println(String.format(Locale.CHINA,"sign string -> %s",signStr));
//        SIG_K1_KdxetgEKPZqXYgSFVTuDHFT1Z4fFiR8KfBRCSbyRmuxwQveeZQF2ZerV1KoEq8Shjh1PwTuS2q14zae4zgxFb2jj5kHRqA
//        SIG_K1_KYomYJ8trBBTfQyxxVLWXvnz6Nm6RSfp3ufVBoTGsEqSRqtKvGyfo498v9bDvLWkvpQ1zWhH9AcebyurWXYsNYKQG3AUKF
        EosPublicKey eosPublicKey = EcDsa.recoverPubKey(signData.getBytes(),new EcSignature(signStr));
        System.out.println(String.format(Locale.CHINA,"public key -> %s",eosPublicKey.toString()));
    }
}