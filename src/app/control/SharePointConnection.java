package app.control;

import java.io.File;
import java.net.URL;

import dashboard.connect.DataSource;
import dashboard.connect.generic.GenericCredentials;
import dashboard.connect.generic.GenericDataSource;

public final class SharePointConnection {

	static {
		GenericCredentials.setRoot(new File(File.separator + "home" + File.separator + "frederik"
				+ File.separator + "Downloads" + File.separator + "splists examples"));		
	}
	
	public enum State {
		INIT,
		CONNECTED,
		FAIL,
		WAITING
	}

	public URL url;
	public DataSource source;
	public SharePointConnection.State state;

	public SharePointConnection(URL url) {
		this.url = url;
//		this.source = new SharePointDataSource(url.getProtocol() + "://" + url.getHost(), url.getPath());
		this.source = new GenericDataSource();
		this.state = State.INIT;
	}

	@Override
	public int hashCode() {
		return url.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this.equals((SharePointConnection) obj);
	}

	public boolean equals(SharePointConnection conn) {
		return url.equals(conn.url);
	}

}