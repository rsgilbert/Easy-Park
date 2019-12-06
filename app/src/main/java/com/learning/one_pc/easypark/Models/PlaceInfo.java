package com.learning.one_pc.easypark.Models;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {
    private String name;
    private String phoneNumber;
    private String id;
    private String address;
    private Uri websiteUri;
    private LatLng latLng;
    private float rating;
    private String attributions;
    private String lots;

    public PlaceInfo(String name, String phoneNumber, String id, String address,
                     Uri websiteUri, LatLng latLng, float rating, String attributions, String lots) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.address = address;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
        this.attributions = attributions;
        this.lots = lots;
    }
    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public float getRating() {
        return rating;
    }

    public String getAttributions() {
        return attributions;
    }
    public  String getLots(){
        return lots;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }
    public void setLots(String lots){
        this.lots = lots;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", id='" + id + '\'' +
                ", address='" + address + '\'' +
                ", websiteUri=" + websiteUri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
