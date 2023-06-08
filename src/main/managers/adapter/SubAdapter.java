package main.managers.adapter;

import com.google.gson.*;
import main.tasks.StatusTask;
import main.tasks.Subtask;
import main.tasks.Task;
import main.tasks.TypeTask;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class SubAdapter implements JsonDeserializer<Subtask>, JsonSerializer<Subtask> {

    @Override
    public Subtask deserialize(
            JsonElement jsonElement
            , Type type
            , JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        int id = 0;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        StatusTask status = StatusTask.NEW;
        int duration = 0;
        LocalDateTime startTime = null;
        int ownEpic = 0;

        if (jsonObject.has("id")) {
            id = jsonObject.get("id").getAsInt();
        }
        if (jsonObject.has("status")) {
            status = StatusTask.valueOf(jsonObject.get("status").getAsString());
        }
        if (jsonObject.has("duration")) {
            duration = jsonObject.get("duration").getAsInt();
        }
        if (jsonObject.has("startTime")) {
            startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString());
        }
        if (jsonObject.has("ownEpic")) {
            ownEpic = jsonObject.get("ownEpic").getAsInt();
        }
        return new Subtask(id, TypeTask.SUBTASK,title, description, status, duration, startTime, ownEpic);
    }

    @Override
    public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", subtask.getId());
        result.addProperty("title", subtask.getTitle());
        result.addProperty("description", subtask.getDescription());
        result.addProperty("status", subtask.getStatus().name());
        result.addProperty("duration", subtask.getDuration());
        result.addProperty("startTime", subtask.getStartTime().toString());
        result.addProperty("type", subtask.getType().name());
        result.addProperty("ownEpic", subtask.getOwnEpic());
        return result;
    }
}
