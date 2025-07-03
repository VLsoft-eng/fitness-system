ALTER TABLE full_exercises
    ADD COLUMN gym_room_id BIGINT,
ADD COLUMN training_machine_id BIGINT;

ALTER TABLE full_exercises
    ADD CONSTRAINT fk_full_exercises_gym_room
        FOREIGN KEY (gym_room_id)
            REFERENCES gym_rooms(id);

ALTER TABLE full_exercises
    ADD CONSTRAINT fk_full_exercises_training_machine
        FOREIGN KEY (training_machine_id)
            REFERENCES train_machine(id);