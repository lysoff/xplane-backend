DO '
    DECLARE
        MODEL_BASIC_ID            uuid := ''2d4e6d50-5883-48e2-bac1-d33c660fa75a''::uuid;
        RESOURCE_VIGOR_ID         uuid := ''e040b0c0-dfbc-49e1-b315-ce2d5c449c15''::uuid;
        RESOURCE_WELL_BEING_ID    uuid := ''a7a3d9bb-180c-427b-9f5d-e139e8da1f53''::uuid;
        ACTIVITY_WORKOUT_ID       uuid := ''abd6f39b-b386-48ce-8598-06c20482c38b''::uuid;
        ACTIVITY_EAT_JUNK_FOOD_ID uuid := ''48651546-92c2-4e33-b583-38d1f237bd51''::uuid;
    BEGIN

        INSERT INTO model (id, name)
        VALUES (MODEL_BASIC_ID, ''Basic Model'');

        INSERT INTO resource(id, name)
        VALUES (RESOURCE_VIGOR_ID, ''Vigor''),
               (RESOURCE_WELL_BEING_ID, ''Well-being'');

        INSERT into model_resource(model_id, resource_id, amount)
        VALUES (MODEL_BASIC_ID, RESOURCE_VIGOR_ID, 100),
               (MODEL_BASIC_ID, RESOURCE_WELL_BEING_ID, 50);

        INSERT INTO activity(id, name)
        VALUES (ACTIVITY_WORKOUT_ID, ''Workout''),
               (ACTIVITY_EAT_JUNK_FOOD_ID, ''Eating junk food'');

        INSERT INTO model_activity(model_id, activity_id)
        VALUES (MODEL_BASIC_ID, ACTIVITY_WORKOUT_ID),
               (MODEL_BASIC_ID, ACTIVITY_EAT_JUNK_FOOD_ID);
    END;
' LANGUAGE plpgsql;