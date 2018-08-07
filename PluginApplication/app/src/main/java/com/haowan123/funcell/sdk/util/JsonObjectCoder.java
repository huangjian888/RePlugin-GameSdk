package com.haowan123.funcell.sdk.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonObjectCoder {

	@SuppressWarnings("unchecked")
	public static Map<String, ?> decode(String input, Void condition) {
		Map<String, Object> decodedMap;
		try {
			JSONObject jsonObject = new JSONObject(input);
			decodedMap = new HashMap<String, Object>();

			for (Iterator<String> keysIterator = jsonObject.keys(); keysIterator
					.hasNext();) {
				String key = keysIterator.next();

				Object value = jsonObject.get(key);

				if (value instanceof JSONObject) {
					decodedMap.put(key, decode(value.toString(), condition));

				} else if (value instanceof JSONArray) {
					decodedMap.put(key,
							JsonListCoder.decode(value.toString(), condition));
				} else {
					decodedMap.put(key, value);
				}

			}
		} catch (JSONException e) {

			e.printStackTrace();
			return null;
		}
		return decodedMap;
	}

	public static String encode(Map<String, ?> input, Void condition) {

		return new JSONObject(input).toString();
	}

}
