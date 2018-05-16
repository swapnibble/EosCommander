package io.plactal.eoscommander.crypto.digest;

import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import io.plactal.eoscommander.crypto.util.HexUtils;

public class Sha512 implements Comparable<Sha512> {

   public static final int HASH_LENGTH = 64;
   public static final Sha512 ZERO_HASH = new Sha512(new byte[HASH_LENGTH]);

   final private byte[] mHashBytes;

   public Sha512(byte[] bytes) {
      Preconditions.checkArgument(bytes.length == HASH_LENGTH);
      this.mHashBytes = bytes;
   }

   public static Sha512 from(byte[] data) {
      MessageDigest digest;
      try {
         digest = MessageDigest.getInstance("SHA-512");
      } catch (NoSuchAlgorithmException e) {
         throw new RuntimeException(e); //cannot happen
      }

      digest.update(data, 0, data.length);

      return new Sha512(digest.digest());
   }


   private Sha512(byte[] bytes, int offset) {
      //defensive copy, since incoming bytes is of arbitrary length
      mHashBytes = new byte[HASH_LENGTH];
      System.arraycopy(bytes, offset, mHashBytes, 0, HASH_LENGTH);
   }

   @Override
   public boolean equals(Object other) {
      if (other == this) {
         return true;
      }
      if (!(other instanceof Sha512))
         return false;
      return Arrays.equals(mHashBytes, ((Sha512) other).mHashBytes);
   }


   @Override
   public String toString() {
      return HexUtils.toHex(mHashBytes);
   }

   public byte[] getBytes() {
      return mHashBytes;
   }

   @Override
   public int compareTo(Sha512 o) {
      for (int i = 0; i < HASH_LENGTH; i++) {
         byte myByte = mHashBytes[i];
         byte otherByte = o.mHashBytes[i];

         final int compare = Ints.compare(myByte, otherByte);
         if (compare != 0)
            return compare;
      }
      return 0;
   }
}
