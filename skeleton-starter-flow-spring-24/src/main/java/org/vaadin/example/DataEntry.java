package org.vaadin.example;

import java.util.UUID;

public class DataEntry {
    private UUID _id;
    private String mscode;
    private String year;
    private String estCode;
    private double estimate;
    private double se;
    private double lowerCIB;
    private double upperCIB;
    private String flag;

    public UUID get_id() {
        return _id;
    }

    public void set_id(UUID _id) {
        this._id = _id;
    }

    public String getMscode() {
        return mscode;
    }

    public void setMscode(String mscode) {
        this.mscode = mscode;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getEstCode() {
        return estCode;
    }

    public void setEstCode(String estCode) {
        this.estCode = estCode;
    }

    public double getEstimate() {
        return estimate;
    }

    public void setEstimate(double estimate) {
        this.estimate = estimate;
    }

    public double getSe() {
        return se;
    }

    public void setSe(double se) {
        this.se = se;
    }

    public double getLowerCIB() {
        return lowerCIB;
    }

    public void setLowerCIB(double lowerCIB) {
        this.lowerCIB = lowerCIB;
    }

    public double getUpperCIB() {
        return upperCIB;
    }

    public void setUpperCIB(double upperCIB) {
        this.upperCIB = upperCIB;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }




}
