package com.semonsys.server.service.db;

import com.semonsys.server.interceptor.MethodParamsInterceptor;
import com.semonsys.server.model.dao.CompositeData;
import com.semonsys.server.model.dao.SingleData;
import com.semonsys.server.model.dto.ParamTO;
import com.semonsys.server.model.dto.SingleDataTO;
import lombok.extern.log4j.Log4j;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j
@Stateless
public class CompositeDataService {

    @PersistenceContext(unitName = "provider")
    private EntityManager entityManager;

    public CompositeData find(final long id) {return entityManager.find(CompositeData.class, id);}

    @Transactional
    public void save(CompositeData compositeData){
        entityManager.persist(compositeData);
    }

    @Transactional
    public void save(List<CompositeData> compositeData){
        for(CompositeData data : compositeData){
               entityManager.persist(data);
        }
    }

    @Interceptors(MethodParamsInterceptor.class)
    public List<SingleDataTO> findLastSingleDataListWithIdentifier(final String dataGroupName,
                                                                   final long serverId,
                                                                   final String identifier){
        List<SingleDataTO> list = new ArrayList<>();

        String query = "SELECT \n" +
            "    dt.name,\n" +
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
            "    JOIN composite_data" +
            "        ON(composite_data.id = data.comp_id)" +
            "WHERE\n" +
            "    data.time = (\n" +
            "        SELECT \n" +
            "            MAX(data.time) \n" +
            "        FROM \n" +
            "            data\n" +
            "            JOIN data_type AS dt_inner\n" +
            "                ON(data.data_type_id = dt_inner.id)\n" +
            "        WHERE " +
            "            dt.id = dt_inner.id\n" +
            "            AND data.comp_id is not null \n" +
            "        )\n" +
            "    AND data_group.name = :groupName\n" +
            "    AND data.server_id = :serverId" +
            "    AND composite_data.identifier = :identifier";

        List<Object[]> temp;

        try{
            temp = (List<Object[]>) entityManager.createNativeQuery(query)
                .setParameter("identifier", identifier)
                .setParameter("groupName", dataGroupName)
                .setParameter("serverId", serverId)
                .getResultList();
        } catch (NoResultException e){
            return list;
        }

        for(Object[] object : temp){
            SingleDataTO singleData = Converter.convertToSingleDataTO(object);
            list.add(singleData);
        }

        log.info("There were " + list.size() + " singleData objects found from db");
        return list;
    }

    @Interceptors(MethodParamsInterceptor.class)
    public Set<ParamTO> findAllParamsFromTimeWithIdentifier(final String group,
                                                            final String type,
                                                            final long serverId,
                                                            final long time,
                                                            final String identifier){
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
            "    JOIN composite_data" +
            "        ON(data.comp_id = composite_data.id)" +
            "WHERE\n" +
            "    data.server_id = :server\n" +
            "    AND data_group.name = :group\n" +
            "    AND data.time > :time\n" +
            "    AND data_type.name = :type\n" +
            "    AND composite_data.identifier = :identifier";

        Set<ParamTO> list = new HashSet<>();

        List<Object[]> temp;

        try{
            temp = (List<Object[]>) entityManager.createNativeQuery(query)
                .setParameter("identifier", identifier)
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

        log.info("There were " + list.size() + " params objects found from db");
        return list;
    }

    @Interceptors(MethodParamsInterceptor.class)
    public Set<String> findIdentifiers(final long serverId, final String groupName) {
        List<String> list;
        try {
            list = (List<String>) entityManager.createNativeQuery(
                "SELECT "
                    + "    DISTINCT identifier "
                    + "FROM "
                    + "    composite_data "
                    + "    JOIN data "
                    + "         ON(data.comp_id = composite_data.id) "
                    + "    JOIN data_group"
                    + "         ON(data.data_group_id = data_group.id)"
                    + "WHERE "
                    + "    composite_data.server_id=:serverId \n"
                    + "      AND data_group.name = :group")
                .setParameter("serverId", serverId)
                .setParameter("group", groupName)
                .getResultList();
        } catch (NoResultException e) {
            return new HashSet<>();
        }

        log.info("There were " + list.size() + " identifiers objects found from db");
        return new HashSet<>(list);
    }


    @Interceptors(MethodParamsInterceptor.class)
    public long getMaxTime(){
        BigInteger temp;
        try{
            temp = (BigInteger) entityManager.createNativeQuery(
                "SELECT " +
                    "   MAX(time) " +
                    "FROM " +
                    "   data " +
                    "WHERE " +
                    "   data.comp_id is not null")
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
