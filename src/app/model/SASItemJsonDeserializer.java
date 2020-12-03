package app.model;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import app.model.SASItem.ListLocator;

public class SASItemJsonDeserializer implements JsonDeserializer<SASItem> {

	private static ListLocator parseURL(String url) {
		String[] subList = url.split("\\/?Lists\\/?");
		try {
			return new ListLocator(
					new URL(subList[0]),
					subList[1].replaceFirst("\\/+$", ""));
		} catch (MalformedURLException e) {
			throw new JsonParseException(e);
		}
	}

	@Override
	public SASItem deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {

		JsonObject jobj = json.getAsJsonObject();

		String compound = jobj.get("compound").getAsString();
		String indication = jobj.get("indication").getAsString();
		String study = jobj.get("study").getAsString();
		String revent = jobj.get("revent").getAsString();
		ListLocator splist = parseURL(jobj.get("splist").getAsString());

		return new SASItem(compound, indication, study, revent, splist);
	}

}
