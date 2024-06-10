package com.bootlabs.springbatch5mongodb.job.step;


import com.bootlabs.springbatch5mongodb.domain.document.Trips;
import org.springframework.batch.item.data.MongoCursorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.bootlabs.springbatch5mongodb.domain.constant.BatchConstants.DEFAULT_CHUNK_SIZE;
import static com.bootlabs.springbatch5mongodb.domain.constant.BatchConstants.DEFAULT_LIMIT_SIZE;

@Component
public class TripItemReader extends MongoCursorItemReader<Trips> {

    /*"""
                    {
                        "birth year": { "$ne": "" },
                        "usertype": { "$eq": "Subscriber" },
                        "tripduration": { "$gt": 500 },
                        "$expr": {
                            "$ne": ["$start station name",
                                "$end station name"]
                        }
                    }
     """*/
    public TripItemReader(@Autowired MongoTemplate mongoTemplate) {

        Criteria criteria = Criteria.where("birth year").ne("").and("usertype").is("Subscriber").and("tripduration").gt(500);
        BasicQuery query = new BasicQuery("{ $expr: {'$ne': ['$start station name', '$end station name']}}");
        query.addCriteria(criteria);

        setName("reader");
        setTargetType(Trips.class);
        setTemplate(mongoTemplate);
        setCollection("trips");
        setBatchSize(DEFAULT_CHUNK_SIZE);
        setQuery(query);
        setLimit(DEFAULT_LIMIT_SIZE);
        Map<String, Sort.Direction> sortOptions = new HashMap<>();
        sortOptions.put("birth year", Sort.Direction.ASC);
        setSort(sortOptions);

    }

}