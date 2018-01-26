package com.outsystems.plugins.epassportreader;
/*
 * Created by João Gonçalves on 23/01/2018.
 */

import net.sf.scuba.data.Gender;

public class Passport {

    private String documentCode;
    private String issuingState;
    private String primaryIdentifier;
    private String secondaryIdentifier;
    private String nationality;
    private String documentNumber;
    private String dateOfBirth;
    private Gender gender;
    private String dateOfExpiry;
    private String optionalData1; /* NOTE: holds personal number for some issuing states (e.g. NL), but is used to hold (part of) document number for others. */
    private String optionalData2;

    public Passport(String documentCode, String issuingState, String primaryIdentifier, String secondaryIdentifier, String nationality, String documentNumber, String dateOfBirth, Gender gender, String dateOfExpiry, String optionalData1, String optionalData2) {
        this.documentCode = documentCode;
        this.issuingState = issuingState;
        this.primaryIdentifier = primaryIdentifier;
        this.secondaryIdentifier = secondaryIdentifier;
        this.nationality = nationality;
        this.documentNumber = documentNumber;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.dateOfExpiry = dateOfExpiry;
        this.optionalData1 = optionalData1;
        this.optionalData2 = optionalData2;
    }

    public String getDocumentCode() {
        return documentCode;
    }

    public String getIssuingState() {
        return issuingState;
    }

    public String getPrimaryIdentifier() {
        return primaryIdentifier;
    }

    public String getSecondaryIdentifier() {
        return secondaryIdentifier;
    }

    public String getNationality() {
        return nationality;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public String getDateOfExpiry() {
        return dateOfExpiry;
    }

    public String getOptionalData1() {
        return optionalData1;
    }

    public String getOptionalData2() {
        return optionalData2;
    }

    public static class Builder {
        private String documentCode;
        private String issuingState;
        private String primaryIdentifier;
        private String secondaryIdentifier;
        private String nationality;
        private String documentNumber;
        private String dateOfBirth;
        private Gender gender;
        private String dateOfExpiry;
        private String optionalData1;
        private String optionalData2;

        public Builder setDocumentCode(String documentCode) {
            this.documentCode = documentCode;
            return this;
        }

        public Builder setIssuingState(String issuingState) {
            this.issuingState = issuingState;
            return this;
        }

        public Builder setPrimaryIdentifier(String primaryIdentifier) {
            this.primaryIdentifier = primaryIdentifier;
            return this;
        }

        public Builder setSecondaryIdentifier(String secondaryIdentifier) {
            this.secondaryIdentifier = secondaryIdentifier;
            return this;
        }

        public Builder setNationality(String nationality) {
            this.nationality = nationality;
            return this;
        }

        public Builder setDocumentNumber(String documentNumber) {
            this.documentNumber = documentNumber;
            return this;
        }

        public Builder setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder setGender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder setDateOfExpiry(String dateOfExpiry) {
            this.dateOfExpiry = dateOfExpiry;
            return this;
        }


        public Builder setOptionalData1(String optionalData1) {
            this.optionalData1 = optionalData1;
            return this;
        }

        public Builder setOptionalData2(String optionalData2) {
            this.optionalData2 = optionalData2;
            return this;
        }

        public Passport build() {
            return new Passport(documentCode, issuingState, primaryIdentifier, secondaryIdentifier, nationality, documentNumber, dateOfBirth, gender, dateOfExpiry, optionalData1, optionalData2);
        }
    }

}
