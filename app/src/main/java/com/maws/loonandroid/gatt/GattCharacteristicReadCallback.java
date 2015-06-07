package com.maws.loonandroid.gatt;

public interface GattCharacteristicReadCallback {
    void call(String address, byte[] characteristic);
}
