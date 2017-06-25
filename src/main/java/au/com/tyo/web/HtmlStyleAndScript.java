/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.web;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class HtmlStyleAndScript {
	
	public static String[] STYLES_N_SCRIPTS = {"styles.css", "common.js"};
	
	public static String[] THEME_DEPENDANT_CSS = {"theme.css"};
	
	public static String[] LANDSCAPE_DEPENDANT_CSS = {"image.css"};
	
	public static String[] DEVICE_DEPENDANT_JS = {"platform.js"};
	
	private String themeName;
	
	private String path;

	private boolean isLandscapeMode;
	
	private boolean isTablet;
	
	private String device;

//	public HtmlStyleAndScript(String path) {
//		this(path, true, null, false;
//	}
	
	public HtmlStyleAndScript(String path, String themeName, boolean isLandscapeMode, boolean isTablet, String device) {
		super();
		this.path = path;
		this.themeName = themeName;
		this.isLandscapeMode = isLandscapeMode;
		this.isTablet = isTablet;
		this.device = device;
	}
	
	private void buildDeviceDependant(StringBuffer sb, Set<String> set) {
		for (String str : set)
			sb.append(this.makeJsPathFile((device != null && device.length() > 0) ? device : null, str));
	}
	
	private void buildThemeDependant(StringBuffer sb, Set<String> themeDependents) {
		for (String str : themeDependents) 
			sb.append(this.makeCssPathFile((themeName != null && themeName.length() > 0) ? themeName : null, str));
	}
	
	private void buildWideScreenDependant(StringBuffer sb, Set<String> orientationDependents) {
		for (String str : orientationDependents) 
			sb.append(this.makeCssPathFile(isLandscapeMode ? "land" : null, str));
	}
	
//	private void buildStylesAndScripts(StringBuffer sb, String[] stylesAndScripts) {
//		for (int i = 0; i < stylesAndScripts.length; ++i) {
//			if (stylesAndScripts[i].endsWith(".css")) 
//					sb.append(PageBuilder.createCss(path + "css" + File.separator + stylesAndScripts[i]));
//			else 
//				sb.append(PageBuilder.createJavaScript(path + "js" + File.separator + stylesAndScripts[i]));
//		}
//	}
	
	private String makeJsPathFile(String special, String name) {
		String jsPath = "js";
		if (special != null)
			jsPath = jsPath + File.separator + special;
		return PageBuilder.createJavaScript(path + jsPath + File.separator + name);
	}
	
	private String makeCssPathFile(String special, String name) {
		if (special != null)
			return	PageBuilder.createCss(path + "css-" + special + File.separator + name); 
		return PageBuilder.createCss(path + "css" + File.separator + name);
	}
	
	public String build(String[] stylesAndScripts, String[] themeCss,
			String[] orientationDependents, String inlineCss, String[] deviceDepdendents) {
		return build(stylesAndScripts, 
				themeCss != null ? new HashSet<String>(Arrays.asList(themeCss)) : null, 
				orientationDependents != null ? new HashSet<String>(Arrays.asList(orientationDependents)) : null, 
				inlineCss,
				deviceDepdendents != null ? new HashSet<String>(Arrays.asList(deviceDepdendents)) : null);
	}
	
	public String build(String[] stylesAndScripts, Set<String> themeDependents, Set<String> orientationDependents, String inlineCss, Set<String> deviceDepdendents) {
		StringBuffer sb = new StringBuffer();
	
		if (stylesAndScripts != null)
			for (int i = 0; i < stylesAndScripts.length; ++i) {
				String what = stylesAndScripts[i];
				if ((themeDependents != null && themeDependents.contains(what)) ||
						(orientationDependents != null && orientationDependents.contains(what)))
						continue;
				
				if (what.endsWith(".css")) 
						sb.append(PageBuilder.createCss(path + "css" + File.separator + what));
				else 
					sb.append(PageBuilder.createJavaScript(path + "js" + File.separator + what));
			}
//			this.buildStylesAndScripts(sb, stylesAndScripts);
		
		this.buildThemeDependant(sb, new HashSet<String>(Arrays.asList(THEME_DEPENDANT_CSS)));
		if (themeDependents != null)
			this.buildThemeDependant(sb, themeDependents);
		
		this.buildWideScreenDependant(sb, new HashSet<String>(Arrays.asList(LANDSCAPE_DEPENDANT_CSS)));
		if (orientationDependents != null)
			this.buildWideScreenDependant(sb, orientationDependents);
		
		if (inlineCss != null)
			createStlyeElement(sb, inlineCss);
		
		if (deviceDepdendents != null)
			this.buildDeviceDependant(sb, deviceDepdendents);
		
		return sb.toString();
	}
	
//	private void buildInlineCss(StringBuffer sb, String[] inlineCss) {
//		for (String css : inlineCss)
//			createStlyeElement(sb, css);
//	}

	public String build(String[] stylesAndScripts) {
		return build(stylesAndScripts, THEME_DEPENDANT_CSS, LANDSCAPE_DEPENDANT_CSS, null, DEVICE_DEPENDANT_JS);
	}
	
	public String build() {
		return build(STYLES_N_SCRIPTS);
	}
	
	public static void createStlyeElement(StringBuffer sb, String css) {
		sb.append("<style type=\"text/css\">\n");
		
		sb.append(css);
		
		sb.append("</style>\n");
	}

}
