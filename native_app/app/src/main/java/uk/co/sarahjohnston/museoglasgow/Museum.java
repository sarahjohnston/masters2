package uk.co.sarahjohnston.museoglasgow;

import java.util.List;

/**
 * Created by sarahjohnston on 27/10/15.
 */
public class Museum {


    private String _museumName;
    private int _id;
    private String _streetAddress1;
    private String _streetAddress2;
    private String _city;
    private String _county;
    private String _postcode;
    private String _description;
    private String _mainImage;
    private double [] _location;
    private List<String> _openingHours;



    public Museum (int id, String museumName, String mainImage) {
        _museumName = museumName;
        _id = id;
        _location = new double[2];
        _mainImage = mainImage;

    }

    public int getId() { return _id; }

    public String get_museumName() {
        return _museumName;
    }

    public void setAddress(String StreetAddress1, String StreetAddress2, String City, String County, String PostCode) {
        this._streetAddress1 = StreetAddress1;
        this._streetAddress2 = StreetAddress2;
        this._city = City;
        this._county = County;
        this._postcode = PostCode;

    }

    public void set_description(String Description) {
        this._description = Description;
    }

    public void set_openingHours(List<String> OpeningHours) {
        this._openingHours = OpeningHours;
    }

    public List<String> get_openingHours() {
        return _openingHours;
    }

    public String get_AddressText() {
        String address = _streetAddress1 + ", ";
        if (_streetAddress2.length() > 2) {address += _streetAddress2 + ", ";}
        if (_city.length() > 2) {address += _city + ", ";}
        if (_county.length() > 2) {address += _county + ", ";}
        address += _postcode;

        return address;
    }

    public String get_description(){
        return _description;
    }

    public void set_mainImage(String mainImage) {
        this._mainImage = mainImage;
    }

    public String get_mainImage() {
        return _mainImage;
    }

    public void set_location(double lat, double lon) {
        this._location[0] = lat;
        this._location[1] = lon;
    }

    public double[] get_location() {
        return _location;
    }

}
