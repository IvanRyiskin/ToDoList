package todo.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@RequiredArgsConstructor
public class Task {
    @Setter(AccessLevel.NONE)
    private final int ID;
    @NonNull
    private String title;
    @NonNull
    private String description;
    @Setter(AccessLevel.NONE)
    private final LocalDateTime CREATEDATE;
    private LocalDateTime updateDate;
}
