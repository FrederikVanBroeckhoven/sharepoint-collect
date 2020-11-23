package app.view.extras;

import java.awt.Image;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import javax.swing.ImageIcon;

import app.Main;

public class IconSet {

	private static final String ICONS_PATH = "resources/images/";
	
	public static final String ICON_FILE_ERROR = "file-error.png" ;
	public static final String ICON_FILE_OK = "file-ok.png"    ;
	public static final String ICON_LOGIN = "login.png"      ;
	public static final String ICON_LOGOUT = "logout.png"     ;
	public static final String ICON_OPEN = "open-folder.png";
	public static final String ICON_STOP = "stop.png";	
	public static final String ICON_INFO = "info.png";	
	public static final String ICON_OK = "ok.png";	
	public static final String ICON_LIST = "list.png";	
	public static final String ICON_STUDY = "study.png";	
	public static final String ICON_USER = "user.png";	
	public static final String ICON_COMPOUND = "compound.png";	
	
	private static class IconId {
		
		public String name;
		public Integer size;
		
		public IconId(String name, Integer size) {
			this.name = name;
			this.size = size;
		}
		
		@Override
		public boolean equals(Object obj) {
			return this.equals((IconId)obj);
		}

		public boolean equals(IconId si) {
			return size == si.size && name.equals(si.name);
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(size, name);
		}
		
	}
	
	private static IconSet instance;
	
	public static IconSet getInstance() {
		if(instance == null) {
			instance = new IconSet();
		}
		return instance;
	}
	
	private Map<IconId, ImageIcon> icons;
	
	private IconSet() {
		icons = new Hashtable<IconId, ImageIcon>();
	}

	public ImageIcon getImageIcon(String name, int s) {
		IconId key = new IconId(name, s);
		
		ImageIcon icon = icons.get(key);
		if(icon == null) {
			URL url = Main.class.getClassLoader().getResource(ICONS_PATH + name);
			icon = loadImage(url, s, s);
			icons.put(key, icon);
		}
		
		return icon;
	}
	
	public static ImageIcon getByName(String name, int s) {
		return getInstance().getImageIcon(name, s);		
	}
	
	public static ImageIcon getFileOk(int s) {
		return getByName(ICON_FILE_OK, s);
	}

	public static ImageIcon getFileError(int s) {
		return getByName(ICON_FILE_ERROR, s);
	}

	public static ImageIcon getLogin(int s) {
		return getByName(ICON_LOGIN, s);
	}

	public static ImageIcon getLogout(int s) {
		return getByName(ICON_LOGOUT, s);
	}

	public static ImageIcon getOpen(int s) {
		return getByName(ICON_OPEN, s);
	}

	public static ImageIcon getStop(int s) {
		return getByName(ICON_STOP, s);
	}

	public static ImageIcon getInfo(int s) {
		return getByName(ICON_INFO, s);
	}
	
	public static ImageIcon getOk(int s) {
		return getByName(ICON_OK, s);
	}
	
	public static ImageIcon getList(int s) {
		return getByName(ICON_LIST, s);
	}

	public static ImageIcon getStudy(int s) {
		return getByName(ICON_STUDY, s);
	}

	public static ImageIcon getUser(int s) {
		return getByName(ICON_USER, s);
	}

	public static ImageIcon getCompound(int s) {
		return getByName(ICON_COMPOUND, s);
	}

	
	private static ImageIcon loadImage(URL url, int sizex, int sizey) {
		ImageIcon icon = new ImageIcon(url);
		Image image = icon.getImage();
		Image scaled = image.getScaledInstance(sizex, sizey, Image.SCALE_SMOOTH);
		return new ImageIcon(scaled);
	}
	
}
