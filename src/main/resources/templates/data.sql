INSERT INTO swot_factor (id, name, type, expected_min, expected_max, extreme_min, extreme_max) VALUES
                                                                                                   (1, 'Сильная команда', 'strong', 4, 7, 1, 10),
                                                                                                   (2, 'Хороший UX', 'strong', 4, 7, 1, 10),
                                                                                                   (3, 'Гибкость архитектуры', 'strong', 4, 7, 1, 10),
                                                                                                   (4, 'Мало пользователей', 'weak', 3, 6, 1, 9),
                                                                                                   (5, 'Недостаток финансирования', 'weak', 4, 6, 2, 8),
                                                                                                   (6, 'Рост рынка', 'opportunity', 5, 8, 2, 10),
                                                                                                   (7, 'Гос. гранты', 'opportunity', 4, 7, 1, 9),
                                                                                                   (8, 'Конкуренты', 'threat', 4, 6, 1, 8),
                                                                                                   (9, 'Снижение интереса к теме', 'threat', 3, 5, 1, 9);

INSERT INTO swot_factor (id, name, type, number) VALUES
                                                     (1, 'Команда', 'strong', '0.212'),
                                                     (2, 'UX', 'strong', '0.52'),
                                                     (3, 'Гибкость', 'strong', '0.663');

INSERT INTO alternative (id, factor1, factor2, d_minus, d_plus, d_star, factor1_percentage, factor2_percentage)
VALUES
    (1, 'Команда', 'Финансирование', 0.24, 0.17, 0.28, 50, 50),
    (2, 'UX', 'Рынок', 0.3, 0.22, 0.25, 60, 40);
