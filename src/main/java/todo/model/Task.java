package todo.model;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Task  implements Serializable {
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
