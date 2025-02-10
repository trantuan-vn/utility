import java.util.HashMap;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.streaming.Durations;
import org.apache.spark.sql.*;
import org.apache.spark.sql.functions.*;
import org.apache.spark.sql.types.*;
import scala.Tuple2;
import org.apache.spark.sql.Row;
import java.io.Serializable;
import org.apache.spark.streaming.dstream.DStream;

public final class JavaDirectKafkaXmlToHDFS {
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.err.println("Usage: JavaDirectKafkaXmlToHDFS <brokers> <groupId> <topics>\n" +
                               "  <brokers> is a list of one or more Kafka brokers\n" +
                               "  <groupId> is a consumer group name to consume from topics\n" +
                               "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }

        StreamingExamples.setStreamingLogLevels();

        String brokers = args[0];
        String groupId = args[1];
        String topics = args[2];

        // Create SparkConf and StreamingContext
        SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaXmlToHDFS");
        JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(2));
        SQLContext sqlContext = new SQLContext(jssc.sparkContext());

        Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
        kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Create direct kafka stream with brokers and topics
        JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
            jssc,
            LocationStrategies.PreferConsistent(),
            ConsumerStrategies.Subscribe(topicsSet, kafkaParams)
        );

        // Get the lines (XML data), and parse them to extract columns
        JavaDStream<Row> rows = messages.map(record -> {
            String xml = record.value();
            // Use a simple XML parser (e.g., Java XML or third-party library like JDOM, XMLBeans, etc.)
            String col1 = extractXmlColumn(xml, "column1");
            String col2 = extractXmlColumn(xml, "column2");
            String col3 = extractXmlColumn(xml, "column3");
            
            // Create a Row to represent the 3 columns
            return RowFactory.create(col1, col2, col3);
        });

        // Define the schema for the 3 columns
        StructType schema = new StructType()
            .add("col1", DataTypes.StringType)
            .add("col2", DataTypes.StringType)
            .add("col3", DataTypes.StringType);

        // Convert DStream<Row> to DStream<DataFrame>
        JavaDStream<DataFrame> dataFrames = rows.map(row -> sqlContext.createDataFrame(jssc.sparkContext().parallelize(Arrays.asList(row)), schema));

        // Accumulator to accumulate DataFrames in each batch
        final java.util.List<DataFrame> accumulatedData = new java.util.ArrayList<>();

        // Store the data in the accumulator
        dataFrames.foreachRDD(rdd -> {
            if (!rdd.isEmpty()) {
                DataFrame df = rdd.first();
                // Add the current batch DataFrame to the accumulator
                accumulatedData.add(df);
            }

            // If accumulated data exceeds a threshold, write to HDFS
            if (accumulatedData.size() >= 5) {  // e.g., write data after accumulating 5 batches
                DataFrame finalDF = accumulatedData.get(accumulatedData.size() - 1);
                // Write accumulated data to HDFS in Parquet format
                finalDF.write().mode(SaveMode.Append).parquet("hdfs://<path_to_hdfs>/test");
                // Clear the accumulator after writing
                accumulatedData.clear();
            }
        });

        // Start the computation
        jssc.start();
        jssc.awaitTermination();
    }

    // Helper function to extract a column value from XML
    private static String extractXmlColumn(String xml, String columnName) {
        // Implement XML parsing logic here to extract the value for a specific column
        // This is just an example using simple string matching; you can use a real XML parser (e.g., DOM, SAX)
        String openingTag = "<" + columnName + ">";
        String closingTag = "</" + columnName + ">";
        int startIndex = xml.indexOf(openingTag) + openingTag.length();
        int endIndex = xml.indexOf(closingTag);
        if (startIndex >= 0 && endIndex > startIndex) {
            return xml.substring(startIndex, endIndex);
        } else {
            return ""; // Return empty if the column is not found
        }
    }
}
