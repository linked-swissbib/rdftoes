package linked.swissbib.ch;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;


/**
 * Writes Elasticsearch Bulk API compliant strings to Elasticsearch Index
 *
 * @author Sebastian Schüpbach, project swissbib, Basel
 *
 */
public class ESBulkIndexer {

    TransportClient esClient;
    BulkProcessor bulkProcessor;
    String[] esNodes;
    String esClustername;
    short recordsPerUpload;

    Boolean connEstablished = false;



    ESBulkIndexer(String[] esNodes, String esClustername, short recordsPerUpload) {
        this.esNodes = esNodes;
        this.esClustername = esClustername;
        this.recordsPerUpload = recordsPerUpload;
    }
    protected void establishConn() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", this.esClustername)
                .build();

        this.esClient = new TransportClient(settings);
        for (String elem: this.esNodes) {
            String[] node = elem.split(":");
            this.esClient.addTransportAddress(new InetSocketTransportAddress(node[0], Integer.parseInt(node[1])));
        }

        this.bulkProcessor = BulkProcessor.builder(this.esClient, new BulkProcessor.Listener() {

            @Override
            public void beforeBulk(long l, BulkRequest bulkRequest) {
                System.out.println("Bulk requests to be processed: " + bulkRequest.numberOfActions());
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, BulkResponse bulkResponse) {
                System.out.println("Indexing took " + bulkResponse.getTookInMillis() + " ms");
            }

            @Override
            public void afterBulk(long l, BulkRequest bulkRequest, Throwable throwable) {
                System.out.println("Some errors were reported: " + throwable.getMessage());
            }
        })
                // Header and body line
                .setBulkActions(this.recordsPerUpload * 2)
                .setConcurrentRequests(1)
                .build();
    }


    public void index(String obj) {
        if (!this.connEstablished) {
            this.establishConn();
            this.connEstablished = true;
        }

        BytesArray ba = new BytesArray(obj);
        try {
            this.bulkProcessor.add(ba, false, "testsb", "bibliographicResource");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        this.bulkProcessor.flush();
        this.bulkProcessor.close();
        this.esClient.close();
    }

}
