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
package io.mithrilcoin.eoscommander.data.remote.model.api;

/**
 * Created by swapnibble on 2017-09-08.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EosChainInfo {

    @SerializedName("head_block_num")
    @Expose
    private Integer headBlockNum;
    @SerializedName("last_irreversible_block_num")
    @Expose
    private Integer lastIrreversibleBlockNum;
    @SerializedName("head_block_id")
    @Expose
    private String headBlockId;
    @SerializedName("head_block_time")
    @Expose
    private String headBlockTime;
    @SerializedName("head_block_producer")
    @Expose
    private String headBlockProducer;
    @SerializedName("recent_slots")
    @Expose
    private String recentSlots;
    @SerializedName("participation_rate")
    @Expose
    private String participationRate;

    public Integer getHeadBlockNum() {
        return headBlockNum;
    }

    public void setHeadBlockNum(Integer headBlockNum) {
        this.headBlockNum = headBlockNum;
    }

    public Integer getLastIrreversibleBlockNum() {
        return lastIrreversibleBlockNum;
    }

    public void setLastIrreversibleBlockNum(Integer lastIrreversibleBlockNum) {
        this.lastIrreversibleBlockNum = lastIrreversibleBlockNum;
    }

    public String getHeadBlockId() {
        return headBlockId;
    }

    public void setHeadBlockId(String headBlockId) {
        this.headBlockId = headBlockId;
    }

    public String getHeadBlockTime() {
        return headBlockTime;
    }

    public void setHeadBlockTime(String headBlockTime) {
        this.headBlockTime = headBlockTime;
    }

    public String getTimeAfterHeadBlockTime(int diffInMilSec) {
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = sdf.parse( this.headBlockTime);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add( Calendar.MILLISECOND, diffInMilSec);
            date = c.getTime();

            return sdf.format(date);

        } catch (ParseException e) {
            e.printStackTrace();
            return this.headBlockTime;
        }
    }

    public String getHeadBlockProducer() {
        return headBlockProducer;
    }

    public void setHeadBlockProducer(String headBlockProducer) {
        this.headBlockProducer = headBlockProducer;
    }

    public String getRecentSlots() {
        return recentSlots;
    }

    public void setRecentSlots(String recentSlots) {
        this.recentSlots = recentSlots;
    }

    public String getParticipationRate() {
        return participationRate;
    }

    public void setParticipationRate(String participationRate) {
        this.participationRate = participationRate;
    }

    public String getBrief(){
        return "head block num: " + headBlockNum
                + "\nlast irreversible block: " + lastIrreversibleBlockNum
                + "\nhead block time: " + headBlockTime
                + "\nhead block producer: " + headBlockProducer ;
    }
}
