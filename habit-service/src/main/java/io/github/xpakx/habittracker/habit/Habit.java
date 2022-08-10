package io.github.xpakx.habittracker.habit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(name = "habit-details",
        attributeNodes = {@NamedAttributeNode("context"), @NamedAttributeNode("trigger")}
)
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "context_id")
    @JsonIgnore
    private HabitContext context;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "trigger_id")
    @JsonIgnore
    private HabitTrigger trigger;

    private Integer interval;
    private Integer dailyCompletions;
    private Integer completions;
    private LocalDateTime start;
    private LocalDateTime nextDue;
    private Integer difficulty;
}
