package com.example.demo.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface CustomSerializable {
    void writeToStream(DataOutputStream out) throws IOException;
    void readFromStream(DataInputStream in) throws IOException;
}
