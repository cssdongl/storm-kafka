package net.joshdevins.storm.topologies;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import net.joshdevins.storm.spout.KafkaSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.utils.Utils;

public class KafkaTestTopology {

    public enum COMPONENTS {
        KAFKA_SPOUT;

        public int id() {
            return ordinal() + 1;
        }
    }

    public static void main(final String[] args) {

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout(COMPONENTS.KAFKA_SPOUT.id(), createKafkaSpout());

        Map<String, Object> conf = new HashMap<String, Object>();
        conf.put(Config.TOPOLOGY_DEBUG, true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("test", conf, builder.createTopology());

        // for testing, just leave up for 10 mins then kill it all
        Utils.sleep(10 * 60 * 1000); // 10 mins
        cluster.killTopology("test");
        cluster.shutdown();
    }

    private static IRichSpout createKafkaSpout() {

        // setup Kafka consumer
        Properties kafkaProps = new Properties();
        kafkaProps.put("zk.connect", "localhost:2182");
        kafkaProps.put("zk.connectiontimeout.ms", "1000000");
        kafkaProps.put("groupid", "storm_group");

        return new KafkaSpout(kafkaProps, "test");
    }
}