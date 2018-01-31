package com.outsystems.plugins.epassportreader;
/*
 * Created by João Gonçalves on 30/01/2018.
 */

import java.util.List;

public class PassportAdditionalPersonalDetails {

    private String nameOfHolder;
    private List<String> otherNames;
    private String personalNumber;
    private String fullDateOfBirth;
    private List<String> placeOfBirth;
    private List<String> permanentAddress;
    private String telephone;
    private String profession;
    private String title;
    private String personalSummary;
    private byte[] proofOfCitizenship;
    private List<String> otherValidTDNumbers;
    private String custodyInformation;

    public String getNameOfHolder() {
        return nameOfHolder;
    }

    public List<String> getOtherNames() {
        return otherNames;
    }

    public String getPersonalNumber() {
        return personalNumber;
    }

    public String getFullDateOfBirth() {
        return fullDateOfBirth;
    }

    public List<String> getPlaceOfBirth() {
        return placeOfBirth;
    }

    public List<String> getPermanentAddress() {
        return permanentAddress;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getProfession() {
        return profession;
    }

    public String getTitle() {
        return title;
    }

    public String getPersonalSummary() {
        return personalSummary;
    }

    public byte[] getProofOfCitizenship() {
        return proofOfCitizenship;
    }

    public List<String> getOtherValidTDNumbers() {
        return otherValidTDNumbers;
    }

    public String getCustodyInformation() {
        return custodyInformation;
    }

    private PassportAdditionalPersonalDetails(Builder builder) {
        nameOfHolder = builder.nameOfHolder;
        otherNames = builder.otherNames;
        personalNumber = builder.personalNumber;
        fullDateOfBirth = builder.fullDateOfBirth;
        placeOfBirth = builder.placeOfBirth;
        permanentAddress = builder.permanentAddress;
        telephone = builder.telephone;
        profession = builder.profession;
        title = builder.title;
        personalSummary = builder.personalSummary;
        proofOfCitizenship = builder.proofOfCitizenship;
        otherValidTDNumbers = builder.otherValidTDNumbers;
        custodyInformation = builder.custodyInformation;
    }

    public static Builder newBuilder(PassportAdditionalPersonalDetails copy) {
        Builder builder = new Builder();
        builder.custodyInformation = copy.custodyInformation;
        builder.otherValidTDNumbers = copy.otherValidTDNumbers;
        builder.proofOfCitizenship = copy.proofOfCitizenship;
        builder.personalSummary = copy.personalSummary;
        builder.title = copy.title;
        builder.profession = copy.profession;
        builder.telephone = copy.telephone;
        builder.permanentAddress = copy.permanentAddress;
        builder.placeOfBirth = copy.placeOfBirth;
        builder.fullDateOfBirth = copy.fullDateOfBirth;
        builder.personalNumber = copy.personalNumber;
        builder.otherNames = copy.otherNames;
        builder.nameOfHolder = copy.nameOfHolder;
        return builder;
    }

    public static Builder builder() {
        return new Builder();
    }


    interface IBuild {
        PassportAdditionalPersonalDetails build();
    }

    interface ICustodyInformation {
        IBuild withCustodyInformation(String val);
    }

    interface IOtherValidTDNumbers {
        ICustodyInformation withOtherValidTDNumbers(List<String> val);
    }

    interface IProofOfCitizenship {
        IOtherValidTDNumbers withProofOfCitizenship(byte[] val);
    }

    interface IPersonalSummary {
        IProofOfCitizenship withPersonalSummary(String val);
    }

    interface ITitle {
        IPersonalSummary withTitle(String val);
    }

    interface IProfession {
        ITitle withProfession(String val);
    }

    interface ITelephone {
        IProfession withTelephone(String val);
    }

    interface IPermanentAddress {
        ITelephone withPermanentAddress(List<String> val);
    }

    interface IPlaceOfBirth {
        IPermanentAddress withPlaceOfBirth(List<String> val);
    }

    interface IFullDateOfBirth {
        IPlaceOfBirth withFullDateOfBirth(String val);
    }

    interface IPersonalNumber {
        IFullDateOfBirth withPersonalNumber(String val);
    }

    interface IOtherNames {
        IPersonalNumber withOtherNames(List<String> val);
    }

    interface INameOfHolder {
        IOtherNames withNameOfHolder(String val);
    }

    public static final class Builder implements ICustodyInformation, IOtherValidTDNumbers, IProofOfCitizenship, IPersonalSummary, ITitle, IProfession, ITelephone, IPermanentAddress, IPlaceOfBirth, IFullDateOfBirth, IPersonalNumber, IOtherNames, INameOfHolder, IBuild {
        private String custodyInformation;
        private List<String> otherValidTDNumbers;
        private byte[] proofOfCitizenship;
        private String personalSummary;
        private String title;
        private String profession;
        private String telephone;
        private List<String> permanentAddress;
        private List<String> placeOfBirth;
        private String fullDateOfBirth;
        private String personalNumber;
        private List<String> otherNames;
        private String nameOfHolder;

        private Builder() {
        }

        @Override
        public IBuild withCustodyInformation(String val) {
            custodyInformation = val;
            return this;
        }

        @Override
        public ICustodyInformation withOtherValidTDNumbers(List<String> val) {
            otherValidTDNumbers = val;
            return this;
        }

        @Override
        public IOtherValidTDNumbers withProofOfCitizenship(byte[] val) {
            proofOfCitizenship = val;
            return this;
        }

        @Override
        public IProofOfCitizenship withPersonalSummary(String val) {
            personalSummary = val;
            return this;
        }

        @Override
        public IPersonalSummary withTitle(String val) {
            title = val;
            return this;
        }

        @Override
        public ITitle withProfession(String val) {
            profession = val;
            return this;
        }

        @Override
        public IProfession withTelephone(String val) {
            telephone = val;
            return this;
        }

        @Override
        public ITelephone withPermanentAddress(List<String> val) {
            permanentAddress = val;
            return this;
        }

        @Override
        public IPermanentAddress withPlaceOfBirth(List<String> val) {
            placeOfBirth = val;
            return this;
        }

        @Override
        public IPlaceOfBirth withFullDateOfBirth(String val) {
            fullDateOfBirth = val;
            return this;
        }

        @Override
        public IFullDateOfBirth withPersonalNumber(String val) {
            personalNumber = val;
            return this;
        }

        @Override
        public IPersonalNumber withOtherNames(List<String> val) {
            otherNames = val;
            return this;
        }

        @Override
        public IOtherNames withNameOfHolder(String val) {
            nameOfHolder = val;
            return this;
        }

        public PassportAdditionalPersonalDetails build() {
            return new PassportAdditionalPersonalDetails(this);
        }
    }
}
