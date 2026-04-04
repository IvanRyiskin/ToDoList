package todo.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Task implements Serializable {
    @Setter(AccessLevel.NONE)
    private final int ID;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @Setter(AccessLevel.NONE)
    private final LocalDateTime CREATEDATE;
    private LocalDateTime updateDate;

    public Task(int ID, String title, String description, LocalDateTime CREATEDATE, LocalDateTime updateDate) {
        this.ID = ID;
        this.title = title;
        this.description = description;
        this.CREATEDATE = CREATEDATE;
        this.updateDate = updateDate;
    }
}
