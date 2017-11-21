/*
 * Copyright (c) 2017 Mithril coin.
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
package io.mithrilcoin.eoscommander.util;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Closeable;
import java.io.FileInputStream;

/**
 * Created by swapnibble on 2017-10-18.
 */

public class Utils {

    public static byte[] getFileContentFromUri(ContentResolver cr, Uri uri ) {
        if ( null == uri ) {
            return null;
        }

        ParcelFileDescriptor parcelFD = null;
        FileInputStream fi	= null;

        try {
            parcelFD = cr.openFileDescriptor(uri, "r");
            if ( null == parcelFD ) {
                return null;
            }

            long size = parcelFD.getStatSize();
            if ( size <= 0 ){
                return null;
            }

            fi = new FileInputStream( parcelFD.getFileDescriptor() );

            byte[] data = new byte[ (int)size ];
            fi.read(data);

            return data;

        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        finally {
            closeSilently( parcelFD ); // parcel 쪽을 먼저 close 해야 함.
            closeSilently( fi );
        }
    }


    public static void closeSilently( Closeable c ) {
        if ( null != c ) {
            try {
                c.close();
            } catch (Throwable t) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }
        }
    }

    public static long parseLongSafely(String content, int defaultValue) {
        if ( null == content) return defaultValue;

        try {
            return Long.parseLong(content);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public static String prettyPrintJson(Object object) {
        return new GsonBuilder().setPrettyPrinting().create().toJson( object );
    }
}
