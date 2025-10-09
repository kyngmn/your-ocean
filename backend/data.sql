BEGIN;

-- ===========================
-- Big Five Codes 데이터
-- ===========================

-- O: 개방성 (Openness)
INSERT INTO big_five_codes (id, big_code, small_code, content) VALUES
                                                                   (1, 'O','O1','상상력(Imagination)'),
                                                                   (2, 'O','O2','예술적 관심(Artistic Interests)'),
                                                                   (3, 'O','O3','감정성(Emotionality)'),
                                                                   (4, 'O','O4','모험성(Adventurousness)'),
                                                                   (5, 'O','O5','지적 호기심(Intellect)'),
                                                                   (6, 'O','O6','자유주의(Liberalism)');

-- C: 성실성 (Conscientiousness)
INSERT INTO big_five_codes (id, big_code, small_code, content) VALUES
                                                                   (7, 'C','C1','자기효능감(Self-efficacy)'),
                                                                   (8, 'C','C2','질서성(Orderliness)'),
                                                                   (9, 'C','C3','책임감(Dutifulness)'),
                                                                   (10, 'C','C4','성취추구(Achievement Striving)'),
                                                                   (11, 'C','C5','자기통제(Self-discipline)'),
                                                                   (12, 'C','C6','신중함(Cautiousness)');

-- E: 외향성 (Extraversion)
INSERT INTO big_five_codes (id, big_code, small_code, content) VALUES
                                                                   (13, 'E','E1','친밀감(Friendliness)'),
                                                                   (14, 'E','E2','사교성(Gregariousness)'),
                                                                   (15, 'E','E3','자기주장(Assertiveness)'),
                                                                   (16, 'E','E4','활동 수준(Activity Level)'),
                                                                   (17, 'E','E5','자극 추구(Excitement Seeking)'),
                                                                   (18, 'E','E6','쾌활성(Cheerfulness)');

-- A: 친화성 (Agreeableness)
INSERT INTO big_five_codes (id, big_code, small_code, content) VALUES
                                                                   (19, 'A','A1','신뢰(Trust)'),
                                                                   (20, 'A','A2','도덕성(Morality)'),
                                                                   (21, 'A','A3','이타심(Altruism)'),
                                                                   (22, 'A','A4','협동(Cooperation)'),
                                                                   (23, 'A','A5','겸손(Modesty)'),
                                                                   (24, 'A','A6','연민(Sympathy)');

-- N: 신경성 (Neuroticism)
INSERT INTO big_five_codes (id, big_code, small_code, content) VALUES
                                                                   (25, 'N','N1','불안(Anxiety)'),
                                                                   (26, 'N','N2','분노(Anger)'),
                                                                   (27, 'N','N3','우울(Depression)'),
                                                                   (28, 'N','N4','자의식(Self-consciousness)'),
                                                                   (29, 'N','N5','충동성(Immoderation)'),
                                                                   (30, 'N','N6','취약성(Vulnerability)');

-- ===========================
-- Survey 데이터 (120개 설문 문항) - ID 순서대로 정렬
-- ===========================

