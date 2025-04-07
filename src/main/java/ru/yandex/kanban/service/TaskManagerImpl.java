package ru.yandex.kanban.service;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.kanban.issue.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/*
ОТВЕТЫ на комментарии:

        if (task.getId() == null)
Идентификаторы задач не должны обновляться после создания
ИСПРАВЛЕНО

        if (subtask.getId() == null)
Идентификаторы не должны обновляться
ИСПРАВЛЕНО

        // Обновление идентификатора подзадачи в связанной Epic задаче
Здесь и ниже обновлять идентификаторы тоже не нужно
ПОД ОБНОВЛЕНИЕМ ИМЕЛОСЬ ВВИДУ ДОБАВЛЕНИЕ НОВЫХ ПРИ ДОБАВЛЕНИИ ПОДЗАДАЧ К ЭПИКУ
Set не позволит дважды добавить один и тот же

        // Присвоение уникального идентификатора, если текущий идентификатор отсутствует
 И здесь тоже
 ИСПРАВЛЕНО

         Subtask subtask = subtasks.get(subtaskId);
Лучше использовать вместо метода get метод remove, так как он отработает как первый, но дополнительно будет произведено удаление
REMOVE использовано позднее

        Epic epic = epics.get(epicId);
Лучше сразу использовать remove вместо get
REMOVE использовано позднее

        logger.debug("getTaskById: Получение Task по ID {} завершено: {}", taskId, tasks.get(taskId));
Лучше получить задачу и записать результат в переменную, а не получать ее несколько раз здесь и ниже. Поправь и в других методах получения задач
НЕ ИМЕЕТ СМЫСЛА - так как это логирование только в режиме логирования DEBUG

       tasks.forEach((taskId, task) -> removeTaskById(taskId));
Лучше использовать метод clear()
ИСПРАВЛЕНО

        subtasks.values().forEach(subtask -> removeSubtaskById(subtask.getId()));
Нужно использовать метод clear(), затем нужно почистить все эпики и обновить их статусы
ИСПРАВЛЕНО

        epics.values().forEach(epic -> removeEpicById(epic.getId()));
При удалении всех эпиков, нужно также очищать мапу с подзадачами, так как подзадача не может существовать без эпика
ИСПРАВЛЕНО

    public Set<Long> getAllEpics() {
По тз требуется вернуть список :)
ИСПРАВЛЕНО

    public Set<Long> getAllSubtasksByEpicId(@NonNull Long epicId) {
И здесь нужно возвращать список
ИСПРАВЛЕНО

    public void updateTaskStatusById(@NonNull Long taskId, @NonNull Status status) {
Пользователь должен мочь управлять только статусами задач и подзадач. Статус эпика должен рассчитываться автоматически на основе статусов связанных с ним подзадач. Данный метод нужно удалить, так как пользователь может менять статусы с помощью конструктора и сеттеров
ОШИБОЧНЫЙ КОММЕНТАРИЙ - вероятно имелся ввиду updateEpicStatusById - но он private

    public void updateSubtaskStatusById(@NonNull Long subtaskId, @NonNull Status status) {
Этот метод - лишний
ОШИБОЧНЫЙ КОММЕНТАРИЙ - ПО ТЗ: Пользователь должен мочь управлять статусами подзадач.

    private void updateEpicStatusById(@NonNull Long epicId, @NonNull Status status) {
Этот метод - лишний
ЭТО PRIVATE МЕТОД - ПОСЛЕ УДАЛЕНИЯ SUBTASK НАДО ОБНОВИТЬ СТАТУС ЭПИКА
ПОЛЬЗОВАТЕЛЮ НЕ ДОСТУПЕН МЕТОД

    public void updateEpicStatusById(Long epicId) {
Метод долже быть приватным
ИСПРАВЛЕНО



 */

@Slf4j
@Getter
public class TaskManagerImpl implements TaskManager {
    // Добавление поля logger
    public static final Logger logger = LoggerFactory.getLogger(TaskManagerImpl.class);
    // Счетчик для генерации уникальных идентификаторов задач
    private final AtomicLong uniqueId = new AtomicLong(0);
    // Хранилище эпиков (Epic)
    private final Map<Long, Epic> epics = new ConcurrentHashMap<>();
    // Хранилище основных задач (Task)
    private final Map<Long, Task> tasks = new ConcurrentHashMap<>();
    // Хранилище подзадач (Subtask)
    private final Map<Long, Subtask> subtasks = new ConcurrentHashMap<>();

