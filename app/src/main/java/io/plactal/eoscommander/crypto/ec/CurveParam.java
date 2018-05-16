/*
 * Copyright (c) 2017-2018 PLACTAL.
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.plactal.eoscommander.crypto.ec;

import java.math.BigInteger;

import io.plactal.eoscommander.crypto.util.HexUtils;

/**
 * Created by swapnibble on 2018-02-02.
 */

public class CurveParam {
    public static final int SECP256_K1 = 0;
    public static final int SECP256_R1 = 1;

    private final int curveParamType;
    private final EcCurve curve;
    private final EcPoint G;
    private final BigInteger n;
    //private final BigInteger h;

    private final BigInteger HALF_CURVE_ORDER;

    public CurveParam( int curveParamType, String pInHex, String aInHex, String bInHex, String GxInHex, String GyInHex, String nInHex ){
        this.curveParamType = curveParamType;
        BigInteger p = new BigInteger(pInHex, 16); //p
        BigInteger b = new BigInteger(bInHex , 16);
        BigInteger a = new BigInteger( aInHex, 16);
        curve = new EcCurve(p, a, b);

        G = curve.decodePoint( HexUtils.toBytes("04" + GxInHex + GyInHex)  );
        n = new BigInteger(nInHex, 16);
        //h = BigInteger.ONE;

        HALF_CURVE_ORDER = n.shiftRight(1);
    }

    public int getCurveParamType() {
        return curveParamType;
    }

    public boolean isType(int paramType ) {
        return curveParamType == paramType;
    }


    public EcPoint G() {
        return this.G;
    }

    public BigInteger n() {
        return this.n;
    }

    public BigInteger halfCurveOrder() {
        return HALF_CURVE_ORDER;
    }

    public EcCurve getCurve() {
        return curve;
    }
}
