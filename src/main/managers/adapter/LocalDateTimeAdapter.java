package main.managers.adapter;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> implements JsonSerializer<LocalDateTime> ,JsonDeserializer<LocalDateTime> {
private static final DateTimeFormatter formatterWriter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
private static final DateTimeFormatter formatterReader = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException {
        jsonWriter.value(localDateTime.format(formatterWriter));
    }

    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        return LocalDateTime.parse(jsonReader.nextString(), formatterReader);
    }

    @Override
    public LocalDateTime deserialize(JsonElement jsonElement
            , Type type
            , JsonDeserializationContext jsonDeserializationContext
    ) throws JsonParseException {
        return LocalDateTime.parse(jsonElement.getAsJsonObject().get("startTime").getAsString(), formatterReader);
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime
            , Type type
            , JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.format(formatterWriter));
    }
}