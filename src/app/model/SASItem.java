package app.model;

import java.net.URL;
import java.util.Objects;

import com.google.gson.annotations.JsonAdapter;

@JsonAdapter(SASItemJsonDeserializer.class)
public class SASItem {

	public static class ListLocator {

		public URL url;
		public String name;

		public ListLocator(URL url, String name) {
			this.url = url;
			this.name = name;
		}

		@Override
		public int hashCode() {
			return Objects.hash(url, name);
		}

		@Override
		public boolean equals(Object obj) {
			return this.equals((ListLocator) obj);
		}

		public boolean equals(ListLocator obj) {
			return url.equals(obj.url) && name.equals(obj.name);
		}

	}

	private String compound;
	private String indication;
	private String study;
	private String rEvent;
	private ListLocator listLocation;

	public SASItem(String compound, String indication, String study, String rEvent, ListLocator listLocation) {
		this.compound = compound;
		this.indication = indication;
		this.study = study;
		this.rEvent = rEvent;
		this.listLocation = listLocation;
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

	public ListLocator getListLocation() {
		return listLocation;
	}

}
