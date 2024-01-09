INSERT INTO model (id, name) VALUES
    ('2d4e6d50-5883-48e2-bac1-d33c660fa75a', 'Basic Model');

INSERT INTO resource(id, name) VALUES
    ('e040b0c0-dfbc-49e1-b315-ce2d5c449c15', 'Vigor'),
    ('a7a3d9bb-180c-427b-9f5d-e139e8da1f53', 'Well-being');

INSERT into model_resource(model_id, resource_id, amount) VALUES
    ('2d4e6d50-5883-48e2-bac1-d33c660fa75a', 'e040b0c0-dfbc-49e1-b315-ce2d5c449c15', 100),  -- Vigor
    ('2d4e6d50-5883-48e2-bac1-d33c660fa75a', 'a7a3d9bb-180c-427b-9f5d-e139e8da1f53', 50);   -- Well-being

INSERT INTO activity(id, name) VALUES
    ('abd6f39b-b386-48ce-8598-06c20482c38b', 'Workout'),
    ('48651546-92c2-4e33-b583-38d1f237bd51', 'Eating junk food');

INSERT INTO model_activity(model_id, activity_id) VALUES
    ('2d4e6d50-5883-48e2-bac1-d33c660fa75a', 'abd6f39b-b386-48ce-8598-06c20482c38b'),   -- Workout
    ('2d4e6d50-5883-48e2-bac1-d33c660fa75a', '48651546-92c2-4e33-b583-38d1f237bd51');   -- Eating junk food
