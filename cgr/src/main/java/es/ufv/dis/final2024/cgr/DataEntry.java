package es.ufv.dis.final2024.cgr;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class DataEntry {
    @Expose
    private UUID _id;
    @Expose
    private String mscode;
    @Expose
    private String year;
    @Expose
    private String estCode;
    @Expose
    private double estimate;
    @Expose
    private double se;
    @Expose
    private double lowerCIB;
    @Expose
    private double upperCIB;
    @SerializedName("flag")
    @Expose
    private String flag;

    public DataEntry(UUID _id, String mscode, String year, String estCode, double estimate, double se, double lowerCIB, double upperCIB, String flag) {
        this._id = _id;
        this.mscode = mscode;
        this.year = year;
        this.estCode = estCode;
        this.estimate = estimate;
        this.se = se;
        this.lowerCIB = lowerCIB;
        this.upperCIB = upperCIB;
        this.flag = flag;
    }

    public UUID  get_id() {
        return _id;
    }

    public String getMscode() {
        return mscode;
    }

    public String getYear() {
        return year;
    }

    public String getEstCode() {
        return estCode;
    }

    public double getEstimate() {
        return estimate;
    }

    public double getSe() {
        return se;
    }

    public double getLowerCIB() {
        return lowerCIB;
    }

    public double getUpperCIB() {
        return upperCIB;
    }

    public String getFlag() {
        return flag;
    }

    public UUID set_id() {
        return _id;
    }

    // Setters
    public void set_id(UUID _id) { this._id = _id;}
    public void setMscode(String mscode) { this.mscode = mscode; }
    public void setYear(String year) { this.year = year; }
    public void setEstCode(String estCode) { this.estCode = estCode; }
    public void setEstimate(double estimate) { this.estimate = estimate; }
    public void setSe(double se) { this.se = se; }
    public void setLowerCIB(double lowerCIB) { this.lowerCIB = lowerCIB; }
    public void setUpperCIB(double upperCIB) { this.upperCIB = upperCIB; }
    public void setFlag(String flag) { this.flag = flag; }

    @Override
    public String toString() {
        return "User{" +
                "_id=" + _id +
                ", mscode='" + mscode + '\'' +
                ", year='" + year + '\'' +
                ", estCode='" + estCode + '\'' +
                ", estimate=" + estimate +
                ", se=" + se +
                ", lowerCIB=" + lowerCIB +
                ", upperCIB=" + upperCIB +
                ", flag='" + flag + '\'' +
                '}';
    }
}