/*
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Parts of this code was extracted from the Java cryptography library from
 * www.bouncycastle.org.
 */
package io.plactal.eoscommander.crypto.ec;

import java.math.BigInteger;

/**
 * modified by swapnibble from plactal.io
 * Various tools for elliptic curves
 */
public class EcTools {

   private static CurveParam[] sCurveParams = new CurveParam[2];

   public static CurveParam getCurveParam( int curveType ){

      if ( (curveType < 0 ) || ( sCurveParams.length <= curveType ) ) {
         throw new IllegalArgumentException("Unknown Curve Type: " + curveType);
      }

      if ( null == sCurveParams[curveType] ) {
         if (CurveParam.SECP256_K1 == curveType) {
            sCurveParams[CurveParam.SECP256_K1] = new CurveParam( CurveParam.SECP256_K1
                    ,"FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F" // p
                    , "0"  // a
                    , "7"  // b
                    , "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798"  //Gx
                    , "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"  //Gy
                    , "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"); // n
         }
         else if (CurveParam.SECP256_R1 == curveType) {
            sCurveParams[CurveParam.SECP256_R1] = new CurveParam( CurveParam.SECP256_R1
                    , "ffffffff00000001000000000000000000000000ffffffffffffffffffffffff" // p
                    , "ffffffff00000001000000000000000000000000fffffffffffffffffffffffc" // a
                    , "5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b" // b
                    , "6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296"  //Gx
                    , "4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5"  //Gy
                    , "ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551"); // n
         }
      }

      return sCurveParams[curveType];
   }


   /**
    * Get the length of the byte encoding of a field element
    */
   public static int getByteLength(int fieldSize) {
      return (fieldSize + 7) / 8;
   }

   /**
    * Get a big integer as an array of bytes of a specified length
    */
   public static byte[] integerToBytes(BigInteger s, int length) {
      byte[] bytes = s.toByteArray();

      if (length < bytes.length) {
         // The length is smaller than the byte representation. Truncate by
         // copying over the least significant bytes
         byte[] tmp = new byte[length];
         System.arraycopy(bytes, bytes.length - tmp.length, tmp, 0, tmp.length);
         return tmp;
      } else if (length > bytes.length) {
         // The length is larger than the byte representation. Copy over all
         // bytes and leave it prefixed by zeros.
         byte[] tmp = new byte[length];
         System.arraycopy(bytes, 0, tmp, tmp.length - bytes.length, bytes.length);
         return tmp;
      }
      return bytes;
   }

   /**
    * Multiply a point with a big integer
    */
   public static EcPoint multiply(EcPoint p, BigInteger k) {
      BigInteger e = k;
      BigInteger h = e.multiply(BigInteger.valueOf(3));

      EcPoint neg = p.negate();
      EcPoint R = p;

      for (int i = h.bitLength() - 2; i > 0; --i) {
         R = R.twice();

         boolean hBit = h.testBit(i);
         boolean eBit = e.testBit(i);

         if (hBit != eBit) {
            R = R.add(hBit ? p : neg);
         }
      }

      return R;
   }

   public static EcPoint sumOfTwoMultiplies(EcPoint P, BigInteger k, EcPoint Q, BigInteger l) {
      int m = Math.max(k.bitLength(), l.bitLength());
      EcPoint Z = P.add(Q);
      EcPoint R = P.getCurve().getInfinity();

      for (int i = m - 1; i >= 0; --i) {
         R = R.twice();

         if (k.testBit(i)) {
            if (l.testBit(i)) {
               R = R.add(Z);
            } else {
               R = R.add(P);
            }
         } else {
            if (l.testBit(i)) {
               R = R.add(Q);
            }
         }
      }

      return R;
   }

   //ported from BitcoinJ
   public static EcPoint decompressKey(CurveParam param, BigInteger x, boolean firstBit) {
      int size = 1 + getByteLength( param.getCurve().getFieldSize());// Secp256k1Param.curve.getFieldSize());
      byte[] dest = integerToBytes(x, size);
      dest[0] = (byte) (firstBit ? 0x03 : 0x02);
      return param.getCurve().decodePoint(dest);// Secp256k1Param.curve.decodePoint(dest);
   }
}
