package com.semonsys.server.model.dto;

import com.google.gson.annotations.SerializedName;
import com.semonsys.server.model.dao.CompositeData;
import com.semonsys.server.model.dao.SingleData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompositeDataTO implements Comparable<CompositeDataTO> {
    @SerializedName(value = "i")
    private String identifier = "";
    @SerializedName(value = "d")
    private List<SingleDataTO> data;

    public CompositeDataTO() {
    }

    public static CompositeDataTO convert(final CompositeData data) {
        CompositeDataTO compositeDataTO = new CompositeDataTO();

        compositeDataTO.setIdentifier(data.getIdentifier());

        List<SingleDataTO> list = new ArrayList<>();

        for (SingleData singleData : data.getData()) {
            list.add(SingleDataTO.convert(singleData));
        }

        compositeDataTO.setData(list);

        return compositeDataTO;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public List<SingleDataTO> getData() {
        return data;
    }

    public void setData(final List<SingleDataTO> data) {
        this.data = data;
    }

    @Override
    public int compareTo(final CompositeDataTO o) {
        return identifier.compareTo(o.getIdentifier());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeDataTO that = (CompositeDataTO) o;
        return identifier.equals(that.identifier)
            && Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, data);
    }
}
