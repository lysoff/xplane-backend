DO '
    DECLARE
        MODEL_SUPERHERO_ID uuid     := ''a4fe64d1-9779-465a-9754-78c683eab335'';
        RESOURCE_SUPERPOWER_ID uuid := ''2b4311ac-028a-439c-94a4-a5cd2f0b5fdb'';
        ACTIVITY_FLYING_ID uuid     := ''2d487791-61c2-4ba5-9c13-3c1374835912'';
    BEGIN
        INSERT INTO model (id, name) VALUES
            (MODEL_SUPERHERO_ID, ''Superhero Model'');

        INSERT INTO resource(id, name) VALUES
            (RESOURCE_SUPERPOWER_ID, ''Superpower'');

        INSERT into model_resource(model_id, resource_id, amount) VALUES
            (MODEL_SUPERHERO_ID, RESOURCE_SUPERPOWER_ID, 99);

        INSERT INTO activity(id, name) VALUES
            (ACTIVITY_FLYING_ID, ''Flying'');

        INSERT INTO model_activity(model_id, activity_id) VALUES
            (MODEL_SUPERHERO_ID, ACTIVITY_FLYING_ID);
    END;
'