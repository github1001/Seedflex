import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;

public class Seedflex {

	// fetch JSON from URL
	private static String fetchJson(String urlString) throws Exception {
		URL url = new URL(urlString);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

		StringBuilder response = new StringBuilder();
		String line;

		while ((line = in.readLine()) != null) {
			response.append(line);
		}

		in.close();

		return response.toString();

	}

	public static void main(String[] args) {
		String apiUrl = "https://jsonplaceholder.typicode.com/todos";

		// fetch
		try {
			String jsonResponse = fetchJson(apiUrl);
			// System.out.println(jsonResponse);

			// Parse JSON data
			JSONArray todos = new JSONArray(jsonResponse);

			// Group todos by userId, then by completion status
			JSONObject groupedTodos = new JSONObject();

			for (int i = 0; i < todos.length(); i++) {
				JSONObject todo = todos.getJSONObject(i);
				int userId = todo.getInt("userId");
				boolean completed = todo.getBoolean("completed");

				// Construct unique userId key format
				String userKey = "userId_" + userId;

				// Create task without userId
				JSONObject task = new JSONObject();
				task.put("id", todo.getInt("id"));
				task.put("title", todo.getString("title"));

				// Initialize userId entry if not already present
				if (!groupedTodos.has(userKey)) {
					groupedTodos.put(userKey, new JSONObject());
				}

				// Get the user's object
				JSONObject userTodos = groupedTodos.getJSONObject(userKey);

				// Initialize "completed" and "incomplete" categories if not present
				if (!userTodos.has("completed")) {
					userTodos.put("completed", new JSONArray());
				}
				if (!userTodos.has("incomplete")) {
					userTodos.put("incomplete", new JSONArray());
				}

				// Add task to the correct category
				if (completed) {
					userTodos.getJSONArray("completed").put(task);
				} else {
					userTodos.getJSONArray("incomplete").put(task);
				}
			}

			// Print formatted JSON result
			System.out.println(groupedTodos.toString(4));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}