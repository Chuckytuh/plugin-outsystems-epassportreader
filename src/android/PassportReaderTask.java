package com.outsystems.plugins.epassportreader;

import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;

import com.outsystems.plugins.epassportreader.exceptions.BACInvalidData;

import net.sf.scuba.smartcards.CardService;
import net.sf.scuba.smartcards.CardServiceException;

import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.DataGroup;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.icao.COMFile;
import org.jmrtd.lds.icao.DG11File;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.icao.MRZInfo;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Created by João Gonçalves on 29/01/2018.
 */

public class PassportReaderTask extends AsyncTask<Tag, Void, PassportReaderTask.PassportReaderAsyncResult> {
    WeakReference<NfcPassportReaderCallback.PassportReaderCallback> callbackRef;
    WeakReference<NfcPassportReaderCallback.BACKeyProvider> backeyProviderRef;

    public PassportReaderTask(NfcPassportReaderCallback.PassportReaderCallback callback, NfcPassportReaderCallback.BACKeyProvider bacKeyProvider) {
        callbackRef = new WeakReference<NfcPassportReaderCallback.PassportReaderCallback>(callback);
        backeyProviderRef = new WeakReference<NfcPassportReaderCallback.BACKeyProvider>(bacKeyProvider);
    }

    private DG1File readDg1(PassportService passportService) throws CardServiceException {
        InputStream dg1In = null;
        try {
            dg1In = passportService.getInputStream(PassportService.EF_DG1);
            DG1File dg1 = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, dg1In);
            return dg1;
        } catch (CardServiceException e) {
            e.printStackTrace();
            throw new CardServiceException("An error occurred while reading MRZ information from passport.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new CardServiceException("An error occurred while reading MRZ information from passport.");
        } finally {
            if (dg1In != null) {
                try {
                    dg1In.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DG2File readDg2(PassportService passportService) throws CardServiceException {
        InputStream in = null;
        try {
            in = passportService.getInputStream(PassportService.EF_DG2);
            DG2File dg2 = (DG2File) LDSFileUtil.getLDSFile(PassportService.EF_DG2, in);
            return dg2;
        } catch (CardServiceException e) {
            e.printStackTrace();
            throw new CardServiceException("An error occurred while reading image from passport.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new CardServiceException("An error occurred while reading image from passport.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private DG11File readDg11(PassportService passportService) throws CardServiceException {
        InputStream in = null;
        try {
            in = passportService.getInputStream(PassportService.EF_DG11);
            DG11File dg = (DG11File) LDSFileUtil.getLDSFile(PassportService.EF_DG11, in);
            return dg;
        } catch (CardServiceException e) {

            if (e.getSW() == 0x6A82) { //File not found
                throw e;
            }

            e.printStackTrace();
            throw new CardServiceException("An error occurred while reading additional personal details.");
        } catch (IOException e) {
            e.printStackTrace();
            throw new CardServiceException("An error occurred while reading additional personal details.");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readImage(DG2File dg) throws CardServiceException {
        List<FaceInfo> faceInfos = dg.getFaceInfos();
        if (!faceInfos.isEmpty()) {
            FaceInfo faceInfo = faceInfos.iterator().next();
            List<FaceImageInfo> faceImageInfos = faceInfo.getFaceImageInfos();
            if (!faceImageInfos.isEmpty()) {
                FaceImageInfo faceImageInfo = faceImageInfos.iterator().next();

                int imageLength = faceImageInfo.getImageLength();
                DataInputStream dataInputStream = new DataInputStream(faceImageInfo.getImageInputStream());
                byte[] buffer = new byte[imageLength];
                try {
                    dataInputStream.readFully(buffer, 0, imageLength);

                } catch (IOException e) {
                    throw new CardServiceException("Failed to read image data from passport.");
                }

            }
        }
    }

    private Passport buildPassport(List<DataGroup> dataGroups) {
        Passport.Builder builder = new Passport.Builder();


        for (DataGroup dataGroup : dataGroups) {
            if (dataGroup instanceof DG1File) {
                MRZInfo mrzInfo = ((DG1File) dataGroup).getMRZInfo();
                builder.setDocumentCode(mrzInfo.getDocumentCode())
                        .setIssuingState(mrzInfo.getIssuingState())
                        .setPrimaryIdentifier(mrzInfo.getPrimaryIdentifier())
                        .setSecondaryIdentifier(mrzInfo.getSecondaryIdentifier())
                        .setNationality(mrzInfo.getNationality())
                        .setDocumentNumber(mrzInfo.getDocumentNumber())
                        .setDateOfBirth(mrzInfo.getDateOfBirth())
                        .setGender(mrzInfo.getGender())
                        .setDateOfExpiry(mrzInfo.getDateOfExpiry())
                        .setOptionalData1(mrzInfo.getOptionalData1())
                        .setOptionalData2(mrzInfo.getOptionalData2());
            }
            if (dataGroup instanceof DG11File) {
                DG11File details = (DG11File) dataGroup;

                PassportAdditionalPersonalDetails.Builder detailsBuilder = PassportAdditionalPersonalDetails.builder();
                detailsBuilder.withNameOfHolder(details.getNameOfHolder())
                        .withOtherNames(details.getOtherNames())
                        .withPersonalNumber(details.getPersonalNumber())
                        .withFullDateOfBirth(details.getFullDateOfBirth())
                        .withPlaceOfBirth(details.getPlaceOfBirth())
                        .withPermanentAddress(details.getPermanentAddress())
                        .withTelephone(details.getTelephone())
                        .withProfession(details.getProfession())
                        .withTitle(details.getTitle())
                        .withPersonalSummary(details.getPersonalSummary())
                        .withProofOfCitizenship(Arrays.copyOf(details.getProofOfCitizenship(), details.getProofOfCitizenship().length))
                        .withOtherValidTDNumbers(details.getOtherValidTDNumbers())
                        .withCustodyInformation(details.getCustodyInformation());

                builder.setPassportAdditionalPersonalDetails(detailsBuilder.build());
            }
        }

        return builder.build();
    }

    @Override
    protected PassportReaderAsyncResult doInBackground(Tag... tags) {
        PassportService ps = null;
        Tag tag = tags[0];
        try {
            IsoDep nfc = IsoDep.get(tag);
            nfc.setTimeout(5000);
            CardService cs = CardService.getInstance(nfc);
            ps = new PassportService(cs, PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                    PassportService.DEFAULT_MAX_BLOCKSIZE, false);
            ps.open();

            ps.sendSelectApplet(false);
            NfcPassportReaderCallback.BACKeyProvider bacKeyProvider = backeyProviderRef.get();
            if (bacKeyProvider == null) {
                return PassportReaderAsyncResult.InitWithError("BACKeyProvider can't be null.");
            }

            BACKeySpec bacKey = bacKeyProvider.getBACKey();

            ps.doBAC(bacKey);


            InputStream comIn = null;
            InputStream sodIn = null;

            try {

                comIn = ps.getInputStream(PassportService.EF_COM);
                COMFile comFile = (COMFile) LDSFileUtil.getLDSFile(PassportService.EF_COM, comIn);
                List<DataGroup> dgs = new ArrayList<DataGroup>();
//                sodIn = ps.getInputStream(PassportService.EF_SOD);
//                SODFile sodFile = (SODFile) LDSFileUtil.getLDSFile(PassportService.EF_SOD, sodIn);

                // Basic data
                DG1File dg1 = readDg1(ps);
                dgs.add(dg1);

                // from https://www.icao.int/publications/Documents/9303_p10_cons_en.pdf page 28
                for (int i = 0; i < comFile.getTagList().length; i++) {
                    // if this passport contains DG11, read it!
                    if (comFile.getTagList()[i] == 0x6B) {
                        DG11File dg11 = readDg11(ps);
                        dgs.add(dg11);
                    }
                }


//                DG2File dg2 = readDg2(ps);
//                List<FaceInfo> faceInfos = dg2.getFaceInfos();

                Passport passport = buildPassport(dgs);
                return PassportReaderAsyncResult.InitWithPassport(passport);

            } catch (CardServiceException e) {
                e.printStackTrace();
                return PassportReaderAsyncResult.InitWithError(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                return PassportReaderAsyncResult.InitWithError(e.getMessage());
            } finally {
                try {
                    comIn.close();
//                    sodIn.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (BACInvalidData e) {
            return PassportReaderAsyncResult.InitWithError("Failed to perform BAC. Check provided document number, date of birth and date of expiry.");
        } catch (CardServiceException e) {
            e.printStackTrace();
            return PassportReaderAsyncResult.InitWithError("Failed to communicate with NFC tag. Got APDU " + e.getSW() + " status word.");
        } catch (Exception e) {
            e.printStackTrace();
            return PassportReaderAsyncResult.InitWithError(e.getMessage());
        } finally {
            try {
                ps.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onPostExecute(PassportReaderAsyncResult passportReaderAsyncResult) {
        super.onPostExecute(passportReaderAsyncResult);

        NfcPassportReaderCallback.PassportReaderCallback callback = callbackRef.get();
        if (callback != null) {
            if (passportReaderAsyncResult.isSuccess()) {
                callback.onPassportRead(passportReaderAsyncResult.getPassport());
            } else {
                callback.onError(passportReaderAsyncResult.getError());
            }
        }
    }

    public static class PassportReaderAsyncResult {
        boolean success;
        Passport passport;
        String error;

        public static PassportReaderAsyncResult InitWithError(String errorMessage) {
            PassportReaderAsyncResult res = new PassportReaderAsyncResult();
            res.error = errorMessage;
            res.success = false;
            return res;
        }

        public static PassportReaderAsyncResult InitWithPassport(Passport passport) {
            PassportReaderAsyncResult res = new PassportReaderAsyncResult();
            res.passport = passport;
            res.success = true;
            return res;
        }

        public boolean isSuccess() {
            return success;
        }

        public Passport getPassport() {
            return passport;
        }

        public String getError() {
            return error;
        }
    }


}
