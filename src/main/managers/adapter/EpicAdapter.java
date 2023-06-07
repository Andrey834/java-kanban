package main.managers.adapter;

import com.google.gson.*;
import main.tasks.Epic;
import main.tasks.StatusTask;
import main.tasks.Task;
import main.tasks.TypeTask;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EpicAdapter implements JsonDeserializer<Epic>, JsonSerializer<Epic> {

    @Override
    public Epic deserialize(
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
        ;
        LocalDateTime startTime = null;

        List<JsonElement> idListSubtask = jsonObject.get("idListSubtasks").getAsJsonArray().asList();
        List<Integer> idList = new ArrayList<>();
        if (!idListSubtask.isEmpty()) {
            for (JsonElement element : idListSubtask) {
                idList.add(element.getAsInt());
            }
        }

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
        return new Epic(id, TypeTask.EPIC, title, description, status, duration, startTime, idList);
    }

    @Override
    public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject result = new JsonObject();
        result.addProperty("id", epic.getId());
        result.addProperty("title", epic.getTitle());
        result.addProperty("description", epic.getDescription());
        result.addProperty("status", epic.getStatus().name());
        result.addProperty("duration", epic.getDuration());
        result.addProperty("startTime", epic.getStartTime().toString());
        result.addProperty("type", epic.getType().name());

        JsonArray subs = new JsonArray();
        result.add("idListSubtasks", subs);
        if (epic.getIdListSubtasks() != null) {
            for (Integer idListSubtask : epic.getIdListSubtasks()) {
                subs.add(idListSubtask);
            }
        }
        return result;
    }
}
