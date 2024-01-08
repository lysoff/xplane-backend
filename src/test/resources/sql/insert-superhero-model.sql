INSERT INTO model (id, name) VALUES
    ('a4fe64d1-9779-465a-9754-78c683eab335', 'Superhero Model');

INSERT INTO resource(id, name) VALUES
    ('2b4311ac-028a-439c-94a4-a5cd2f0b5fdb', 'Superpower');

INSERT into model_resource(model_id, resource_id, amount) VALUES
    ('a4fe64d1-9779-465a-9754-78c683eab335', '2b4311ac-028a-439c-94a4-a5cd2f0b5fdb', 99);   -- Well-being
