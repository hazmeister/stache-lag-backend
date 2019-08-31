package model;

import java.sql.Timestamp;

public class Positions {
    private boolean alert;
    private int altitude;
    private String type;    //TODO: Make enum later, after finding the other values
    private double dtfKm;    //Distance to finish
    private double dtfNm;
    private int id;
    private Timestamp gpsAt;
    private int battery;
    private int cog;
    private Timestamp txAt;
    private double latitude;
    private double longitude;
    private long gpsAtMillis;
    private double sogKmph; //(Wind) Speed Over Ground
    private double sogKnots;

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDtfKm() {
        return dtfKm;
    }

    public void setDtfKm(double dtfKm) {
        this.dtfKm = dtfKm;
    }

    public double getDtfNm() {
        return dtfNm;
    }

    public void setDtfNm(double dtfNm) {
        this.dtfNm = dtfNm;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Timestamp getGpsAt() {
        return gpsAt;
    }

    public void setGpsAt(Timestamp gpsAt) {
        this.gpsAt = gpsAt;
    }

    public double getSogKnots() {
        return sogKnots;
    }

    public void setSogKnots(double sogKnots) {
        this.sogKnots = sogKnots;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public int getCog() {
        return cog;
    }

    public void setCog(int cog) {
        this.cog = cog;
    }

    public Timestamp getTxAt() {
        return txAt;
    }

    public void setTxAt(Timestamp txAt) {
        this.txAt = txAt;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public long getGpsAtMillis() {
        return gpsAtMillis;
    }

    public void setGpsAtMillis(long gpsAtMillis) {
        this.gpsAtMillis = gpsAtMillis;
    }

    public double getSogKmph() {
        return sogKmph;
    }

    public void setSogKmph(double sogKmph) {
        this.sogKmph = sogKmph;
    }
}
