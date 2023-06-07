package main.managers.adapter;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import main.tasks.StatusTask;
import main.tasks.Task;
import main.tasks.TypeTask;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TaskAdapter implements JsonDeserializer<Task>, JsonSerializer<Task> {

    @Override
    public Task deserialize(JsonElement jsonElement
            ,Type type
            ,JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        int id = 0;
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String title = jsonObject.get("title").getAsString();
        String description = jsonObject.get("description").getAsString();
        StatusTask status = StatusTask.NEW;
        int duration = 0;;
        LocalDateTime startTime = null;

        TypeTask typeTask = TypeTask.valueOf(jsonObject.get("type").getAsString());

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

        return new Task(id, typeTask, title, description, status, duration, startTime);
    }

    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", task.getId());
        result.addProperty("title", task.getTitle());
        result.addProperty("description", task.getDescription());
        result.addProperty("status", task.getStatus().name());
        result.addProperty("duration", task.getDuration());
        result.addProperty("startTime", task.getStartTime().toString());
        result.addProperty("type", task.getType().name());
        return result;
    }
}
