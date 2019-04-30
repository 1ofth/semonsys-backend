package com.semonsys.server.service.db;

import com.semonsys.server.model.dao.SingleData;
import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Stateless
public class SingleDataService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;


    public List<SingleData> find() {
        try {
            return entityManager.createQuery("SELECT data FROM SingleData AS data", SingleData.class)
                .getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    @Transactional
    public void save(final SingleData singleData){
        entityManager.persist(singleData.getParam());
        entityManager.persist(singleData);
    }

    @Transactional
    public void save(final List<SingleData> singleData){
        for(SingleData data : singleData){
            entityManager.persist(data.getParam());
            entityManager.persist(data);
        }
    }

    public Set<SingleDataTO> findLastSingleDataPack(final String dataGroupName, final  long serverId){
        Set<SingleDataTO> result = new HashSet<>();

        String query = "SELECT \n" +
            "    dt.name,\n" +
            "    dt.monitoring, \n" +
            "    data.time,\n" +
            "    param.int_value,\n" +
            "    param.float_value,\n" +
            "    param.text_value\n" +
            "FROM\n" +
            "    data_type AS dt\n" +
            "    JOIN data\n" +
            "        ON(dt.id = data.data_type_id)\n" +
            "    JOIN param\n" +
            "        ON(data.param_id = param.id)\n" +
            "    JOIN data_group\n" +
            "        ON(data.data_group_id = data_group.id)\n" +
            "WHERE\n" +
            "    data.time = (\n" +
            "        SELECT \n" +
            "            MAX(data.time) \n" +
            "        FROM \n" +
            "            data\n" +
            "            JOIN data_type AS dt_inner\n" +
            "                ON(data.data_type_id = dt_inner.id)\n" +
            "        WHERE dt.id = dt_inner.id\n" +
            "        )\n" +
            "    AND data_group.name = :groupName\n" +
            "    AND data.server_id = :serverId";

        List<Object[]> temp;

        try{
            temp = (List<Object[]>) entityManager.createNativeQuery(query)
                .setParameter("groupName", dataGroupName)
                .setParameter("serverId", serverId)
                .getResultList();
        } catch (NoResultException e){
            return result;
        }

        for(Object[] object : temp){
            SingleDataTO singleData = Converter.convertToSingleDataTO(object);
            result.add(singleData);
        }

        return result;
    }

    public Set<ParamTO> findAllParamsFromTime (final String group, final String type, final long serverId, final long time){

        String query = "SELECT \n" +
            "    data.time,\n" +
            "    param.int_value,\n" +
            "    param.float_value,\n" +
            "    param.text_value\n" +
            "FROM\n" +
            "    data_type\n" +
            "    JOIN data\n" +
            "        ON(data_type.id = data.data_type_id)\n" +
            "    JOIN param\n" +
            "        ON(data.param_id = param.id)\n" +
            "    JOIN data_group\n" +
            "        ON(data.data_group_id = data_group.id)\n" +
            "WHERE\n" +
            "    data.server_id = :server\n" +
            "    AND data_group.name = :group\n" +
            "    AND data.time > :time\n" +
            "    AND data_type.name = :type\n" +
            "    AND data.comp_id is NULL\n";

        Set<ParamTO> list = new HashSet<>();

        List<Object[]> temp;

        try{
            temp = (List<Object[]>) entityManager.createNativeQuery(query)
                .setParameter("group", group)
                .setParameter("server", serverId)
                .setParameter("type", type)
                .setParameter("time", time)
                .getResultList();

        } catch (NoResultException e){
            return list;
        }

        for(Object[] object : temp){
            list.add(Converter.convertToParamTO(object));
        }

        return list;
    }

    public long getMaxTime(){
        BigInteger temp;
        try{
            temp = (BigInteger) entityManager.createNativeQuery(
                "SELECT " +
                    "   MAX(time) " +
                    "FROM " +
                    "   data " +
                    "WHERE " +
                    "   data.comp_id is NULL")
                .getSingleResult();

        } catch (NoResultException e){
            return 0L;
        }

        if(temp != null) {
            return (temp).longValue();
        } else {
            return 0L;
        }
    }
}
