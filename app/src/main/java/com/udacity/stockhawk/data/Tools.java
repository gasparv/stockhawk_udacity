package com.udacity.stockhawk.data;

import java.util.ArrayList;
import java.util.List;
import lecho.lib.hellocharts.model.PointValue;


/**
 * Created by gaspa on 25.3.2017.
 */

public class Tools {
    public static List<PointValue> parseStringHistoryData(String history) {
        List<PointValue> entries = new ArrayList<>();
        if (!history.equals("")) {
            String[] valueList = history.split("\n");
            for (String value : valueList) {
                if (!value.equals("")) {
                    String[] splitValue = value.split(",");
                    if (splitValue.length == 2) {
                        entries.add(new PointValue(Float.parseFloat(splitValue[0]), Float.parseFloat(splitValue[1])));
                    }
                }
            }
        }
        return entries;
    }
}
