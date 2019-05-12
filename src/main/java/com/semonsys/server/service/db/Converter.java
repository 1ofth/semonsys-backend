package com.semonsys.server.service.db;

import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;

import java.math.BigInteger;

final class Converter {
    private static final int TIME_COLUMN = 0;
    private static final int INTEGER_COLUMN = 1;
    private static final int DOUBLE_COLUMN = 2;
    private static final int STRING_COLUMN = 3;
    private static final int TYPE_COLUMN = 0;
    private static final int MONITORING_COLUMN = 1;
    private static final int SINGLE_DATA_TO_OBJECT_SIZE = 4;

    private Converter() {
    }

    static ParamTO convertToParamTO(final Object[] object) {
        ParamTO param = new ParamTO();

        param.setTime(((BigInteger) object[TIME_COLUMN]).longValue());
        if (object[INTEGER_COLUMN] != null) {
            param.setValue(((Integer) object[INTEGER_COLUMN]).toString());
        } else if (object[DOUBLE_COLUMN] != null) {
            param.setValue(String.format("%.4f", (Double) object[DOUBLE_COLUMN]));
        } else if (object[STRING_COLUMN] != null) {
            param.setValue((String) object[STRING_COLUMN]);
        }

        return param;
    }

    static SingleDataTO convertToSingleDataTO(final Object[] object) {
        SingleDataTO singleData = new SingleDataTO();

        singleData.setType((String) object[TYPE_COLUMN]);
        singleData.setMonitoring((Boolean) object[MONITORING_COLUMN]);

        Object[] objects = new Object[SINGLE_DATA_TO_OBJECT_SIZE];
        System.arraycopy(object, 2, objects, 0, SINGLE_DATA_TO_OBJECT_SIZE);

        singleData.setParam(convertToParamTO(objects));

        return singleData;
    }
}
