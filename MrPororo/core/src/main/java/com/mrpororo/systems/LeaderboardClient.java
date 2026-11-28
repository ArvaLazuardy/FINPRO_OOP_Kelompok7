package com.mrpororo.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LeaderboardClient {
    // Replace with the deployed HTTPS address for actual online database
    public static final String API_URL = "http://localhost:8080/api/leaderboard";

    public static class Entry {
        public final String playerName;
        public final long completionTimeMs, realTimeMs;
        public final int successfulParries, freezeCount;

        public Entry(String playerName, long completionTimeMs, long realTimeMs, int successfulParries, int freezeCount) {
            this.playerName = playerName;
            this.completionTimeMs = completionTimeMs;
            this.realTimeMs = realTimeMs;
            this.successfulParries = successfulParries;
            this.freezeCount = freezeCount;
        }
    }

    public void submitRecord(String name, RunTimer timer, Runnable onSuccess, Consumer<String> onError) {
        JsonValue body = new JsonValue(JsonValue.ValueType.object);
        body.addChild("playerName", new JsonValue(name));
        body.addChild("completionTimeMs", new JsonValue(timer.getCompletionTimeMs()));
        body.addChild("realTimeMs", new JsonValue(timer.getRealTimeMs()));
        body.addChild("successfulParries", new JsonValue((long) timer.getSuccessfulParries()));
        body.addChild("freezeCount", new JsonValue((long) timer.getFreezeCount()));
        send(Net.HttpMethods.POST, body.toJson(JsonWriter.OutputType.json),
            response -> onSuccess.run(), onError);
    }

    public void getLeaderboard(Consumer<List<Entry>> onSuccess, Consumer<String> onError) {
        send(Net.HttpMethods.GET, null, response -> {
            List<Entry> entries = new ArrayList<>();
            try {
                JsonValue root = new JsonReader().parse(response);
                if (!root.isArray()) throw new IllegalArgumentException("Expected an array");
                for (JsonValue row = root.child; row != null; row = row.next) {
                    entries.add(new Entry(row.getString("playerName"),
                        row.getLong("completionTimeMs"), row.getLong("realTimeMs"),
                        row.getInt("successfulParries"), row.getInt("freezeCount")));
                }
            } catch (RuntimeException ex) {
                onError.accept("Invalid leaderboard response");
                return;
            }
            onSuccess.accept(entries);
        }, onError);
    }

    private void send(String method, String body, Consumer<String> onSuccess, Consumer<String> onError) {
        Net.HttpRequest request = new Net.HttpRequest(method);
        request.setUrl(API_URL);
        request.setTimeOut(15000);
        request.setHeader("Accept", "application/json");
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
            request.setContent(body);
        }

        try {
            Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
                @Override public void handleHttpResponse(Net.HttpResponse response) {
                    int status = response.getStatus().getStatusCode();
                    String result = response.getResultAsString();
                    Gdx.app.postRunnable(() -> {
                        if (status >= 200 && status < 300) onSuccess.accept(result);
                        else onError.accept("HTTP " + status);
                    });
                }
                @Override public void failed(Throwable t) {
                    Gdx.app.postRunnable(() -> onError.accept("Server unavailable or request timed out"));
                }
                @Override public void cancelled() {
                    Gdx.app.postRunnable(() -> onError.accept("Request cancelled"));
                }
            });
        } catch (RuntimeException ex) {
            Gdx.app.postRunnable(() -> onError.accept("Unable to start HTTP request"));
        }
    }
}