INSERT INTO surveys (id, big_five_id, is_reverse_scored, question_text) VALUES
                                                                            (1,   (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 사소한 일에도 걱정을 많이 하는 편이다.'),
                                                                            (2,   (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 쉽게 친구를 사귀는 편이다.'),
                                                                            (3,   (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 상상력이 풍부한 편이다.'),
                                                                            (4,   (SELECT id FROM big_five_codes WHERE small_code='A1'), false, '나는 다른 사람을 잘 믿는 편이다.'),
                                                                            (5,   (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 맡은 일을 잘 끝내는 편이다.'),
                                                                            (6,   (SELECT id FROM big_five_codes WHERE small_code='N2'), false, '나는 쉽게 화를 내는 편이다.'),
                                                                            (7,   (SELECT id FROM big_five_codes WHERE small_code='E2'), false, '나는 사람들이 많이 모이는 자리를 좋아한다.'),
                                                                            (8,   (SELECT id FROM big_five_codes WHERE small_code='O2'), false, '나는 다른 사람들이 잘 보지 못하는 아름다움을 발견하는 편이다.'),
                                                                            (9,   (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 아부를 이용해 이익을 얻으려는 편이다.'),
                                                                            (10,  (SELECT id FROM big_five_codes WHERE small_code='C2'), false, '나는 질서 정연한 것을 좋아한다.'),
                                                                            (11,  (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 자주 우울감을 느끼는 편이다.'),
                                                                            (12,  (SELECT id FROM big_five_codes WHERE small_code='E3'), false, '나는 상황을 주도하려는 편이다.'),
                                                                            (13,  (SELECT id FROM big_five_codes WHERE small_code='O3'), false, '나는 감정을 강하게 느끼는 편이다.'),
                                                                            (14,  (SELECT id FROM big_five_codes WHERE small_code='A3'), false, '나는 다른 사람을 따뜻하게 맞이하는 편이다.'),
                                                                            (15,  (SELECT id FROM big_five_codes WHERE small_code='C3'), false, '나는 약속을 잘 지키는 편이다.'),
                                                                            (16,  (SELECT id FROM big_five_codes WHERE small_code='N4'), false, '나는 다른 사람에게 먼저 다가가기 어렵다.'),
                                                                            (17,  (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 늘 바쁘게 지내는 편이다.'),
                                                                            (18,  (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 새로운 것보다는 익숙한 것을 고수하는 편이다.'),
                                                                            (19,  (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 건강한 논쟁을 좋아하는 편이다.'),
                                                                            (20,  (SELECT id FROM big_five_codes WHERE small_code='C4'), false, '나는 열심히 일하는 편이다.'),
                                                                            (21,  (SELECT id FROM big_five_codes WHERE small_code='N5'), false, '나는 음식을 자주 과식하는 편이다.'),
                                                                            (22,  (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 흥분되는 상황을 좋아한다.'),
                                                                            (23,  (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 추상적인 아이디어에는 관심이 없는 편이다.'),
                                                                            (24,  (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 내가 다른 사람보다 낫다고 생각하는 편이다.'),
                                                                            (25,  (SELECT id FROM big_five_codes WHERE small_code='C5'), false, '나는 일을 바로 시작하는 편이다.'),
                                                                            (26,  (SELECT id FROM big_five_codes WHERE small_code='N6'), false, '나는 일을 감당하기 힘들다고 느낄 때가 많다.'),
                                                                            (27,  (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 즐거움을 잘 드러내는 편이다.'),
                                                                            (28,  (SELECT id FROM big_five_codes WHERE small_code='O6'), false, '나는 진보적인 정치 성향의 후보에게 투표하는 편이다.'),
                                                                            (29,  (SELECT id FROM big_five_codes WHERE small_code='A6'), false, '나는 노숙자나 어려운 사람들에게 연민을 느낀다.'),
                                                                            (30,  (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 깊이 생각하지 않고 덤비는 편이다.'),
                                                                            (31,  (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 최악의 상황을 두려워하는 편이다.'),
                                                                            (32,  (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 다른 사람과 금세 친해지는 편이다.'),
                                                                            (33,  (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 공상을 좋아하는 편이다.'),
                                                                            (34,  (SELECT id FROM big_five_codes WHERE small_code='A1'), false, '나는 다른 사람들이 선의를 가지고 있다고 믿는 편이다.'),
                                                                            (35,  (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 내가 하는 일에서 뛰어난 성과를 내는 편이다.'),
                                                                            (36,  (SELECT id FROM big_five_codes WHERE small_code='N2'), false, '나는 쉽게 짜증이 나는 편이다.'),
                                                                            (37,  (SELECT id FROM big_five_codes WHERE small_code='E2'), false, '나는 모임에서 다양한 사람들과 이야기하는 편이다.'),
                                                                            (38,  (SELECT id FROM big_five_codes WHERE small_code='O2'), true,  '나는 예술을 좋아하지 않는다.'),
                                                                            (39,  (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 규칙을 피하는 방법을 잘 안다.'),
                                                                            (40,  (SELECT id FROM big_five_codes WHERE small_code='C2'), false, '나는 정리정돈을 좋아한다.'),
                                                                            (41,  (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 자신을 싫어하는 편이다.'),
                                                                            (42,  (SELECT id FROM big_five_codes WHERE small_code='E3'), false, '나는 다른 사람을 이끌려고 한다.'),
                                                                            (43,  (SELECT id FROM big_five_codes WHERE small_code='O3'), true,  '나는 감정이 잘 드러나지 않는 편이다.'),
                                                                            (44,  (SELECT id FROM big_five_codes WHERE small_code='A3'), false, '나는 다른 사람을 돕는 것을 좋아한다.'),
                                                                            (45,  (SELECT id FROM big_five_codes WHERE small_code='C3'), false, '나는 진실을 말하는 편이다.'),
                                                                            (46,  (SELECT id FROM big_five_codes WHERE small_code='N4'), false, '나는 쉽게 위축되는 편이다.'),
                                                                            (47,  (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 늘 바쁘게 움직이는 편이다.'),
                                                                            (48,  (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 변화를 좋아하지 않는다.'),
                                                                            (49,  (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 자주 소리를 지른다.'),
                                                                            (50,  (SELECT id FROM big_five_codes WHERE small_code='C4'), false, '나는 기대 이상의 일을 해내는 편이다.'),
                                                                            (51,  (SELECT id FROM big_five_codes WHERE small_code='N5'), false, '나는 폭식이나 폭음 같은 과한 행동을 하는 편이다.'),
                                                                            (52,  (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 모험을 추구한다.'),
                                                                            (53,  (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 철학적 논의를 피하는 편이다.'),
                                                                            (54,  (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 나 자신을 높이 평가한다.'),
                                                                            (55,  (SELECT id FROM big_five_codes WHERE small_code='C5'), true,  '나는 일을 시작하기 어려워한다.'),
                                                                            (56,  (SELECT id FROM big_five_codes WHERE small_code='N6'), true,  '나는 압박 속에서도 침착함을 유지한다.'),
                                                                            (57,  (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 즐거움을 많이 느끼는 편이다.'),
                                                                            (58,  (SELECT id FROM big_five_codes WHERE small_code='O6'), true,  '나는 단 하나의 진정한 종교를 믿는다.'),
                                                                            (59,  (SELECT id FROM big_five_codes WHERE small_code='A6'), false, '나는 나보다 어려운 처지에 있는 이들에게 동정심을 느낀다.'),
                                                                            (60,  (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 성급하게 결정을 내리는 편이다.'),
                                                                            (61,  (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 많은 것들을 두려워한다.'),
                                                                            (62,  (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 사람들 사이에서 편안함을 느낀다.'),
                                                                            (63,  (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 공상에 빠지는 것을 좋아한다.'),
                                                                            (64,  (SELECT id FROM big_five_codes WHERE small_code='A1'), false, '나는 사람들이 하는 말을 잘 믿는다.'),
                                                                            (65,  (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 일을 매끄럽게 처리하는 편이다.'),
                                                                            (66,  (SELECT id FROM big_five_codes WHERE small_code='N2'), false, '나는 자주 화를 낸다.'),
                                                                            (67,  (SELECT id FROM big_five_codes WHERE small_code='E2'), true,  '나는 사람들이 붐비는 모임을 좋아하지 않는다.'),
                                                                            (68,  (SELECT id FROM big_five_codes WHERE small_code='O2'), true,  '나는 시를 좋아하지 않는다.'),
                                                                            (69,  (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 다른 사람을 속여서라도 앞서 나가려는 편이다.'),
                                                                            (70,  (SELECT id FROM big_five_codes WHERE small_code='C2'), true,  '나는 방이나 물건을 어질러 놓는 편이다.'),
                                                                            (71,  (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 자주 우울감에 빠진다.'),
                                                                            (72,  (SELECT id FROM big_five_codes WHERE small_code='E3'), false, '나는 상황을 통제하려 한다.'),
                                                                            (73,  (SELECT id FROM big_five_codes WHERE small_code='O3'), true,  '나는 감정에 쉽게 휘둘리지 않는다.'),
                                                                            (74,  (SELECT id FROM big_five_codes WHERE small_code='A3'), false, '나는 타인을 배려하는 편이다.'),
                                                                            (75,  (SELECT id FROM big_five_codes WHERE small_code='C3'), true,  '나는 약속을 잘 어긴다.'),
                                                                            (76,  (SELECT id FROM big_five_codes WHERE small_code='N4'), true,  '나는 쉽게 당황하지 않는 편이다.'),
                                                                            (77,  (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 여가 시간에 많은 일을 한다.'),
                                                                            (78,  (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 변화라는 개념을 좋아하지 않는다.'),
                                                                            (79,  (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 다른 사람을 모욕하는 경우가 있다.'),
                                                                            (80,  (SELECT id FROM big_five_codes WHERE small_code='C4'), false, '나는 나와 다른 사람에게 높은 기준을 세우는 편이다.'),
                                                                            (81,  (SELECT id FROM big_five_codes WHERE small_code='N5'), true,  '나는 과도하게 몰두하는 일이 드물다.'),
                                                                            (82,  (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 활동적인 것을 좋아한다.'),
                                                                            (83,  (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 추상적인 개념을 이해하기 어렵다.'),
                                                                            (84,  (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 나 자신을 높게 평가하는 편이다.'),
                                                                            (85,  (SELECT id FROM big_five_codes WHERE small_code='C5'), true,  '나는 일을 시작할 때 다른 사람의 자극이 필요하다.'),
                                                                            (86,  (SELECT id FROM big_five_codes WHERE small_code='N6'), true,  '나는 어려움에 잘 대처할 줄 안다.'),
                                                                            (87,  (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 삶을 사랑한다.'),
                                                                            (88,  (SELECT id FROM big_five_codes WHERE small_code='O6'), true,  '나는 보수 정치 후보에게 투표하는 경향이 있다.'),
                                                                            (89,  (SELECT id FROM big_five_codes WHERE small_code='A6'), false, '나는 타인의 슬픔에 공감하는 편이다.'),
                                                                            (90,  (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 일을 성급하게 처리하는 편이다.'),
                                                                            (91,  (SELECT id FROM big_five_codes WHERE small_code='N1'), false, '나는 쉽게 스트레스를 받는 편이다.'),
                                                                            (92,  (SELECT id FROM big_five_codes WHERE small_code='E1'), false, '나는 사람들과 함께 있을 때 편안하다.'),
                                                                            (93,  (SELECT id FROM big_five_codes WHERE small_code='O1'), false, '나는 혼자 사색하는 것을 좋아한다.'),
                                                                            (94,  (SELECT id FROM big_five_codes WHERE small_code='A1'), true,  '나는 사람들을 잘 믿지 않는다.'),
                                                                            (95,  (SELECT id FROM big_five_codes WHERE small_code='C1'), false, '나는 일을 잘 해내는 방법을 알고 있다.'),
                                                                            (96,  (SELECT id FROM big_five_codes WHERE small_code='N2'), true,  '나는 좀처럼 짜증을 내지 않는다.'),
                                                                            (97,  (SELECT id FROM big_five_codes WHERE small_code='E2'), true,  '나는 사람이 많은 곳을 피한다.'),
                                                                            (98,  (SELECT id FROM big_five_codes WHERE small_code='O2'), true,  '나는 미술관에 가는 것을 즐기지 않는다.'),
                                                                            (99,  (SELECT id FROM big_five_codes WHERE small_code='A2'), true,  '나는 다른 사람을 이용하는 편이다.'),
                                                                            (100, (SELECT id FROM big_five_codes WHERE small_code='C2'), true,  '나는 물건을 아무 데나 두는 편이다.'),
                                                                            (101, (SELECT id FROM big_five_codes WHERE small_code='N3'), false, '나는 나 자신에 대해 낮게 평가한다.'),
                                                                            (102, (SELECT id FROM big_five_codes WHERE small_code='E3'), true,  '나는 타인이 먼저 나서길 기다리는 편이다.'),
                                                                            (103, (SELECT id FROM big_five_codes WHERE small_code='O3'), true,  '나는 감정적 기복이 거의 없다.'),
                                                                            (104, (SELECT id FROM big_five_codes WHERE small_code='A3'), true,  '나는 타인에게 등을 돌린다.'),
                                                                            (105, (SELECT id FROM big_five_codes WHERE small_code='C3'), true,  '나는 내 할 일을 다른 사람에게 떠넘기는 편이다.'),
                                                                            (106, (SELECT id FROM big_five_codes WHERE small_code='N4'), true,  '나는 내 의견을 당당히 말할 수 있다.'),
                                                                            (107, (SELECT id FROM big_five_codes WHERE small_code='E4'), false, '나는 동시에 많은 일을 해낼 수 있다.'),
                                                                            (108, (SELECT id FROM big_five_codes WHERE small_code='O4'), true,  '나는 전통적인 방식을 고수하는 편이다.'),
                                                                            (109, (SELECT id FROM big_five_codes WHERE small_code='A4'), true,  '나는 다른 사람에게 되갚음을 하려는 편이다.'),
                                                                            (110, (SELECT id FROM big_five_codes WHERE small_code='C4'), true,  '나는 성공하려는 의지가 강하지 않다.'),
                                                                            (111, (SELECT id FROM big_five_codes WHERE small_code='N5'), true,  '나는 욕구를 잘 절제할 수 있다.'),
                                                                            (112, (SELECT id FROM big_five_codes WHERE small_code='E5'), false, '나는 무모한 행동을 즐기는 편이다.'),
                                                                            (113, (SELECT id FROM big_five_codes WHERE small_code='O5'), true,  '나는 이론적인 토론에는 관심이 없다.'),
                                                                            (114, (SELECT id FROM big_five_codes WHERE small_code='A5'), true,  '나는 스스로를 주목받게 만드는 편이다.'),
                                                                            (115, (SELECT id FROM big_five_codes WHERE small_code='C5'), true,  '나는 일을 시작하기 어려워한다.'),
                                                                            (116, (SELECT id FROM big_five_codes WHERE small_code='N6'), true,  '나는 긴장된 상황에서도 침착하다.'),
                                                                            (117, (SELECT id FROM big_five_codes WHERE small_code='E6'), false, '나는 크게 웃는 편이다.'),
                                                                            (118, (SELECT id FROM big_five_codes WHERE small_code='O6'), true,  '나는 국가의식 행사(국기에 대한 맹세 등)에 기꺼이 참여하는 편이다.'),
                                                                            (119, (SELECT id FROM big_five_codes WHERE small_code='A6'), true,  '나는 다른 사람의 문제에 관심이 없는 편이다.'),
                                                                            (120, (SELECT id FROM big_five_codes WHERE small_code='C6'), true,  '나는 깊이 생각하지 않고 행동하는 편이다.');

-- ===========================
-- UG Game Order 데이터 (90개 게임 순서)
-- ===========================

-- Day 1: id 1-30 (role 1→2→3, persona 랜덤)
-- role 1 (랜덤 순서)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (1,  1, 'FRIEND',   'LARGE',  0),
                                                                         (2,  1, 'STRANGER', 'SMALL',  0),
                                                                         (3,  1, 'FAMILY',   'LARGE',  0),
                                                                         (4,  1, 'FRIEND',   'SMALL',  0),
                                                                         (5,  1, 'STRANGER', 'LARGE',  0),
                                                                         (6,  1, 'FAMILY',   'SMALL',  0);

-- role 2 (각 케이스별 3개씩)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (7,  2, 'STRANGER', 'LARGE',  1),
                                                                         (8,  2, 'FAMILY',   'SMALL',  5),
                                                                         (9,  2, 'FRIEND',   'LARGE',  3),
                                                                         (10, 2, 'STRANGER', 'SMALL',  7),
                                                                         (11, 2, 'FAMILY',   'LARGE',  2),
                                                                         (12, 2, 'FRIEND',   'SMALL',  9),
                                                                         (13, 2, 'STRANGER', 'LARGE',  3),
                                                                         (14, 2, 'FAMILY',   'SMALL',  1),
                                                                         (15, 2, 'FRIEND',   'LARGE',  6),
                                                                         (16, 2, 'FAMILY',   'LARGE',  9),
                                                                         (17, 2, 'STRANGER', 'SMALL',  5),
                                                                         (18, 2, 'FRIEND',   'SMALL',  2),
                                                                         (19, 2, 'FAMILY',   'SMALL',  8),
                                                                         (20, 2, 'FRIEND',   'LARGE',  4),
                                                                         (21, 2, 'STRANGER', 'LARGE',  8),
                                                                         (22, 2, 'STRANGER', 'SMALL',  4),
                                                                         (23, 2, 'FAMILY',   'LARGE',  7),
                                                                         (24, 2, 'FRIEND',   'SMALL',  6);

-- role 3 (랜덤 순서)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (25, 3, 'STRANGER', 'LARGE',  0),
                                                                         (26, 3, 'FAMILY',   'SMALL',  0),
                                                                         (27, 3, 'FRIEND',   'LARGE',  0),
                                                                         (28, 3, 'STRANGER', 'SMALL',  0),
                                                                         (29, 3, 'FAMILY',   'LARGE',  0),
                                                                         (30, 3, 'FRIEND',   'SMALL',  0);

-- Day 2: id 31-60 (role 1→2→3, persona 랜덤)
-- role 1 (랜덤 순서)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (31, 1, 'FAMILY',   'SMALL',  0),
                                                                         (32, 1, 'STRANGER', 'LARGE',  0),
                                                                         (33, 1, 'FRIEND',   'SMALL',  0),
                                                                         (34, 1, 'FAMILY',   'LARGE',  0),
                                                                         (35, 1, 'STRANGER', 'SMALL',  0),
                                                                         (36, 1, 'FRIEND',   'LARGE',  0);

-- role 2 (각 케이스별 3개씩)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (37, 2, 'FRIEND',   'LARGE',  9),
                                                                         (38, 2, 'FAMILY',   'LARGE',  1),
                                                                         (39, 2, 'STRANGER', 'SMALL',  6),
                                                                         (40, 2, 'FAMILY',   'SMALL',  2),
                                                                         (41, 2, 'STRANGER', 'LARGE',  4),
                                                                         (42, 2, 'FRIEND',   'SMALL',  5),
                                                                         (43, 2, 'FAMILY',   'LARGE',  3),
                                                                         (44, 2, 'FRIEND',   'LARGE',  1),
                                                                         (45, 2, 'STRANGER', 'LARGE',  6),
                                                                         (46, 2, 'FAMILY',   'SMALL',  4),
                                                                         (47, 2, 'STRANGER', 'SMALL',  9),
                                                                         (48, 2, 'FRIEND',   'SMALL',  8),
                                                                         (49, 2, 'FAMILY',   'LARGE',  8),
                                                                         (50, 2, 'FRIEND',   'LARGE',  7),
                                                                         (51, 2, 'STRANGER', 'LARGE',  2),
                                                                         (52, 2, 'FAMILY',   'SMALL',  7),
                                                                         (53, 2, 'FRIEND',   'SMALL',  3),
                                                                         (54, 2, 'STRANGER', 'SMALL',  1);

-- role 3 (랜덤 순서)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (55, 3, 'FRIEND',   'LARGE',  0),
                                                                         (56, 3, 'FAMILY',   'LARGE',  0),
                                                                         (57, 3, 'STRANGER', 'SMALL',  0),
                                                                         (58, 3, 'FRIEND',   'SMALL',  0),
                                                                         (59, 3, 'STRANGER', 'LARGE',  0),
                                                                         (60, 3, 'FAMILY',   'SMALL',  0);

-- Day 3: id 61-90 (role 1→2→3, persona 랜덤)
-- role 1 (랜덤 순서)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (61, 1, 'STRANGER', 'LARGE',  0),
                                                                         (62, 1, 'FAMILY',   'LARGE',  0),
                                                                         (63, 1, 'FRIEND',   'SMALL',  0),
                                                                         (64, 1, 'STRANGER', 'SMALL',  0),
                                                                         (65, 1, 'FAMILY',   'SMALL',  0),
                                                                         (66, 1, 'FRIEND',   'LARGE',  0);

-- role 2 (남은 rate로 1~9 완주)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (67, 2, 'FRIEND',   'SMALL',  7),
                                                                         (68, 2, 'STRANGER', 'SMALL',  3),
                                                                         (69, 2, 'FAMILY',   'LARGE',  6),
                                                                         (70, 2, 'FRIEND',   'LARGE',  5),
                                                                         (71, 2, 'STRANGER', 'LARGE',  9),
                                                                         (72, 2, 'FAMILY',   'SMALL',  9),
                                                                         (73, 2, 'STRANGER', 'SMALL',  8),
                                                                         (74, 2, 'FAMILY',   'LARGE',  4),
                                                                         (75, 2, 'FRIEND',   'SMALL',  1),
                                                                         (76, 2, 'FAMILY',   'SMALL',  3),
                                                                         (77, 2, 'FRIEND',   'LARGE',  8),
                                                                         (78, 2, 'STRANGER', 'LARGE',  7),
                                                                         (79, 2, 'FAMILY',   'LARGE',  5),
                                                                         (80, 2, 'STRANGER', 'SMALL',  2),
                                                                         (81, 2, 'FRIEND',   'LARGE',  2),
                                                                         (82, 2, 'FAMILY',   'SMALL',  6),
                                                                         (83, 2, 'STRANGER', 'LARGE',  5),
                                                                         (84, 2, 'FRIEND',   'SMALL',  4);

-- role 3 (랜덤 순서)
INSERT INTO game_ug_order (id, role_type, persona_type, money, rate) VALUES
                                                                         (85, 3, 'FAMILY',   'SMALL',  0),
                                                                         (86, 3, 'STRANGER', 'LARGE',  0),
                                                                         (87, 3, 'FRIEND',   'SMALL',  0),
                                                                         (88, 3, 'FAMILY',   'LARGE',  0),
                                                                         (89, 3, 'FRIEND',   'LARGE',  0),
                                                                         (90, 3, 'STRANGER', 'SMALL',  0);

-- ID 1=O, 2=C, 3=E, 4=A, 5=N
INSERT INTO actors (id, kind, user_id, persona_id) VALUES
                                                       (1, 'SYSTEM', NULL, NULL),  -- Openness (개방성)
                                                       (2, 'SYSTEM', NULL, NULL),  -- Conscientiousness (성실성)
                                                       (3, 'SYSTEM', NULL, NULL),  -- Extraversion (외향성)
                                                       (4, 'SYSTEM', NULL, NULL),  -- Agreeableness (친화성)
                                                       (5, 'SYSTEM', NULL, NULL)  -- Neuroticism (신경성)
ON CONFLICT (id) DO NOTHING

-- actors sequence를 6부터 시작하도록 재설정
SELECT setval('actors_id_seq', 5, true);

COMMIT;

select * from actors;

select * from big5_results;