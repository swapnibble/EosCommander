package io.plactal.eoscommander.data.remote.model.types;


import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.plactal.eoscommander.util.StringUtils;

/**
 * Created by swapnibble on 2017-09-12.
 */

public class TypeAsset implements EosType.Packer {

    public static final String CORE_SYMBOL_NAME = "EOS";
    private static final byte[] CORE_SYMBOL = new byte[]{4, 69, 79, 83, 0, 0, 0, 0}; // (int64_t(4) | (uint64_t('E') << 8) | (uint64_t('O') << 16) | (uint64_t('S') << 24))

    // 아래 table 은, eos/libraries/types/Asset.cpp 에서 가져옴.
    private static final long[] PRECISION_TABLE = {
            1,
            10,
            100,
            1000,
            10000,
            100000,
            1000000,
            10000000,
            100000000,
            1000000000,
            10000000000L,
            100000000000L,
            1000000000000L,
            10000000000000L,
            100000000000000L,
            1000000000000000L,
            10000000000000000L,
            100000000000000000L
    };

    private long mAmount;
    private byte[] mAssetSymbol;
    private String mSymbolName;

    public TypeAsset(String value) {

        value = value.trim();
        Pattern pattern = Pattern.compile("^([0-9]+)\\.?([0-9]*)([ ][a-zA-Z0-9]{1,7})?$");//\\s(\\w)$")
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            String beforeDotVal = matcher.group(1), afterDotVal = matcher.group(2);
            boolean symbolNameIsEmpty = StringUtils.isEmpty(matcher.group(3));
            int decimals = (symbolNameIsEmpty ? CORE_SYMBOL[0] : afterDotVal.length());

            this.mAmount = Long.valueOf(beforeDotVal + afterDotVal);
            this.mSymbolName = symbolNameIsEmpty ? CORE_SYMBOL_NAME : matcher.group(3).trim();
            this.mAssetSymbol = makeAssetSymbol(mSymbolName, decimals);
        } else {
            this.mAmount = 0;
            this.mSymbolName = CORE_SYMBOL_NAME;
            this.mAssetSymbol = CORE_SYMBOL;
        }
    }

    public TypeAsset(long amount) {
        this(amount, CORE_SYMBOL);
    }

    public TypeAsset(long amount, byte[] symbol) {
        mAmount = amount;
        mAssetSymbol = symbol;

        final int byteLen = mAssetSymbol.length;
        int symbolLen = 0;
        char[] sym = new char[byteLen];
        for (int i = 1; i < byteLen; i++) {
            char oneChar = (char) mAssetSymbol[i];
            if (oneChar != 0) {
                sym[i] = oneChar;
                symbolLen++;
            } else {
                break;
            }
        }
        mSymbolName = new String(sym, 1, symbolLen);
    }

    private byte[] makeAssetSymbol(String symbolName, int decimals) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < (7 - symbolName.length()); i++) {
            stringBuilder.append("\0");
        }
        String asset = (char) decimals + symbolName + stringBuilder;
        ByteBuffer byteBuffer = ByteBuffer.wrap(asset.getBytes());
        return byteBuffer.array();
    }

    public int decimals() {
        return mAssetSymbol[0];
    }

    public long precision() {
        int decimal = decimals();
        if (decimal >= PRECISION_TABLE.length) {
            decimal = 0;
        }

        return PRECISION_TABLE[decimal];
    }

    public double toDouble() {
        return mAmount / precision();
    }

    public String symbolName() {
        return mSymbolName;
    }

    public byte[] assetSymbol() {
        return mAssetSymbol;
    }

    public long getAmount() {
        return mAmount;
    }

    @Override
    public String toString() {
        long precisionVal = precision();
        String result = String.valueOf(mAmount / precisionVal);

        if (decimals() > 0) {
            long fract = mAmount % precisionVal;
            result += "." + String.valueOf(precisionVal + fract).substring(1);
        }

        return result + " " + mSymbolName;
    }

    @Override
    public void pack(EosType.Writer writer) {

        writer.putLongLE(mAmount);

        writer.putBytes(mAssetSymbol);
    }

    public void add(TypeAsset other) {
        //mAmount
    }
}