package com.spazomatic.jobyjob.nosql.entities;

public class EstimatedTime {

	private Double timeSpan;
	private String timeMetric;
	
	public Double getTimeSpan() {
		return timeSpan;
	}
	public void setTimeSpan(Double timeSpan) {
		this.timeSpan = timeSpan;
	}
	public String getTimeMetric() {
		return timeMetric;
	}
	public void setTimeMetric(String timeMetric) {
		this.timeMetric = timeMetric;
	}
	
}
