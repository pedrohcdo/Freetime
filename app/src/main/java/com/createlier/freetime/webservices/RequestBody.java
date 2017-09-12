package com.createlier.freetime.webservices;


import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by pedro on 27/09/16.
 */
public interface RequestBody {

    /**
     * On Write
     */
    public void onWrite(final DataOutputStream bufferedReader) throws IOException;
}
