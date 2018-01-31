package com.outsystems.plugins.epassportreader;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;

import com.outsystems.plugins.epassportreader.exceptions.BACInvalidData;

import org.jmrtd.BACKey;

import java.security.Security;

/*
 * Created by João Gonçalves on 23/01/2018.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcPassportReaderCallback implements NfcAdapter.ReaderCallback {

//    static {
//        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
//    }

    private PassportReaderCallback callback;
    private BACKeyProvider bacKeyProvider;

    public NfcPassportReaderCallback(BACKeyProvider bacKeyProvider) {
        this.bacKeyProvider = bacKeyProvider;
    }

    @Override
    public void onTagDiscovered(Tag tag) {

        if (callback == null) {
            return;
        }
        PassportReaderTask readerTask = new PassportReaderTask(callback, bacKeyProvider);
        readerTask.execute(tag);
    }

    public void setPassportReaderCallback(PassportReaderPlugin passportReaderCallback) {
        this.callback = passportReaderCallback;
    }

    public interface PassportReaderCallback {
        void onPassportRead(Passport passport);

        void onError(Exception e);

        void onError(String errorMessage);
    }

    public interface BACKeyProvider {
        BACKey getBACKey() throws BACInvalidData;
    }


}
