import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.evaluation.RegressionEvaluator;

public class PredictThresholdWithLatencyAdjustment {
    public static void main(String[] args) throws Exception {
        // Tạo Spark session
        SparkSession spark = SparkSession.builder()
                .appName("Predict Threshold with Latency Adjustment")
                .getOrCreate();

        // Giả sử dữ liệu đã được tải vào DataFrame
        Dataset<Row> historicalData = spark.read().format("csv")
                .option("header", "true")
                .load("hdfs://<path_to_hdfs>/historical_data.csv");

        // Chọn các đặc trưng cần thiết (cpuUsage, batchSize, numMessages, writeLatency)
        Dataset<Row> data = historicalData.selectExpr(
                "cpuUsage", 
                "batchSize", 
                "numMessages", 
                "writeLatency",  // Đưa writeLatency vào như một đặc trưng
                "threshold as label"  // 'threshold' là giá trị ngưỡng cần dự đoán
        );

        // Sử dụng VectorAssembler để kết hợp các đặc trưng thành một vector
        VectorAssembler assembler = new VectorAssembler()
                .setInputCols(new String[]{"cpuUsage", "batchSize", "numMessages", "writeLatency"})
                .setOutputCol("features");

        // Áp dụng hồi quy tuyến tính
        LinearRegression lr = new LinearRegression()
                .setLabelCol("label")
                .setFeaturesCol("features");

        // Xây dựng pipeline
        Pipeline pipeline = new Pipeline().setStages(new org.apache.spark.ml.PipelineStage[] {assembler, lr});

        // Tách dữ liệu thành training và testing data
        Dataset<Row>[] splits = data.randomSplit(new double[] {0.8, 0.2}, 1234);
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testData = splits[1];

        // Huấn luyện mô hình
        PipelineModel model = pipeline.fit(trainingData);

        // Dự đoán trên dữ liệu test
        Dataset<Row> predictions = model.transform(testData);

        // Đánh giá mô hình
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("label")
                .setPredictionCol("prediction")
                .setMetricName("rmse");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("Root Mean Squared Error (RMSE) = " + rmse);

        // Dự đoán ngưỡng cho các giá trị mới
        Dataset<Row> newData = spark.createDataFrame(Arrays.asList(
                RowFactory.create(60, 250, 100, 200) // Giả sử các giá trị mới của cpuUsage, batchSize, numMessages, writeLatency
        ), data.schema());
        Dataset<Row> newPredictions = model.transform(newData);

        // Đọc kết quả dự đoán
        double predictedThreshold = newPredictions.select("prediction").head().getDouble(0);

        // Điều chỉnh ngưỡng nếu writeLatency quá cao
        double adjustedThreshold = adjustThreshold(predictedThreshold, newData.head().getDouble(3)); // Giả sử giá trị writeLatency là cột thứ 3
        System.out.println("Adjusted Threshold: " + adjustedThreshold);

        spark.stop();
    }

    // Hàm điều chỉnh ngưỡng khi writeLatency quá cao
    public static double adjustThreshold(double predictedThreshold, double writeLatency) {
        // Nếu writeLatency > 150, giảm ngưỡng threshold (giảm 10% trong trường hợp này)
        if (writeLatency > 150) {
            predictedThreshold *= 0.9;
        }
        return predictedThreshold;
    }
}
