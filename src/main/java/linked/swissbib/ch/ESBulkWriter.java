package linked.swissbib.ch;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Writes Elasticsearch Bulk API compliant strings to a file structure.
 * @author Sebastian Schüpbach, project swissbib, Basel
 */
public class ESBulkWriter implements ESBulkWritable {

    short fileSizeCount = 0;
    short dirSizeCount = 0;
    short fileSize;
    short dirSize;
    String filePrefix;
    String dirPath;
    short subDirCount = 1;
    BufferedWriter writer;

    ESBulkWriter(short fileSize, short dirSize, String dirPath, String filePrefix) {
        this.fileSize = fileSize;
        this.dirSize = dirSize;

        if (!dirPath.endsWith("/")) dirPath += "/";
        this.dirPath = dirPath;
        this.filePrefix = filePrefix;

    }

    @Override
    public void connect() {
        SimpleDateFormat curTime = new SimpleDateFormat("yyMMddHHmmssSSS");
        String fileName = this.filePrefix + "_" + curTime.format(new Date()) + ".jsonld";
        try {
            if (this.dirSizeCount >= this.dirSize) {
                this.dirSizeCount = 0;
                this.subDirCount += 1;
            }
            File dir = new File(this.dirPath + this.subDirCount);
            boolean success = false;
            if (!dir.exists()) success = dir.mkdir();
            if (!success) throw new IOException("Couldn't create new directory!");
            this.writer = new BufferedWriter(new FileWriter(this.dirPath + this.subDirCount + "/" + fileName));
            this.dirSizeCount += 1;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String obj) {
        try {
            if (this.fileSizeCount >= this.fileSize) {
                this.close();
                this.connect();
                this.fileSizeCount = 0;
            }
            this.writer.write(obj);
            //this.writer.write("awwef");
            this.fileSizeCount += 1;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void close() {
        try {
            this.writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
