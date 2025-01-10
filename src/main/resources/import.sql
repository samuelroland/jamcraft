INSERT INTO samples (name, filepath, duration, created_at) VALUES
('Funk Guitar Thing', 'aptbr__funk-guitar-thing.mp3', 12.042449, CURRENT_TIMESTAMP),
('Lotus Guzheng Chops CM', 'f-r-a-g-i-l-e__lotus-guzheng-chops-cm.mp3', 8.803250, CURRENT_TIMESTAMP),
('Piano Loops 120 BPM', 'josefpres__piano-loops-090-efect-3-octave-long-loop-120-bpm.mp3', 130.037551, CURRENT_TIMESTAMP),
('Player Turn Start', 'mickleness__player-turn-start.mp3', 2.56, CURRENT_TIMESTAMP),
('Electro House Loop', 'sakebeats__electro-house-loop.mp3', 7.549388, CURRENT_TIMESTAMP),
('Quality Techno Beat', 'teacoma__quality-techno-beat.mp3', 7.706122, CURRENT_TIMESTAMP),
('Impact Techno Drum F1 Loop', 'trubalesk__125bpm_impact_techno-drum-f1-loop-boom.mp3', 7.706122, CURRENT_TIMESTAMP),
('Cool Breeze Sample 2', 'wave_former__cool-breeze-sample-2.mp3', 8.907755, CURRENT_TIMESTAMP);

INSERT INTO tracks (name, created_at, modified_at) VALUES
    ('Track 1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
('Track 2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
('Track 3', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
('Track 4', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO sample_tracks (track_id, sample_id, start_time) VALUES
(1, 1, 1.623)
(1, 2, 3.623)
(1, 7, 4.1)
(2, 7, 4.1)
(2, 8, 0.0)
(2, 2, 4.1)
(3, 2, 10.1)
(4, 3, 0);

