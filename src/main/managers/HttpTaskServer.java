package main.managers;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskServer {
    private static final int PORT = 8082;
    private HttpServer httpServer;
    private Gson gson;
    private TaskManager taskManager;

    public HttpTaskServer() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        gson = Managers.getGson();
        this.httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", this::handlerGlobal);
        httpServer.createContext("/priority", this::handlerPrioritized);
        httpServer.createContext("/tasks/task", this::handlerTask);
        httpServer.createContext("/tasks/subtask", this::handlerSubtask);
        httpServer.createContext("/tasks/epic", this::handlerEpic);
        httpServer.createContext("/tasks/history", this::handlerHistory);
    }

    public void handlerGlobal(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        String response;

        switch (method) {
            case "GET" -> {
                List<Task> tasks = taskManager.getListAllTasks();
                httpExchange.sendResponseHeaders(200, 0);
                response = gson.toJson(tasks);
            }
            case "DELETE" -> {
                taskManager.removeAllTasks();
                httpExchange.sendResponseHeaders(200, 0);
                response = "Все задачи удалены!";
            }
            default -> {
                httpExchange.sendResponseHeaders(404, 0);
                response = "Unexpected value: " + method;
            }

        }
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public void handlerPrioritized(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            String response;
            List<Task> prioritiesTask = taskManager.getPrioritizedTasks();
            if (prioritiesTask.isEmpty()) {
                httpExchange.sendResponseHeaders(404, 0);
                response = "Список приоритета задач пуст!";

            } else {
                httpExchange.sendResponseHeaders(200, 0);
                response = gson.toJson(taskManager.getPrioritizedTasks());
            }
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            String response = "Method Not Allowed";
            httpExchange.sendResponseHeaders(405, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
                throw new IllegalStateException("Unexpected value: " + method);
            }
        }
    }

    public void handlerTask(HttpExchange httpExchange) throws IOException {
        String methodTask = httpExchange.getRequestMethod();
        String response;
        String query = httpExchange.getRequestURI().getQuery();

        switch (methodTask) {
            case "GET" -> {
                if (query != null && query.startsWith("id")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    if (taskManager.getTaskMap().get(id) != null) {
                        response = gson.toJson(taskManager.getTask(id));
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        response = "Задача № " + id + " не найдена!";
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    List<Task> tasks = new ArrayList<>(taskManager.getTaskMap().values());
                    if (tasks.isEmpty()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        response = "Список задач пуст!";
                    } else {
                        httpExchange.sendResponseHeaders(200, 0);
                        response = gson.toJson(tasks);
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            case "POST" -> {
                InputStream input = httpExchange.getRequestBody();
                String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                int rCode;
                if (task.getId() != 0) {
                    if (taskManager.getTaskMap().containsKey(task.getId())) {
                        Task oldTask = taskManager.getTaskMap().get(task.getId());
                        taskManager.updateTask(task);
                        if (!oldTask.equals(taskManager.getTaskMap().get(task.getId()))) {
                            response = "Задача успешно обновлена!";
                            rCode = 200;
                        } else {
                            response = "При обновлении задачи обнаружено пересечение по времени выполнения!";
                            rCode = 422;
                        }
                    } else {
                        response = "Отсутствует задача № " + task.getId() + " для обновления!";
                        rCode = 404;
                    }
                } else {
                    taskManager.addNewTask(task);
                    if (taskManager.getTaskMap().containsValue(task)) {
                        response = "Задача успешно добавлена! № " + task.getId();
                        rCode = 200;
                    } else {
                        response = "При добавлении задачи обнаружено пересечение по времени выполнения!";
                        rCode = 422;
                    }
                }

                httpExchange.sendResponseHeaders(rCode, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            case "DELETE" -> {
                if (query != null && query.startsWith("id")) {
                    int idRemove = Integer.parseInt(httpExchange.getRequestURI().getQuery().split("=")[1]);
                    if (taskManager.getTaskMap().get(idRemove) != null) {
                        taskManager.removeTask(idRemove);
                        httpExchange.sendResponseHeaders(200, 0);
                        response = "Задача № " + idRemove + " удалена";
                    } else {
                        response = "Задача № " + idRemove + " не найдена!";
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    if (taskManager.getTaskMap().isEmpty()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        response = "Список задач пуст!";
                    } else {
                        List<Integer> list = new ArrayList<>(taskManager.getTaskMap().keySet());
                        list.forEach(taskManager::removeTask);
                        httpExchange.sendResponseHeaders(200, 0);
                        response = "Все задачи удалены!";
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            default -> {
                response = "Method Not Allowed";
                httpExchange.sendResponseHeaders(405, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                    throw new IllegalStateException("Unexpected value: " + methodTask);
                }
            }
        }
    }

    public void handlerSubtask(HttpExchange httpExchange) throws IOException {
        String methodSubtask = httpExchange.getRequestMethod();
        String response;
        String query = httpExchange.getRequestURI().getQuery();

        switch (methodSubtask) {
            case "GET" -> {
                if (query != null && query.startsWith("id")) {
                    int id = Integer.parseInt(query.split("=")[1]);

                    if (taskManager.getSubMap().get(id) != null) {
                        response = gson.toJson(taskManager.getSubtask(id));
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        response = "Суб-Задача № " + id + " не найдена!";
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    List<Subtask> subs = new ArrayList<>(taskManager.getSubMap().values());
                    if (subs.isEmpty()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        response = "Список Суб-Задач пуст!";
                    } else {
                        List<Subtask> subtasks = new ArrayList<>(taskManager.getSubMap().values());
                        httpExchange.sendResponseHeaders(200, 0);
                        response = gson.toJson(subtasks);
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            case "POST" -> {
                InputStream input = httpExchange.getRequestBody();
                String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                int rCode;
                if (subtask.getId() != 0) {
                    if (subtask.getOwnEpic() != 0) {
                        if (taskManager.getSubMap().containsKey(subtask.getId())) {
                            Subtask oldSubtask = taskManager.getSubMap().get(subtask.getId());
                            taskManager.updateSubtask(subtask);
                            if (!oldSubtask.equals(taskManager.getSubMap().get(subtask.getId()))) {
                                response = "Суб-Задача успешно обновлена!";
                                rCode = 200;
                            } else {
                                response = "Обнаружено пересечение по времени выполнения при обновлении задачи!";
                                rCode = 422;
                            }
                        } else {
                            response = "Отсутствует Суб-Задача № " + subtask.getId() + " для обновления!";
                            rCode = 404;
                        }
                    } else {
                        response = "Укажите принадлежность к Эпик-Задаче!";
                        rCode = 422;
                    }
                } else {
                    if (subtask.getOwnEpic() != 0) {
                        if (taskManager.getEpicMap().containsKey(subtask.getOwnEpic())) {
                            taskManager.addNewSubtask(subtask);
                            if (taskManager.getSubMap().containsValue(subtask)) {
                                response = "Суб-Задача успешно добавлена! № " + subtask.getId();
                                rCode = 200;
                            } else {
                                response = "Обнаружено пересечение по времени выполнения при добавлении задачи!";
                                rCode = 422;
                            }
                        } else {
                            response = "Отсутствует Эпик-Задача к которой добавляется Суб-Задача!";
                            rCode = 422;
                        }
                    } else {
                        response = "Укажите принадлежность к Эпик-Задаче!";
                        rCode = 422;
                    }
                }
                httpExchange.sendResponseHeaders(rCode, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            case "DELETE" -> {
                if (query != null && query.startsWith("id")) {
                    int idRemove = Integer.parseInt(httpExchange.getRequestURI().getQuery().split("=")[1]);
                    if (taskManager.getSubMap().get(idRemove) != null) {
                        taskManager.removeSubtask(idRemove);
                        httpExchange.sendResponseHeaders(200, 0);
                        response = "Суб-Задача № " + idRemove + " удалена";
                    } else {
                        response = "Суб-Задача № " + idRemove + " не найдена!";
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    if (taskManager.getSubMap().isEmpty()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        response = "Список задач пуст!";
                    } else {
                        List<Integer> listRemove = new ArrayList<>(taskManager.getSubMap().keySet());
                        listRemove.forEach(taskManager::removeSubtask);
                        httpExchange.sendResponseHeaders(200, 0);
                        response = "Все Суб-Задачи удалены!";
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            default -> {
                response = "Method Not Allowed";
                httpExchange.sendResponseHeaders(405, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                    throw new IllegalStateException("Unexpected value: " + methodSubtask);
                }
            }
        }
    }

    public void handlerEpic(HttpExchange httpExchange) throws IOException {
        String methodEpic = httpExchange.getRequestMethod();
        String response;
        String query = httpExchange.getRequestURI().getQuery();

        switch (methodEpic) {
            case "GET" -> {
                if (query != null && query.startsWith("id")) {
                    int id = Integer.parseInt(query.split("=")[1]);
                    if (taskManager.getEpicMap().get(id) != null) {
                        response = gson.toJson(taskManager.getEpic(id));
                        httpExchange.sendResponseHeaders(200, 0);
                    } else {
                        response = "Задача № " + id + " не найдена!";
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    List<Epic> epics = new ArrayList<>(taskManager.getEpicMap().values());
                    if (epics.isEmpty()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        response = "Список Эпик-Задач пуст!";
                    } else {
                        httpExchange.sendResponseHeaders(200, 0);
                        response = gson.toJson(epics);
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            case "POST" -> {
                InputStream input = httpExchange.getRequestBody();
                String body = new String(input.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                int rCode;
                if (epic.getId() != 0) {
                    if (taskManager.getEpicMap().containsKey(epic.getId())) {
                        taskManager.updateEpic(epic);
                        response = "Задача успешно обновлена!";
                        rCode = 200;
                    } else {
                        response = "Отсутствует Эпик-Задача № " + epic.getId() + " для обновления!";
                        rCode = 404;
                    }
                } else {
                    taskManager.addNewEpic(epic);
                    if (taskManager.getEpicMap().containsValue(epic)) {
                        response = "Эпик-Задача успешно добавлена! № " + epic.getId();
                        rCode = 200;
                    } else {
                        response = "Эпик-Задача не добавилась!";
                        rCode = 400;
                    }
                }

                httpExchange.sendResponseHeaders(rCode, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            case "DELETE" -> {
                if (query != null && query.startsWith("id")) {
                    int idRemove = Integer.parseInt(httpExchange.getRequestURI().getQuery().split("=")[1]);
                    if (taskManager.getEpicMap().get(idRemove) != null) {
                        taskManager.removeEpic(idRemove);
                        httpExchange.sendResponseHeaders(200, 0);
                        response = "Эпик-Задача № " + idRemove + " удалена";
                    } else {
                        response = "Эпик-Задача № " + idRemove + " не найдена!";
                        httpExchange.sendResponseHeaders(404, 0);
                    }
                } else {
                    if (taskManager.getEpicMap().isEmpty()) {
                        httpExchange.sendResponseHeaders(404, 0);
                        response = "Список Эпик-Задач пуст!";
                    } else {
                        List<Integer> listRemove = new ArrayList<>(taskManager.getEpicMap().keySet());
                        listRemove.forEach(taskManager::removeEpic);
                        httpExchange.sendResponseHeaders(200, 0);
                        response = "Все Эпик-Задачи удалены!";
                    }
                }
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
            default -> {
                response = "Method Not Allowed";
                httpExchange.sendResponseHeaders(405, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                    throw new IllegalStateException("Unexpected value: " + methodEpic);
                }
            }
        }
    }

    public void handlerHistory(HttpExchange httpExchange) throws IOException {
        String method = httpExchange.getRequestMethod();
        if (method.equals("GET")) {
            List<Task> history = taskManager.getHistory();
            if (history.isEmpty()) {
                httpExchange.sendResponseHeaders(404, 0);
                String response = "История просмотра пуста!";
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                List<Task> historyList = taskManager.getHistory();
                Type taskType = new TypeToken<List<Task>>() {}.getType();
                String tasks = gson.toJson(historyList, taskType);
                httpExchange.sendResponseHeaders(200, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(tasks.getBytes());
                }
            }
        } else {
            String response = "Method Not Allowed";
            httpExchange.sendResponseHeaders(405, 0);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
                throw new IllegalStateException("Unexpected value: " + method);
            }
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }
}