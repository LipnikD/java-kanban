public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Task theatre = taskManager.createTask("Купить билеты в театр",
                "Выбрать из еще не полученных впечатлений новый сюжет и театр.");
        Task dogTraining = taskManager.createTask("Научить собаку находить сокровища",
                "Питомец, регулярно улучшающий финансовое благосостояние, хорошо дополнит трудовые доходы.");

        Epyc SummerDrive = taskManager.createEpyc("Подготовить машину к лету",
                "Комплекс мероприятий для комфортного вхождения в летний сезон активного вождения.");

        Subtask tyres = taskManager.createSubTask(SummerDrive, "Купить летние шины",
                "Необходимо выбрать на Яндекс маркете лучшее предложение и приобрести новые шины");
        Subtask inpection = taskManager.createSubTask(SummerDrive, "Провести техосмотр",
                "Нужно провести регламентный осмотр всех узлов, агрегатов и тех. жидкостей");
        Subtask sound = taskManager.createSubTask(SummerDrive, "Модернизировать аудиосистему",
                "Установить акустику и необходимые компоненты (усилители, процессор) для дальних поездок");

        Epyc newSchool = taskManager.createEpyc("Перевести ребенка в новую школу",
                "Новая более сильная школа - хороший старт в развитии.");
        Subtask learnMath = taskManager.createSubTask(newSchool, "Подготовка к экзамену по математике",
                "Для успешной сдачи экзаменов точно понадобится уверенное знание математики.");

        System.out.println(theatre);
        taskManager.setStatus(theatre, Status.DONE);
        theatre.setDescription("Кажется, что будет очень интересно!");
        taskManager.updateTask(theatre);
        System.out.println(theatre);

        System.out.println(dogTraining);
        taskManager.setStatus(dogTraining, Status.IN_PROGRESS);
        System.out.println(dogTraining);
        taskManager.setStatus(dogTraining, Status.NEW);
        System.out.println(dogTraining);
        taskManager.setStatus(dogTraining, Status.DONE);
        System.out.println(dogTraining);

        System.out.println(SummerDrive);
        taskManager.setStatus(tyres, Status.IN_PROGRESS);
        System.out.println(SummerDrive);
        taskManager.setStatus(inpection, Status.DONE);
        System.out.println(SummerDrive);

        SummerDrive.setName("Подготовить машину к весне 2024");
        taskManager.updateEpyc(SummerDrive);
        tyres.setName("Купить спортивные шины Michelin Pilot Sport 5");
        taskManager.updateSubtask(tyres);
        taskManager.setStatus(tyres, Status.DONE);
        taskManager.setStatus(sound, Status.DONE);
        System.out.println(SummerDrive);

        System.out.println();
        System.out.println(taskManager.getTasks());
        System.out.println();
        System.out.println(taskManager.getEpycs());
        System.out.println();
        System.out.println(taskManager.getSubtasks(SummerDrive));

        taskManager.deleteTask(dogTraining);
        System.out.println(taskManager.getTasks());

        System.out.println(taskManager.getEpycs());
        taskManager.deleteEpyc(newSchool);
        System.out.println(taskManager.getEpycs());

        taskManager.deleteSubtask(learnMath);
        taskManager.deleteEpyc(newSchool);
        System.out.println(taskManager.getEpycs());

    }
}