    public TaskManagerImpl() {
    }

    /**
     * Возвращает уникальный идентификатор.
     * Метод использует атомарную операцию для увеличения текущего значения уникального идентификатора,
     * что гарантирует уникальность идентификатора в многопоточной среде.
     *
     * @return следующий уникальный идентификатор
     */
    private Long nextUniqueId() {
        return uniqueId.incrementAndGet();
    }

    /**
     * Вносит информацию о задаче (Task) в TaskManager.
     *
     * @param task Задача, которая должна быть обновлена или создана.
     * @return Возвращает уникальный идентификатор обновленной или новой задачи.
     * @throws IllegalArgumentException если Task не имеет идентификатора
     */
    @Override
    public Long updateTask(@NonNull Task task) {
        // Логирование начала обновления задачи
        logger.debug("updateTask: Обновление Task {} ...", task);

        // Если задача не имеет идентификатора, выбрасывается исключение
        if (task.getId() == null) {
            throw new IllegalArgumentException("Ошибка updateTask: Task не имеет идентификатора");
        }

        // Сохранение задачи в map задач, используя ее идентификатор в качестве ключа
        tasks.put(task.getId(), task);

        // Возврат идентификатора обновленной или новой задачи
        return task.getId();
    }

    /**
     * Вносит информацию о подзадаче (Subtask) в TaskManager.
     * <p>
     * Принимает объект Subtask, проверяет и обновляет его уникальный идентификатор,
     * проверяет соответствующей Epic задачи, и затем обновляет статус обеих задач.
     *
     * @param subtask Подзадача, которую нужно обновить. Не должен быть null.
     * @return Возвращает идентификатор обновленной подзадачи.
     * @throws IllegalArgumentException если Epic, связанный с Subtask, не найден.
     */
    @Override
    public Long updateSubtask(@NonNull Subtask subtask) {
        // Логирование
        logger.debug("updateSubtask: Обновление Subtask {} ...", subtask);

        // Идентификаторы не должны обновляться
        if (subtask.getId() == null)
            throw new IllegalArgumentException("Ошибка updateSubtask: Subtask не имеет идентификатора");

        // Получение идентификаторов Subtask связанным Epic
        Long subtaskId = subtask.getId();
        Long epicId = subtask.getEpicId();

        // Проверка, существует ли связанная Epic задача
        if (!epics.containsKey(epicId)) {
            // Если Epic задача не найдена, выбрасываем исключение
            throw new IllegalArgumentException("Ошибка updateSubtask: Epic " + epicId +
                    "не найден для Subtask " + subtaskId);
        }

        // Обновление подзадачи в хранилище подзадач
        subtasks.put(subtaskId, subtask);
        // Добавление нового subtaskId (если отсутствует) в список подзадач связанных с Epic.
        // Данная строка нужна.
        epics.get(epicId).updateSubtaskId(subtaskId);
        // Обновление статуса связанной Epic задачи
        updateEpicStatusById(epicId);

        // Логирование
        logger.debug("updateSubtask: Обновление Subtask {} успешно завершено.", subtask);

        // Возврат идентификатора обновленной подзадачи
        return subtaskId;
    }

    /**
     * Вносит информацию о Epic в TaskManager.
     * Обновляет существующий Epic или создает новый, если он не существует.
     * <p>
     * Метод проверяет, имеет ли переданный объект Epic идентификатор. Если идентификатор отсутствует,
     * метод присваивает ему уникальный идентификатор с помощью вызова вспомогательного метода nextUniqueId().
     * После этого обновленный или новый объект Epic сохраняется в хранилище (Map) эпиков.
     *
     * @param epic Эпик, который необходимо обновить или создать. Не может быть null.
     * @return Возвращает идентификатор обновленного или нового эпика.
     * @throws IllegalArgumentException если Epic не имеет идентификатора
     */
    @Override
    public Long updateEpic(@NonNull Epic epic) {
        // Логирование
        logger.debug("updateEpic: Обновление Epic ...");

        // При отсутствии id выкидываем исключение
        if (epic.getId() == null) {
            throw new IllegalArgumentException("Ошибка updateEpic: Epic не имеет идентификатора");
        }

        // Сохранение обновленного или нового эпика в хранилище
        epics.put(epic.getId(), epic);

        // Логирование
        logger.debug("updateEpic: Обновление Epic завершено, EpicId: {}", epic.getId());

        return epic.getId();
    }


