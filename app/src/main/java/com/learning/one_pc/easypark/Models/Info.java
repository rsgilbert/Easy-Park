package com.learning.one_pc.easypark.Models;

public class Info {
    private String ParkName, Owner, Tel;
    private int Capacity, AvailableRooms;
    public Info(){

    }

    public String getParkName() {
        return ParkName;
    }

    public void setParkName(String parkName) {
        ParkName = parkName;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getAvailableRooms() {
        return AvailableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        AvailableRooms = availableRooms;
    }

    public String getTel() {
        return Tel;
    }

    public void setTel(String tel) {
        Tel = tel;
    }
}
