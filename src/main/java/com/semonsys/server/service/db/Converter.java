package com.semonsys.server.service.db;

import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;

import java.math.BigInteger;

public class Converter {
    public static ParamTO convertToParamTO(Object[] object){
        ParamTO param = new ParamTO();

        param.setTime( ((BigInteger)object[0]).longValue() );
        if(object[1] != null){
            param.setValue( ((Integer) object[1]).toString() );
        } else if(object[2] != null){
            param.setValue( String.format("%.4f", (Double) object[2]) );
        } else if(object[3] != null){
            param.setValue( (String) object[3]);
        }

        return param;
    }

    public static SingleDataTO convertToSingleDataTO(Object[] object){
        SingleDataTO singleData = new SingleDataTO();

        singleData.setType((String)object[0]);

        Object[] objects = new Object[4];
        System.arraycopy(object, 1, objects, 0, 4);

        singleData.setParam(convertToParamTO(objects));

        return singleData;
    }
}
