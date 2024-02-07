public enum Status {
    NEW,
    IN_PROGRESS,
    DONE;

    @Override
    public String toString() {
        switch (name()) {
            case "NEW":
                return "New";
            case "IN_PROGRESS":
                return "In progress";
            case "DONE":
                return "Done";
            default:
                return "";
        }
    }
}