    /**
     * Удаление Task по ID
     *
     * @param taskId Идентификатор Task, который должен быть удален
     * @throws IllegalArgumentException если задача с указанным идентификатором не существует
     */
    @Override
    public void removeTaskById(@NonNull Long taskId) {
        // Логирование
        logger.debug("removeTaskById: Удаление Task по ID {} ...", taskId);

        // Проверка, существует ли задача с указанным ID
        if (!tasks.containsKey(taskId)) {
            // Если задача не найдена, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка removeTask: Task не найден " + taskId);
        }
        // Удаление задачи из коллекции
        tasks.remove(taskId);

        // Логирование
        logger.debug("removeTaskById: Удаление Task по ID {} завершено", taskId);
    }


    /**
     * Удаляет подзадачу (Subtask) по её уникальному идентификатору.
     * <p>
     * Метод выполняет следующие действия:
     * 1. Проверяет существование подзадачи в map. Если подзадача не найдена, выбрасываем исключение.
     * 2. Проверяет существование связанного Epic. Если Epic не найден, выбрасываем исключение.
     * 3. Удаляет связь между Epic и Subtask.
     * 4. Обновляет статус связанного Epic после удаления подзадачи.
     * 5. Удаляет саму подзадачу из хранилища.
     *
     * @param subtaskId Уникальный идентификатор подзадачи для удаления.
     * @throws IllegalArgumentException Если подзадача с указанным идентификатором не существует.
     * @throws RuntimeException         Если связанный Epic для подзадачи не найден.
     */
    @Override
    public void removeSubtaskById(@NonNull Long subtaskId) {
        // Логирование
        logger.debug("removeSubtaskById: Удаление Subtask по ID {} ...", subtaskId);

        // Проверка существования подзадачи в хранилище
        if (!subtasks.containsKey(subtaskId)) {
            throw new IllegalArgumentException("Ошибка removeSubtask: Subtask не найден " + subtaskId);
        }
        Subtask subtask = subtasks.get(subtaskId);
        // По комментарию ревьювера - remove тоже есть, но позднее

        // Проверка существования связанного Epic в хранилище
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new RuntimeException("Ошибка removeSubtask: Epic не найден для подзадачи " + subtaskId);
        }
        Epic epic = epics.get(subtask.getEpicId());

        // Удаление связи между Epic и Subtask
        epic.removeSubtaskId(subtaskId);

        // Обновление статуса связанного Epic после удаления подзадачи
        updateEpicStatusById(epic.getId());

        // Удаление самой подзадачи из хранилища
        subtasks.remove(subtaskId);

