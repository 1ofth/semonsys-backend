package com.semonsys.server.service.db.storedData;

import com.semonsys.server.model.DataGroup;
import com.semonsys.server.service.db.DataGroupService;
import com.semonsys.server.service.db.DataTypeService;
import com.semonsys.shared.DataType;
import com.semonsys.shared.SingleData;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SingleDataService {
    private static final int GROUP_NAME_COLUMN_NUMBER = 1;
    private static final int TIME_COLUMN_NUMBER = 2;
    private static final int LONG_VALUE_COLUMN_NUMBER = 3;
    private static final int DOUBLE_VALUE_COLUMN_NUMBER = 4;
    private static final int STRING_VALUE_COLUMN_NUMBER = 5;

    @PersistenceContext(name = "provider")
    private EntityManager entityManager;

    @EJB
    private DataGroupService dataGroupService;

    @EJB
    private DataTypeService dataTypeService;

    /**
     * Returns a list of single data objects with one data type from timestamp time to newest ones
     *
     * @param dataTypeName a name of type of stored data
     * @param serverId     id of a com.semonsys.com.semonsys.server data belongs to
     * @param timestamp    a time to use as a lowest time of returning pack of data
     * @return a list of SingleData objects
     */
    public List<SingleData> findOneAfter(final String dataTypeName, final long serverId, final Timestamp timestamp) {
        List<Object[]> objects;

        try {
            objects = (List<Object[]>) entityManager.createNativeQuery(
                "SELECT \n"
                    + "    data_type.name AS data_type,\n"
                    + "    data_group.name AS data_group,\n"
                    + "    data.time,\n"
                    + "    param.int_value,\n"
                    + "    param.float_value,\n"
                    + "    param.text_value\n"
                    + "FROM    \n"
                    + "    data_type  \n"
                    + "    JOIN data\n"
                    + "        ON(data_type.id = data.data_type_id)\n"
                    + "     \n"
                    + "    JOIN data_group\n"
                    + "        ON(data_group.id = data.data_group_id)\n"
                    + "        \n"
                    + "    JOIN param\n"
                    + "        ON(data.param_id = param.id)\n"
                    + "        \n"
                    + "WHERE \n"
                    + "    data.server_id = :serverId\n"
                    + "    AND data_type.name = :dataType\n"
                    + "    AND :time \\:\\:timestamp <= data.time\\:\\:timestamp \n "
                    + "    AND data.id NOT IN (SELECT data_id FROM composite_data) ;"
            )
                .setParameter("serverId", serverId)
                .setParameter("dataType", dataTypeName)
                .setParameter("time", timestamp)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }

        List<SingleData> list = new ArrayList<>();

        for (Object[] object : objects) {
            SingleData singleData = convertObjectToData(object);

            list.add(singleData);
        }

        return list;
    }


    /**
     * Returns a list of SingleData objects which contains ALL possible data types of given com.semonsys.com.semonsys.server and user
     *
     * @param serverId id of a com.semonsys.com.semonsys.server data belongs to
     * @param userName a user name will be used to find all possible data types
     * @return a list of SingleData objects containing objects of each possible type of user's data
     */
    public List<SingleData> findLastAll(final long serverId, final String userName) {
        List<SingleData> list = new ArrayList<>();
        List<com.semonsys.server.model.DataType> types = dataTypeService.findWithDefault(userName);

        if (types == null) {
            return null;
        }

        for (com.semonsys.server.model.DataType dataType : types) {
            SingleData singleData = findLastOne(dataType.getName(), serverId);
            if (singleData != null) {
                list.add(singleData);
            }
        }

        return list;
    }


    /**
     * Returns only last one SingleData object with given data type of given com.semonsys.com.semonsys.server
     *
     * @param dataTypeName data type's name
     * @param serverId     com.semonsys.com.semonsys.server id
     * @return a last one SingleData object
     */
    public SingleData findLastOne(final String dataTypeName, final long serverId) {
        Object[] object;
        try {
            object = (Object[]) entityManager.createNativeQuery(
                "SELECT "
                    + "    data_type.name as data_type,"
                    + "    data_group.name as data_group,"
                    + "    data.time,"
                    + "    param.int_value,"
                    + "    param.float_value,"
                    + "    param.text_value "
                    + "FROM "
                    + "    data_type"
                    + "    JOIN data"
                    + "        ON(data_type.id = data.data_type_id)"
                    + "    JOIN data_group"
                    + "        ON(data_group.id = data.data_group_id)"
                    + "    JOIN param"
                    + "        ON(data.param_id = param.id)"
                    + "WHERE "
                    + "    data.id NOT IN (SELECT data_id FROM composite_data)"
                    + "    AND data.server_id = :serverId "
                    + "    AND data_type.name = :dataTypeName "
                    + "ORDER BY data.id DESC "
                    + "LIMIT 1;")
                .setParameter("serverId", serverId)
                .setParameter("dataTypeName", dataTypeName)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        SingleData singleData = new SingleData();

        singleData.setDataTypeName(dataTypeName);
        singleData.setGroupName((String) object[GROUP_NAME_COLUMN_NUMBER]);
        singleData.setTime((Timestamp) object[TIME_COLUMN_NUMBER]);

        if (object[DOUBLE_VALUE_COLUMN_NUMBER] != null) {
            singleData.setValue((Long) object[DOUBLE_VALUE_COLUMN_NUMBER]);
        } else if (object[DOUBLE_VALUE_COLUMN_NUMBER] != null) {
            singleData.setValue((Double) object[DOUBLE_VALUE_COLUMN_NUMBER]);
        } else if (object[STRING_VALUE_COLUMN_NUMBER] != null) {
            singleData.setValue((String) object[STRING_VALUE_COLUMN_NUMBER]);
        }

        return singleData;
    }


    @Transactional
    public BigInteger save(final SingleData data, final Long serverId) {
        if (data.getType() == DataType.NONE || data.getType() == DataType.LIST) {
            return null;
        }

        DataGroup dataGroup = dataGroupService.find(data.getGroupName());

        if (dataGroup == null) {
            return null;
        }

        com.semonsys.server.model.DataType dataType = dataTypeService.findByName(data.getDataTypeName());

        if (dataType == null) {
            return null;
        }

        BigInteger id = insertIntoParam(data);

        entityManager.createNativeQuery("INSERT INTO data(server_id, data_type_id, data_group_id, param_id, time) VALUES "
            + "(:serverId, :dataTypeId, :dataGroupId, :paramId, :time);")
            .setParameter("serverId", serverId)
            .setParameter("dataTypeId", dataType.getId())
            .setParameter("dataGroupId", dataGroup.getId())
            .setParameter("paramId", id)
            .setParameter("time", data.getTime())
            .executeUpdate();

        return (BigInteger) entityManager.createNativeQuery("SELECT last_value FROM data_id_seq;").getSingleResult();
    }

    public Long getMaxTime() {
        Timestamp result;
        try {
            result = (Timestamp) entityManager.createNativeQuery(
                "SELECT "
                    + "   MAX(data.time) "
                    + "FROM data "
                    + "WHERE "
                    + "   data.id NOT IN (SELECT data_id FROM composite_data);").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        if (result == null) {
            return 0L;
        } else {
            return result.getTime();
        }
    }

    private BigInteger insertIntoParam(final SingleData data) {
        switch (data.getType()) {
            case LONG:
                entityManager.createNativeQuery("INSERT INTO param(text_value, int_value, float_value) VALUES (NULL, ?, NULL);")
                    .setParameter(1, data.getValue())
                    .executeUpdate();
                break;

            case STRING:
                entityManager.createNativeQuery("INSERT INTO param(text_value, int_value, float_value) VALUES (?, NULL, NULL);")
                    .setParameter(1, data.getValue())
                    .executeUpdate();
                break;

            case DOUBLE:
                entityManager.createNativeQuery("INSERT INTO param(text_value, int_value, float_value) VALUES (NULL, NULL, ?);")
                    .setParameter(1, data.getValue())
                    .executeUpdate();
                break;

            default:
                return null;
        }

        return (BigInteger) entityManager.createNativeQuery("SELECT last_value FROM param_id_seq;").getSingleResult();
    }

    private SingleData convertObjectToData(final Object[] object) {
        SingleData singleData = new SingleData();

        singleData.setDataTypeName((String) object[0]);
        singleData.setGroupName((String) object[GROUP_NAME_COLUMN_NUMBER]);
        singleData.setTime((Timestamp) object[TIME_COLUMN_NUMBER]);

        if (object[LONG_VALUE_COLUMN_NUMBER] != null) {
            singleData.setValue(((Number) object[LONG_VALUE_COLUMN_NUMBER]).longValue());
        } else if (object[DOUBLE_VALUE_COLUMN_NUMBER] != null) {
            singleData.setValue((Double) object[DOUBLE_VALUE_COLUMN_NUMBER]);
        } else if (object[STRING_VALUE_COLUMN_NUMBER] != null) {
            singleData.setValue((String) object[STRING_VALUE_COLUMN_NUMBER]);
        }

        return singleData;
    }
}
