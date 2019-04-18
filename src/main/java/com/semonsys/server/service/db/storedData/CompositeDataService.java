package com.semonsys.server.service.db.storedData;

import com.semonsys.shared.CompositeData;
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
import java.util.Objects;

@Stateless
public class CompositeDataService {
    private static final int GROUP_NAME_COLUMN_NUMBER = 1;
    private static final int TIME_COLUMN_NUMBER = 2;
    private static final int LONG_VALUE_COLUMN_NUMBER = 3;
    private static final int DOUBLE_VALUE_COLUMN_NUMBER = 4;
    private static final int STRING_VALUE_COLUMN_NUMBER = 5;

    private static final int TIME_DIFFERENCE_MILLISECONDS = 500;

    @PersistenceContext(name = "provider")
    private EntityManager entityManager;

    @EJB
    private SingleDataService singleDataService;


    /**
     * Returns a list of CompositeData objects containing data of given identifier from given time to
     * newest ones
     *
     * @param identifier a string which identifies this composite data object
     * @param serverId   com.semonsys.com.semonsys.server id
     * @param time       time which would be used as lowest time of returning pack of composite data objects
     * @return a list of CompositeData objects of given com.semonsys.com.semonsys.server with one identifier
     */
    public List<CompositeData> findOneAfter(final String identifier, final long serverId, final Timestamp time) {
        List<Object[]> objects;

        try {
            objects = (List<Object[]>) entityManager.createNativeQuery(
                "SELECT "
                    + "    data_type.name AS data_type, "
                    + "    data_group.name AS data_group, "
                    + "    data.time, "
                    + "    param.int_value, "
                    + "    param.float_value, "
                    + "    param.text_value "
                    + "FROM   "
                    + "    data_type "
                    + "    JOIN  data"
                    + "        ON(data_type.id = data.data_type_id) "
                    + "    JOIN data_group "
                    + "        ON(data_group.id = data.data_group_id) "
                    + "    JOIN param "
                    + "        ON(data.param_id = param.id) "
                    + "    JOIN composite_data AS com "
                    + "        ON(com.data_id = data.id) "
                    + "WHERE "
                    + "    com.identifier = :identifier "
                    + "    AND data.server_id = :serverId "
                    + "    AND :time \\:\\:timestamp <= data.time\\:\\:timestamp ;"
            )
                .setParameter("identifier", identifier)
                .setParameter("serverId", serverId)
                .setParameter("time", time)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }

        List<CompositeData> list = new ArrayList<>();

        Timestamp lastTime = new Timestamp(0);
        CompositeData temp = null;

        for (Object[] object : objects) {
            List<SingleData> innerDataList = new ArrayList<>();
            SingleData singleData = convertObjectToData(object);

            if (Objects.requireNonNull(singleData).getTime().getTime() - lastTime.getTime() > TIME_DIFFERENCE_MILLISECONDS
                || singleData.getTime().getTime() == 0) {

                if (temp != null) {
                    list.add(temp);
                }

                temp = new CompositeData();

                temp.setName(identifier);
                temp.setData(new ArrayList<>());
                temp.getData().addAll(innerDataList);

                innerDataList.clear();

                lastTime = singleData.getTime();
            }

            innerDataList.add(singleData);
        }

        return list;
    }


    /**
     * Returns a last one CompositeData object of given com.semonsys.com.semonsys.server with given identifier
     *
     * @param identifier a string which identifies this composite data object
     * @param serverId   com.semonsys.com.semonsys.server id
     * @return last one CompositeData object
     */
    public CompositeData findLastOne(final String identifier, final long serverId) {
        Timestamp timestamp = getMaxTime(identifier, serverId);
        if (timestamp == null) {
            return null;
        }

        return findByTime(identifier, serverId, timestamp);
    }


    /**
     * Returns a list of CompositeData objects of given com.semonsys.com.semonsys.server. Only last values are used.
     *
     * @param serverId com.semonsys.com.semonsys.server id
     * @return a list of CompositeData objects
     */
    public List<CompositeData> findLastAll(final long serverId) {
        List<CompositeData> list = new ArrayList<>();

        List<String> identifiers = findIdentifiers(serverId);

        for (String identifier : identifiers) {
            CompositeData compositeData = findLastOne(identifier, serverId);
            if (compositeData != null) {
                list.add(compositeData);
            }
        }

        return list;
    }


