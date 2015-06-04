package com.maws.loonandroid.gatt;

public interface GattCharacteristicReadCallback {
    void call(byte[] characteristic);
}
