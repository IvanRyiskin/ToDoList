package todo.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static int idCounter = 1;
    private final int ID;
    private String title;
    private String description;
    private final LocalDateTime CREATEDATE;
    private LocalDateTime updateDate;

//    public enum Status {
//        NEW, IN_PROGRESS, DONE
//    }

    public Task(String title, String description) {
        this.ID = idCounter++;
        this.title = title;
        this.description = description;
        this.CREATEDATE = LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getID() {
        return ID;
    }

    public LocalDateTime getCREATEDATE() {
        return CREATEDATE;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate() {
        this.updateDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ID == task.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public String toString() {
        return "Task{" +
                "ID=" + ID +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", CREATEDATE=" + CREATEDATE + '\'' +
                ", updateDate=" + updateDate +
                '}';
    }
}
