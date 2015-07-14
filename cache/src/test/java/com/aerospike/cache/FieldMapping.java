package com.aerospike.cache;
import java.util.HashMap;

public final class FieldMapping {
	public final static int FIELD_IX_MEDIA = 1;
	public final static String FULL_FIELD_NAME_MEDIA = "media";
	public final static int FIELD_IX_IMAGES = 2;
	public final static String FULL_FIELD_NAME_IMAGES = "images";
	public final static int FIELD_IX_PLAYER = 3;
	public final static String FULL_FIELD_NAME_PLAYER = "player";
	public final static int FIELD_IX_URI = 4;
	public final static String FULL_FIELD_NAME_URI = "uri";
	public final static int FIELD_IX_TITLE = 5;
	public final static String FULL_FIELD_NAME_TITLE = "title";
	public final static int FIELD_IX_WIDTH = 6;
	public final static String FULL_FIELD_NAME_WIDTH = "width";
	public final static int FIELD_IX_HEIGHT = 7;
	public final static String FULL_FIELD_NAME_HEIGHT = "height";
	public final static int FIELD_IX_FORMAT = 8;
	public final static String FULL_FIELD_NAME_FORMAT = "format";
	public final static int FIELD_IX_DURATION = 9;
	public final static String FULL_FIELD_NAME_DURATION = "duration";
	public final static int FIELD_IX_SIZE = 10;
	public final static String FULL_FIELD_NAME_SIZE = "size";
	public final static int FIELD_IX_BITRATE = 11;
	public final static String FULL_FIELD_NAME_BITRATE = "bitrate";
	public final static int FIELD_IX_PERSONS = 12;
	public final static String FULL_FIELD_NAME_PERSONS = "persons";
	public final static int FIELD_IX_COPYRIGHT = 13;
	public final static String FULL_FIELD_NAME_COPYRIGHT = "copyright";


	public static final HashMap<String,Integer> fullFieldToIndex = new HashMap<String,Integer>();
	static {
		fullFieldToIndex.put(FULL_FIELD_NAME_MEDIA, FIELD_IX_MEDIA);
		fullFieldToIndex.put(FULL_FIELD_NAME_IMAGES, FIELD_IX_IMAGES);
		fullFieldToIndex.put(FULL_FIELD_NAME_PLAYER, FIELD_IX_PLAYER);
		fullFieldToIndex.put(FULL_FIELD_NAME_URI, FIELD_IX_URI);
		fullFieldToIndex.put(FULL_FIELD_NAME_TITLE, FIELD_IX_TITLE);
		fullFieldToIndex.put(FULL_FIELD_NAME_WIDTH, FIELD_IX_WIDTH);
		fullFieldToIndex.put(FULL_FIELD_NAME_HEIGHT, FIELD_IX_HEIGHT);
		fullFieldToIndex.put(FULL_FIELD_NAME_FORMAT, FIELD_IX_FORMAT);
		fullFieldToIndex.put(FULL_FIELD_NAME_DURATION, FIELD_IX_DURATION);
		fullFieldToIndex.put(FULL_FIELD_NAME_SIZE, FIELD_IX_SIZE);
		fullFieldToIndex.put(FULL_FIELD_NAME_BITRATE, FIELD_IX_BITRATE);
		fullFieldToIndex.put(FULL_FIELD_NAME_PERSONS, FIELD_IX_PERSONS);
		fullFieldToIndex.put(FULL_FIELD_NAME_COPYRIGHT, FIELD_IX_COPYRIGHT);
	}
}
