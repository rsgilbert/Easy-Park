package com.learning.one_pc.easypark.Models;

public class UserInformation {
    private String parkName, owner, capacity, currentLots, phoneNumber;

    public UserInformation(){

    }

    public String getParkName() {
        return parkName;
    }


    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getCurrentLots() {
        return currentLots;
    }

    public void setCurrentLots(String currentLots) {
        this.currentLots = currentLots;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
