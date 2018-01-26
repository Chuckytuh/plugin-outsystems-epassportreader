package com.outsystems.plugins.epassportreader;

import android.annotation.TargetApi;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Build;
import android.util.Log;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;

import org.jmrtd.BACKey;
import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.SODFile;
import org.jmrtd.lds.icao.COMFile;
import org.jmrtd.lds.icao.DG11File;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import java.io.InputStream;
import java.security.Security;
import java.util.List;

/*
 * Created by João Gonçalves on 23/01/2018.
 */

@TargetApi(Build.VERSION_CODES.KITKAT)
public class NfcPassportReaderCallback implements NfcAdapter.ReaderCallback {

    static {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    private PassportReaderPlugin callback;
    private BACKeyProvider bacKeyProvider;

    public NfcPassportReaderCallback(BACKeyProvider bacKeyProvider) {
        this.bacKeyProvider = bacKeyProvider;
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        Log.d(getClass().getSimpleName(), "onTagDiscovered - " + tag.toString());
        PassportService ps = null;

        if (callback == null) {
            return;
        }

        try {
            IsoDep nfc = IsoDep.get(tag);
            CardService cs = CardService.getInstance(nfc);
            ps = new PassportService(cs, PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                    PassportService.DEFAULT_MAX_BLOCKSIZE, false);
            ps.open();

            ps.sendSelectApplet(false);
            if (bacKeyProvider == null) {
                return;
            }

            BACKeySpec bacKey = bacKeyProvider.getBACKey();
            if (bacKey == null) {
                callback.onError(new Exception("BACKeyProvider can't be null!"));
            }

            ps.doBAC(bacKey);

            InputStream is = null;
            InputStream comIn = null;
            InputStream sodIn = null;
            InputStream dg2In = null;

            try {
//                comIn = ps.getInputStream(PassportService.EF_COM);
//                COMFile comFile = (COMFile) LDSFileUtil.getLDSFile(PassportService.EF_COM, comIn);
//
//                sodIn = ps.getInputStream(PassportService.EF_SOD);
//                SODFile sodFile = (SODFile) LDSFileUtil.getLDSFile(PassportService.EF_SOD, sodIn);

                // Basic data
                is = ps.getInputStream(PassportService.EF_DG1);
                DG1File dg1 = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, is);

//                dg2In = ps.getInputStream(PassportService.EF_DG2);
//                DG2File dg2 = (DG2File) LDSFileUtil.getLDSFile(PassportService.EF_DG2, dg2In);
//                List<FaceInfo> faceInfos = dg2.getFaceInfos();


                MRZInfo mrzInfo = dg1.getMRZInfo();
                Passport passport = buildPassport(mrzInfo);

                if (callback != null) {
                    callback.onPassportRead(passport);
                }

            } catch (CardServiceException e) {
                e.printStackTrace();
                callback.onError(e);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onError(e);
            } finally {
                try {
                    is.close();
                    dg2In.close();
                    comIn.close();
                    sodIn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (CardServiceException e) {
            e.printStackTrace();
            callback.onError(e);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        } finally {
            try {
                ps.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                callback.onError(ex);
            }
        }

    }

    private Passport buildPassport(MRZInfo mrzInfo) {
        return new Passport.Builder()
                .setDocumentCode(mrzInfo.getDocumentCode())
                .setIssuingState(mrzInfo.getIssuingState())
                .setPrimaryIdentifier(mrzInfo.getPrimaryIdentifier())
                .setSecondaryIdentifier(mrzInfo.getSecondaryIdentifier())
                .setNationality(mrzInfo.getNationality())
                .setDocumentNumber(mrzInfo.getDocumentNumber())
                .setDateOfBirth(mrzInfo.getDateOfBirth())
                .setGender(mrzInfo.getGender())
                .setDateOfExpiry(mrzInfo.getDateOfExpiry())
                .setOptionalData1(mrzInfo.getOptionalData1())
                .setOptionalData2(mrzInfo.getOptionalData2())
                .build();
    }


    public void setPassportReaderCallback(PassportReaderPlugin passportReaderCallback) {
        this.callback = passportReaderCallback;
    }

    public interface PassportReaderCallback {
        void onPassportRead(Passport passport);

        void onError(Exception e);
    }

    public interface BACKeyProvider {
        BACKey getBACKey();
    }

}
