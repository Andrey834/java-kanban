package main.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import main.managers.adapter.LocalDateTimeAdapter;
import main.tasks.Epic;
import main.tasks.Subtask;
import main.tasks.Task;
import main.managers.adapter.EpicAdapter;
import main.managers.adapter.SubAdapter;
import main.managers.adapter.TaskAdapter;

import java.io.IOException;
import java.time.LocalDateTime;

public class Managers {
    /*public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
*/
    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078/");
    }

    /*public static TaskManager getDefault() throws IOException, InterruptedException {
        return new FileBackedTasksManager();
    }*/



    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Task.class, new TaskAdapter())
                .registerTypeAdapter(Epic.class, new EpicAdapter())
                .registerTypeAdapter(Subtask.class, new SubAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        return gsonBuilder.create();
    }
}
