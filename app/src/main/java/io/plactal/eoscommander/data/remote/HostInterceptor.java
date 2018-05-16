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
package io.plactal.eoscommander.data.remote;


import java.io.IOException;

import io.plactal.eoscommander.util.StringUtils;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by swapnibble on 2017-11-03.
 */
public class HostInterceptor implements Interceptor {

    private String mHost;
    private String mScheme;
    private int mPort;

    public HostInterceptor(){
    }

    public void setInterceptor(String scheme, String host, int port) {
        mScheme = scheme;
        mHost   = host;
        mPort   = port;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        // If new Base URL is properly formatted then replace the old one
        if ( !StringUtils.isEmpty(mScheme) && !StringUtils.isEmpty(mHost) ) {
            HttpUrl newUrl = original.url().newBuilder()
                    .scheme(mScheme)
                    .host(mHost)
                    .port( mPort )
                    .build();
            original = original.newBuilder()
                    .url(newUrl)
                    .build();
        }


        return chain.proceed(original);
    }
}
