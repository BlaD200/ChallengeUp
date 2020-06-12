package com.example.challengeup.backend;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CommentEntity {
    private String id;
    private String message;
    private String user_id;
    private String challenge_id;
    private String date;
    private String reply_on_id;

    private int likes;

    public CommentEntity(String message, String user_id, String challenge_id, String date) {
        this.message = message;
        this.user_id = user_id;
        this.challenge_id = challenge_id;
        this.date = date;
        id = null;
        reply_on_id = "";
    }

    public CommentEntity(String message, String user_id, String challenge_id, String date, String reply_on_id) {
        this(message, user_id, challenge_id, date);
        this.reply_on_id = reply_on_id;
    }

    public static String addNewComment(CommentEntity comment) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("message", comment.message)
                    .put("user_id", comment.user_id)
                    .put("challenge_id", comment.challenge_id)
                    .put("likes", comment.likes)
                    .put("date", comment.date)
                    .put("reply_on_id", comment.reply_on_id);


            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_comment")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);

            comment.setId(object.getString("id"));
            return object.getString("id");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String addNewComment(String message, String user_id, String challenge_id, String date, String reply_on_id) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("message", message)
                    .put("user_id", user_id)
                    .put("challenge_id", challenge_id)
                    .put("likes", 0)
                    .put("date", date)
                    .put("reply_on_id", reply_on_id);
            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/add_comment")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            String resStr = response.body().string();
            JSONObject object = new JSONObject(resStr);

            return object.getString("id");
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static ArrayList<CommentEntity> getAllComments() {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();
            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/get_all_comments")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();
            String resStr = response.body().string();

            JSONObject object = new JSONObject(resStr);
            object = new JSONObject(object.getString("comments"));

            ArrayList<CommentEntity> comments = new ArrayList<>();
            for (Iterator<String> it = object.keys(); it.hasNext(); ) {
                String key = it.next();

                CommentEntity comment = new CommentEntity(
                        object.getJSONObject(key).getString("message"),
                        object.getJSONObject(key).getString("user_id"),
                        object.getJSONObject(key).getString("challenge_id"),
                        object.getJSONObject(key).getString("date"),
                        object.getJSONObject(key).getString("reply_on_id"));
                comment.setId(key);
                comment.setLikes(Integer.parseInt(object.getJSONObject(key).getString("likes")));
                comments.add(comment);
            }
            return comments;
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void update() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            JSONObject jsonObject = new JSONObject()
                    .put("comment_id", id)
                    .put("message", message)
                    .put("user_id", user_id)
                    .put("challenge_id", challenge_id)
                    .put("likes", likes)
                    .put("date", date)
                    .put("reply_on_id", reply_on_id);

            RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

            Request request = new Request.Builder()
                    .url("https://us-central1-challengeup-49057.cloudfunctions.net/update_comment")
                    .post(body)
                    .build();

            client.newCall(request).execute();

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    private void setId(String id) {
        this.id = id;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getUser_id() {
        return user_id;
    }

    public int getLikes() {
        return likes;
    }

    public String getChallenge_id() {
        return challenge_id;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getReply_on_id() {
        return reply_on_id;
    }
}
