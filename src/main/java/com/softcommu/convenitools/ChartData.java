package com.softcommu.convenitools;

import java.util.List;

public class ChartData {
    public String title;
    public XAxis xAxis;
    public YAxis yAxis;
    public List<Series> series;

    public static class XAxis {
        public String label;
        public List<String> categories;
    }

    public static class YAxis {
        public String label;
    }

    public static class Series {
        public String name;
        public List<Double> data;
    }
}
