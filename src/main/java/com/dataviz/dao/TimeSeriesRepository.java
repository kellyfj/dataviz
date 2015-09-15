package com.dataviz.dao;

import org.springframework.data.repository.CrudRepository;

/**
 * Created by kellyfj on 9/15/15.
 */
public interface TimeSeriesRepository extends CrudRepository<TimeSeries, Long> {

}
