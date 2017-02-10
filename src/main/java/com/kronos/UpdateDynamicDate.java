package com.kronos;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kronos.utils.ConfigurationMap;
import com.kronos.utils.Constants;

public class UpdateDynamicDate
{
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDynamicDate.class);
    
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);
        LOGGER.info("Enter Source Directory: ");
        String s = scan.next();
        scan.close();
        File dir = new File(s);
        boolean exists = dir.exists();
        if (!exists)
        {
            LOGGER.info("Source Directory does not exist. Exiting....");
        }
        else
        {
            String str = ConfigurationMap.getPropertyValue4(Constants.EXCLUDELIST);
            List<String> excludeItems = Arrays.asList(str.split("\\s*,\\s*"));
            
            String destinationPath = dir.getParentFile().getPath() + "\\" + dir.getName() + "-"+ConfigurationMap.getPropertyValue4(Constants.FOLDERAPPENDNAME);
            // creating new folder under parent directory
            File file = new File(destinationPath);
            if (!file.exists() && file.mkdirs())
            {
                LOGGER.info("Creating New directory: " + destinationPath);
            }

            File source = new File(s);
            File dest = new File(destinationPath);
            try
            {
                LOGGER.info("Copying files from source to new destination : " + s + " to " + destinationPath);
                FileUtils.copyDirectory(source, dest);
            }
            catch (IOException e)
            {
                LOGGER.error(e.getMessage());
            }

            walk(destinationPath, excludeItems);
        }
    }

    @SuppressWarnings("unused")
    public static void walk(String path, List<String> excludeItems)
    {
        Set<Object> keys = ConfigurationMap.getAllkeys();
        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) return;

        for (File f : list)
        {
            boolean flag = false;
            if (f.isDirectory())
            {
                walk(f.getAbsolutePath(), excludeItems);
                //LOGGER.info("Dir:" + f.getAbsoluteFile());
            }
            else
            {
                String ext = FilenameUtils.getExtension(f.getName());
                if(excludeItems.contains(ext))
                    continue;
                //LOGGER.info("File:" + f.getAbsoluteFile());
                BufferedReader br = null;
                FileWriter fw = null;
                try
                {
                    fw = new FileWriter(f.getAbsoluteFile() + ".new");
                    br = new BufferedReader(new FileReader(f.getAbsoluteFile()));
                    String next, line = br.readLine();
                    for (boolean first = true, last = (line == null); !last; first = false, line = next)
                    {
                        last = ((next = br.readLine()) == null);

                        for (Object stock : keys)
                        {
                            String strStock = (String) stock;
                            if (line.contains(strStock))
                            {
                                flag = true;
                                String date = UpdateDynamicDateHelper.updateDate(strStock);
                                line = line.replace("${" + strStock + "}", date);
                                line = line.replace("{" + strStock + "}", date);
                                line = line.replace(strStock, date);
                            }
                        }

                        if (last)
                        {
                            fw.write(line);
                        }
                        else
                        {
                            fw.write(line);
                            fw.write("\n");
                        }
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error(e.getMessage());
                }
                try
                {
                    br.close();
                    fw.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                br = null;
                
                //Adding next line into new file
                LineNumberReader lnr = null;
                LineNumberReader lnr1 = null;
                try
                {
                    lnr = new LineNumberReader(new FileReader(f.getAbsoluteFile()));
                    lnr.skip(Long.MAX_VALUE);
                    lnr1 = new LineNumberReader(new FileReader(f.getAbsoluteFile() + ".new"));
                    lnr1.skip(Long.MAX_VALUE);
                    int count = lnr.getLineNumber() + 1;
                    int count1= lnr1.getLineNumber() + 1;
                    if(count == count1+1)
                    {
                        fw = new FileWriter(f.getAbsoluteFile() + ".new",true);
                        fw.write("\n");
                    }
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        lnr.close();
                        lnr1.close();
                        fw.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }

                // renaming file
                //LOGGER.info("Deleting " + f.getName());
                //LOGGER.info("File deleted: " + f.delete());
                if(!f.delete())
                {
                    LOGGER.error("Error in deleting file "+f.getName());
                }
                File newfile = new File(f.getAbsoluteFile() + ".new");
                if (newfile.renameTo(f))
                {
                    if(flag)
                        LOGGER.info(f.getName()+" updated successfully");
                }
                else
                {
                    LOGGER.error(f.getName()+" update failed");
                }
            }
        }
    }

}
