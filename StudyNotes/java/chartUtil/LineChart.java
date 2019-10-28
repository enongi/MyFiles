package com.chartUtil;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;

/**
 *
 * 折线图
 *       <p>
 *       创建图表步骤：<br/>
 *       1：创建数据集合<br/>
 *       2：创建Chart：<br/>
 *       3:设置抗锯齿，防止字体显示不清楚<br/>
 *       4:对柱子进行渲染，<br/>
 *       5:对其他部分进行渲染<br/>
 *       6:使用chartPanel接收<br/>
 *
 *       </p>
 */
public class LineChart {
    public LineChart() {
    }

//    public DefaultCategoryDataset createDataset() {
//        // 标注类别
//        String[] categories = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
//        Vector<Serie> series = new Vector<Serie>();
//        // 柱子名称：柱子所有的值集合
//        series.add(new Serie("Tokyo", new Double[] { null, null, 106.4, 129.2, 144.0, 176.0, 135.6, 148.5, 216.4, 194.1, 95.6, 54.4 }));
//        series.add(new Serie("New York", new Double[] { 83.6, null, null, null, 106.0, 84.5, 105.0, 104.3, 91.2, 83.5, 106.6, 92.3 }));
//        series.add(new Serie("London", new Double[] { 48.9, 38.8, 39.3, null, null, null, 59.0, 59.6, 52.4, 65.2, 59.3, 51.2 }));
//        series.add(new Serie("Berlin", new Double[] { 42.4, 33.2, 34.5, 39.7, 52.6, 75.5, 57.4, 60.4, 47.6, null, null, null }));
//        // 1：创建数据集合
//        DefaultCategoryDataset dataset = ChartUtils.createDefaultCategoryDataset(series, categories);
//        System.out.println(categories.length);
//        System.out.println(series.get(0).getData().size());
//        return dataset;
//    }

    public DefaultCategoryDataset createDataset(String[] categories,Vector<Serie> series) {
        // 1：创建数据集合
        DefaultCategoryDataset dataset = ChartUtils.createDefaultCategoryDataset(series, categories);
        return dataset;
    }

    public void createChart(String title,String xLabel,String imgPath,String[] categories,Vector<Serie> series) throws Exception {
        // 2：创建Chart[创建不同图形]
        JFreeChart chart = ChartFactory.createLineChart(title, "", xLabel, createDataset( categories, series));
        // 3:设置抗锯齿，防止字体显示不清楚
        ChartUtils.setAntiAlias(chart);// 抗锯齿
        // 4:对柱子进行渲染[[采用不同渲染]]
        ChartUtils.setLineRender(chart.getCategoryPlot(), false,true);//
        // 5:对其他部分进行渲染
        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
        ChartUtils.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
        ChartUtils.setYAixsRange(chart.getCategoryPlot(),series);
        ChartUtils.setDomainAxis(chart.getCategoryPlot().getDomainAxis(),categories);

        Font xfont = new Font("宋体",Font.PLAIN,50) ;// X轴
        Font yfont = new Font("宋体",Font.PLAIN,50) ;// Y轴
        Font kfont = new Font("宋体",Font.PLAIN,50) ;// 底部
        Font titleFont = new Font("隶书", Font.BOLD , 50) ; // 图片标题
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setTickLabelFont(xfont);
        domainAxis.setLabelFont(xfont);
        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(yfont);
        rangeAxis.setTickLabelFont(yfont);
        TextTitle textTitle = chart.getTitle();
        textTitle.setFont(titleFont);
        chart.getLegend().setItemFont(kfont);
        // 设置标注无边框
        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
        this.saveAsFile(chart,imgPath,628*3, 275*3);
    }

//    public ChartPanel createChart() throws Exception {
//        // 2：创建Chart[创建不同图形]
//        JFreeChart chart = ChartFactory.createLineChart("Monthly Average Rainfallbbb", "", "Rainfall (mm)", createDataset());
//        // 3:设置抗锯齿，防止字体显示不清楚
//        ChartUtils.setAntiAlias(chart);// 抗锯齿
//        // 4:对柱子进行渲染[[采用不同渲染]]
//        ChartUtils.setLineRender(chart.getCategoryPlot(), false,true);//
//        // 5:对其他部分进行渲染
//        ChartUtils.setXAixs(chart.getCategoryPlot());// X坐标轴渲染
//        ChartUtils.setYAixs(chart.getCategoryPlot());// Y坐标轴渲染
//        // 设置标注无边框
//        chart.getLegend().setFrame(new BlockBorder(Color.WHITE));
//        this.saveAsFile(chart,"C:\\\\Users\\\\caishunhui\\\\Desktop\\\\echartbbb.png",628, 275);
//        // 6:使用chartPanel接收
//        ChartPanel chartPanel = new ChartPanel(chart);
//        return chartPanel;
//    }
//
//    public static void main(String[] args) {
//        final JFrame frame = new JFrame();
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(1024, 420);
//        frame.setLocationRelativeTo(null);
//
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                // 创建图形
//                ChartPanel chartPanel = null;
//                try {
//                    chartPanel = new LineChart().createChart();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                frame.getContentPane().add(chartPanel);
//                frame.setVisible(true);
//            }
//        });
//    }

    public  void saveAsFile(JFreeChart chart, String outputPath,
                                  int weight, int height)throws Exception {
        FileOutputStream out = null;
        File outFile = new File(outputPath);
        if (!outFile.getParentFile().exists()) {
            outFile.getParentFile().mkdirs();
        }
        out = new FileOutputStream(outputPath);
        // 保存为PNG
        ChartUtilities.writeChartAsPNG(out, chart, weight, height);
        // 保存为JPEG
        // ChartUtilities.writeChartAsJPEG(out, chart, weight, height);
        out.flush();
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                // do nothing
            }

        }
    }

}