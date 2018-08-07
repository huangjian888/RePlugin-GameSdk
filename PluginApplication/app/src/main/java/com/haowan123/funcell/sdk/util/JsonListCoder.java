package com.haowan123.funcell.sdk.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonListCoder {

	public static Collection<?> decode(String input, Void condition) {
		List<Object> decodedList;
		try {
			JSONArray jsonArray = new JSONArray(input);
			decodedList = new ArrayList<Object>();

			for (int i = 0; i < jsonArray.length(); i++) {

				Object value = jsonArray.get(i);

				if (value instanceof JSONObject) {
					decodedList.add(JsonObjectCoder.decode(value.toString(),
							condition));

				} else if (value instanceof JSONArray) {
					decodedList.add(decode(value.toString(), condition));
				} else {
					decodedList.add(value);
				}

			}
		} catch (JSONException e) {

			e.printStackTrace();
			return null;
		}
		return decodedList;
	}

	public static String encode(Collection<?> input, Void condition) {

		return new JSONArray(input).toString();
	}

	public static String encodeWithListMap(List<Map<String, Object>> input, Void condition) {
		if (null == input || input.isEmpty()) {
			return null;
		}
		JSONArray jsonArray=new JSONArray();
		try {
			for (Map<String, Object> map : input) {
				Set<Entry<String, Object>> entrySet = map.entrySet();
				Iterator<Entry<String, Object>> iterator = entrySet.iterator();
				JSONObject jsonObject = new JSONObject();
				while (iterator.hasNext()) {
					Entry<String, Object> entry = iterator.next();
					String key = entry.getKey();
					Object value = entry.getValue();

					if (value instanceof List) {
						String encodeWithListMap = encodeWithListMap(
								(List) value, condition);

						jsonObject.put(key, encodeWithListMap);
					} else {

						jsonObject.put(key, value);
					}

				}
				jsonArray.put(jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if(null == jsonArray){
			return null;
		}


		System.err.println(jsonArray.toString());
		String result = jsonArray.toString()
				.replaceAll("\\\"\\[", "\\[")
				.replaceAll("\\]\\\"", "\\]")
				.replaceAll("\\\\", "");
		System.err.println("result = " + result);
		return result;
	}

}
