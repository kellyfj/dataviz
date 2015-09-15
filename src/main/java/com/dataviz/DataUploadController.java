package com.dataviz;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.csv.*;

@Controller
public class DataUploadController {

    @RequestMapping(value="/upload", method=RequestMethod.GET)
    public @ResponseBody String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value="/upload", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file){
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                File downloadFile = new File(name);
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(downloadFile));
                stream.write(bytes);
                stream.close();


                Reader in = new FileReader(downloadFile);
                Iterable<CSVRecord> records = CSVFormat.EXCEL.withHeader().parse(in);

                int recordCount =0;
                for (CSVRecord record : records) {
                    // date	calories	gsr	heart-rate	skin-temp	steps
                    String date = record.get("date");
                    //2015-05-12 15:23Z
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mmX", Locale.ENGLISH);
                    Date d = format.parse(date);
                    String gsr = record.get("gsr");
                    if(recordCount % 10 == 0) {
                      System.out.println(date + "-> ("+d+") -> ("+gsr+")");
                    }
                    recordCount++;
                }
                System.out.println("Record count "+recordCount);


                return "You successfully uploaded " + name + "!";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

}
