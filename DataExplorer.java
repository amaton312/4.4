import core.data.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Color;
import java.io.File;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class DataExplorer {

    public static void main(String[] args) {
        DataSource ds = DataSource.connect("video_game_sales_clean.csv");
        ds.load();

        ArrayList<Game> allGames = ds.fetchList(Game.class, "Name", "Platform", "Year_of_Release", "Genre", "Publisher",
            "NA_Sales", "EU_Sales", "JP_Sales", "Other_Sales", "Global_Sales",
            "Critic_Score", "Critic_Count", "User_Score", "User_Count",
            "Developer", "Rating");        

        Stats stats = computeCorrelation(allGames);
        System.out.println("linear correlation in critic score vs global sales: " + String.format("%.3f", stats.r));
        System.out.println("mean sritic score: " + String.format("%.2f", stats.meanX) + ", stdev: " + String.format("%.2f", stats.sdX));
        System.out.println("mean global sales: " + String.format("%.2f", stats.meanY) + ", stdev    : " + String.format("%.2f", stats.sdY));

        try {
            String plotPath = "critic_vs_global.png";
            createScatterPlot(allGames, plotPath);
            System.out.println("made scatter plot: " + plotPath);
        } catch (Exception e) {
            System.out.println("Could not create plot: " + e.getMessage());
        }
    }



    private static Stats computeCorrelation(List<Game> games) {
        List<double[]> points = games.stream()
                .map(DataExplorer::parsePoint)
                .filter(p -> p != null)
                .collect(Collectors.toList());

        int n = points.size();
        if (n < 2) return new Stats(0, 0, 0, 0, 0, 0);

        double[] xs = new double[n];
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            xs[i] = points.get(i)[0];
            ys[i] = points.get(i)[1];
        }

        double r = correlationCoefficient(xs, ys, n);

        double sumX = 0, sumY = 0, sumXX = 0, sumYY = 0;
        for (int i = 0; i < n; i++) {
            sumX += xs[i];
            sumY += ys[i];
            sumXX += xs[i] * xs[i];
            sumYY += ys[i] * ys[i];
        }
        double meanX = sumX / n;
        double meanY = sumY / n;
        double sdX = Math.sqrt(Math.max(sumXX / n - meanX * meanX, 0));
        double sdY = Math.sqrt(Math.max(sumYY / n - meanY * meanY, 0));

        return new Stats(n, r, meanX, sdX, meanY, sdY);
    }

    static double correlationCoefficient(double[] X, double[] Y, int n) {
        double sum_X = 0, sum_Y = 0, sum_XY = 0;
        double squareSum_X = 0, squareSum_Y = 0;

        for (int i = 0; i < n; i++) {
            sum_X += X[i];
            sum_Y += Y[i];
            sum_XY += X[i] * Y[i];
            squareSum_X += X[i] * X[i];
            squareSum_Y += Y[i] * Y[i];
        }

        double numerator = n * sum_XY - sum_X * sum_Y;
        double denom = Math.sqrt((n * squareSum_X - sum_X * sum_X) * (n * squareSum_Y - sum_Y * sum_Y));
        if (denom == 0) return 0;
        return numerator / denom;
    }

    private static class Stats {
        int n;
        double r;
        double meanX, sdX;
        double meanY, sdY;
        Stats(int n, double r, double meanX, double sdX, double meanY, double sdY) {
            this.n = n; this.r = r; this.meanX = meanX; this.sdX = sdX; this.meanY = meanY; this.sdY = sdY;
        }
    }
    private static void createScatterPlot(List<Game> games, String outputPath) throws Exception {
        List<double[]> points = games.stream()
                .map(g -> parsePoint(g))
                .filter(p -> p != null)
                .collect(Collectors.toList());

        if (points.isEmpty()) {
            throw new IllegalStateException("No numeric data to plot.");
        }

        XYSeries series = new XYSeries("Critic vs Global");
        for (double[] p : points) {
            series.add(p[0], p[1]);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot(
                "Critic Score vs Global Sales",
                "Critic Score",
                "Global Sales (millions)",
                dataset
        );

        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0.0, 100.0);

        double maxY = points.stream().mapToDouble(p -> p[1]).max().orElse(0);
        double padY = maxY * 0.05 + 1e-6;
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setRange(0.0, maxY + padY);

        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(52, 120, 246, 180));

        ChartUtils.saveChartAsPNG(new File(outputPath), chart, 900, 600);
    }


    private static double[] parsePoint(Game g) {
        double critic = Double.parseDouble(g.Critic_Score);
        double global = Double.parseDouble(g.Global_Sales);
        return new double[]{critic, global};
    }
}