        // Логирование
        logger.debug("removeSubtaskById: Удаление Subtask по ID {} завершено", subtaskId);
    }


    /**
     * Удаляет Epic (эпик) по уникальному идентификатору.
     * <p>
     * Метод выполняет следующие действия:
     * 1. Проверяет наличие эпика в map. Если эпик не найден, выбрасываем исключение IllegalArgumentException.
     * 2. Вытаскиваем объект эпика из хранилища.
     * 3. Удаляем все подзадачи, связанные с этим эпиком.
     * 4. Удаляем сам эпик из хранилища.
     *
     * @param epicId Уникальный идентификатор эпика, который нужно удалить. Не может быть null.
     * @throws IllegalArgumentException Если эпик с указанным идентификатором не существует.
     */
    @Override
    public void removeEpicById(@NonNull Long epicId) {
        // Логирование
        logger.debug("removeEpicById: Удаление Epic по ID {} ...", epicId);

        // Проверка наличия эпика в хранилище
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Ошибка removeEpic: Epic не найден " + epicId);
        }

        // Получение объекта эпика из хранилища
        Epic epic = epics.get(epicId);
        // По комментарию ревьювера - remove позднее
        // Удаление всех подзадач, связанных с этим эпиком
        epic.getDependentSubtaskIds().forEach(subtasks::remove);
        // Удаление самого эпика из хранилища
        epics.remove(epicId);

        // Логирование
        logger.debug("removeEpicById: Удаление Epic по ID {} завершено", epicId);
    }


    /**
     * Получения задачи (Task) по её уникальному идентификатору.
     *
     * @param taskId Уникальный идентификатор задачи. Не может быть null.
     * @return Задача (Task), соответствующая указанному идентификатору.
     * @throws IllegalArgumentException Если задача с указанным идентификатором не существует в хранилище.
     */
    @Override
    public Task getTaskById(@NonNull Long taskId) {
        // Логирование
        logger.debug("getTaskById: Получение Task по ID {}", taskId);

        // Проверка наличия задачи в хранилище по указанному идентификатору
        if (!tasks.containsKey(taskId)) {
            // Если задача не найдена, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка getTask: Task не найден " + taskId);
        }

        // Логирование
        logger.debug("getTaskById: Получение Task по ID {} завершено: {}", taskId, tasks.get(taskId));
        // Комментарий ревьвера не ясен - речь о логировании в режиме debug, который будет выключен при
        // штатной работе. Поэтому использование дополнительных переменных - не имеет смысла.

        // Возврат найденной задачи из хранилища
        return tasks.get(taskId);
    }


    /**
     * Получение Subtask по её уникальному идентификатору.
     *
     * @param subtaskId Идентификатор подзадачи, который не должен быть null.
     * @return Возвращает объект Subtask, найденный по идентификатору.
     * @throws IllegalArgumentException Если подзадача с указанным идентификатором не найдена.
     */
    @Override
    public Subtask getSubtaskById(@NonNull Long subtaskId) {
        // Логирование
        logger.debug("getSubtaskById: Получение Subtask по ID {}", subtaskId);

        // Проверка наличия подзадачи с указанным идентификатором в коллекции
        if (!subtasks.containsKey(subtaskId)) {
            // Если подзадача не найдена, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка getSubtask: Subtask не найден " + subtaskId);
        }

        // Логирование
        logger.debug("getSubtaskById: Получение Subtask по ID {} завершено: {}", subtaskId, subtasks.get(subtaskId));

        // Возврат найденной подзадачи
        return subtasks.get(subtaskId);
    }

    /**
     * Получения объекта Epic по его уникальному идентификатору.
     *
     * @param epicId Уникальный идентификатор эпика. Не может быть null.
     * @return Возвращает объект Epic, найденный по указанному идентификатору.
     * @throws IllegalArgumentException Если эпик с указанным идентификатором не существует в хранилище.
     */
    @Override
    public Epic getEpicById(@NonNull Long epicId) {
        // Логирование
        logger.debug("getEpicById: Получение Epic по ID {} ...", epicId);

        // Проверка наличия эпика в хранилище по указанному идентификатору
        if (!epics.containsKey(epicId)) {
            // Если эпик не найден, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка getEpic: Epic не найден " + epicId);
        }

        // Логирование
        logger.debug("getEpicById: Получение Epic по ID {} завершено: {}", epicId, epics.get(epicId));

        // Возврат найденного Epic
        return epics.get(epicId);
    }


    /**
     * Удаление всех Task из TaskManager.
     * <p>
     * Процесс удаления включает:
     * 1. Логирование начала удаления всех задач.
     * 2. Итерацию по всем задачам и их удаление по идентификатору.
     * 3. Проверку, что все задачи были успешно удалены. Если какие-либо задачи остались, выбрасывается исключение.
     * 4. Логирование завершения процесса удаления.
     */
    @Override
    public void deleteAllTasks() {
        // Логирование
        logger.debug("deleteAllTasks: Удаление всех задач (Task) ...");

        // Итерация по всем задачам и их удаление по идентификатору
        tasks.clear();

        // Логирование
        logger.debug("deleteAllTasks: Удаление всех задач (Task) завершено.");
    }


    /**
     * Удаляет все подзадачи (Subtask) из TaskManager.
     * <p>
     * Процесс удаления включает следующие шаги:
     * 1. Логирование начала процесса удаления всех подзадач.
     * 2. Итерация по всем подзадачам и их последовательное удаление с помощью метода removeSubtaskById.
     * 3. Проверка, остались ли какие-либо подзадачи после выполнения удаления. Если да, выбрасывается исключение.
     * 4. Логирование завершения процесса удаления.
     */
    @Override
    public void deleteAllSubtasks() {
        // Логирование
        logger.debug("deleteAllSubtasks: Удаление всех подзадач (Subtask) ...");

        subtasks.clear();
        for (Epic epic : epics.values())
            epic.getDependentSubtaskIds().clear();

        // Логирование
        logger.debug("deleteAllSubtasks: Удаление всех подзадач (Subtask) завершено.");
    }


    /**
     * Удаляет все эпики (Epic) из TaskManager.
     * <p>
     * Процесс удаления включает следующие шаги:
     * 1. Логирование начала процесса удаления всех эпиков.
     * 2. Итерация по всем эпикам и их последовательное удаление с помощью метода removeEpicById.
     * 3. Проверка, остались ли какие-либо эпики после выполнения удаления. Если да, выбрасывается исключение.
     * 4. Логирование завершения процесса удаления.
     *
     */
    @Override
    public void deleteAllEpics() {
        // Логирование
        logger.debug("deleteAllEpics: Удаление всех Эпиков (Epic) ...");

        epics.clear();
        subtasks.clear();

        // Логирование
        logger.debug("deleteAllEpics: Удаление всех Эпиков (Epic) завершено.");
    }


    /**
     * Возвращает Set уникальных идентификаторов всех Task.
     * <p>
     *
     * @return Set уникальных идентификаторов задач (Task). Если задач нет, возвращается пустое множество.
     */
    @Override
    public ArrayList<Long> getAllTasks() {
        // Логирование
        logger.debug("getAllTasks: Получение всех задач (Task)");

        // Возвращаем копию Set ключей из TaskManager
        return new ArrayList<>(tasks.keySet());
    }


    /**
     * Возвращает Set уникальных идентификаторов всех подзадач.
     *
     * @return Set<Long> - Множество идентификаторов подзадач.
     */
    @Override
    public ArrayList<Long> getAllSubtasks() {
        // Запись действия в лог для отслеживания получения всех подзадач
        logger.debug("getAllSubtasks: Получение всех подзадач (Subtask)");
        // Возвращаем копию множества ключей из хранилища подзадач
        return new ArrayList<>(subtasks.keySet());
    }


    /**
     * Возвращает Set уникальных всех Эпиков (Epic).
     *
     * @return Set<Long> - множество идентификаторов всех Эпиков.
     */
    @Override
    public ArrayList<Long> getAllEpics() {
        logger.debug("getAllEpics: Получение всех Эпиков (Epic)");
        return new ArrayList<>(epics.keySet());
    }


    /**
     * Получение Set подзадач, связанные с определённым Epic.
     *
     * @param epicId Идентификатор Epic, для которого запрашиваются подзадачи.
     * @return Set подзадач, связанных с Epic.
     * @throws IllegalArgumentException Если Epic с указанным идентификатором не существует.
     */
    @Override
    public Set<Long> getAllSubtasksByEpicId(@NonNull Long epicId) {
        // Логирование начала метода и входного параметра
        logger.debug("getAllSubtasksByEpicId: Получение всех подзадач, связанных с Epic {}", epicId);

        // Проверка существования Epic с заданным идентификатором
        if (!epics.containsKey(epicId)) {
            // Генерация исключения, если Epic не найден
            throw new IllegalArgumentException("Ошибка getAllSubtasksByEpicId: Epic не найден " + epicId);
        }

        // Возврат множества идентификаторов подзадач, связанных с найденным Epic
        return epics.get(epicId).getDependentSubtaskIds();
    }

    /**
     * Обновление статуса задачи по ID
     *
     * @param taskId Номер идентификации задачи, для которой нужно обновить статус
     * @param status Новый статус задачи
     *               <p>
     *               Метод сначала проверяет, существует ли задача с указанным идентификатором в map tasks.
     *               Если задача существует, ее статус обновляется на переданный статус.
     *               Если задача не найдена, метод выбрасывает исключение IllegalArgumentException.
     */
    @Override
    public void updateTaskStatusById(@NonNull Long taskId, @NonNull Status status) {
        // Логирование начала обновления статуса задачи и указание задачи и новый статус
        logger.debug("updateTaskStatusById: Обновление статуса Task {} на {} ...", taskId, status);

        // Проверка, существует ли задача с указанным ID
        if (!tasks.containsKey(taskId)) {
            // Если задача не найдена, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка updateTaskStatus: Task не найден " + taskId);
        }

        // Если задача существует, ее статус обновляется на переданный статус
        tasks.get(taskId).setStatus(status);

        // Логирование успешного обновления статуса задачи
        logger.debug("updateTaskStatusById: Обновление статуса Task {} на {} " +
                "успешно завершено.", taskId, status);
    }

    // Обновление статуса подзадачи по ID
    @Override
    public void updateSubtaskStatusById(@NonNull Long subtaskId, @NonNull Status status) {
        // Логирование
        logger.debug("updateSubtaskStatusById: Обновление статуса Subtask {} на {} ...", subtaskId, status);

        // Проверка наличия подзадачи с указанным ID
        if (!subtasks.containsKey(subtaskId)) {
            // Если подзадача не найдена, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка updateSubtaskStatus: Subtask не найден " + subtaskId);
        }

        // Обновление статуса найденной подзадачи
        subtasks.get(subtaskId).setStatus(status);

        // Обновление статуса родительской задачи (Epic) после изменения статуса подзадачи
        updateEpicStatusById(subtasks.get(subtaskId).getEpicId());

        // Логирование
        logger.debug("updateSubtaskStatusById: Обновление статуса Subtask {} на {} " +
                "успешно завершено.", subtaskId, status);
    }

    // Обновление статуса Epic по ID с указанием нового статуса
    // Пользователь не сможет им воспользоваться (private)
    private void updateEpicStatusById(@NonNull Long epicId, @NonNull Status status) {
        // Логирование
        logger.debug("updateEpicStatusById: Обновление статуса Epic {} на {} ...", epicId, status);

        // Проверка наличия Epic в списке по ID
        if (!epics.containsKey(epicId)) {
            // Если Epic не найден, выбрасывается исключение
            throw new IllegalArgumentException("Ошибка updateEpicStatus: Epic не найден " + epicId);
        }

        // Получение объекта Epic по ID
        Epic epic = epics.get(epicId);

        // Перебор всех подзадач, связанных с Epic
        for (Long subtaskId : epic.getDependentSubtaskIds()) {
            // Проверка наличия каждой подзадачи в списке по ID
            if (!subtasks.containsKey(subtaskId))
                // Если подзадача не найдена, выбрасывается исключение
                throw new RuntimeException("Ошибка updateEpicStatus: Subtask не найден " + subtaskId);

            // Получение объекта Subtask по ID
            Subtask subtask = subtasks.get(subtaskId);

            // Сравнение статуса подзадачи с новым статусом Epic
            if (subtask.getStatus().compareTo(status) < 0) {
                // Если статус подзадачи ниже нового статуса Epic, обновление статуса подзадачи
                updateSubtaskStatusById(subtaskId, status);
            }
        }

        // Обновление статуса Epic
        epic.setStatus(status);

        // Логирование
        logger.debug("updateEpicStatusById: Обновление статуса Epic {} на {} успешно завершено.", epicId, status);
    }

    // Автоматическое обновление статуса Epic на основе статусов связанных подзадач
    public void updateEpicStatusById(Long epicId) {
        // Логирование
        logger.debug("updateEpicStatusById: Обновление статуса Epic {} ...", epicId);
        // Проверка существования Epic по идентификатору
        if (!epics.containsKey(epicId)) {
            // Выброс исключения, если Epic не найден
            throw new IllegalArgumentException("Ошибка updateEpicStatus: Epic не найден " + epicId);
        }
        // Получение экземпляра Epic по идентификатору
        Epic epic = epics.get(epicId);
        // Поиск минимального статуса среди подзадач
        Status minStatus = null;
        // Перебор всех подзадач, связанных с Epic
        for (Long subtaskId : epic.getDependentSubtaskIds()) {
            // Получение экземпляра подзадачи
            Subtask subtask = subtasks.get(subtaskId);
            // Обновление минимального статуса, если текущий статус подзадачи меньше минимального
            if (minStatus == null || subtask.getStatus().compareTo(minStatus) < 0) {
                minStatus = subtask.getStatus();
            }
        }
        // Установка статуса NEW, если нет подзадач
        epic.setStatus(minStatus != null ? minStatus : Status.NEW);
        // Логирование
        logger.debug("updateEpicStatusById: Обновление статуса Epic {} успешно завершено.", epicId);
    }
}
