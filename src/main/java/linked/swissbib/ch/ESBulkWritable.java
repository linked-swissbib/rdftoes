package linked.swissbib.ch;

/**
 * @autor Sebastian Schüpbach, project swissbib, Basel
 */
public interface ESBulkWritable {

    public void connect();

    public void write(String obj);

    public void close();
}
