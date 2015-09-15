package com.dataviz;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.dataviz.dao.TimeSeries;
import com.dataviz.dao.TimeSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.csv.*;

@Controller
public class DataUploadController {

  @Autowired
  TimeSeriesRepository timeSeriesDao;

  @RequestMapping(value = "/upload", method = RequestMethod.GET)
  public
  @ResponseBody
  String provideUploadInfo() {
    return "You can upload a file by posting to this same URL.";
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public
  @ResponseBody
  String handleFileUpload(
      @RequestParam("file") MultipartFile file
  ) {
    if (!file.isEmpty()) {
      try {

        //Write data to temporary file for ingestion to Commons CSV
        byte[] bytes = file.getBytes();
        File downloadFile = new File(System.currentTimeMillis() + ".txt");
        BufferedOutputStream stream =
            new BufferedOutputStream(new FileOutputStream(downloadFile));
        stream.write(bytes);
        stream.close();

        //Ingest and parse
        Reader in = new FileReader(downloadFile);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);
        int recordCount = 0;

        for (CSVRecord record : records) {
          // date	calories	gsr	heart-rate	skin-temp	steps
          String rawDate = record.get("date");
          //2015-05-12 15:23Z
          DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mmX", Locale.ENGLISH);
          Date d = format.parse(rawDate);
          String rawGsr = record.get("gsr");
          Double gsr = null;
          if (rawGsr != null && !rawGsr.isEmpty()) {
            gsr = Double.parseDouble(rawGsr);
          }
          if (recordCount % 10 == 0) {
            System.out.println(recordCount + " records loaded");
          }
          TimeSeries t = new TimeSeries("kellyfj", d, gsr);
          timeSeriesDao.save(t);

          recordCount++;
        }
        System.out.println("Record count " + recordCount);

        return "You successfully uploaded the file!";
      } catch (Exception e) {
        e.printStackTrace();
        return "You failed to upload => " + e.getMessage();
      }
    } else {
      return "You failed to upload because the file was empty.";
    }
  }

  @RequestMapping("/view")
  public String getTimeSeries(Model model) {
    System.out.println("Eeeeh oooh!");
    List<TimeSeries> list = new ArrayList<>();
    Iterable<TimeSeries> iter = timeSeriesDao.findAll();
    int i = 0;
    for(TimeSeries t: iter) {
      i++;
      list.add(t);
      if(i % 10 ==0) {
        System.out.println("Loaded " + i + " rows");
      }
    }
    model.addAttribute("timeseries", list);
    return "view";
  }

  @RequestMapping("/greeting")
  public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) {
    model.addAttribute("name", name);
    return "greeting";
  }
}
