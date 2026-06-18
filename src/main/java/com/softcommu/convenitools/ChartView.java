package com.softcommu.convenitools;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@PermitAll
@Route("chart")
public class ChartView extends VerticalLayout {

    AiService aiService = null;
    TextField textField = new TextField(); 
    Button button = new Button("AIグラフ生成");

    Double[] data1 = {10.0, 41.0, 35.0, 51.0, 49.0, 62.0};
    Double[] data2 = {20.0, 32.0, 45.0, 42.0, 38.0, 46.0};
    Double[] data3 = {5.0, 10.0, 20.0, 35.0, 40.0, 80.0};

    public ChartView(AiService aiService) {
        
        this.aiService = aiService;
        this.textField.setValue("日経平均、ダウ、S&P500の過去5年間のグラフ");
        this.textField.setWidthFull();
        this.button.setWidthFull();
        this.button.addClickListener(click -> onButtonClicked());
        this.setWidthFull();
        this.add(textField, button);
            // createLineChart(),
            // createColumnChart(),
            // createAreaChart(),
            // createPieChart()
    }

    private void onButtonClicked(){

        var prompt = 
        """
        折れ線グラフ用のデータを作りたいです。" + 
        X軸のlabel、categoriesの配列要素数,categoriesの各要素の文字列、"+
        Y軸のlabel、" +
        seriesのname、date配列の要素数、内容を考えてください" +              
        下記のようなJSONフォーマットで答えを返してください。
        {
            "title": "Line Chart",
            "xAxis": {
                "label": "Month",
                "categories": ["Jan", "Feb", "Mar", "Apr", "May", "Jun"]
            },
            "yAxis": {
                "label": "Values"
            },
            "series": [
                {
                "name": "Data 1",
                "data": [10, 41, 35, 51, 49, 62]
                },
                {
                "name": "Data 2",
                "data": [20, 32, 45, 42, 38, 46]
                },
                {
                "name": "Data 3",
                "data": [5, 10, 20, 35, 40, 80]
                }
            ]
        }
        作りたいデータは下記のとおりです:"
        """;

        // UIスレッドでないバックグラウンドスレッドからUIを更新するため、現在のUIを取得しておく。
        var ui = getUI().orElseThrow();
        var question = prompt + textField.getValue();

        // AIへの問い合わせは時間がかかるため、別スレッドで実行してUIスレッドをブロックしない。
        new Thread(() -> {

            var jsonText = aiService.getChatAnswerByJson(question);
            System.out.println(jsonText);

            // UIの更新は必ずUIスレッドで行う必要があるため、ui.access()で囲む。
            ui.access(() -> add(createLineChartJson(jsonText)));

        }).start();
    }

    /* ---------- Line Chart (JSON) ---------- */
    private Chart createLineChartJson(String json) {

        ChartData chartData;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            chartData = objectMapper.readValue(json, ChartData.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("JSONの解析に失敗しました。", e);
        }

        Chart chart = new Chart(ChartType.LINE);
        Configuration config = chart.getConfiguration();
        config.setTitle(chartData.title);

        XAxis xAxis = new XAxis();
        if (chartData.xAxis != null) {
            if (chartData.xAxis.label != null) {
                xAxis.setTitle(chartData.xAxis.label);
            }
            if (chartData.xAxis.categories != null) {
                xAxis.setCategories(chartData.xAxis.categories.toArray(new String[0]));
            }
        }
        config.addxAxis(xAxis);

        if (chartData.yAxis != null && chartData.yAxis.label != null) {
            YAxis yAxis = new YAxis();
            yAxis.setTitle(chartData.yAxis.label);
            config.addyAxis(yAxis);
        }

        if (chartData.series != null) {
            for (ChartData.Series s : chartData.series) {
                Double[] values = s.data == null
                        ? new Double[0]
                        : s.data.toArray(new Double[0]);
                config.addSeries(new ListSeries(s.name, values));
            }
        }

        chart.setWidthFull();
        chart.setHeight("600px");

        return chart;
    }

    /* ---------- Line Chart ---------- */
    private Chart createLineChart() {
        Chart chart = new Chart(ChartType.LINE);
        Configuration config = chart.getConfiguration();
        config.setTitle("Line Chart");

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun");
        config.addxAxis(xAxis);

        config.addSeries(new ListSeries("Data 1", data1));
        config.addSeries(new ListSeries("Data 2", data2));
        config.addSeries(new ListSeries("Data 3", data3));

        chart.setWidthFull();
        chart.setHeight("600px");

        return chart;
    }

    /* ---------- Column Chart ---------- */
    private Chart createColumnChart() {
        Chart chart = new Chart(ChartType.COLUMN);
        Configuration config = chart.getConfiguration();
        config.setTitle("Column Chart");

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun");
        config.addxAxis(xAxis);

        config.addSeries(new ListSeries("Data 1", data1));
        config.addSeries(new ListSeries("Data 2", data2));

        chart.setWidthFull();
        chart.setHeight("600px");

        return chart;
    }

    /* ---------- Area Chart ---------- */
    private Chart createAreaChart() {
        Chart chart = new Chart(ChartType.AREA);
        Configuration config = chart.getConfiguration();
        config.setTitle("Area Chart");

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun");
        config.addxAxis(xAxis);

        PlotOptionsArea options = new PlotOptionsArea();
        options.setStacking(Stacking.NORMAL);

        ListSeries s1 = new ListSeries("Data 1", data1);
        s1.setPlotOptions(options);

        ListSeries s2 = new ListSeries("Data 2", data2);
        s2.setPlotOptions(options);

        config.addSeries(s1);
        config.addSeries(s2);

        chart.setWidthFull();
        chart.setHeight("600px");

        return chart;
    }

    /* ---------- Pie Chart ---------- */
    private Chart createPieChart() {
        Chart chart = new Chart(ChartType.PIE);
        Configuration config = chart.getConfiguration();
        config.setTitle("Pie Chart");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("Jan", data1[0]));
        series.add(new DataSeriesItem("Feb", data1[1]));
        series.add(new DataSeriesItem("Mar", data1[2]));
        series.add(new DataSeriesItem("Apr", data1[3]));
        series.add(new DataSeriesItem("May", data1[4]));
        series.add(new DataSeriesItem("Jun", data1[5]));

        config.setSeries(series);

        chart.setWidthFull();
        chart.setHeight("600px");

        return chart;
    }
}