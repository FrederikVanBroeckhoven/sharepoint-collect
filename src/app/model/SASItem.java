package app.model;

import com.google.gson.annotations.SerializedName;

public class SASItem {

	@SerializedName("compound")
	private String compound;
	@SerializedName("indication")
	private String indication;
	@SerializedName("study")
	private String study;
	@SerializedName("revent")
	private String rEvent;
	@SerializedName("splist")
	private String spList;
	
	public SASItem() {
	}

	public String getCompound() {
		return compound;
	}

	public String getIndication() {
		return indication;
	}

	public String getStudy() {
		return study;
	}

	public String getREvent() {
		return rEvent;
	}

	public String getSpList() {
		return spList;
	}
	
}
