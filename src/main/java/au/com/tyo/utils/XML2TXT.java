/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/*
 * @author Eric Tang
 * 
 */
public class XML2TXT {

	private static XML2TXT instance = null;
	
	private static final String TAG_START_OPEN = "<";
	private static final String TAG_END_OPEN = "</";
	private static final char TAG_CLOSE = '>';
	
	public XML2TXT() {
		
	}
	
	public static XML2TXT getInstance() {
		if (instance == null)
			instance = new XML2TXT();
		return instance;
	}
	
	@SuppressWarnings("finally")
	public static int textLength(byte[] bytes, int byteOffset, int length) {
		byte[] newBytes = new byte[length];
		String source = "";
		try {
			System.arraycopy(bytes, byteOffset, newBytes, 0, length);
			source = new String(newBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} 
		catch (Exception ex) {
//			ex.printStackTrace();
			throw ex;
		}
		finally {
			return source.length();
		}
	}
	
	public static int byteOffsetToTextOffset(byte[] bytes, int byteOffset) {
		return textLength(bytes, 0, byteOffset);
	}
	
	public static String replaceNonAlphabet(String source, String with)
	{
		source.replaceAll("[\\W]", with);
		return source;
	}
	
	public static String cleanTag(String content/*, String tagName*/) {
		int pos = 0;
		int pre_pos = 0;
		StringBuffer sb = new StringBuffer();
		pos = content.indexOf(TAG_START_OPEN, pre_pos);
		if (pos > -1) {
			sb.append(content.substring(pre_pos, pos));
			while(pos > -1 && pos < content.length()) {
				while (pos < (content.length() - 1 ) && content.charAt(pos) != TAG_CLOSE)
					++pos;
//				if (pos < content.length())
					++pos;
				
				pre_pos = pos;
				
				pos = content.indexOf(TAG_START_OPEN, pre_pos);
				if (pos != -1)		
					sb.append(content.substring(pre_pos, pos));
			}
			if (pos < 0 && pre_pos >= 0)
				sb.append(content.substring(pre_pos));
			return sb.toString();
		}
		return content;
	}
	

	public static byte[] clean(byte[] bytes) {
		return clean(bytes, 0, bytes.length);
	}
	
	/*
	 * This snippet code is written by Andrew Trotman initially
	 */
	public static byte[] clean(byte[] file, int index, int end)
	{
		//byte[] ch; //, *from, *to;
		/*
			remove XML tags and remove all non-alnum (but keep case)
		*/
		//ch = file;
		int count = index;
		while (count < end && count < file.length)
			{
			byte ch = file[count];
			if (ch == '<')			// then remove the XML tags
				{
				while (file[count] != '>')
					file[count++] = ' ';
				file[count] = ' '; // replace >
				}
//			else if (!isalnum(ch))	// then remove it
//				ch++ = ' ';
//			else
//				{
//				if (lower_case_only)
//					{
////					ch = (char)tolower(ch);
//					ch++;
//					}
//				else
//					ch++;
//				}
			else
				++count;
			}


	/*
		now remove multiple, head, and tail spaces.
	*/
//		int offset = 0;
//		int length = file.length;
//		while (Character.isWhitespace(file[offset]) && offset < length)
//			++offset;
//		while (Character.isWhitespace(file[length - 1]) && length > 0)
//			--length;
//		length -= offset;
//		byte[] result = new byte[length];
//		System.arraycopy(file, offset, result, 0, length);
//		return result;
		return file;
	}
	
	public static String cleanAllTags(byte[] file)
	{
		String fulltext = null;
		try {
			fulltext = new String(file, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return cleanAllTags(fulltext);
	}
	
	public static String cleanAllTags(String fulltext)
	{
		return cleanAllTags(fulltext, false);
	}
	
	public static String cleanAllTags(String fulltext, boolean replaceWithSpace)
	{
		StringBuffer sb = new StringBuffer();
		int count = 0;
		while (count < fulltext.length())
		{
			char ch = fulltext.charAt(count);
			if (ch == '<')			// then remove the XML tags
				{
				while (fulltext.charAt(count++) != '>') {
					if (replaceWithSpace)
						sb.append(' ');
				}
				
				if (replaceWithSpace)
					sb.append(' '); // replace >
				}
			else {
				sb.append(ch);
				++count;
			}
		}
		return sb.toString();
	}
		
	private byte[] read(String xmlfile) {
	    int size;
	    byte[] bytes = null;
		try {
			FileInputStream fis = new FileInputStream(xmlfile);
			size = fis.available();
		    bytes    = new byte[size];
		    fis.read(bytes, 0, size);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytes;
	}
	
	public byte[] convertFile(String xmlfile) {
		return clean(read(xmlfile));
	}
	
	public String toText(String xmlfile) {
		return cleanAllTags(read(xmlfile));
	}
	
	public byte[] convert(String xml) {
		return clean(xml.getBytes());
	}
	
	public String getText(String xmlfile) {
		String text = null;
		try {
			text = new String(convert(xmlfile), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public String getXmlFileText(String xmlfile) {
		String text = null;
		try {
			text = new String(convertFile(xmlfile), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	static void usage() {
		System.out.println("Usage: ");
		System.out.println("	XML2TXT [-o:offset:length] input_xml");
		System.out.println("		return the text with the given offset and length.");
		System.out.println("Or ");
		System.out.println("	XML2TXT [-O:offset:length] input_xml");
		System.out.println("		return the text with the given character offset and length.");
		System.out.println("Or ");		
		System.out.println("	XML2TXT input_xml");
		System.out.println("		remove all the tags");
		//System.out.println("			[-r] -r replace the non-alphabet characters");
		System.exit(-1);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			usage();
		}
		int offset = 0, length = -1;
		XML2TXT converter = new XML2TXT();
		String xmlfile = null;
		
		byte[] bytes = null;
		
		try {
			if (args[0].charAt(0) == '-')
			{
				if (args.length < 2 || args[0].length() < 2 || (args[0].charAt(1) != 'o' && args[0].charAt(1) != 'O'))
					usage();
				int pos = 0, pos2 = 0;
	        	if ((pos = args[0].indexOf(":", pos)) != -1){
	        		++pos;
	        		if ((pos2 = args[0].indexOf(":", pos)) != -1) { 
	        			offset = Integer.valueOf(args[0].substring(pos, pos2)).intValue();
	        			length = Integer.valueOf(args[0].substring(pos2 + 1)).intValue();
	        		}
	        		else
	        			offset = Integer.valueOf(args[0].substring(pos + 1)).intValue();
	        	}
	        	System.err.printf("Showing offset: %d with length %d\n", (Object [])new Integer[] {new Integer(offset), new Integer(length)});
	        	xmlfile = args[1];
	        	
	        	bytes = converter.read(xmlfile);
	        	String text = null;
	        	if (args[0].charAt(1) == 'o') {
		    		if (length == -1)
		    			length = bytes.length;
		    		
		    		byte[] result = new byte[length];
		    		System.arraycopy(bytes, offset, result, 0, length);
		    		text = new String(result, "UTF-8");
	        	}
	        	else {
	        		String fulltext = new String(bytes, "UTF-8");
		    		if (length == -1)
		    			text = fulltext.substring(offset);
		    		else
		    			text = fulltext.substring(offset, offset + length);
	        	}
    			System.out.println("Text:\"" + text + "\"");
	  
			}			
			else { 
				xmlfile = args[0];
				bytes = converter.convertFile(xmlfile);
				System.out.println(new String(bytes, "UTF-8"));
			}
  		}
		catch (UnsupportedEncodingException e) {
 			// TODO Auto-generated catch block
			e.printStackTrace();
 		}

	}
}
