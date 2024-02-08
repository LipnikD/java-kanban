package ru.lipnik.taskmanager;

import ru.lipnik.taskmanager.model.*;
import ru.lipnik.taskmanager.service.TaskManager;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task theatre = new Task(taskManager.newId(), "Купить билеты в театр",
                "Выбрать из еще не полученных впечатлений новый сюжет и театр.");
        taskManager.addTask(theatre);
        Task dogTraining = new Task(taskManager.newId(),"Научить собаку находить сокровища",
                "Питомец, регулярно улучшающий финансовое благосостояние, хорошо дополнит трудовые доходы.");
        taskManager.addTask(dogTraining);
        Epic SummerDrive = new Epic(taskManager.newId(),"Подготовить машину к лету",
                "Комплекс мероприятий для комфортного вхождения в летний сезон активного вождения.");
        taskManager.addEpic(SummerDrive);
        Subtask tyres = new Subtask(taskManager.newId(), "Купить летние шины",
                "Необходимо выбрать на Яндекс маркете лучшее предложение и приобрести новые шины");
        taskManager.addSubtask(SummerDrive, tyres);
        Subtask inspection = new Subtask(taskManager.newId(), "Провести техосмотр",
                "Нужно провести регламентный осмотр всех узлов, агрегатов и тех. жидкостей");
        taskManager.addSubtask(SummerDrive, inspection);
        Subtask sound = new Subtask(taskManager.newId(), "Модернизировать аудиосистему",
                "Установить акустику и необходимые компоненты (усилители, процессор) для дальних поездок");
        taskManager.addSubtask(SummerDrive, sound);

        Epic newSchool = new Epic(taskManager.newId(),"Перевести ребенка в новую школу",
                "Новая более сильная школа - хороший старт в развитии.");
        taskManager.addEpic(newSchool);
        Subtask learnMath = new Subtask(taskManager.newId(), "Подготовка к экзамену по математике",
                "Для успешной сдачи экзаменов точно понадобится уверенное знание математики.");
        taskManager.addSubtask(newSchool, learnMath);
        System.out.println(theatre);
        taskManager.setStatus(theatre, Status.DONE);
        theatre.setDescription("Кажется, что будет очень интересно!");
        taskManager.updateTask(theatre);
        System.out.println(theatre);

        System.out.println(taskManager.getTask(dogTraining.getId()));
        taskManager.setStatus(dogTraining, Status.IN_PROGRESS);
        System.out.println(dogTraining);
        taskManager.setStatus(dogTraining, Status.NEW);
        System.out.println(dogTraining);
        taskManager.setStatus(dogTraining, Status.DONE);
        System.out.println(dogTraining);

        System.out.println(taskManager.getEpic(SummerDrive.getId()));
        taskManager.setStatus(tyres, Status.IN_PROGRESS);
        System.out.println(SummerDrive);
        taskManager.setStatus(inspection, Status.DONE);
        System.out.println(SummerDrive);

        SummerDrive.setName("Подготовить машину к весне 2024");
        taskManager.updateEpic(SummerDrive);
        tyres.setName("Купить спортивные шины Michelin Pilot Sport 5");
        System.out.println(taskManager.getSubtask(tyres.getId()));
        taskManager.updateSubtask(tyres);
        taskManager.setStatus(tyres, Status.DONE);
        taskManager.setStatus(sound, Status.DONE);
        System.out.println(SummerDrive);

        System.out.println();
        System.out.println(taskManager.getTasks());
        System.out.println();
        System.out.println(taskManager.getEpics());
        System.out.println();
        System.out.println(taskManager.getSubtasks(SummerDrive));

        taskManager.deleteTask(dogTraining.getId());
        System.out.println(taskManager.getTasks());

        System.out.println(taskManager.getEpics());
        taskManager.deleteEpic(newSchool.getId());
        System.out.println(taskManager.getEpics());

        Subtask testUpdate = new Subtask(99, "Обновление несуществующей подзадачи",
                "Эта подзадача не должна обновиться");
        taskManager.updateSubtask(testUpdate);
        System.out.println(SummerDrive);
    }
}
