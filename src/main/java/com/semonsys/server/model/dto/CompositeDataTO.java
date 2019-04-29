package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;
import com.semonsys.server.model.dao.CompositeData;
import com.semonsys.server.model.dao.SingleData;

import java.util.ArrayList;
import java.util.List;

public class CompositeDataTO {
    @SerializedName(value = "i")
    private String identifier;
    @SerializedName(value = "d")
    private List<SingleDataTO> data;

    public CompositeDataTO (){}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<SingleDataTO> getData() {
        return data;
    }

    public void setData(List<SingleDataTO> data) {
        this.data = data;
    }



    public static CompositeDataTO convert(CompositeData data){
        CompositeDataTO compositeDataTO = new CompositeDataTO();

        compositeDataTO.setIdentifier(data.getIdentifier());

        List<SingleDataTO> list = new ArrayList<>();

        for(SingleData singleData : data.getData()){
            list.add(SingleDataTO.convert(singleData));
        }

        compositeDataTO.setData(list);

        return compositeDataTO;
    }
}
