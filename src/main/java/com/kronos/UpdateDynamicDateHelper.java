package com.kronos;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kronos.utils.ConfigurationMap;
import com.kronos.utils.Constants;

public class UpdateDynamicDateHelper
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDynamicDateHelper.class);
    
    public static void main(String[] args)
    {
        String updateDate = UpdateDynamicDateHelper.updateDate("dayMinus030");
        LOGGER.info(updateDate);
    }

    public static String updateDate(String searchKey)
    {
        String dateFormat = ConfigurationMap.getPropertyValue4(Constants.DATEFORMAT);
        String date1 = "";
        String searchValue = "";
        Calendar c = null;
        int value = ConfigurationMap.getPropertyValue1(searchKey) != null ? 1 : ConfigurationMap.getPropertyValue2(searchKey) != null ? 2 : ConfigurationMap.getPropertyValue3(searchKey) != null ? 3 : 0;

        switch (value)
        {
            case 1:
                searchValue = ConfigurationMap.getPropertyValue1(searchKey);
                c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE,
                    java.util.Calendar.SUNDAY - java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_WEEK)
                        + (Integer.parseInt(searchValue)));
                date1 = new java.text.SimpleDateFormat(dateFormat).format(c.getTime());
                break;
            case 2:
                searchValue = ConfigurationMap.getPropertyValue2(searchKey);
                if (searchKey.equals(Constants.CURRENTYEAR) || searchKey.equals(Constants.CURRENTMONTH)
                    || searchKey.equals(Constants.CURRENTDATE))
                {
                    date1 = new java.text.SimpleDateFormat(searchValue).format(new Date());
                }
                else
                {
                    try
                    {
                        date1 = new java.text.SimpleDateFormat(dateFormat).format(new java.text.SimpleDateFormat(dateFormat).parse(searchValue));
                    }
                    catch (ParseException e)
                    {
                        e.printStackTrace();
                    }
                }
                break;
            case 3:
                searchValue = ConfigurationMap.getPropertyValue3(searchKey);
                c = Calendar.getInstance();
                c.setTime(new Date());
                c.add(Calendar.DATE, Integer.parseInt(searchValue));
                date1 = new java.text.SimpleDateFormat(dateFormat).format(c.getTime());
                break;
            default:
                LOGGER.info("Not Found" + ": " + searchKey);
        }
        return date1;
    }
}
