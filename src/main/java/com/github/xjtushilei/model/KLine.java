package com.github.xjtushilei.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by yee on 15/6/26.
 */
public class KLine implements Serializable, Cloneable {

    private static final long serialVersionUID = -8538999199849613805L;

    private String code; //品种代码
    private String excode; //交易所代码
    private double open;
    private double close;
    private double high;
    private double low;
    private double volume;
    private String openTime; //品种的开盘时间表达式 [1]08:00|[2-7]06:00
    private String level; //K线级别 1m 5m 15m 30m 1h 4h 1d 1w month
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;

    private double macd;
    private double dif;
    private double dea;

    private double ma5;
    private double ma10;
    private double ma20;
    private double ma30;

    private double avg_price;

    private int fetchTimes; //获取时间段内的tick数据次数
    private int status; //-1 没有数据跳过 0 未完成计算 1 完成计算
    private int tickNum = 0;

    private int tick_number = 0;

    private boolean finished = false;

    private String direction;

    public KLine() {
        this.open = 0.00;
        this.close = 0.00;
        this.high = 0.00;
        this.low = 0.00;
        this.volume = 0.0;
        this.fetchTimes = 0;
        this.volume = 0.0;
        this.status = 0;
        this.tickNum = -1;
        this.finished = false;
    }

    public KLine(String code, String excode, String level, LocalDateTime startTime, LocalDateTime toTime) {
        this.code = code;
        this.excode = excode;
        this.level = level;
        this.reset(startTime, toTime);
    }

    public KLine(String code, String excode, String level, LocalDateTime startTime, double open, double close, double high, double low, double volume) {
        this.code = code;
        this.excode = excode;
        this.level = level;
        this.startTime = startTime;
        this.open = open;
        this.close = close;
        this.high = high;
        this.low = low;
        this.volume = volume;
    }

    public void reset(LocalDateTime beginTime) {
        this.startTime = beginTime;
        this.createTime = beginTime;
        this.open = 0.00;
        this.close = 0.00;
        this.high = 0.00;
        this.low = 0.00;
        this.volume = 0.0;
        this.fetchTimes = 0;
        this.volume = 0.0;
        this.status = 0;
        this.tickNum = -1;
        this.finished = false;
    }

    public void reset(LocalDateTime beginTime, LocalDateTime toTime) {
        this.startTime = beginTime;
        this.createTime = beginTime;
        this.endTime = toTime;
        this.open = 0.00;
        this.close = 0.00;
        this.high = 0.00;
        this.low = 0.00;
        this.volume = 0.0;
        this.fetchTimes = 0;
        this.status = 0;
        this.tickNum = -1;
        this.finished = false;
    }

    public int getTick_number() {
        return tick_number;
    }

    public void setTick_number(int tick_number) {
        this.tick_number = tick_number;
    }

    public double getAvg_price() {
        return avg_price;
    }

    public void setAvg_price(double avg_price) {
        this.avg_price = avg_price;
    }

    public double getMa5() {
        return ma5;
    }

    public void setMa5(double ma5) {
        this.ma5 = ma5;
    }


    public double getMa10() {
        return ma10;
    }

    public void setMa10(double ma10) {
        this.ma10 = ma10;
    }

    public double getMa20() {
        return ma20;
    }

    public void setMa20(double ma20) {
        this.ma20 = ma20;
    }

    public double getMa30() {
        return ma30;
    }

    public void setMa30(double ma30) {
        this.ma30 = ma30;
    }

    public double getMacd() {
        return macd;
    }

    public void setMacd(double macd) {
        this.macd = macd;
    }

    public double getDea() {
        return dea;
    }

    public void setDea(double dea) {
        this.dea = dea;
    }

    public void setDif(double dif) {
        this.dif = dif;
    }

    public double getDif() {
        return dif;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExCode() {
        return excode;
    }

    public void setExCode(String excode) {
        this.excode = excode;
    }

    public double getOpen() {
        return open;
    }

    public void setOpen(double open) {
        this.open = open;
    }

    public double getClose() {
        return close;
    }

    public void setClose(double close) {
        this.close = close;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public void setStartTime(LocalDateTime createTime) {
        this.startTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return createTime;
    }

    public void setUpdateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public int getFetchTimes() {
        return fetchTimes;
    }

    public void setFetchTimes(int fetchTimes) {
        this.fetchTimes = fetchTimes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }


    public int getTickNum() {
        return tickNum;
    }

    public void setTickNum(int tickNum) {
        this.tickNum = tickNum;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime to) {
        this.endTime = to;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    @Override
    public KLine clone() throws CloneNotSupportedException {
        return (KLine) super.clone();
    }

    public String getExcode() {
        return excode;
    }

    public void setExcode(String excode) {
        this.excode = excode;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "KLine{" +
                "code='" + code + '\'' +
                ", open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", level='" + level + '\'' +
                ", createTime=" + startTime +
                ", endTime=" + endTime +
                ", avg_price=" + avg_price +
                ", direction=" + direction +
                '}';
    }

}
