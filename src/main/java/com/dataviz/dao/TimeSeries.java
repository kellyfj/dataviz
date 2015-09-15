package com.dataviz.dao;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by kellyfj on 9/15/15.
 */
@Entity
public class TimeSeries {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @NotNull
  public String username;

  @NotNull
  @Temporal(TemporalType.TIMESTAMP)
  public Date dateTime;

  public Double gsr;


  public TimeSeries(String username, java.util.Date utilDate, Double gsr) {
    this.username = username;
    this.dateTime = utilDate;
    this.gsr = gsr;
  }


  public TimeSeries() {
    this.username="Default";
    this.dateTime= new Date();
    this.gsr = 0.0;
  }
}
