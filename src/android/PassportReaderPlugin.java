package com.outsystems.plugins.epassportreader;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.jmrtd.BACKey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author João Gonçalves on 23/01/2018
 */
public class PassportReaderPlugin extends CordovaPlugin implements NfcPassportReaderCallback.PassportReaderCallback {

    public static final String TAG = "NFCCardReaderPlugin";

    private static int READER_FLAGS = NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NFC_A;
    private NfcPassportReaderCallback nfcPassportReaderCallback;
    private CallbackContext callbackContext;

    private String documentNumber;
    private String dateOfBirth;
    private String dateOfExpiry;

    private NfcPassportReaderCallback.BACKeyProvider bacKeyProvider = new NfcPassportReaderCallback.BACKeyProvider() {
        @Override
        public BACKey getBACKey() {
            try {
                return new BACKey(documentNumber, dateOfBirth, dateOfExpiry);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
    };

    private void enableNfcReaderMode(Activity activity) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
        if (nfcAdapter != null) {
            if (nfcPassportReaderCallback == null) {
                nfcPassportReaderCallback = new NfcPassportReaderCallback(bacKeyProvider);
                nfcPassportReaderCallback.setPassportReaderCallback(this);
            }
            nfcAdapter.enableReaderMode(activity, nfcPassportReaderCallback, READER_FLAGS, null);

        }

    }

    private void disableNfcReaderMode(Activity activity) {
        if (activity != null && !activity.isDestroyed()) {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
            if (nfcAdapter != null) {
                nfcAdapter.disableReaderMode(activity);
            }
            if (nfcPassportReaderCallback != null) {
                nfcPassportReaderCallback.setPassportReaderCallback(null);
            }
        }
    }

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        enableNfcReaderMode(cordova.getActivity());
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        enableNfcReaderMode(cordova.getActivity());
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
    }

    @Override
    public void onDestroy() {
        disableNfcReaderMode(cordova.getActivity());
        super.onDestroy();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext)
            throws JSONException {
        if (action.equals("deviceReady")) {
            this.deviceReady(callbackContext);
            return true;
        } else if (action.equals("enableReader")) {
            enableReader(args, callbackContext);
        } else if (action.equals("disableReader")) {
            disableReader(callbackContext);
        } else if (action.equals("checkNfcSupport")) {
            checkNfcSupport(callbackContext);
        } else if (action.equals("isNfcEnabled")) {
            isNfcEnabled(callbackContext);
        }
        return false;
    }

    private void deviceReady(CallbackContext callbackContext) {
        PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
        pr.setKeepCallback(true);
        callbackContext.sendPluginResult(pr);
        this.callbackContext = callbackContext;
    }

    private void enableReader(final JSONArray args, final CallbackContext callbackContext) {
        if (args.length() < 3) {
            callbackContext.error("Invalid arguments");
        }

        try {
            documentNumber = args.getString(0);
            dateOfBirth = args.getString(1);
            dateOfExpiry = args.getString(2);
            callbackContext.success();
        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
            return;
        }

    }

    private void disableReader(CallbackContext callbackContext) {
        documentNumber = "";
        dateOfBirth = "";
        dateOfExpiry = "";
        callbackContext.success();
    }

    private void checkNfcSupport(final CallbackContext callbackContext) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(cordova.getActivity());
        PluginResult pr = new PluginResult(PluginResult.Status.OK, nfcAdapter != null);
        callbackContext.sendPluginResult(pr);
    }

    private void isNfcEnabled(CallbackContext callbackContext) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(cordova.getActivity());
        PluginResult pr = new PluginResult(PluginResult.Status.OK, nfcAdapter != null && nfcAdapter.isEnabled());
        callbackContext.sendPluginResult(pr);
    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onPassportRead(Passport passport) {
        dispatchPassportRead(callbackContext, passport);
    }

    @Override
    public void onError(Exception e) {
        PluginResult pr = new PluginResult(PluginResult.Status.ERROR, e.getMessage());
        pr.setKeepCallback(true);
        callbackContext.sendPluginResult(pr);
    }


    // Example:
    //    dateOfBirth: "yyMMdd"
    //    dateOfExpiry: "yyMMdd"
    //    documentCode: "P"
    //    documentNumber: "XXXXXXX"
    //    gender: "MALE"
    //    issuingState: "PRT"
    //    nationality: "PRT"
    //    optionalData1: "XXXXXXXX<<<<<<X"
    //    primaryIdentifier: "Doe"
    //    secondaryIdentifier: "John<Dalton<<<<<<<<<<"

    private void dispatchPassportRead(CallbackContext ctx, Passport passport) {
        if (ctx == null) {
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("documentCode", passport.getDocumentCode());
            jsonObject.put("issuingState", passport.getIssuingState());
            jsonObject.put("primaryIdentifier", passport.getPrimaryIdentifier());
            jsonObject.put("secondaryIdentifier", passport.getSecondaryIdentifier());
            jsonObject.put("nationality", passport.getNationality());
            jsonObject.put("documentNumber", passport.getDocumentNumber());
            jsonObject.put("dateOfBirth", passport.getDateOfBirth());
            jsonObject.put("gender", passport.getGender());
            jsonObject.put("dateOfExpiry", passport.getDateOfExpiry());
            jsonObject.put("optionalData1", passport.getOptionalData1());
            jsonObject.put("optionalData2", passport.getOptionalData2());

            PluginResult pr = new PluginResult(PluginResult.Status.OK, jsonObject);
            pr.setKeepCallback(true);
            ctx.sendPluginResult(pr);

        } catch (JSONException e) {
            e.printStackTrace();

            PluginResult pr = new PluginResult(PluginResult.Status.ERROR, "Failed to create JSON from Passport");
            pr.setKeepCallback(true);
            ctx.sendPluginResult(pr);
        }
    }
}