    /**
     * Returns a list of identifies which are possible to use with given com.semonsys.com.semonsys.server
     *
     * @param serverId com.semonsys.com.semonsys.server id
     * @return a list of identifiers of given com.semonsys.com.semonsys.server
     */
    public List<String> findIdentifiers(final long serverId) {
        List<String> list;
        try {
            list = (List<String>) entityManager.createNativeQuery(
                "SELECT "
                    + "    DISTINCT identifier "
                    + "FROM "
                    + "    composite_data "
                    + "WHERE "
                    + "    composite_data.server_id=:serverId ;"
            )
                .setParameter("serverId", serverId)
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }

        return list;
    }


    @Transactional
    public boolean save(final CompositeData data, final long serverId) {
        for (SingleData innerData : data.getData()) {
            if (innerData.getType() == DataType.LIST || innerData.getType() == DataType.NONE) {
                entityManager.getTransaction().rollback();
                return false;
            }

            BigInteger id = singleDataService.save(innerData, serverId);

            if (id == null) {
                return false;
            } else {
                entityManager.createNativeQuery("INSERT INTO composite_data(identifier, server_id, data_id) VALUES "
                    + "(:identifier, :server_id, :data_id);")
                    .setParameter("identifier", data.getName())
                    .setParameter("server_id", serverId)
                    .setParameter("data_id", id)
                    .executeUpdate();
            }
        }

        return true;
    }

    public Long getMaxTime() {
        Timestamp result;
        try {
            result = (Timestamp) entityManager.createNativeQuery(
                "SELECT "
                    + "   MAX(data.time) "
                    + "FROM data "
                    + "WHERE "
                    + "   data.id IN (SELECT data_id FROM composite_data);").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        if (result == null) {
            return 0L;
        } else {
            return result.getTime();
        }
    }

    private CompositeData findByTime(final String identifier, final long serverId, final Timestamp timestamp) {
        List<Object[]> objects;

        try {
            objects = (List<Object[]>) entityManager.createNativeQuery(
                "SELECT "
                    + "    data_type.name AS data_type, "
                    + "    data_group.name AS data_group, "
                    + "    data.time, "
                    + "    param.int_value, "
                    + "    param.float_value, "
                    + "    param.text_value "
                    + "FROM   "
                    + "    data_type "
                    + "    JOIN  data"
                    + "        ON(data_type.id = data.data_type_id) "
                    + "    JOIN data_group "
                    + "        ON(data_group.id = data.data_group_id) "
                    + "    JOIN param "
                    + "        ON(data.param_id = param.id) "
                    + "    JOIN composite_data AS com "
                    + "        ON(com.data_id = data.id) "
                    + "WHERE "
                    + "    com.identifier = :identifier "
                    + "    AND data.server_id = :serverId "
                    + "    AND ABS(EXTRACT(EPOCH FROM (:time \\:\\:timestamp - data.time\\:\\:timestamp))) < 0.5 ;"
            )
                .setParameter("identifier", identifier)
                .setParameter("serverId", serverId)
                .setParameter("time", timestamp.toString())
                .getResultList();
        } catch (NoResultException e) {
            return null;
        }

        CompositeData compositeData = new CompositeData();
        compositeData.setName(identifier);

        List<SingleData> list = new ArrayList<>();

        for (Object[] object : objects) {
            list.add(convertObjectToData(object));
        }

        compositeData.setData(list);
        return compositeData;
    }

    private Timestamp getMaxTime(final String identifier, final long serverId) {
        try {
            return (Timestamp) entityManager.createNativeQuery(
                "SELECT \n"
                    + "    MAX(data.time)\n"
                    + "FROM data\n"
                    + "    JOIN composite_data\n"
                    + "        ON(data.id = composite_data.data_id)\n"
                    + "WHERE\n"
                    + "    composite_data.identifier=:identifier "
                    + "    AND data.server_id = :serverId ;"
            )
                .setParameter("identifier", identifier)
                .setParameter("serverId", serverId)
                .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
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
