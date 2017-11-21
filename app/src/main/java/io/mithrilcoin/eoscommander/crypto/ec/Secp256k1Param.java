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

package io.mithrilcoin.eoscommander.crypto.ec;

import java.math.BigInteger;

import io.mithrilcoin.eoscommander.crypto.util.HexUtils;


/**
 * modified by swapnibble from Mithrilcoin.io
 * secp256k1 parameters.
 * see https://github.com/cryptocoinjs/ecurve/blob/master/lib/curves.json
 */
public class Secp256k1Param {
   public static final EcCurve curve;
   public static final byte[] seed;
   public static final EcPoint G;
   public static final BigInteger n;
   public static final BigInteger h;

   public static final BigInteger HALF_CURVE_ORDER;
   /**
    * The maximum number a signature can have in version 3 transactions
    */
   public static final BigInteger MAX_SIG_S;

   // secp256k1 curve params
   static {
      BigInteger q = new BigInteger(1,
            HexUtils.toBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));
      BigInteger a = BigInteger.ZERO;
      BigInteger b = BigInteger.valueOf(7);
      curve = new EcCurve(q, a, b);
      seed = null;
      G = curve.decodePoint(HexUtils.toBytes("04" + "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798"
            + "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"));
      n = new BigInteger(1, HexUtils.toBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"));
      h = BigInteger.ONE;
      MAX_SIG_S = new BigInteger(1, HexUtils.toBytes("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0"));

      HALF_CURVE_ORDER = n.shiftRight(1);
   }
}
